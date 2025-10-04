package com.example.housekeeping.controller;

import com.example.housekeeping.dto.BookingRequest;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.service.ServiceBookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

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
    public List<ServiceBooking> list(Authentication authentication) {
        String role = extractRole(authentication);
        if ("ADMIN".equals(role) || "PROVIDER".equals(role)) {
            return bookingService.findAllForAdmin();
        }
        return bookingService.findAllForUser(authentication.getName());
    }

    @GetMapping("/mine")
    public List<ServiceBooking> listMine(Authentication authentication) {
        return bookingService.findAllForUser(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<ServiceBooking> create(Authentication authentication,
                                                 @Valid @RequestBody BookingRequest request) {
        String role = extractRole(authentication);
        ServiceBooking booking = bookingService.createBooking(role, authentication.getName(), request);
        return ResponseEntity.created(URI.create("/api/bookings/" + booking.getId())).body(booking);
    }

    @PutMapping("/{id}")
    public ServiceBooking update(@PathVariable Long id,
                                 Authentication authentication,
                                 @Valid @RequestBody BookingRequest request) {
        String role = extractRole(authentication);
        return bookingService.updateBooking(id, role, authentication.getName(), request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        String role = extractRole(authentication);
        bookingService.deleteBooking(id, role, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/accept")
    public ServiceBooking accept(@PathVariable Long id, Authentication authentication) {
        String role = extractRole(authentication);
        if (!"PROVIDER".equals(role)) {
            throw new IllegalStateException("只有家政服务人员可以接受预约");
        }
        return bookingService.acceptBooking(id, authentication.getName());
    }

    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.replace("ROLE_", ""))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("未识别的角色"));
    }
}
