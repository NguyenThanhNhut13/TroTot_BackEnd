package vn.edu.iuh.fit.reportservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.reportservice.dto.ReportDTO;
import vn.edu.iuh.fit.reportservice.enums.ReportStatus;
import vn.edu.iuh.fit.reportservice.service.EmailService;
import vn.edu.iuh.fit.reportservice.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @PostMapping
    public ResponseEntity<ReportDTO> submitReport(@RequestBody ReportDTO reportDTO) {
        return ResponseEntity.ok(reportService.submitReport(reportDTO));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReportDTO> updateReportStatus(@PathVariable Long id, @RequestBody ReportStatus status) {
        return ResponseEntity.ok(reportService.processReport(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}

