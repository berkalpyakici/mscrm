package edu.rice.comp416.scorer;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            reportError(
                    "No parameters given.\n\tTry '-h' for information on command-line syntax.\n");
            printHelpMessage();
            System.exit(1);
        }

        // Parse all flags.
        Set<Character> flags = new HashSet<>();

        for (String arg : args) {
            if (arg.length() == 2 && arg.startsWith("-")) {
                flags.add(arg.charAt(1));
            }
        }

        if (flags.contains('h')) {
            printHelpMessage();
            System.exit(0);
        }
    }

    public static void reportMessage(String message) {
        System.out.println(message);
    }

    public static void reportError(String message) {
        System.err.println(message);
    }

    /** Prints help message to console. */
    private static void printHelpMessage() {
        String helpMessage =
                "Genome-Scale Map Scorer (Katherine Dyson, Elizabeth Sims, Berk Alp Yakici)\n"
                    + "Command syntax:\n"
                    + "\tscore [OPTIONS] MAP TRUTH\n"
                    + "\n"
                    + "Required arguments:\n"
                    + "\tMAP is the pathname (absolute or relative) to the mapping file generated"
                    + " by the mapper\n"
                    + "\tTRUTH is the pathname (absolute or relative) to the ground truth file\n"
                    + "\n"
                    + "Optional flags:\n"
                    + "\t-h\t  prints this message\n"
                    + "\n"
                    + "Example use:\n"
                    + "\tscore map.sam truth.txt";
        System.out.println(helpMessage);
    }
}
