package edu.rice.comp416.mapper.util;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TrieTest {
    Trie trie;

    @Before
    public void init() {
        List<String> kmers = new ArrayList<>();
        kmers.add("abc");
        kmers.add("bcd");
        kmers.add("bef");
        kmers.add("abc");

        trie = Trie.fromKmers(kmers.iterator(), 0);
    }

    @Test
    public void testContains() {
        Assert.assertTrue(trie.contains("abc"));
        Assert.assertTrue(trie.contains("bcd"));
        Assert.assertTrue(trie.contains("bef"));
        Assert.assertFalse(trie.contains("cde"));
        Assert.assertFalse(trie.contains("def"));
    }

    @Test
    public void testPosition() {
        Assert.assertEquals(2, trie.position("abc").size());
        Assert.assertTrue(trie.position("abc").contains(0));
        Assert.assertTrue(trie.position("abc").contains(3));

        Assert.assertEquals(1, trie.position("bcd").size());
        Assert.assertTrue(trie.position("bcd").contains(1));

        Assert.assertEquals(1, trie.position("bef").size());
        Assert.assertTrue(trie.position("bef").contains(2));
    }
}
