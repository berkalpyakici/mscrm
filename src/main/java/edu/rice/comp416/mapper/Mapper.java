package edu.rice.comp416.mapper;

import edu.rice.comp416.mapper.reader.ReadFasta;
import edu.rice.comp416.mapper.reader.ReadFastq;
import edu.rice.comp416.mapper.util.SAMWriter;
import edu.rice.comp416.mapper.util.Timer;
import edu.rice.comp416.mapper.util.Transform;
import edu.rice.comp416.mapper.util.Trie;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.genome.io.fastq.Fastq;

/** Mapper class. */
public class Mapper {

    /** Loaded FASTA file for reference genome. */
    private final LinkedHashMap<String, DNASequence> reference;

    /** List of tries for each reference genome. Assumes referenceTrie.size() == 1 */
    private final List<Trie> referenceTrie;

    /** List of strings for each reference string. Assumes referenceString.size() == 1 */
    private final List<String> referenceString;

    /** List of FASTQ reads for sample genomes. */
    private final List<Iterator<Fastq>> samples;

    /** SAM writer instance */
    private final SAMWriter samWriter;

    /** Success threshold */
    private static final double successThreshold = 0.2;

    /**
     * Constructor for the mapper class.
     *
     * @param referenceFile Reference fasta file path.
     * @param sampleFiles List of sample fastq file paths.
     * @param outFile Output sam file path.
     * @throws IOException If an exception occurs when fasta and fastq files are being read.
     */
    public Mapper(String referenceFile, List<String> sampleFiles, String outFile)
            throws IOException {
        Timer referenceLoadTimer = new Timer();
        this.reference = ReadFasta.readFromFile(referenceFile);
        referenceLoadTimer.stop();

        Timer samplesLoadTimer = new Timer();
        this.samples = new ArrayList<>();
        for (String sampleFile : sampleFiles) {
            this.samples.add(ReadFastq.readFromFile(sampleFile).iterator());
        }
        samplesLoadTimer.stop();

        this.referenceTrie = new ArrayList<>();
        this.referenceString = new ArrayList<>();

        System.out.println(
                "Loaded reference sequence in "
                        + referenceLoadTimer.getTimeInSeconds()
                        + " seconds.");
        System.out.println(
                "Loaded "
                        + this.samples.size()
                        + " sample sequence(s) in "
                        + samplesLoadTimer.getTimeInSeconds()
                        + " seconds.");

        this.samWriter = new SAMWriter(outFile, this.reference);
    }

    /**
     * Generate kmer tries from loaded reference fasta file.
     *
     * @param k Kmer size.
     */
    public void generateReferenceKmerTrie(int k) {
        Timer timer = new Timer();

        for (Map.Entry<String, DNASequence> entry : this.reference.entrySet()) {
            Iterator<String> kmers = Transform.getKmers(entry.getValue().getSequenceAsString(), k);
            Trie trie = Trie.fromKmers(kmers);
            this.referenceTrie.add(trie);
            this.referenceString.add(entry.getValue().getSequenceAsString());
        }

        System.out.println(
                "Generated reference sequence k-mer trie in "
                        + timer.getTimeInSeconds()
                        + " seconds.");
    }

    /**
     * Perform mapping of sample reads on reference genome.
     *
     * @param k Kmer size.
     */
    public void map(int k) {
        Timer timer = new Timer();

        int processors = Runtime.getRuntime().availableProcessors();

        System.out.println("Running mapper on " + processors + " threads.");

        // Here, we create executor service and a list of callable tasks.
        ExecutorService executorService = Executors.newFixedThreadPool(processors);
        List<Callable<List<Result>>> tasks = new ArrayList<>();

        try {
            int numReads = 0;
            while (true) {
                // Add current pair of samples to the reads array.
                List<Fastq> curReads = new ArrayList<>();
                for (Iterator<Fastq> sample : this.samples) {
                    if (sample.hasNext()) {
                        curReads.add(sample.next());
                    }
                }

                // There are no more reads. Halt.
                if (curReads.isEmpty()) {
                    break;
                }

                // If one sample finished before others, then report error and halt.
                if (this.samples.size() != curReads.size()) {
                    Main.reportError("Two reads...");
                    break;
                }

                // Increase number of reads.
                numReads += curReads.size();

                // Add all tasks to our list to execute in parallel.
                tasks.add(() -> processPairReads(curReads, k));
            }

            // Invoke all processed to run them in parallel.
            List<Future<List<Result>>> results = executorService.invokeAll(tasks);

            // After all processes are complete, we iterate through them in a single process and
            // write the results through samWriter.
            for (Future<List<Result>> fr : results) {
                List<Result> aligns = fr.get();

                if (aligns.contains(null) || aligns.size() != 2) {
                    continue;
                }

                this.samWriter.addAlignment(aligns.get(0), aligns.get(1), true, 99);
                this.samWriter.addAlignment(aligns.get(1), aligns.get(0), false, 99);
            }

            System.out.println(
                    "Mapped " + numReads + " reads in " + timer.getTimeInSeconds() + " seconds.");
        } catch (ExecutionException e) {
            Main.reportError("An execution exception occurred. See stack trace for more details.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Main.reportError(
                    "An interrupted exception occurred. See stack trace for more details.");
            e.printStackTrace();
        } finally {
            this.samWriter.close();
            executorService.shutdown();
        }
    }

