package edu.rice.comp416.mapper.util;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import java.nio.file.Paths;
import java.util.Random;

public class SAMWriter {
    private final SAMFileWriter fileWriter;

    public SAMWriter(String path) {
        SAMFileHeader header = new SAMFileHeader();
        header.addComment("Test");

        this.fileWriter = new SAMFileWriterFactory().makeSAMWriter(header, true, Paths.get(path));
    }

    public void addAlignment(String readName, String readString, int startPos) {
        SAMFileHeader header = new SAMFileHeader();
        header.addComment("Test");

        SAMRecord record = new SAMRecord(header);
        record.setReadName(readName);
        record.setAlignmentStart(new Random().nextInt(20));
        record.setReadString(readString);

        this.fileWriter.addAlignment(record);
    }

    public void close() {
        this.fileWriter.close();
    }
}
