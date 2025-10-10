package com.example.housekeeping.controller.user;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.dto.request.BookingCreateRequest;
import com.example.housekeeping.dto.request.BookingStatusUpdateRequest;
import com.example.housekeeping.entity.ServiceBooking;
import com.example.housekeeping.enums.BookingStatus;
import com.example.housekeeping.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/bookings")
@RequiredArgsConstructor
public class UserBookingController {

    private final BookingService bookingService;

    @GetMapping
    public ApiResponse<Page<ServiceBooking>> list(@RequestParam Long userId, Pageable pageable) {
        return ApiResponse.success(bookingService.findByUser(userId, pageable));
    }

    @PostMapping
    public ApiResponse<ServiceBooking> create(@RequestBody @Valid BookingCreateRequest request) {
        return ApiResponse.success(bookingService.createBooking(request));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<ServiceBooking> cancel(@PathVariable Long id) {
        BookingStatusUpdateRequest request = new BookingStatusUpdateRequest();
        request.setStatus(BookingStatus.CANCELLED);
        return ApiResponse.success(bookingService.updateStatus(id, request));
    }
}
