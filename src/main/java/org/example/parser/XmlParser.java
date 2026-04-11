package org.example.parser;

import org.example.model.MissionBuilder;
import org.example.model.MissionOutcome;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParser extends BaseParser {

    @Override
    protected void parseContent(String content, MissionBuilder builder) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(new StringReader(content));

        String currentElement = "";
        String currentParent = "";

        boolean insideCurse = false;
        boolean insideSorcerer = false;
        boolean insideTechnique = false;
        boolean insideEnemyActivity = false;

        String currentSorcererName = null;
        String currentSorcererRank = null;
        String currentTechniqueName = null;
        String currentTechniqueType = null;
        String currentTechniqueOwner = null;
        int currentTechniqueDamage = 0;

        Map<String, Object> currentEnemyActivity = new HashMap<>();
        List<String> attackPatterns = new ArrayList<>();
        List<String> countermeasures = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                currentElement = startElement.getName().getLocalPart();

                if (currentElement.equals("curse")) {
                    insideCurse = true;
                } else if (currentElement.equals("sorcerer")) {
                    insideSorcerer = true;
                    currentSorcererName = null;
                    currentSorcererRank = null;
                } else if (currentElement.equals("technique")) {
                    insideTechnique = true;
                    currentTechniqueName = null;
                    currentTechniqueType = null;
                    currentTechniqueOwner = null;
                    currentTechniqueDamage = 0;
                } else if (currentElement.equals("enemyActivity")) {
                    insideEnemyActivity = true;
                    currentEnemyActivity = new HashMap<>();
                    attackPatterns = new ArrayList<>();
                    countermeasures = new ArrayList<>();
                } else if (currentElement.equals("attackPatterns")) {
                    currentParent = "attackPatterns";
                } else if (currentElement.equals("countermeasuresUsed")) {
                    currentParent = "countermeasuresUsed";
                }

            } else if (event.isCharacters()) {
                Characters characters = event.asCharacters();
                String data = characters.getData().trim();
                if (data.isEmpty()) continue;

                if (insideEnemyActivity) {
                    if (currentParent.equals("attackPatterns")) {
                        attackPatterns.add(data);
                    } else if (currentParent.equals("countermeasuresUsed")) {
                        countermeasures.add(data);
                    } else {
                        switch (currentElement) {
                            case "behaviorType":
                                String behavior = data;
                                switch (behavior) {
                                    case "AMBUSH_PREDATOR": behavior = "ЗАСАДА-ХИЩНИК"; break;
                                }
                                currentEnemyActivity.put("Тип поведения", behavior);
                                break;
                            case "targetPriority":
                                String target = data;
                                switch (target) {
                                    case "ISOLATED_HUMANS": target = "ИЗОЛИРОВАННЫЕ ЛЮДИ"; break;
                                }
                                currentEnemyActivity.put("Приоритет целей", target);
                                break;
                            case "mobility":
                                String mobility = data;
                                switch (mobility) {
                                    case "HIGH": mobility = "ВЫСОКАЯ"; break;
                                    case "MEDIUM": mobility = "СРЕДНЯЯ"; break;
                                    case "LOW": mobility = "НИЗКАЯ"; break;
                                }
                                currentEnemyActivity.put("Мобильность", mobility);
                                break;
                            case "escalationRisk":
                                String risk = data;
                                switch (risk) {
                                    case "HIGH": risk = "ВЫСОКИЙ"; break;
                                    case "MEDIUM": risk = "СРЕДНИЙ"; break;
                                    case "LOW": risk = "НИЗКИЙ"; break;
                                }
                                currentEnemyActivity.put("Риск эскалации", risk);
                                break;
                        }
                    }
                } else {
                    switch (currentElement) {
                        case "missionId":
                            builder.setMissionId(data);
                            break;
                        case "date":
                            builder.setDate(data);
                            break;
                        case "location":
                            builder.setLocation(data);
                            break;
                        case "damageCost":
                            builder.setDamageCost(Integer.parseInt(data));
                            break;
                        case "outcome":
                            switch (data) {
                                case "SUCCESS":
                                    builder.setOutcome(MissionOutcome.SUCCESS);
                                    break;
                                case "PARTIAL_SUCCESS":
                                    builder.setOutcome(MissionOutcome.PARTIAL_SUCCESS);
                                    break;
                                default:
                                    builder.setOutcome(MissionOutcome.FAILURE);
                                    break;
                            }
                            break;
                        case "comment":
                            builder.setComment(data);
                            break;
                        case "threatLevel":
                            if (insideCurse) {
                                String threatLevel = data;
                                switch (threatLevel) {
                                    case "HIGH": threatLevel = "ВЫСОКИЙ"; break;
                                    case "MEDIUM": threatLevel = "СРЕДНИЙ"; break;
                                    case "LOW": threatLevel = "НИЗКИЙ"; break;
                                    case "SPECIAL_GRADE": threatLevel = "ОСОБЫЙ РАНГ"; break;
                                }
                                builder.setCurseThreatLevel(threatLevel);
                            }
                            break;
                        case "name":
                            if (insideCurse) {
                                builder.setCurseName(data);
                            } else if (insideSorcerer) {
                                currentSorcererName = data;
                            } else if (insideTechnique) {
                                currentTechniqueName = data;
                            }
                            break;
                        case "rank":
                            if (insideSorcerer) {
                                String rank = data;
                                switch (rank) {
                                    case "GRADE_1": rank = "1 РАНГ"; break;
                                    case "GRADE_2": rank = "2 РАНГ"; break;
                                    case "SEMI_GRADE_1": rank = "ПОЛУРАНГ 1"; break;
                                }
                                currentSorcererRank = rank;
                            }
                            break;
                        case "type":
                            if (insideTechnique) {
                                String type = data;
                                switch (type) {
                                    case "INNATE": type = "ВРОЖДЕННАЯ"; break;
                                    case "SHIKIGAMI": type = "ШИКИГАМИ"; break;
                                    case "WEAPON": type = "ОРУЖИЕ"; break;
                                    case "BODY": type = "ТЕЛЕСНАЯ"; break;
                                }
                                currentTechniqueType = type;
                            }
                            break;
                        case "owner":
                            if (insideTechnique) {
                                currentTechniqueOwner = data;
                            }
                            break;
                        case "damage":
                            if (insideTechnique) {
                                currentTechniqueDamage = Integer.parseInt(data);
                            }
                            break;
                    }
                }

            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                String localName = endElement.getName().getLocalPart();

                if (localName.equals("curse")) {
                    insideCurse = false;
                } else if (localName.equals("sorcerer")) {
                    if (currentSorcererName != null && currentSorcererRank != null) {
                        builder.setSorcerer(currentSorcererName, currentSorcererRank);
                    }
                    insideSorcerer = false;
                } else if (localName.equals("technique")) {
                    if (currentTechniqueName != null && currentTechniqueType != null && currentTechniqueOwner != null) {
                        builder.setTechnique(currentTechniqueName, currentTechniqueType,
                                currentTechniqueOwner, currentTechniqueDamage);
                    }
                    insideTechnique = false;
                } else if (localName.equals("enemyActivity")) {
                    if (!attackPatterns.isEmpty()) {
                        currentEnemyActivity.put("Паттерны атак", attackPatterns);
                    }
                    if (!countermeasures.isEmpty()) {
                        currentEnemyActivity.put("Использованные меры", countermeasures);
                    }
                    if (!currentEnemyActivity.isEmpty()) {
                        builder.addExtension("Действия противника", currentEnemyActivity);
                    }
                    insideEnemyActivity = false;
                } else if (localName.equals("attackPatterns")) {
                    currentParent = "";
                } else if (localName.equals("countermeasuresUsed")) {
                    currentParent = "";
                }
            }
        }

        reader.close();
    }
}