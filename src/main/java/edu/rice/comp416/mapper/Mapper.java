package edu.rice.comp416.mapper;

import edu.rice.comp416.mapper.reader.ReadFasta;
import edu.rice.comp416.mapper.reader.ReadFastq;
import edu.rice.comp416.mapper.util.Timer;
import edu.rice.comp416.mapper.util.Transform;
import edu.rice.comp416.mapper.util.Trie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.genome.io.fastq.Fastq;

/** Mapper class. */
public class Mapper {

    /** Loaded FASTA file for reference genome. */
    private final LinkedHashMap<String, DNASequence> reference;

    /** List of tries for each reference genome. */
    private final List<Trie> referenceTrie;

    /** List of FASTQ reads for sample genomes. */
    private final List<Iterable<Fastq>> samples;

    /**
     * Constructor for the mapper class.
     *
     * @param referenceFile Reference fasta file path.
     * @param sampleFiles List of sample fastq file paths.
     * @throws IOException If an exception occurs when fasta and fastq files are being read.
     */
    public Mapper(String referenceFile, List<String> sampleFiles) throws IOException {
        Timer referenceLoadTimer = new Timer();
        this.reference = ReadFasta.readFromFile(referenceFile);
        referenceLoadTimer.stop();

        Timer samplesLoadTimer = new Timer();
        this.samples = new ArrayList<>();
        for (String sampleFile : sampleFiles) {
            this.samples.add(ReadFastq.readFromFile(sampleFile));
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
}
