package org.example.parser;

import org.example.model.Mission;

public interface Parser {
    Mission parse(String content) throws Exception;
}