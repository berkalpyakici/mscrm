package edu.rice.comp416.mapper.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/** Utilities for string transformations. */
public class Transform {

    /**
     * Get the reverse complement of input sequence.
     *
     * @param s Input sequence.
     * @return Reverse complement of the input sequence.
     * @throws UnsupportedEncodingException If the input sequence contains characters other than
     *     A,T,C,G.
     */
    public static String getReverseComplement(String s) throws UnsupportedEncodingException {
        StringBuilder r = new StringBuilder();

        for (int i = s.length() - 1; i >= 0; i--) {
            switch (s.charAt(i)) {
                case 'A':
                    r.append('T');
                    break;
                case 'T':
                    r.append('A');
                    break;
                case 'C':
                    r.append('G');
                    break;
                case 'G':
                    r.append('C');
                    break;
                default:
                    throw new UnsupportedEncodingException(
                            "The input sequence can only contain A,T,C,G.");
            }
        }

        return r.toString();
    }

    /**
     * Get reverse of a string.
     *
     * @param s Input sequence.
     * @return Reverse of input sequence.
     */
    public static String getReverse(String s) {
        StringBuilder r = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            r.append(s.charAt(i));
        }

        return r.toString();
    }

    /**
     * Get an iterator over k-mers of given size from the input sequence.
     *
     * @param s Input sequence.
     * @param k K-mer size.
     * @return Iterator over k-mers.
     */
    public static Iterator<String> getKmers(String s, int k) {
        final int numKmers = getNumKmers(s, k);

        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numKmers;
            }

            @Override
            public String next() {
                if (i < numKmers) {
                    String kmer = s.substring(i, i + k);
                    i += 1;
                    return kmer;
                } else {
                    return null;
                }
            }
        };
    }

    /**
     * Gets the number of kmers.
     *
     * @param s Input sequence.
     * @param k K-mer size.
     * @return Number of kmers.
     */
    public static int getNumKmers(String s, int k) {
        if (k > s.length() || k <= 0) {
            return 0;
        }

        return s.length() - k + 1;
    }
}
