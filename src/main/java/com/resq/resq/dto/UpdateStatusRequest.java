package com.resq.resq.dto;

import com.resq.resq.model.ReportStatus;

public class UpdateStatusRequest {

    private ReportStatus status;

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
}