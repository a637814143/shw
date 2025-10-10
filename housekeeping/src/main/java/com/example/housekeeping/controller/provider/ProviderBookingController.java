package com.example.housekeeping.controller.provider;

import com.example.housekeeping.common.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/bookings")
@RequiredArgsConstructor
public class ProviderBookingController {

    private final BookingService bookingService;

    @GetMapping
    public ApiResponse<Page<ServiceBooking>> list(@RequestParam Long providerId, Pageable pageable) {
        return ApiResponse.success(bookingService.findByProvider(providerId, pageable));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<ServiceBooking> updateStatus(@PathVariable Long id,
                                                    @RequestBody @Valid BookingStatusUpdateRequest request) {
        return ApiResponse.success(bookingService.updateStatus(id, request));
    }
}
