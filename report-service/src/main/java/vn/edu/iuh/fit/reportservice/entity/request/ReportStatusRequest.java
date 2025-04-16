package vn.edu.iuh.fit.reportservice.entity.request;

import vn.edu.iuh.fit.reportservice.enums.ReportStatus;

public class ReportStatusRequest {
    private ReportStatus status;

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
}