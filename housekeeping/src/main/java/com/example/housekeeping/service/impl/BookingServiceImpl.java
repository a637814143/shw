package com.example.housekeeping.service.impl;

import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.BookingAssignRequest;
import com.example.housekeeping.dto.request.BookingCreateRequest;
import com.example.housekeeping.dto.request.BookingStatusUpdateRequest;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.ServiceBooking;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.enums.BookingStatus;
import com.example.housekeeping.enums.PaymentStatus;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ServiceBookingRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserRepository;
import com.example.housekeeping.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final ServiceBookingRepository serviceBookingRepository;
    private final UserRepository userRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;

    @Override
    public ServiceBooking createBooking(BookingCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        HousekeepingService service = housekeepingServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new BusinessException("服务不存在"));
        ServiceProvider provider;
        if (request.getProviderId() != null) {
            provider = serviceProviderRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new BusinessException("家政服务者不存在"));
        } else {
            provider = service.getProvider();
        }

        ServiceBooking booking = new ServiceBooking();
        booking.setBookingNo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        booking.setUser(user);
        booking.setProvider(provider);
        booking.setService(service);
        booking.setBookingDate(request.getBookingDate());
        booking.setBookingTime(request.getBookingTime());
        booking.setAddress(request.getAddress());
        booking.setContactPhone(request.getContactPhone());
        booking.setContactName(request.getContactName());
        booking.setRemark(request.getRemark());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        return serviceBookingRepository.save(booking);
    }

    @Override
    public ServiceBooking assignProvider(Long bookingId, BookingAssignRequest request) {
        ServiceBooking booking = findById(bookingId);
        ServiceProvider provider = serviceProviderRepository.findById(request.getProviderId())
                .orElseThrow(() -> new BusinessException("家政服务者不存在"));
        booking.setProvider(provider);
        booking.setStatus(BookingStatus.ASSIGNED);
        return serviceBookingRepository.save(booking);
    }

    @Override
    public ServiceBooking updateStatus(Long bookingId, BookingStatusUpdateRequest request) {
        ServiceBooking booking = findById(bookingId);
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
            if (request.getStatus() == BookingStatus.COMPLETED) {
                booking.setCompleteTime(LocalDateTime.now());
            }
            if (request.getStatus() == BookingStatus.CANCELLED) {
                booking.setPaymentStatus(PaymentStatus.REFUNDED);
            }
        }
        if (request.getPaymentStatus() != null) {
            booking.setPaymentStatus(request.getPaymentStatus());
            if (request.getPaymentStatus() == PaymentStatus.PAID) {
                booking.setPaymentTime(LocalDateTime.now());
            }
        }
        return serviceBookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceBooking> findAll(Pageable pageable) {
        return serviceBookingRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceBooking> findByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return serviceBookingRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceBooking> findByProvider(Long providerId, Pageable pageable) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new BusinessException("家政服务者不存在"));
        return serviceBookingRepository.findByProvider(provider, pageable);
    }

    private ServiceBooking findById(Long bookingId) {
        return serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("预约不存在"));
    }
}
