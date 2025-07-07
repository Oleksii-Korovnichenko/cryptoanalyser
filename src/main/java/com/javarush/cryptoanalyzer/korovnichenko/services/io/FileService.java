package com.javarush.cryptoanalyzer.korovnichenko.services.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileService {

    public String readFirstLines(File file, int maxLines) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < maxLines) {
                sb.append(line).append("\n");
                count++;
            }
        } catch (IOException e) {
            sb.append("Error reading the file: ").append(file.getAbsolutePath()).append(e.getMessage());
        }
        return sb.toString();
    }
}
