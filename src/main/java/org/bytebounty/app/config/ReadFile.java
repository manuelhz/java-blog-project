package org.bytebounty.app.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadFile {
    public static String readFile(String path) {
        try {
            String secret = new String(Files.readAllBytes(Paths.get(path)));
            return secret;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}