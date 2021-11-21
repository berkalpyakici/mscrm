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

    private final SAMWriter samWriter;

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
            List<String> kmers = Transform.getKmers(entry.getValue().getSequenceAsString(), k);
            Trie trie = Trie.fromKmers(kmers);
            this.referenceTrie.add(trie);
        }

        System.out.println(
                "Generated reference sequence k-mer trie in "
                        + timer.getTimeInSeconds()
                        + " seconds.");
    }

    public void map() {
        while (true) {
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

            Map<String, List<String>> curReadsSeq = new HashMap<>();
            curReads.forEach(
                    i -> {
                        try {
                            curReadsSeq.put(
                                    i.getDescription(),
                                    List.of(
                                            i.getSequence(),
                                            Transform.getReverseComplement(i.getSequence())));

                            // TODO: This is here for testing file writing. Remove afterwards.
                            this.samWriter.addAlignment(i, new Random().nextInt(20));
                        } catch (UnsupportedEncodingException e) {
                            Main.reportError(e.getMessage());
                        }
                    });

            System.out.println(curReadsSeq);
        }

        this.samWriter.close();
    }
}
