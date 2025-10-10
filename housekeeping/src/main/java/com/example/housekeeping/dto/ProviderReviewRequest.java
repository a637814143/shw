package com.example.housekeeping.dto;

import com.example.housekeeping.entity.ProviderCertificationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ProviderReviewRequest {

    @NotNull(message = "认证状态不能为空")
    private ProviderCertificationStatus status;

    private String reviewComment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;

    public ProviderCertificationStatus getStatus() {
        return status;
    }

    public void setStatus(ProviderCertificationStatus status) {
        this.status = status;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
