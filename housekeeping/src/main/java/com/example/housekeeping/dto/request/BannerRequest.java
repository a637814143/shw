package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BannerRequest {

    @NotBlank
    private String imageUrl;

    private String title;

    private String linkUrl;

    private Integer sortOrder;

    private Integer status;

    private Long serviceId;
}
