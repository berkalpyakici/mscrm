# Genome-Scale Mapper
This mapper assumes that the reads contain no indels.

### Command syntax:
map [OPTIONS] REF SAMPLE [SAMPLE SAMPLE...] OUT

### Required arguments:
REF is the pathname (absolute or relative) to the reference fastq file
SAMPLE is the pathname (absolute or relative) to the sample fasta file
OUT is the pathname (absolute or relative) to the output sam file

### Optional flags:
-h	  prints this message

### Example use:
map reference.fasta sample1.fastq sample2.fastq out.sam
