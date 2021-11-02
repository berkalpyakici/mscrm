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
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("ref.fasta"))
                        .getFile();

        LinkedHashMap<String, DNASequence> fasta = ReadFasta.readFromFile(file);
        DNASequence seq =
                fasta.get(
                        "NZ_CP044031.1 Orientia tsutsugamushi strain Wuj/2014 chromosome, complete"
                                + " genome");

        Assert.assertEquals(600615, seq.getGCCount());
    }
}
