package com.example.housekeeping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardOfferItemResponse {
    private Long id;
    private String title;
    private String content;
    private String tag;
}
