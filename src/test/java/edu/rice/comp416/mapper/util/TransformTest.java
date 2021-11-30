package edu.rice.comp416.mapper.util;

import java.util.Iterator;
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
        Iterator<String> test1 = Transform.getKmers("abcdefg", 3);

        Assert.assertTrue(test1.hasNext());
        Assert.assertEquals("abc", test1.next());
        Assert.assertTrue(test1.hasNext());
        Assert.assertEquals("bcd", test1.next());
        Assert.assertTrue(test1.hasNext());
        Assert.assertEquals("cde", test1.next());
        Assert.assertTrue(test1.hasNext());
        Assert.assertEquals("def", test1.next());
        Assert.assertTrue(test1.hasNext());
        Assert.assertEquals("efg", test1.next());
        Assert.assertFalse(test1.hasNext());
    }

    @Test
    public void testGetKmersEdgeCases() {
        Iterator<String> test1 = Transform.getKmers("abcdefg", 7);
        int test1Size = Transform.getNumKmers("abcdefg", 7);

        Assert.assertEquals(1, test1Size);
        Assert.assertEquals("abcdefg", test1.next());

        Iterator<String> test2 = Transform.getKmers("abcdefg", 8);
        int test2Size = Transform.getNumKmers("abcdefg", 8);

        Assert.assertEquals(0, test2Size);
    }
}
