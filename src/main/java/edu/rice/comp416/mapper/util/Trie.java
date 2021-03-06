package edu.rice.comp416.mapper.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Utilities for building tries. */
public class Trie {

    /** Represent trie in adjacency list. */
    private final Node g;

    /**
     * Build trie from given kmers.
     *
     * @param kmers Iterator over kmers.
     * @param offset Offset for the position of first kmer in kmers.
     * @return An instance of Trie build from given kmers.
     */
    public static Trie fromKmers(Iterator<String> kmers, int offset) {
        Trie trie = new Trie();

        int i = 0;
        while (kmers.hasNext()) {
            String kmer = kmers.next();
            Node curNode = trie.g;
            for (char c : kmer.toCharArray()) {
                boolean foundInCurNode = false;
                for (Node childNode : curNode.getChildren()) {
                    if (childNode.getC().equals(c)) {
                        curNode = childNode;
                        foundInCurNode = true;
                        break;
                    }
                }

                if (!foundInCurNode) {
                    Node newNode = new Node(c);
                    curNode.addChild(newNode);
                    curNode = newNode;
                }
            }

            curNode.addLoc(offset + i);

            i++;
        }

        return trie;
    }

    /**
     * Build trie from given kmers.
     *
     * @param kmers Iterator over kmers.
     * @return An instance of Trie build from given kmers.
     */
    public static Trie fromKmers(Iterator<String> kmers) {
        return fromKmers(kmers, 0);
    }

    /**
     * Check if the trie contains the string.
     *
     * @param s String to search.
     * @return True if the trie contains the string; false otherwise.
     */
    public boolean contains(String s) {
        return !getNodeFromString(s).equals(this.g);
    }

    /**
     * Get the list of positions where the string matches.
     *
     * @param s String to search.
     * @return List of integers, representing the location.
     */
    public List<Integer> position(String s) {
        return getNodeFromString(s).getLocs();
    }

    /**
     * Get a string to print the trie, starting from its root.
     *
     * @return String that represents the trie starting from its root.
     */
    public String toString() {
        return this.g.toString();
    }

    /**
     * Gets the node from given pattern. If the returned node is equal to the root, then it means
     * the pattern is not found.
     *
     * @param s String to search.
     * @return Node that matches with the given string. If string does not match anything, then
     *     returns the root.
     */
    private Node getNodeFromString(String s) {
        Node curNode = this.g;

        for (char c : s.toCharArray()) {
            boolean foundInCurNode = false;
            for (Node childNode : curNode.getChildren()) {
                if (childNode.getC().equals(c)) {
                    curNode = childNode;
                    foundInCurNode = true;
                    break;
                }
            }

            if (!foundInCurNode) {
                return this.g;
            }
        }

        return curNode;
    }

    /** Private constructor for trie, to be called from public methods. */
    private Trie() {
        this.g = new Node(' ');
    }

    /** Represents a node in the trie. */
    private static class Node {
        private final Character c;
        private final List<Node> n;
        private final List<Integer> loc;

        /**
         * Construct a new node.
         *
         * @param c Character to represent.
         */
        public Node(Character c) {
            this.c = c;
            this.n = new ArrayList<>();
            this.loc = new ArrayList<>();
        }

        /**
         * Get children of the current node.
         *
         * @return List of nodes, each representing a child.
         */
        public List<Node> getChildren() {
            return this.n;
        }

        /**
         * Add a new child to this node.
         *
         * @param node Node to add as a child.
         */
        public void addChild(Node node) {
            this.n.add(node);
        }

        /**
         * Get the character represented by the current node.
         *
         * @return Character represented by the node.
         */
        public Character getC() {
            return this.c;
        }

        /**
         * Get list of locations that string building up to this node from root are found in.
         *
         * @return List of locations.
         */
        public List<Integer> getLocs() {
            return this.loc;
        }

        /**
         * Add a new location that string building up to this node from root are found in.
         *
         * @param newLoc New location to add.
         */
        public void addLoc(int newLoc) {
            this.loc.add(newLoc);
        }

        /**
         * Get a string to print the trie starting from current node.
         *
         * @return String that represents the trie starting from the current node.
         */
        public String toString() {
            StringBuilder string = new StringBuilder();

            string.append(this.getC());

            if (!this.getLocs().isEmpty()) {
                string.append(" ");
                string.append(this.getLocs());
            }

            string.append("\n");

            for (Node child : this.getChildren()) {
                string.append("\t");
                string.append(child.toString().replace("\n", "\n\t"));
                string.append("\n");
            }

            return string.toString();
        }
    }
}
