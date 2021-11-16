package edu.rice.comp416.mapper.util;

import org.junit.Assert;
import org.junit.Test;

public class BWTTest {
    @Test
    public void testEncode() {
        Assert.assertEquals("ipssm$pissii", BWT.encode("mississippi"));
        Assert.assertEquals("s$nnaaa", BWT.encode("ananas"));
        Assert.assertEquals("tsvtntshyoleiixg$aer", BWT.encode("thisisaverylongtext"));
    }

    @Test
    public void testDecode() {
        Assert.assertEquals("mississippi", BWT.decode("ipssm$pissii"));
        Assert.assertEquals("ananas", BWT.decode("s$nnaaa"));
        Assert.assertEquals("thisisaverylongtext", BWT.decode("tsvtntshyoleiixg$aer"));
    }
}
