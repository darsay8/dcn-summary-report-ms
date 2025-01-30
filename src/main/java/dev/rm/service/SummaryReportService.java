package dev.rm.service;

import java.util.List;
import java.util.Optional;

import dev.rm.model.SummaryReport;
import dev.rm.model.VitalSigns;

public interface SummaryReportService {
    void generateSummaryReport(List<VitalSigns> vitalSigns);

    List<SummaryReport> getAllReports();

    Optional<SummaryReport> getReportById(Long id);
}