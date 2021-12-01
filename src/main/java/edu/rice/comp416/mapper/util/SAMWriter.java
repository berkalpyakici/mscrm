package edu.rice.comp416.mapper.util;

import htsjdk.samtools.*;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.genome.io.fastq.Fastq;

public class SAMWriter {
    private final SAMFileHeader fileHeader;
    private final SAMFileWriter fileWriter;

    public SAMWriter(String path, LinkedHashMap<String, DNASequence> reference) {
        this.fileHeader = new SAMFileHeader();
        this.fileHeader.addComment(
                "Genome-Scale Mapper (Katherine Dyson, Elizabeth Sims, Berk Alp Yakici)");

        for (Map.Entry<String, DNASequence> entry : reference.entrySet()) {
            SAMSequenceRecord sequenceRecord =
                    new SAMSequenceRecord(
                            entry.getKey().split(" ")[0], entry.getValue().getLength());
            this.fileHeader.addSequence(sequenceRecord);
        }

        this.fileWriter =
                new SAMFileWriterFactory().makeSAMWriter(this.fileHeader, true, Paths.get(path));
    }

    public void addAlignment(Fastq read, int startPos, String cigar) {
        SAMRecord record = new SAMRecord(this.fileHeader);
        record.setReadName(read.getDescription());
        record.setReadString(read.getSequence());
        record.setBaseQualityString(read.getQuality());
        record.setAlignmentStart(startPos);
        record.setCigarString(cigar);

        this.fileWriter.addAlignment(record);
    }

    public void close() {
        this.fileWriter.close();
    }
}
