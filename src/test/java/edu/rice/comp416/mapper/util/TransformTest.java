package edu.rice.comp416.mapper.util;

import org.junit.Assert;
import org.junit.Test;

public class TransformTest {
    @Test
    public void testReverseComplement() {
        Assert.assertEquals("GTACCT", Transform.reverseComplement("AGGTAC"));
        Assert.assertEquals("AATGTACTA", Transform.reverseComplement("TAGTACATT"));
    }
}
