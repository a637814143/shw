package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingAssignRequest {

    @NotNull
    private Long providerId;
}
