package com.example.housekeeping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardServiceItemResponse {
    private Long id;
    private String name;
    private String description;
    private String icon;
}
