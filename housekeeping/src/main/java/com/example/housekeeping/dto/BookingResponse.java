package com.example.housekeeping.dto;

import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.model.ServiceItem;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预约返回体
 */
public class BookingResponse {

    private Long id;
    private String customerName;
    private String phone;
    private String address;
    private LocalDate serviceDate;
    private String notes;
    private String status;
    private BigDecimal price;
    private String createdBy;
    private String assignedProvider;
    private ServiceItemSummary serviceItem;

    public static BookingResponse from(ServiceBooking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setCustomerName(booking.getCustomerName());
        response.setPhone(booking.getPhone());
        response.setAddress(booking.getAddress());
        response.setServiceDate(booking.getServiceDate());
        response.setNotes(booking.getNotes());
        response.setStatus(booking.getStatus());
        response.setPrice(booking.getPrice());
        response.setCreatedBy(booking.getCreatedBy());
        response.setAssignedProvider(booking.getAssignedProvider());

        ServiceItem item = booking.getServiceItem();
        if (item != null) {
            response.setServiceItem(new ServiceItemSummary(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getPrice()
            ));
        }
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAssignedProvider() {
        return assignedProvider;
    }

    public void setAssignedProvider(String assignedProvider) {
        this.assignedProvider = assignedProvider;
    }

    public ServiceItemSummary getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItemSummary serviceItem) {
        this.serviceItem = serviceItem;
    }

    public record ServiceItemSummary(Long id, String name, String description, BigDecimal price) {
    }
}
