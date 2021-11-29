package edu.rice.comp416.scorer.util;

import edu.rice.comp416.mapper.Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAMReader {
    private final BufferedReader fileReader;

    public SAMReader(String path) throws IOException {
        this.fileReader = new BufferedReader(new FileReader(path));
    }

    public Map<String, Integer> getReadPos() {
        Map<String, Integer> readPos = new HashMap<>();
        String line;

        while (true) {
            try {
                if ((line = this.fileReader.readLine()) == null) break;

                if (line.startsWith("@")) continue;

                List<String> parsedLine = List.of(line.split("\t"));

                String seqName = parsedLine.get(0);
                String seqPos = parsedLine.get(3);

                if (!seqName.contains("/")) {
                    if (!readPos.containsKey(seqName + "/1")) {
                        seqName = seqName + "/1";
                    } else if (!readPos.containsKey(seqName + "/2")) {
                        seqName = seqName + "/2";
                    }
                }
                readPos.put(seqName, Integer.parseInt(seqPos));
            } catch (IOException e) {
                Main.reportError(e.getMessage());
                break;
            }
        }

        return readPos;
    }
}
