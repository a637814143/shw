package com.example.housekeeping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardReviewItemResponse {
    private Long id;
    private String author;
    private String serviceName;
    private double rating;
    private String content;
}
