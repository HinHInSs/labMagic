package org.example.parser;

import org.example.model.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;

public class XmlParser implements Parser {

    @Override
    public Mission parse(String content) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(content.getBytes()));
            Element root = doc.getDocumentElement();

            Mission mission = new Mission();

            mission.setMissionId(getText(root, "missionId"));
            mission.setDate(getText(root, "date"));
            mission.setLocation(getText(root, "location"));
            mission.setDamageCost(getInt(root, "damageCost"));

            String outcome = getText(root, "outcome");
            if (outcome.equals("SUCCESS")) {
                mission.setOutcome(MissionOutcome.SUCCESS);
            } else {
                mission.setOutcome(MissionOutcome.FAILURE);
            }

            mission.setComment(getText(root, "comment"));

            Element curseElem = getElement(root, "curse");
            if (curseElem != null) {
                Curse curse = new Curse();
                curse.setName(getText(curseElem, "name"));
                curse.setThreatLevel(getText(curseElem, "threatLevel"));
                mission.setCurse(curse);
            }

            Element sorcerersElem = getElement(root, "sorcerers");
            if (sorcerersElem != null) {
                NodeList nodes = sorcerersElem.getElementsByTagName("sorcerer");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element e = (Element) nodes.item(i);
                    Sorcerer sorcerer = new Sorcerer();
                    sorcerer.setName(getText(e, "name"));
                    sorcerer.setRank(getText(e, "rank"));
                    mission.getSorcerers().add(sorcerer);
                }
            }

            Element techniquesElem = getElement(root, "techniques");
            if (techniquesElem != null) {
                NodeList nodes = techniquesElem.getElementsByTagName("technique");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element e = (Element) nodes.item(i);
                    Technique technique = new Technique();
                    technique.setName(getText(e, "name"));
                    technique.setType(getText(e, "type"));
                    technique.setOwner(getText(e, "owner"));
                    technique.setDamage(getInt(e, "damage"));
                    mission.getTechniques().add(technique);
                }
            }

            return mission;

        } catch (Exception e) {
            throw new RuntimeException("XML parsing error", e);
        }
    }

    private Element getElement(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? (Element) nodes.item(0) : null;
    }

    private String getText(Element parent, String tagName) {
        Element elem = getElement(parent, tagName);
        if (elem == null || elem.getFirstChild() == null) return "";
        return elem.getFirstChild().getTextContent();
    }

    private int getInt(Element parent, String tagName) {
        try {
            return Integer.parseInt(getText(parent, tagName));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}