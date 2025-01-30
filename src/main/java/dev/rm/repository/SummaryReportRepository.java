package dev.rm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.model.SummaryReport;

public interface SummaryReportRepository extends JpaRepository<SummaryReport, Long> {

}
