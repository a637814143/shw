package com.example.housekeeping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardTipItemResponse {
    private Long id;
    private String title;
    private String content;
}
