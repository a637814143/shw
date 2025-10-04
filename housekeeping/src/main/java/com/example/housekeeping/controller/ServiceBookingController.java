package com.example.housekeeping.controller;

import com.example.housekeeping.dto.BookingRequest;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.service.ServiceBookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class ServiceBookingController {

    private final ServiceBookingService bookingService;

    public ServiceBookingController(ServiceBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<ServiceBooking> list() {
        return bookingService.findAll();
    }

    @PostMapping
    public ResponseEntity<ServiceBooking> create(@Valid @RequestBody BookingRequest request) {
        ServiceBooking booking = bookingService.createBooking(request);
        return ResponseEntity
                .created(URI.create("/api/bookings/" + booking.getId()))
                .body(booking);
    }
}