    /**
     * Process paired reads (that are taken from the same fragment).
     *
     * @param curReads List of paired reads that are taken from the same fragment.
     * @param k Kmer size.
     * @return List of results that include the mapped position (if mapped); else null.
     */
    private List<Result> processPairReads(List<Fastq> curReads, int k) {
        AtomicBoolean skipCurReads = new AtomicBoolean(false);
        AtomicBoolean skipToRevComp = new AtomicBoolean(false);

        return curReads.stream()
                .map(
                        read -> {
                            if (!skipCurReads.get()) {
                                try {
                                    int beginPos;

                                    if (!skipToRevComp.get()) {
                                        String curSeq = read.getSequence();
                                        beginPos = align(curSeq, k);

                                        if (beginPos >= 0) {
                                            skipToRevComp.set(true);
                                            String cigar =
                                                    getCigar(
                                                            this.referenceString
                                                                    .get(0)
                                                                    .substring(
                                                                            beginPos,
                                                                            beginPos
                                                                                    + read.getSequence()
                                                                                            .length()),
                                                            read.getSequence());
                                            return new Result(
                                                    read.getDescription(),
                                                    read.getSequence(),
                                                    read.getQuality(),
                                                    beginPos,
                                                    cigar,
                                                    false);
                                        }
                                    }

                                    String curSeqComp =
                                            Transform.getReverseComplement(read.getSequence());
                                    beginPos = align(curSeqComp, k);

                                    if (beginPos >= 0) {
                                        String cigar =
                                                getCigar(
                                                        this.referenceString
                                                                .get(0)
                                                                .substring(
                                                                        beginPos,
                                                                        beginPos
                                                                                + read.getSequence()
                                                                                        .length()),
                                                        curSeqComp);
                                        return new Result(
                                                read.getDescription(),
                                                curSeqComp,
                                                Transform.getReverse(read.getQuality()),
                                                beginPos,
                                                cigar,
                                                true);
                                    }

                                    skipCurReads.set(true);
                                } catch (UnsupportedEncodingException e) {
                                    Main.reportError(e.getMessage());
                                }
                            }
                            return null;
                        })
                .collect(Collectors.toList());
    }

    /**
     * Find alignment for read.
     *
     * @param read Individual read sequence.
     * @param k K-mer size.
     * @return Position from beginning if aligned; if not, returns -1.
     */
    private int align(String read, int k) {
        int numRequiredMatches = (int) Math.round(read.length() * successThreshold);

        int curMatches = 0;
        int curConsensus = -1;

        int offset = 0;

        Iterator<String> kmers = Transform.getKmers(read, k);

        while (kmers.hasNext()) {
            String kmer = kmers.next();
            for (int position : this.referenceTrie.get(0).position(kmer)) {
                if (position - offset == curConsensus) {
                    curMatches += 1;

                    if (curMatches >= numRequiredMatches) {
                        return curConsensus;
                    }
                } else {
                    curMatches = 0;
                    curConsensus = position - offset;
                }
            }

            offset += 1;
        }

        return -1;
    }

    /**
     * Get cigar string from reference and read sequences.
     *
     * @implNote Assumes that length of reference and read are the same.
     * @param ref Reference sequence.
     * @param read Read sequence.
     * @return Cigar string, only consisting of '=' and 'X' operands.
     */
    private String getCigar(String ref, String read) {
        int count = 0;
        char op = '=';

        StringBuilder cigar = new StringBuilder();

        for (int i = 0; i < ref.length(); i++) {
            if (ref.charAt(i) == read.charAt(i)) {
                if (op == '=') {
                    count += 1;
                } else {
                    cigar.append(count);
                    cigar.append(op);
                    count = 1;
                    op = '=';
                }
            } else {
                if (op == 'X') {
                    count += 1;
                } else {
                    cigar.append(count);
                    cigar.append(op);
                    count = 1;
                    op = 'X';
                }
            }
        }

        if (count > 0) {
            cigar.append(count);
            cigar.append(op);
        }

        return cigar.toString();
    }

    /** Class to represent mapping results. */
    public static class Result {
        private final String description;
        private final String sequence;
        private final String quality;
        private final int pos;
        private final String cigar;
        private final boolean reversed;

        public Result(
                String description,
                String sequence,
                String quality,
                int pos,
                String cigar,
                boolean reversed) {
            this.description = description;
            this.sequence = sequence;
            this.quality = quality;
            this.pos = pos;
            this.cigar = cigar;
            this.reversed = reversed;
        }

        public String getDescription() {
            return this.description;
        }

        public String getSequence() {
            return this.sequence;
        }

        public String getQuality() {
            return this.quality;
        }

        public int getPos() {
            return this.pos;
        }

        public String getCigar() {
            return this.cigar;
        }

        public boolean getReversed() {
            return this.reversed;
        }
    }
}
