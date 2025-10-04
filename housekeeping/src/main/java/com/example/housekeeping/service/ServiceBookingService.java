package com.example.housekeeping.service;

import com.example.housekeeping.dto.BookingRequest;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.repository.ServiceBookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceBookingService {

    private final ServiceBookingRepository bookingRepository;
    private final ServiceItemService serviceItemService;

    public ServiceBookingService(ServiceBookingRepository bookingRepository, ServiceItemService serviceItemService) {
        this.bookingRepository = bookingRepository;
        this.serviceItemService = serviceItemService;
    }

    public List<ServiceBooking> findAll() {
        return bookingRepository.findAll();
    }

    public ServiceBooking createBooking(BookingRequest request) {
        ServiceItem serviceItem = serviceItemService.findById(request.getServiceItemId());
        ServiceBooking booking = new ServiceBooking(
                request.getCustomerName(),
                request.getPhone(),
                request.getAddress(),
                request.getServiceDate(),
                request.getNotes(),
                serviceItem
        );
        return bookingRepository.save(booking);
    }
}
