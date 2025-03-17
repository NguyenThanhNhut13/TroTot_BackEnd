package vn.edu.iuh.fit.reportservice.dto;

import lombok.*;
import vn.edu.iuh.fit.reportservice.entity.ReportStatus;
import vn.edu.iuh.fit.reportservice.entity.ReportType;

import java.time.LocalDateTime;

public class ReportDTO {
    private Long id;
    private Long roomId;
    private Long userId;
    private ReportType type;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;

    // Constructor có đầy đủ tham số
    public ReportDTO(Long id, Long roomId, Long userId, ReportType type, String description, ReportStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor không có tham số
    public ReportDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
