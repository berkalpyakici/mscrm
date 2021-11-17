package edu.rice.comp416.mapper.util;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class TransformTest {
    @Test
    public void testGetReverseComplement() throws Exception {
        Assert.assertEquals("GTACCT", Transform.getReverseComplement("AGGTAC"));
        Assert.assertEquals("AATGTACTA", Transform.getReverseComplement("TAGTACATT"));
    }

    @Test
    public void testGetKmersABCDEFG() {
        List<String> test1 = Transform.getKmers("abcdefg", 3);

        Assert.assertEquals(5, test1.size());
        Assert.assertTrue(test1.contains("abc"));
        Assert.assertTrue(test1.contains("bcd"));
        Assert.assertTrue(test1.contains("cde"));
        Assert.assertTrue(test1.contains("def"));
        Assert.assertTrue(test1.contains("efg"));
    }

    @Test
    public void testGetKmersEdgeCases() {
        List<String> test1 = Transform.getKmers("abcdefg", 7);

        Assert.assertEquals(1, test1.size());
        Assert.assertTrue(test1.contains("abcdefg"));

        List<String> test2 = Transform.getKmers("abcdefg", 8);

        Assert.assertEquals(0, test2.size());
    }
}
