package edu.rice.comp416.mapper;

import edu.rice.comp416.mapper.reader.ReadFasta;
import edu.rice.comp416.mapper.reader.ReadFastq;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.genome.io.fastq.Fastq;

public class Mapper {

    private final LinkedHashMap<String, DNASequence> reference;
    private final List<Iterable<Fastq>> samples;

    public Mapper(String referenceFile, List<String> sampleFiles) throws IOException {
        this.reference = ReadFasta.readFromFile(referenceFile);
        this.samples = new ArrayList<>();

        for (String sampleFile : sampleFiles) {
            this.samples.add(ReadFastq.readFromFile(sampleFile));
        }
    }
}
