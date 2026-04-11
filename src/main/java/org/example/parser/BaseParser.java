package org.example.parser;

import org.example.model.Mission;
import org.example.model.MissionBuilder;

public abstract class BaseParser implements Parser {

    @Override
    public final Mission parse(String content) throws Exception {
        MissionBuilder builder = new MissionBuilder();
        builder.createNewMission();
        parseContent(content.trim(), builder);
        return builder.getMission();
    }

    protected abstract void parseContent(String content, MissionBuilder builder) throws Exception;
}


