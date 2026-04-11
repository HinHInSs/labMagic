// ParserFabric.java
package org.example.parser;

import org.example.datasource.DataSource;

public class ParserFabric {

    public static Parser getParser(DataSource source) throws Exception {
        String content = source.read();
        String identifier = source.getIdentifier();

        if (identifier.equals("json")) {
            return new JsonParser();
        }
        if (identifier.equals("xml")) {
            return new XmlParser();
        }
        if (identifier.equals("txt")) {
            return new TxtParser();
        }
        if (identifier.equals("yaml") || identifier.equals("yml")) {
            return new YamlParser();
        }

        if (content.contains("|") && content.contains("MISSION_CREATED")) {
            return new PipeParser();
        }
        if (content.trim().startsWith("{")) {
            return new JsonParser();
        }
        if (content.trim().startsWith("<")) {
            return new XmlParser();
        }
        if (content.contains("[") && content.contains("]") && content.contains("=")) {
            return new TxtParser();
        }
        if (content.contains(":") && !content.contains("=")) {
            return new YamlParser();
        }

        throw new IllegalArgumentException("Не удалось определить формат данных");
    }
}