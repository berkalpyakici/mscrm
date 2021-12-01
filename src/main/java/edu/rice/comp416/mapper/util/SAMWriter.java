package edu.rice.comp416.mapper.util;

import edu.rice.comp416.mapper.Mapper;
import htsjdk.samtools.*;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import org.biojava.nbio.core.sequence.DNASequence;

/** Utility to create and write to a sam file. */
public class SAMWriter {
    private final SAMFileHeader fileHeader;
    private final SAMFileWriter fileWriter;

    /**
     * Constructor for a sam writer.
     *
     * @param path Path to the new .sam file.
     * @param reference Reference genome to initialize the header with.
     */
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

    /**
     * Add alignment to the .sam file.
     *
     * @param result Result from mapper.
     * @param pairResult Result of pair/mate from mapper.
     * @param isFirstPair Is the current result the first pair/mate.
     * @param quality Quality of the map.
     */
    public void addAlignment(
            Mapper.Result result, Mapper.Result pairResult, boolean isFirstPair, int quality) {
        SAMRecord record = new SAMRecord(this.fileHeader);

        record.setReadName(result.getDescription());
        record.setFlags(
                0x1 + 0x2 + (isFirstPair ? 0x40 : 0x80) + (result.getReversed() ? 0x10 : 0x20));
        record.setReferenceName(this.fileHeader.getSequence(0).getSequenceName());
        record.setAlignmentStart(result.getPos());
        record.setMappingQuality(quality);
        record.setCigarString(result.getCigar());
        record.setMateReferenceName(this.fileHeader.getSequence(0).getSequenceName());
        record.setMateAlignmentStart(pairResult.getPos());
        record.setInferredInsertSize(
                pairResult.getPos()
                        - result.getPos()
                        + result.getSequence().length() * ((result.getReversed() ? -1 : 1)));
        record.setReadString(result.getSequence());
        record.setBaseQualityString(result.getQuality());

        this.fileWriter.addAlignment(record);
    }

    /** Close the file writer. */
    public void close() {
        this.fileWriter.close();
    }
}
