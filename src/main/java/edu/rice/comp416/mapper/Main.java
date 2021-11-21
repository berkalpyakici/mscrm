package edu.rice.comp416.mapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

        // Parse sample files and reference file.
        List<String> sampleFiles = new ArrayList<>();
        String refFile = "";
        String outFile = "";

        for (String arg : args) {
            if (arg.endsWith(".fasta")) {
                if (refFile.isBlank()) {
                    refFile = arg;
                } else {
                    reportError(
                            "Can only supply one reference FASTA file.\n"
                                    + "\tTry '-h' for information on command-line syntax.\n");
                    printHelpMessage();
                    System.exit(1);
                }
            } else if (arg.endsWith(".sam")) {
                if (outFile.isBlank()) {
                    outFile = arg;
                } else {
                    reportError(
                            "Can only supply one output SAM file.\n"
                                    + "\tTry '-h' for information on command-line syntax.\n");
                    printHelpMessage();
                    System.exit(1);
                }
            } else if (arg.endsWith(".fastq")) {
                sampleFiles.add(arg);
            }
        }

        if (refFile.isBlank()) {
            reportError(
                    "A reference FASTA file must be supplied.\n"
                            + "\tTry '-h' for information on command-line syntax.\n");
            printHelpMessage();
            System.exit(1);
        }

        if (outFile.isBlank()) {
            reportError(
                    "An output SAM file must be supplied.\n"
                            + "\tTry '-h' for information on command-line syntax.\n");
            printHelpMessage();
            System.exit(1);
        }

        if (sampleFiles.isEmpty()) {
            reportError(
                    "At least one sample FASTQ file must be supplied.\n"
                            + "\tTry '-h' for information on command-line syntax.\n");
            printHelpMessage();
            System.exit(1);
        }

        Mapper mapper = null;
        try {
            mapper = new Mapper(refFile, sampleFiles, outFile);
        } catch (FileNotFoundException e) {
            reportError(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            reportError(
                    "Failed to process input files when initializing mapper. See the stack tree"
                            + " for more information.\n");
            e.printStackTrace();
            System.exit(1);
        }

        mapper.generateReferenceKmerTrie(13);
        mapper.map();
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
                "Genome-Scale Mapper (Katherine Dyson, Elizabeth Sims, Berk Alp Yakici)\n"
                    + "Command syntax:\n"
                    + "\tmap [OPTIONS] REF SAMPLE [SAMPLE SAMPLE...] OUT\n"
                    + "\n"
                    + "Required arguments:\n"
                    + "\tREF is the pathname (absolute or relative) to the reference fastq file\n"
                    + "\tSAMPLE is the pathname (absolute or relative) to the sample fasta file\n"
                    + "\tOUT is the pathname (absolute or relative) to the output sam file\n"
                    + "\n"
                    + "Optional flags:\n"
                    + "\t-h\t  prints this message\n"
                    + "\n"
                    + "Example use:\n"
                    + "\tmap reference.fasta sample1.fastq sample2.fastq out.sam";
        System.out.println(helpMessage);
    }
}
