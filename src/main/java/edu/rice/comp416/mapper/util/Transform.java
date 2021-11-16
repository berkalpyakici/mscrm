package edu.rice.comp416.mapper.util;

/** Utilities for string transformations. */
public class Transform {

    /**
     * Get the reverse complement of input sequence.
     *
     * @param s Input sequence.
     * @return Reverse complement of the input sequence.
     */
    public static String reverseComplement(String s) {
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
                    r.append('-');
                    break;
            }
        }

        return r.toString();
    }
}
