package edu.rice.comp416.mapper.reader;

import java.util.LinkedHashMap;
import java.util.Objects;
import org.biojava.nbio.core.sequence.DNASequence;
import org.junit.Assert;
import org.junit.Test;

public class ReadFastaTest {
    @Test
    public void testRefRead() throws Exception {
        String file =
                Objects.requireNonNull(
                                this.getClass()
                                        .getClassLoader()
                                        .getResource("sars_cov_2_reference_genome.fasta"))
                        .getFile();

        LinkedHashMap<String, DNASequence> fasta = ReadFasta.readFromFile(file);
        DNASequence seq =
                fasta.get(
                        "NC_045512.2 Severe acute respiratory syndrome coronavirus 2 isolate"
                                + " Wuhan-Hu-1, complete genome");

        Assert.assertEquals(11355, seq.getGCCount());
    }
}
