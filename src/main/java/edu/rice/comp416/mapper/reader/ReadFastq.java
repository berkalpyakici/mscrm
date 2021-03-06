package edu.rice.comp416.mapper.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.biojava.nbio.genome.io.fastq.*;
import org.biojava.nbio.genome.io.fastq.Fastq;
import org.biojava.nbio.genome.io.fastq.FastqReader;

public class ReadFastq {
    /**
     * Read the input Fastq file and return an iterable over Fastq lines.
     *
     * @param filename File to read.
     * @return Iterable object over the Fastq lines.
     * @throws IOException If the read fails.
     */
    public static Iterable<Fastq> readFromFile(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists()) {
            throw new FileNotFoundException("Input file '" + filename + "' does not exist.");
        }

        FastqReader fastqReader = new SangerFastqReader();
        return fastqReader.read(file);
    }
}
