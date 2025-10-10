package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long serviceId;
}
