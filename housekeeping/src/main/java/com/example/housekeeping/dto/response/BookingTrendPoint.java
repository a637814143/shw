package com.example.housekeeping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingTrendPoint {
    private LocalDate date;
    private long count;
}
