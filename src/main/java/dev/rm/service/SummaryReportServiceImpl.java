package dev.rm.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.rm.model.SummaryReport;
import dev.rm.model.VitalSigns;
import dev.rm.repository.SummaryReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryReportServiceImpl implements SummaryReportService {

    private final S3Service s3Service;

    private final SummaryReportRepository summaryReportRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void generateSummaryReport(List<VitalSigns> vitalSigns) {
        log.info("Generating summary report for {} vital signs...", vitalSigns.size());

        if (vitalSigns.isEmpty()) {
            log.warn("No data available. Skipping report generation.");
            return;
        }

        try {
            // Convert the vital signs to a JSON string with pretty printing
            String jsonReport = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vitalSigns);
            saveReportToFile(jsonReport);
        } catch (Exception e) {
            log.error("Error generating summary report", e);
        }
    }

    private void saveReportToFile(String jsonReport) {

        String pattern = "ddMMyy-HHmm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String formattedDate = sdf.format(new Date());
        String fileName = "vs_sum_" + formattedDate + ".json";

        try {
            InputStream inputStream = new ByteArrayInputStream(jsonReport.getBytes(StandardCharsets.UTF_8));
            s3Service.uploadReport(fileName, inputStream);
            log.info("Summary report saved to S3: {}", fileName);

            saveReportMetadata(fileName);
        } catch (IOException e) {
            log.error("Error saving report to file", e);
            throw new RuntimeException("Failed to save report");
        }
    }

    private void saveReportMetadata(String fileName) {
        // Use the custom constructor to create a SummaryReport
        SummaryReport summaryReport = new SummaryReport(fileName);
        summaryReportRepository.save(summaryReport);
        log.info("Summary report metadata saved: {}", summaryReport);
    }

    @Override
    public List<SummaryReport> getAllReports() {
        return summaryReportRepository.findAll();
    }

    @Override
    public Optional<SummaryReport> getReportById(Long id) {
        return summaryReportRepository.findById(id);
    }
}
