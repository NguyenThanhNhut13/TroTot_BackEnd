package vn.edu.iuh.fit.reportservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.reportservice.dto.ReportDTO;
import vn.edu.iuh.fit.reportservice.entity.Report;
import vn.edu.iuh.fit.reportservice.enums.ReportStatus;
import vn.edu.iuh.fit.reportservice.repository.ReportRepository;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private EmailService emailService;

    public ReportService() {
    }

    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(report -> new ReportDTO(
                        report.getId(), report.getRoomId(), report.getUserId(),
                        report.getType(), report.getDescription(), report.getStatus(),
                        report.getCreateAt()))
                .collect(Collectors.toList());
    }

    public ReportDTO submitReport(ReportDTO reportDTO) {
        Report report = new Report();
        report.setRoomId(reportDTO.getRoomId());
        report.setUserId(reportDTO.getUserId());
        report.setType(reportDTO.getType());
        report.setDescription(reportDTO.getDescription());
        report.setStatus(ReportStatus.PENDING);
        report.setCreateAt(LocalDateTime.now());
        report.setUpdateAt(LocalDateTime.now());
        report = (Report)this.reportRepository.save(report);

        // Prepare detailed email content for admin
        String emailBody = String.format(
                "A new report has been submitted. Please review the details below:\n\n" +
                        "Report ID: %d\n" +
                        "Room ID: %d\n" +
                        "User ID: %d\n" +
                        "Type: %s\n" +
                        "Description: %s\n" +
                        "Status: %s\n" +
                        "Created At: %s\n\n" +
                        "Please take appropriate action.",
                report.getId(),
                report.getRoomId(),
                report.getUserId(),
                report.getType(),
                report.getDescription(),
                report.getStatus(),
                report.getCreateAt().toString()
        );

        // Send email to admin
        this.emailService.sendEmail("toananhyeu12@gmail.com", "New Report Submission", emailBody);
        return new ReportDTO(report.getId(), report.getRoomId(), report.getUserId(), report.getType(), report.getDescription(), report.getStatus(), report.getCreateAt());
    }

    public ReportDTO processReport(Long reportId, ReportStatus status) {
        Report report = (Report)this.reportRepository.findById(reportId).orElseThrow(() -> {
            return new RuntimeException("Report not found");
        });
        report.setStatus(status);
        report.setUpdateAt(LocalDateTime.now());
        this.reportRepository.save(report);

        // Prepare detailed email content for user
        String emailBody = String.format(
                "Your report has been updated. Here are the details:\n\n" +
                        "Report ID: %d\n" +
                        "Room ID: %d\n" +
                        "Type: %s\n" +
                        "Description: %s\n" +
                        "Status: %s\n" +
                        "Updated At: %s\n\n" +
                        "If you have any questions, please contact support.",
                report.getId(),
                report.getRoomId(),
                report.getType(),
                report.getDescription(),
                report.getStatus(),
                report.getUpdateAt().toString()
        );

        // Send email to user
        this.emailService.sendEmail("user@example.com", "Report Status Updated", emailBody);
        return new ReportDTO(report.getId(), report.getRoomId(), report.getUserId(), report.getType(), report.getDescription(), report.getStatus(), report.getCreateAt());
    }

    public void deleteReport(Long reportId) {
        this.reportRepository.deleteById(reportId);
    }

    public ReportDTO resolveReport(Long reportId) {
        Report report = (Report)this.reportRepository.findById(reportId).orElseThrow(() -> {
            return new RuntimeException("Report not found");
        });
        report.setStatus(ReportStatus.RESOLVED);
        this.reportRepository.save(report);
        return new ReportDTO(report.getId(), report.getRoomId(), report.getUserId(), report.getType(), report.getDescription(), report.getStatus(), report.getCreateAt());
    }
}