package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.dto.request.BookingAssignRequest;
import com.example.housekeeping.dto.request.BookingStatusUpdateRequest;
import com.example.housekeeping.entity.ServiceBooking;
import com.example.housekeeping.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    public ApiResponse<Page<ServiceBooking>> list(Pageable pageable) {
        return ApiResponse.success(bookingService.findAll(pageable));
    }

    @PutMapping("/{id}/assign")
    public ApiResponse<ServiceBooking> assign(@PathVariable Long id, @RequestBody @Valid BookingAssignRequest request) {
        return ApiResponse.success(bookingService.assignProvider(id, request));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<ServiceBooking> updateStatus(@PathVariable Long id,
                                                    @RequestBody @Valid BookingStatusUpdateRequest request) {
        return ApiResponse.success(bookingService.updateStatus(id, request));
    }
}
