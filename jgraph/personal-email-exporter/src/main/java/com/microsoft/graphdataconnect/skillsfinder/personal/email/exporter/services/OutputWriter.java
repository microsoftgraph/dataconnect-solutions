package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputWriter {

    public void writeEmails(String outputFilePath, List<String> emails) {
        try {
            FileWriter fw = new FileWriter(outputFilePath, false);
            for (String line : emails) {
                fw.write(line);
                fw.write(System.lineSeparator());
            }

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
