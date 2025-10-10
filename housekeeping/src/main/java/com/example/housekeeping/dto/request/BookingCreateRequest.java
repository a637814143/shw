package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingCreateRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long serviceId;

    private Long providerId;

    @NotNull
    @FutureOrPresent
    private LocalDate bookingDate;

    @NotNull
    private LocalTime bookingTime;

    @NotBlank
    private String address;

    @NotBlank
    private String contactPhone;

    @NotBlank
    private String contactName;

    private String remark;

    @NotNull
    private BigDecimal totalAmount;
}
