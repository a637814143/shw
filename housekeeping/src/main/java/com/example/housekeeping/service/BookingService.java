package com.example.housekeeping.service;

import com.example.housekeeping.model.BookingStatus;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.repository.ServiceBookingRepository;
import com.example.housekeeping.repository.ServiceItemRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final ServiceBookingRepository bookingRepository;
    private final ServiceItemRepository serviceItemRepository;

    public BookingService(ServiceBookingRepository bookingRepository, ServiceItemRepository serviceItemRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<ServiceBooking> findAll() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<ServiceBooking> findMyBookings(String username) {
        return bookingRepository.findByCreatedByOrderByCreatedAtDesc(username);
    }

    public List<ServiceBooking> findAssignedBookings(String provider) {
        return bookingRepository.findByAssignedProviderOrderByCreatedAtDesc(provider);
    }

    @Transactional
    public ServiceBooking createBooking(String customerName,
                                        String phone,
                                        String address,
                                        LocalDate serviceDate,
                                        String notes,
                                        Long serviceItemId,
                                        String createdBy) {
        ServiceItem serviceItem = serviceItemRepository.findById(serviceItemId)
                .orElseThrow(() -> new IllegalArgumentException("服务项目不存在"));

        ServiceBooking booking = new ServiceBooking();
        booking.setCustomerName(customerName);
        booking.setPhone(phone);
        booking.setAddress(address);
        booking.setServiceDate(serviceDate);
        booking.setNotes(notes);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedBy(createdBy);
        booking.setServiceItem(serviceItem);
        booking.setPrice(serviceItem.getPrice());

        return bookingRepository.save(booking);
    }

    @Transactional
    public ServiceBooking updateBooking(Long bookingId,
                                        String customerName,
                                        String phone,
                                        String address,
                                        LocalDate serviceDate,
                                        String notes,
                                        Long serviceItemId,
                                        String username,
                                        boolean isAdmin) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        if (!isAdmin && !booking.getCreatedBy().equalsIgnoreCase(username)) {
            throw new AccessDeniedException("无权修改该预约");
        }

        booking.setCustomerName(customerName);
        booking.setPhone(phone);
        booking.setAddress(address);
        booking.setServiceDate(serviceDate);
        booking.setNotes(notes);

        if (serviceItemId != null && !serviceItemId.equals(booking.getServiceItem().getId())) {
            ServiceItem serviceItem = serviceItemRepository.findById(serviceItemId)
                    .orElseThrow(() -> new IllegalArgumentException("服务项目不存在"));
            booking.setServiceItem(serviceItem);
            booking.setPrice(serviceItem.getPrice());
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId, String username, boolean isAdmin) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        if (!isAdmin && !booking.getCreatedBy().equalsIgnoreCase(username)) {
            throw new AccessDeniedException("无权删除该预约");
        }

        bookingRepository.delete(booking);
    }

    @Transactional
    public ServiceBooking acceptBooking(Long bookingId, String providerName) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        booking.setAssignedProvider(providerName);
        booking.setStatus(BookingStatus.ACCEPTED);
        return bookingRepository.save(booking);
    }
}

