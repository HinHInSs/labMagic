package org.example.parser;

import org.example.model.*;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.*;

public class XmlParser implements Parser {

    @Override
    public Mission parse(String content) throws Exception {
        Mission mission = new Mission();
        Curse curse = null;
        Sorcerer sorcerer = null;
        Technique technique = null;
        List<Sorcerer> sorcerers = new ArrayList<>();
        List<Technique> techniques = new ArrayList<>();
        String currentElement = "";

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(new StringReader(content));

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                currentElement = startElement.getName().getLocalPart();

                if (currentElement.equals("sorcerer")) {
                    sorcerer = new Sorcerer();
                } else if (currentElement.equals("technique")) {
                    technique = new Technique();
                } else if (currentElement.equals("curse")) {
                    curse = new Curse();
                }

            } else if (event.isCharacters()) {
                Characters characters = event.asCharacters();
                String data = characters.getData().trim();

                if (data.isEmpty()) continue;

                switch (currentElement) {
                    case "missionId":
                        mission.setMissionId(data);
                        break;
                    case "date":
                        mission.setDate(data);
                        break;
                    case "location":
                        mission.setLocation(data);
                        break;
                    case "damageCost":
                        mission.setDamageCost(Integer.parseInt(data));
                        break;
                    case "outcome":
                        if ("SUCCESS".equals(data)) {
                            mission.setOutcome(MissionOutcome.SUCCESS);
                        } else {
                            mission.setOutcome(MissionOutcome.FAILURE);
                        }
                        break;
                    case "comment":
                        mission.setComment(data);
                        break;
                    case "name":
                        if (curse != null) {
                            curse.setName(data);
                        } else if (technique != null) {
                            technique.setName(data);
                        }
                        break;
                    case "threatLevel":
                        if (curse != null) {
                            curse.setThreatLevel(data);
                        }
                        break;
                    case "rank":
                        if (sorcerer != null) {
                            sorcerer.setRank(data);
                        }
                        break;
                    case "type":
                        if (technique != null) {
                            technique.setType(data);
                        }
                        break;
                    case "owner":
                        if (technique != null) {
                            technique.setOwner(data);
                        }
                        break;
                    case "damage":
                        if (technique != null) {
                            technique.setDamage(Integer.parseInt(data));
                        }
                        break;
                }

            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                String localName = endElement.getName().getLocalPart();

                if (localName.equals("sorcerer") && sorcerer != null) {
                    sorcerers.add(sorcerer);
                    sorcerer = null;
                } else if (localName.equals("technique") && technique != null) {
                    techniques.add(technique);
                    technique = null;
                } else if (localName.equals("curse") && curse != null) {
                    mission.setCurse(curse);
                    curse = null;
                }
            }
        }

        mission.setSorcerers(sorcerers);
        mission.setTechniques(techniques);

        reader.close();
        return mission;
    }
}