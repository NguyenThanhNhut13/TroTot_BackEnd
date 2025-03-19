package vn.edu.iuh.fit.reportservice.dto;

import java.time.LocalDateTime;
import vn.edu.iuh.fit.reportservice.entity.ReportStatus;
import vn.edu.iuh.fit.reportservice.entity.ReportType;

public class ReportDTO {
    private Long id;
    private Long roomId;
    private Long userId;
    private ReportType type;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;

    public ReportDTO(Long id, Long roomId, Long userId, ReportType type, String description, ReportStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public ReportDTO() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ReportType getType() {
        return this.type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportStatus getStatus() {
        return this.status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
