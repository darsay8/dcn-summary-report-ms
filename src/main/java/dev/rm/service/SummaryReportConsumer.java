package dev.rm.service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.rm.model.VitalSigns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryReportConsumer {

    private final SummaryReportService summaryReportService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "summaryQueue")
    public void receiveSummaryRequest(String message) {
        log.info("Received request to generate summary report...");

        try {
            // Deserialize the received message (which is a JSON string)
            List<VitalSigns> vitalSigns = objectMapper.readValue(message, new TypeReference<List<VitalSigns>>() {
            });
            log.info("Processing {} alerts for summary report generation", vitalSigns.size());

            summaryReportService.generateSummaryReport(vitalSigns);
        } catch (Exception e) {
            log.error("Error processing summary report request", e);
        }
    }
}
