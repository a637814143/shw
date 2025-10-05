package com.example.housekeeping.controller;

import com.example.housekeeping.model.AccountType;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingView> listAll() {
        return bookingService.findAll().stream().map(BookingController::toView).collect(Collectors.toList());
    }

    @GetMapping("/mine")
    public List<BookingView> listMine(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String userTypeRaw = (String) request.getAttribute("userType");
        AccountType userType = StringUtils.hasText(userTypeRaw) ? AccountType.from(userTypeRaw) : AccountType.USER;

        List<ServiceBooking> bookings;
        if (userType == AccountType.PROVIDER) {
            bookings = bookingService.findAssignedBookings(username);
        } else {
            bookings = bookingService.findMyBookings(username);
        }
        return bookings.stream().map(BookingController::toView).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<BookingView> create(@RequestBody @Valid BookingRequest requestBody, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态已失效");
        }

        LocalDate serviceDate = parseDate(requestBody.getServiceDate());
        ServiceBooking booking = bookingService.createBooking(
                requestBody.getCustomerName(),
                requestBody.getPhone(),
                requestBody.getAddress(),
                serviceDate,
                requestBody.getNotes(),
                requestBody.getServiceItemId(),
                username
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toView(booking));
    }

    @PutMapping("/{id}")
    public BookingView update(@PathVariable Long id,
                              @RequestBody @Valid BookingRequest requestBody,
                              HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String userTypeRaw = (String) request.getAttribute("userType");
        boolean isAdmin = StringUtils.hasText(userTypeRaw) && AccountType.from(userTypeRaw) == AccountType.ADMIN;

        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态已失效");
        }

        LocalDate serviceDate = parseDate(requestBody.getServiceDate());
        ServiceBooking booking = bookingService.updateBooking(
                id,
                requestBody.getCustomerName(),
                requestBody.getPhone(),
                requestBody.getAddress(),
                serviceDate,
                requestBody.getNotes(),
                requestBody.getServiceItemId(),
                username,
                isAdmin
        );

        return toView(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String userTypeRaw = (String) request.getAttribute("userType");
        boolean isAdmin = StringUtils.hasText(userTypeRaw) && AccountType.from(userTypeRaw) == AccountType.ADMIN;

        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态已失效");
        }

        bookingService.deleteBooking(id, username, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/accept")
    public BookingView accept(@PathVariable Long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String userTypeRaw = (String) request.getAttribute("userType");
        AccountType userType = StringUtils.hasText(userTypeRaw) ? AccountType.from(userTypeRaw) : AccountType.USER;

        if (userType != AccountType.PROVIDER) {
            throw new AccessDeniedException("仅家政服务人员可接受预约");
        }

        ServiceBooking booking = bookingService.acceptBooking(id, username);
        return toView(booking);
    }

    private static BookingView toView(ServiceBooking booking) {
        ServiceItem item = booking.getServiceItem();
        ServiceItemView itemView = new ServiceItemView(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice()
        );

        return new BookingView(
                booking.getId(),
                booking.getCustomerName(),
                booking.getPhone(),
                booking.getAddress(),
                booking.getServiceDate(),
                booking.getNotes(),
                booking.getStatus().name(),
                booking.getPrice(),
                booking.getCreatedBy(),
                booking.getAssignedProvider(),
                itemView
        );
    }

    private LocalDate parseDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "预约日期格式不正确，需使用YYYY-MM-DD格式");
        }
    }

    public static class BookingRequest {

        @NotBlank
        @Size(max = 100)
        private String customerName;

        @NotBlank
        @Size(max = 30)
        private String phone;

        @NotBlank
        private String address;

        @NotBlank
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
        private String serviceDate;

        private String notes;

        @NotNull
        private Long serviceItemId;

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getServiceDate() {
            return serviceDate;
        }

        public void setServiceDate(String serviceDate) {
            this.serviceDate = serviceDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Long getServiceItemId() {
            return serviceItemId;
        }

        public void setServiceItemId(Long serviceItemId) {
            this.serviceItemId = serviceItemId;
        }
    }

    public record ServiceItemView(Long id, String name, String description, BigDecimal price) {
    }

    public record BookingView(Long id,
                              String customerName,
                              String phone,
                              String address,
                              LocalDate serviceDate,
                              String notes,
                              String status,
                              BigDecimal price,
                              String createdBy,
                              String assignedProvider,
                              ServiceItemView serviceItem) {
    }
}

