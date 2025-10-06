package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.ServiceAppointment;
import com.example.housekeeping.domain.entity.ServiceProvider;
import com.example.housekeeping.domain.entity.UserAccount;
import com.example.housekeeping.domain.enums.AppointmentStatus;
import com.example.housekeeping.dto.AppointmentAssignmentRequest;
import com.example.housekeeping.dto.AppointmentCreateRequest;
import com.example.housekeeping.dto.AppointmentStatusUpdateRequest;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ServiceAppointmentRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.service.AppointmentService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final ServiceAppointmentRepository serviceAppointmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    @Override
    public ServiceAppointment createAppointment(Long userId, AppointmentCreateRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        HousekeepingService service = housekeepingServiceRepository.findById(request.serviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "服务不存在"));
        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setUser(user);
        appointment.setService(service);
        appointment.setStatus(AppointmentStatus.PENDING_REVIEW);
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setAddress(request.address());
        appointment.setContactName(request.contactName());
        appointment.setContactPhone(request.contactPhone());
        appointment.setUserNotes(request.userNotes());
        appointment.setPrice(request.price() != null ? request.price() : service.getPrice());
        return serviceAppointmentRepository.save(appointment);
    }

    @Override
    public ServiceAppointment getAppointment(Long id) {
        return serviceAppointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约不存在"));
    }

    @Override
    public List<ServiceAppointment> getAppointmentsForUser(Long userId) {
        return serviceAppointmentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<ServiceAppointment> getAppointmentsForProvider(Long providerId) {
        return serviceAppointmentRepository.findByProviderIdOrderByCreatedAtDesc(providerId);
    }

    @Override
    public List<ServiceAppointment> getAllAppointments() {
        return serviceAppointmentRepository.findAll();
    }

    @Override
    public ServiceAppointment assignProvider(Long appointmentId, AppointmentAssignmentRequest request, String adminName) {
        ServiceAppointment appointment = getAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已完成或已取消的订单不能分配");
        }
        ServiceProvider provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "服务者不存在"));
        appointment.setProvider(provider);
        appointment.setStatus(AppointmentStatus.ASSIGNED);
        appointment.setAdminNotes(request.adminNotes());
        appointment.setAuditBy(adminName);
        appointment.setAuditAt(LocalDateTime.now());
        return appointment;
    }

    @Override
    public ServiceAppointment updateStatus(Long appointmentId, AppointmentStatusUpdateRequest request, String actor) {
        ServiceAppointment appointment = getAppointment(appointmentId);
        AppointmentStatus newStatus = request.status();
        switch (newStatus) {
            case APPROVED, REJECTED -> {
                appointment.setStatus(newStatus);
                appointment.setAuditBy(actor);
                appointment.setAuditRemark(request.remark());
                appointment.setAuditAt(LocalDateTime.now());
            }
            case CANCELLED -> {
                if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单已完成，无法取消");
                }
                appointment.setStatus(AppointmentStatus.CANCELLED);
                appointment.setAuditRemark(request.remark());
            }
            case IN_SERVICE -> {
                if (appointment.getStatus() != AppointmentStatus.ASSIGNED && appointment.getStatus() != AppointmentStatus.APPROVED) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单尚未分配或审核通过");
                }
                appointment.setStatus(AppointmentStatus.IN_SERVICE);
                appointment.setActualStartAt(LocalDateTime.now());
            }
            case COMPLETED -> {
                if (appointment.getStatus() != AppointmentStatus.IN_SERVICE && appointment.getStatus() != AppointmentStatus.ASSIGNED) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单未处于服务中状态");
                }
                appointment.setStatus(AppointmentStatus.COMPLETED);
                appointment.setActualEndAt(LocalDateTime.now());
                if (appointment.getProvider() != null) {
                    ServiceProvider provider = appointment.getProvider();
                    provider.setCompletedOrders(provider.getCompletedOrders() + 1);
                }
            }
            case ASSIGNED, PENDING_REVIEW -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请通过专用接口更新预约分配或新建预约");
        }
        return appointment;
    }
}
