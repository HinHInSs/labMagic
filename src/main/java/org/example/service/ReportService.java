package org.example.service;

import org.example.model.Mission;

public class ReportService {

    private ReportGenerator generator;

    public ReportService() {
        this.generator = new DetailedReportService();
    }

    public void setGenerator(ReportGenerator generator) {
        this.generator = generator;
    }

    public String getReport(Mission mission) {
        return generator.generate(mission);
    }
}