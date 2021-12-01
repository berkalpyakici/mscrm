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

    /** List of tries for each reference genome. */
    private final List<Trie> referenceTrie;

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
            // write the results
            // through samWriter.
            for (Future<List<Result>> fr : results) {
                List<Result> aligns = fr.get();

                if (aligns.size() % 2 == 0 && !aligns.contains(null)) {
                    aligns.forEach(
                            align -> this.samWriter.addAlignment(align.getRead(), align.getPos()));
                }
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
                                            return new Result(read, beginPos);
                                        }
                                    }

                                    String curSeqComp =
                                            Transform.getReverseComplement(read.getSequence());
                                    beginPos = align(curSeqComp, k);

                                    if (beginPos >= 0) {
                                        return new Result(read, beginPos);
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

    /** Class to represent mapping results. */
    static class Result {
        private final Fastq read;
        private final int pos;

        public Result(Fastq read, int pos) {
            this.read = read;
            this.pos = pos;
        }

        public Fastq getRead() {
            return this.read;
        }

        public int getPos() {
            return this.pos;
        }
    }
}
