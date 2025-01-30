package dev.rm.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.Resource;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.rm.model.SummaryReport;
import dev.rm.service.SummaryReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class SummaryReportController {

    private final SummaryReportService summaryReportService;

    @GetMapping
    public ResponseEntity<List<SummaryReport>> getAllReports() {
        List<SummaryReport> reports = summaryReportService.getAllReports();
        return reports.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) {
        Optional<SummaryReport> report = summaryReportService.getReportById(id);

        if (report.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get("reports", report.get().getFileName());

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.get().getFileName())
                    .body(resource);
        } catch (IOException e) {
            log.error("Error downloading report", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}