# Multi-Seed Consensus Reference-Based Mapper (MSCRM)
MSCRM is a reference-based read-mapper which performs ungapped alignment of
sample reads on reference sequence.

### Compiling from Source
The application is written in Java 11 and built as a Maven project. To build from
source, simply run `mvn package` on the main directory, which compiles and builds
the Java project, producing an artifact under
`target/mapper-1.0-jar-with-dependencies.jar`.

While building the project, Maven runs provided JUnit tests to ensure that all
unit-tests pass before producing the artifact. All unit-tests can be found
under `src/test/java/` directory.

All source code can be found under `src/main/java/edu/rice/comp416/mapper/`
directory.

### Requirements
* Java JRE >= `11.0.12`
* Apache Maven >= `3.8.2`

### Command Syntax
`java -jar target/mapper-1.0-jar-with-dependencies.jar [OPTIONS] REF SAMPLE SAMPLE OUT`

### Required Arguments
* `REF` is the pathname (absolute or relative) to the reference fastq file
* `SAMPLE` is the pathname (absolute or relative) to the sample fasta file
* `OUT` is the pathname (absolute or relative) to the output sam file

### Optional Flags
* `-h`	  prints this message

### Example Use
`java -jar target/mapper-1.0-jar-with-dependencies.jar reference.fasta sample1.fastq sample2.fastq out.sam`
