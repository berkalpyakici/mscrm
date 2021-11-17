package edu.rice.comp416.mapper.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
     * Get an ordered list of k-mers of given size from the input sequence.
     *
     * @param s Input sequence.
     * @param k K-mer size.
     * @return Ordered list of k-mers.
     */
    public static List<String> getKmers(String s, int k) {
        List<String> kmers = new ArrayList<>();

        if (k > s.length() || k <= 0) {
            return kmers;
        }

        for (int i = 0; i < s.length() - k + 1; i++) {
            kmers.add(s.substring(i, i + k));
        }

        return kmers;
    }
}
