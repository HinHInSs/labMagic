package org.example.datasource;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileDataSource implements DataSource {

    private final Path filePath;

    public FileDataSource(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public String read() throws Exception {
        return Files.readString(filePath);
    }

    @Override
    public String getIdentifier() {
        String fileName = filePath.getFileName().toString();
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase();
    }
}