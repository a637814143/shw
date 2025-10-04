package com.example.housekeeping.controller;

import com.example.housekeeping.dto.BookingRequest;
import com.example.housekeeping.dto.BookingResponse;
import com.example.housekeeping.service.ServiceBookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class ServiceBookingController {

    private final ServiceBookingService bookingService;

    public ServiceBookingController(ServiceBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> getAll(Authentication authentication) {
        String userType = resolveUserType(authentication);
        if (!"ADMIN".equalsIgnoreCase(userType) && !"PROVIDER".equalsIgnoreCase(userType)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前账号无权查看全部预约");
        }
        return bookingService.findAllBookings();
    }

    @GetMapping("/mine")
    public List<BookingResponse> getMine(Authentication authentication) {
        String username = ensureAuthenticated(authentication);
        return bookingService.findBookingsForUser(username);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(Authentication authentication,
                                                  @Valid @RequestBody BookingRequest request) {
        String username = ensureAuthenticated(authentication);
        String userType = resolveUserType(authentication);
        BookingResponse booking = bookingService.createBooking(username, request, userType);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Long id,
                                  Authentication authentication,
                                  @Valid @RequestBody BookingRequest request) {
        String username = ensureAuthenticated(authentication);
        String userType = resolveUserType(authentication);
        return bookingService.updateBooking(id, request, username, userType);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        String username = ensureAuthenticated(authentication);
        String userType = resolveUserType(authentication);
        bookingService.deleteBooking(id, username, userType);
    }

    @PostMapping("/{id}/accept")
    public BookingResponse accept(@PathVariable Long id, Authentication authentication) {
        String username = ensureAuthenticated(authentication);
        String userType = resolveUserType(authentication);
        return bookingService.acceptBooking(id, username, userType);
    }

    private String ensureAuthenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        return authentication.getName();
    }

    private String resolveUserType(Authentication authentication) {
        if (authentication == null) {
            return "";
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .findFirst()
                .orElse("");
    }
}
