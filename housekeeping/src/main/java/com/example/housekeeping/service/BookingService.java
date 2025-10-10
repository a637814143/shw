package com.example.housekeeping.service;

import com.example.housekeeping.dto.request.BookingAssignRequest;
import com.example.housekeeping.dto.request.BookingCreateRequest;
import com.example.housekeeping.dto.request.BookingStatusUpdateRequest;
import com.example.housekeeping.entity.ServiceBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    ServiceBooking createBooking(BookingCreateRequest request);

    ServiceBooking assignProvider(Long bookingId, BookingAssignRequest request);

    ServiceBooking updateStatus(Long bookingId, BookingStatusUpdateRequest request);

    Page<ServiceBooking> findAll(Pageable pageable);

    Page<ServiceBooking> findByUser(Long userId, Pageable pageable);

    Page<ServiceBooking> findByProvider(Long providerId, Pageable pageable);
}
