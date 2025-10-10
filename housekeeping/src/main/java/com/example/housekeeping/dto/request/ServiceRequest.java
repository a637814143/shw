package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceRequest {

    @NotBlank
    private String name;

    private String description;

    private String content;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long providerId;

    @NotNull
    private BigDecimal price;

    private String unit;

    private Integer duration;

    private String images;

    private String tags;

    private Integer status;
}
