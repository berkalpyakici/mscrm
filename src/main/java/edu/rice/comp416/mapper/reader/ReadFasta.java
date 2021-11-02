package edu.rice.comp416.mapper.reader;

import java.io.File;
import java.util.LinkedHashMap;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

public class ReadFasta {
    /**
     * Read the input Fasta file and return an linked hashmap over Fasta lines.
     *
     * @param filename File to read.
     * @return Hashmap over Fasta lines.
     * @throws Exception If the read fails.
     */
    public static LinkedHashMap<String, DNASequence> readFromFile(String filename)
            throws Exception {
        return FastaReaderHelper.readFastaDNASequence(new File(filename));
    }
}
