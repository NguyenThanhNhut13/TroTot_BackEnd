package vn.edu.iuh.fit.reportservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.reportservice.entity.reponse.BaseResponse;
import vn.edu.iuh.fit.reportservice.dto.ReportDTO;
import vn.edu.iuh.fit.reportservice.entity.request.ReportStatusRequest;
import vn.edu.iuh.fit.reportservice.service.EmailService;
import vn.edu.iuh.fit.reportservice.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ReportDTO>>> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(new BaseResponse<>(true, "Get all reports successfully", reports));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ReportDTO>> submitReport(@RequestBody ReportDTO reportDTO) {
        ReportDTO created = reportService.submitReport(reportDTO);
        return ResponseEntity.ok(new BaseResponse<>(true, "Report submitted successfully", created));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse<ReportDTO>> updateReportStatus(
            @PathVariable Long id,
            @RequestBody ReportStatusRequest request) {
        ReportDTO updated = reportService.processReport(id, request.getStatus());
        return ResponseEntity.ok(new BaseResponse<>(true, "Report status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(new BaseResponse<>(true, "Report deleted successfully", null));
    }
}
