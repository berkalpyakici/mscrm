package edu.rice.comp416.mapper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Utilities for Burrows-Wheeler transform. */
public class BWT {

    /**
     * Get the Burrows–Wheeler transform of input sequence.
     *
     * @param s Input sequence.
     * @return Burrows-Wheeler transform of the input sequence.
     */
    public static String encode(String s) {
        // Add dollar sign at the end of the given input.
        String originalSequence = s + "$";

        // Get all rotations of the string.
        List<String> suffixes = new ArrayList<>();
        for (int i = 0; i < originalSequence.length(); i++) {
            suffixes.add(rotateString(originalSequence, i));
        }

        // Sort all rotations.
        Collections.sort(suffixes);

        // Get the last characters from sorted rotations.
        StringBuilder bwtSequence = new StringBuilder();
        for (String curSeq : suffixes) {
            bwtSequence.append(curSeq.charAt(curSeq.length() - 1));
        }

        return bwtSequence.toString();
    }

    /**
     * Get original sequence from its Burrows-Wheeler transform.
     *
     * @param bwt Burrows-wheeler transform.
     * @return Original sequence from bwt.
     */
    public static String decode(String bwt) {
        List<String> suffixes = new ArrayList<>();
        for (int i = 0; i < bwt.length(); ++i) {
            suffixes.add("");
        }

        while (suffixes.get(0).length() < bwt.length()) {
            for (int i = 0; i < bwt.length(); ++i) {
                suffixes.set(i, bwt.charAt(i) + suffixes.get(i));
            }
            Collections.sort(suffixes);
        }

        for (String suffix : suffixes) {
            if (suffix.charAt(suffix.length() - 1) == '$') {
                return suffix.substring(0, suffix.length() - 1);
            }
        }

        return "";
    }

    /**
     * Get a rotation of the given string.
     *
     * @param s String to rotate.
     * @param offset Number of characters to shift by.
     * @return Rotation of s with the given offset.
     */
    private static String rotateString(String s, int offset) {
        int i = offset % s.length();
        return s.substring(i) + s.substring(0, i);
    }
}
