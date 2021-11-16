package edu.rice.comp416.mapper.reader;

import java.util.Iterator;
import java.util.Objects;
import org.biojava.nbio.genome.io.fastq.Fastq;
import org.junit.Assert;
import org.junit.Test;

public class ReadFastqTest {
    @Test
    public void testSample1TrimRead() throws Exception {
        String file =
                Objects.requireNonNull(
                                this.getClass().getClassLoader().getResource("sample_1_trim.fastq"))
                        .getFile();

        Iterator<Fastq> fastq = ReadFastq.readFromFile(file).iterator();

        Fastq first = fastq.next();
        Assert.assertEquals("S0R0/1", first.getDescription());
        Assert.assertEquals(
                "TTTACTTACAAAGTCCTCAGAAGACAAAGGTCCTATTACGGATGTTTTCTACAAAGAAAACAGTTACACAACAACCATAAAACCAGTTACTTATAAATTGGATCGTGTTGTTTGTACAGTAATTGACCCTAAGTTGGACAATTATTATAA",
                first.getSequence());
        Assert.assertEquals(
                "=C1GGGGGGGGCGJGGGJJJJJ$JJGJ1GCGGGGGGCCGJJJJGJJGJGGJGJGCCJ=CJCGCGGGGGGCCCJGCGGG=8GGCGCGG8G1$GGCC=GGGG1=G$GGGGGGGGC$CGG=G$CGGG$GGGGGCGGGGGGCCGGG=C$CGGGC",
                first.getQuality());
    }

    @Test
    public void testSample2TrimRead() throws Exception {
        String file =
                Objects.requireNonNull(
                                this.getClass().getClassLoader().getResource("sample_2_trim.fastq"))
                        .getFile();

        Iterator<Fastq> fastq = ReadFastq.readFromFile(file).iterator();

        Fastq first = fastq.next();
        Fastq second = fastq.next();
        Assert.assertEquals("S0R1/2", second.getDescription());
        Assert.assertEquals(
                "GACAAATGCTGGTGATTACATTTTAGCTAACACCTGTACTGAAAGACTCAAGCTTTTTGCAGCAGAAACGCTCCAAACTACTGAGGAGACATTTAAAGTGTCTTATGGTATTGCTACCGTACGTGAAGTGCTGGCTGACAGAGATTTACA",
                second.getSequence());
        Assert.assertEquals(
                "CCCGGGGG$GGGGJCGJJGJJ$JJJJCJJJJJGJJJCJCCGJJCGJGJJJGCJCJG(1$JG$GGJJGJGG=(G$CJ$GGGGCGCCGGGGGCGGGCGG$GCGJCCJJCGC=GGCG=GG$CCGGGG1CGGCCGC=$G$GGGG$GGC$GCGGC",
                second.getQuality());
    }
}
