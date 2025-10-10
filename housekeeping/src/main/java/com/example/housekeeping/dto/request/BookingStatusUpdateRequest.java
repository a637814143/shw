package com.example.housekeeping.dto.request;

import com.example.housekeeping.enums.BookingStatus;
import com.example.housekeeping.enums.PaymentStatus;
import lombok.Data;

@Data
public class BookingStatusUpdateRequest {

    private BookingStatus status;

    private PaymentStatus paymentStatus;
}
