package mapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    // ex are just examples because I assume we will want command line parameters
    private static boolean hadError = false;
    private static boolean eFlag = false;
    private static boolean xFlag = false;

    // a help flag to print support
    private static boolean hFlag = false;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printHelp();
            System.exit(64);
        } else {
            List<String> cmdline = Arrays.asList(args);
            if (cmdline.contains("-h") || args[args.length - 1].matches("-[ex]")) {
                printCmdHelp();
                System.exit(69);
            } else if (cmdline.contains("-e")) {
                eFlag = true;
                readFiles(args[args.length - 2], args[args.length - 1]);
            } 
            else if (cmdline.contains("-x")) xFlag = true;
             
        }
    }

    private static void printCmdHelp() {
        String x = "-e: description of e function\n" +
                "-x: description of x function \n" + 
                "provide a fastq file (first) and reference (last) \n"; // clean this up for command line args
        System.out.println(x);
    }

    private static void readFiles(String refPath, String FASTQPath) throws IOException {
        // i don't think this it the correct way to read in fastq in java but just a placeholder
        byte[] refBytes = Files.readAllBytes(Paths.get(refPath));
        byte[] FASTQBytes = Files.readAllBytes(Paths.get(FASTQPath));

        // call the mapper
        runMapping(new String(refBytes, Charset.defaultCharset()), new String(FASTQBytes, Charset.defaultCharset()));
        if (hadError) System.exit(69);
    }

    private static void runMapping(String ref, String inputFile) {

        // run basic mapping and whatever algorithms and such
        Mapping mapping = new Mapping(ref, inputFile);
    
        if(eFlag) {
            // include additional functionality here
        } else if(xFlag) {
            // additional function here
        }
    }