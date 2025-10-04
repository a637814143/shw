package com.example.housekeeping.service;

import com.example.housekeeping.dto.BookingRequest;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.model.UserAccount;
import com.example.housekeeping.repository.ServiceBookingRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServiceBookingService {

    private final ServiceBookingRepository bookingRepository;
    private final ServiceItemService serviceItemService;
    private final UserAccountRepository userAccountRepository;

    public ServiceBookingService(ServiceBookingRepository bookingRepository,
                                 ServiceItemService serviceItemService,
                                 UserAccountRepository userAccountRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceItemService = serviceItemService;
        this.userAccountRepository = userAccountRepository;
    }

    public List<ServiceBooking> findAllForAdmin() {
        return bookingRepository.findAllByOrderByServiceDateDesc();
    }

    public List<ServiceBooking> findAllForUser(String username) {
        return bookingRepository.findAllByCreatedByOrderByServiceDateDesc(username);
    }

    public ServiceBooking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在"));
    }

    @Transactional
    public ServiceBooking createBooking(String operatorType, String operatorUsername, BookingRequest request) {
        String owner = determineOwner(operatorType, operatorUsername, request);
        UserAccount user = userAccountRepository.findById(owner)
                .orElseThrow(() -> new IllegalArgumentException("提交预约的用户不存在"));

        if (!"USER".equalsIgnoreCase(user.getTypes())) {
            throw new IllegalStateException("只有普通用户才能提交预约");
        }

        ServiceItem serviceItem = serviceItemService.findById(request.getServiceItemId());
        BigDecimal price = BigDecimal.valueOf(serviceItem.getPrice());

        if (user.getMoney().compareTo(price) < 0) {
            throw new IllegalStateException("钱包余额不足，无法提交预约");
        }

        user.setMoney(user.getMoney().subtract(price));
        userAccountRepository.save(user);

        ServiceBooking booking = new ServiceBooking(
                request.getCustomerName(),
                request.getPhone(),
                request.getAddress(),
                request.getServiceDate(),
                request.getNotes(),
                serviceItem,
                owner,
                price
        );

        return bookingRepository.save(booking);
    }

    @Transactional
    public ServiceBooking updateBooking(Long id, String operatorType, String operatorUsername, BookingRequest request) {
        ServiceBooking booking = findById(id);

        boolean isAdmin = "ADMIN".equalsIgnoreCase(operatorType);
        boolean isOwner = booking.getCreatedBy().equals(operatorUsername);
        if (!isAdmin && !isOwner) {
            throw new IllegalStateException("无权修改该预约");
        }

        ServiceItem newItem = serviceItemService.findById(request.getServiceItemId());
        BigDecimal newPrice = BigDecimal.valueOf(newItem.getPrice());
        BigDecimal oldPrice = booking.getPrice();

        if (booking.getAssignedProvider() != null && oldPrice.compareTo(newPrice) != 0) {
            throw new IllegalStateException("已被接受的预约不允许修改价格");
        }

        if (oldPrice.compareTo(newPrice) != 0) {
            adjustUserWallet(booking.getCreatedBy(), oldPrice, newPrice);
            booking.setPrice(newPrice);
        }

        if (isAdmin && request.getCreatedBy() != null && !request.getCreatedBy().isBlank()
                && !booking.getCreatedBy().equals(request.getCreatedBy())) {
            if (booking.getAssignedProvider() != null) {
                throw new IllegalStateException("已被接受的预约无法变更提交账号");
            }
            String newOwnerName = request.getCreatedBy();
            UserAccount newOwner = userAccountRepository.findById(newOwnerName)
                    .orElseThrow(() -> new IllegalArgumentException("目标用户不存在"));
            if (!"USER".equalsIgnoreCase(newOwner.getTypes())) {
                throw new IllegalStateException("只有普通用户才能持有预约");
            }
            if (newOwner.getMoney().compareTo(booking.getPrice()) < 0) {
                throw new IllegalStateException("目标用户钱包余额不足，无法转移预约");
            }
            refundUser(booking.getCreatedBy(), booking.getPrice());
            newOwner.setMoney(newOwner.getMoney().subtract(booking.getPrice()));
            userAccountRepository.save(newOwner);
            booking.setCreatedBy(newOwnerName);
        }

        booking.setCustomerName(request.getCustomerName());
        booking.setPhone(request.getPhone());
        booking.setAddress(request.getAddress());
        booking.setServiceDate(request.getServiceDate());
        booking.setNotes(request.getNotes());
        booking.setServiceItem(newItem);

        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long id, String operatorType, String operatorUsername) {
        ServiceBooking booking = findById(id);

        boolean isAdmin = "ADMIN".equalsIgnoreCase(operatorType);
        boolean isOwner = booking.getCreatedBy().equals(operatorUsername);
        if (!isAdmin && !isOwner) {
            throw new IllegalStateException("无权删除该预约");
        }

        if (booking.getAssignedProvider() != null) {
            throw new IllegalStateException("已被接受的预约无法删除");
        }

        refundUser(booking.getCreatedBy(), booking.getPrice());
        bookingRepository.delete(booking);
    }

    @Transactional
    public ServiceBooking acceptBooking(Long id, String providerUsername) {
        ServiceBooking booking = findById(id);
        if (booking.getAssignedProvider() != null) {
            throw new IllegalStateException("预约已被其他人员接受");
        }

        booking.setAssignedProvider(providerUsername);
        booking.setStatus("ACCEPTED");

        UserAccount provider = userAccountRepository.findById(providerUsername)
                .orElseThrow(() -> new IllegalArgumentException("服务人员不存在"));
        if (!"PROVIDER".equalsIgnoreCase(provider.getTypes())) {
            throw new IllegalStateException("只有家政服务人员可以接受预约");
        }

        provider.setMoney(provider.getMoney().add(booking.getPrice()));
        userAccountRepository.save(provider);

        return bookingRepository.save(booking);
    }

    private String determineOwner(String operatorType, String operatorUsername, BookingRequest request) {
        if ("ADMIN".equalsIgnoreCase(operatorType)) {
            if (request.getCreatedBy() == null || request.getCreatedBy().isBlank()) {
                throw new IllegalArgumentException("管理员创建预约时必须指定提交人账号");
            }
            return request.getCreatedBy();
        }
        return operatorUsername;
    }

    private void adjustUserWallet(String userName, BigDecimal oldPrice, BigDecimal newPrice) {
        UserAccount user = userAccountRepository.findById(userName)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        int comparison = newPrice.compareTo(oldPrice);
        if (comparison > 0) {
            BigDecimal diff = newPrice.subtract(oldPrice);
            if (user.getMoney().compareTo(diff) < 0) {
                throw new IllegalStateException("钱包余额不足，无法更新预约");
            }
            user.setMoney(user.getMoney().subtract(diff));
        } else if (comparison < 0) {
            BigDecimal diff = oldPrice.subtract(newPrice);
            user.setMoney(user.getMoney().add(diff));
        }
        userAccountRepository.save(user);
    }

    private void refundUser(String userName, BigDecimal amount) {
        UserAccount user = userAccountRepository.findById(userName)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setMoney(user.getMoney().add(amount));
        userAccountRepository.save(user);
    }
}
