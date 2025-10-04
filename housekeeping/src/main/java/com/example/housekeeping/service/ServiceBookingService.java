package com.example.housekeeping.service;

import com.example.housekeeping.dto.BookingRequest;
import com.example.housekeeping.dto.BookingResponse;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.repository.ServiceBookingRepository;
import com.example.housekeeping.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class ServiceBookingService {

    private final ServiceBookingRepository bookingRepository;
    private final ServiceItemRepository serviceItemRepository;

    public ServiceBookingService(ServiceBookingRepository bookingRepository,
                                 ServiceItemRepository serviceItemRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceItemRepository = serviceItemRepository;
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findAllBookings() {
        return bookingRepository.findAllByOrderByServiceDateDesc()
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findBookingsForUser(String username) {
        return bookingRepository.findAllByCreatedByOrderByServiceDateDesc(username)
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    public BookingResponse createBooking(String username, BookingRequest request, String userType) {
        ServiceItem serviceItem = serviceItemRepository.findById(request.getServiceItemId())
                .orElseThrow(() -> new IllegalArgumentException("服务项目不存在"));

        String creator = resolveCreator(username, userType, request.getCreatedBy());

        ServiceBooking booking = new ServiceBooking();
        booking.setCustomerName(request.getCustomerName());
        booking.setPhone(request.getPhone());
        booking.setAddress(request.getAddress());
        booking.setServiceDate(request.getServiceDate());
        booking.setNotes(request.getNotes());
        booking.setServiceItem(serviceItem);
        booking.setPrice(serviceItem.getPrice());
        booking.setCreatedBy(creator);
        booking.setStatus("PENDING");

        ServiceBooking saved = bookingRepository.save(booking);
        return BookingResponse.from(saved);
    }

    public BookingResponse updateBooking(Long id, BookingRequest request, String username, String userType) {
        ServiceBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        if (!canModify(booking, username, userType)) {
            throw new IllegalArgumentException("当前账号无权修改该预约");
        }

        ServiceItem serviceItem = serviceItemRepository.findById(request.getServiceItemId())
                .orElseThrow(() -> new IllegalArgumentException("服务项目不存在"));

        booking.setCustomerName(request.getCustomerName());
        booking.setPhone(request.getPhone());
        booking.setAddress(request.getAddress());
        booking.setServiceDate(request.getServiceDate());
        booking.setNotes(request.getNotes());
        booking.setServiceItem(serviceItem);
        booking.setPrice(serviceItem.getPrice());

        ServiceBooking saved = bookingRepository.save(booking);
        return BookingResponse.from(saved);
    }

    public void deleteBooking(Long id, String username, String userType) {
        ServiceBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        if (!canModify(booking, username, userType)) {
            throw new IllegalArgumentException("当前账号无权删除该预约");
        }

        bookingRepository.delete(booking);
    }

    public BookingResponse acceptBooking(Long id, String username, String userType) {
        ServiceBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));

        if (!isAdmin(userType) && !isProvider(userType)) {
            throw new IllegalArgumentException("当前账号无权接单");
        }

        if (StringUtils.hasText(booking.getAssignedProvider())
                && !booking.getAssignedProvider().equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("该预约已由其他服务人员接单");
        }

        booking.setAssignedProvider(username);
        booking.setStatus("ACCEPTED");

        ServiceBooking saved = bookingRepository.save(booking);
        return BookingResponse.from(saved);
    }

    private String resolveCreator(String username, String userType, String requestCreator) {
        if (StringUtils.hasText(requestCreator) && isAdmin(userType)) {
            return requestCreator;
        }
        if (StringUtils.hasText(username)) {
            return username;
        }
        throw new IllegalArgumentException("无法确定预约创建人");
    }

    private boolean canModify(ServiceBooking booking, String username, String userType) {
        if (isAdmin(userType)) {
            return true;
        }
        if (isProvider(userType)) {
            return StringUtils.hasText(username)
                    && username.equalsIgnoreCase(booking.getAssignedProvider());
        }
        return StringUtils.hasText(username)
                && username.equalsIgnoreCase(booking.getCreatedBy());
    }

    private boolean isAdmin(String userType) {
        return "ADMIN".equalsIgnoreCase(userType);
    }

    private boolean isProvider(String userType) {
        return "PROVIDER".equalsIgnoreCase(userType);
    }
}
