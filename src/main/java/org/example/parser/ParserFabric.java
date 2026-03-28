package org.example.parser;

import java.nio.file.Path;

public class ParserFabric {

    public static Parser getParser(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (extension.equals("txt")) {
            return new TxtParser();
        }
        if (extension.equals("json")) {
            return new JsonParser();
        }
        if (extension.equals("xml")) {
            return new XmlParser();
        }

        throw new IllegalArgumentException("Формат не поддерживается: " + extension);
    }
}