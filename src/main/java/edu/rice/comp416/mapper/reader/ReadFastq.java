package edu.rice.comp416.mapper.reader;

import java.io.File;
import org.biojava.nbio.genome.io.fastq.*;
import org.biojava.nbio.genome.io.fastq.Fastq;
import org.biojava.nbio.genome.io.fastq.FastqReader;

public class ReadFastq {
    /**
     * Read the input Fastq file and return an iterable over Fastq lines.
     *
     * @param filename File to read.
     * @return Iterable object over the Fastq lines.
     * @throws Exception If the read fails.
     */
    public static Iterable<Fastq> readFromFile(String filename) throws Exception {
        FastqReader fastqReader = new SangerFastqReader();
        return fastqReader.read(new File(filename));
    }
}
