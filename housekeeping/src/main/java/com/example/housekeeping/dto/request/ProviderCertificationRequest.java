package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProviderCertificationRequest {

    @NotNull
    private Long providerId;

    private String idCardFront;
    private String idCardBack;
    private String healthCertificate;
    private String skillCertificate;
    private String workExperience;
}
