package com.example.housekeeping.dto.request;

import com.example.housekeeping.enums.CertificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CertificationAuditRequest {

    @NotNull
    private CertificationStatus status;

    private String remark;
}
