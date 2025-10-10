package com.example.housekeeping.controller;

import com.example.housekeeping.dto.AppointmentRequest;
import com.example.housekeeping.dto.AssignAppointmentRequest;
import com.example.housekeeping.entity.AppointmentStatus;
import com.example.housekeeping.entity.ServiceAppointment;
import com.example.housekeeping.exception.ResourceNotFoundException;
import com.example.housekeeping.repository.ServiceAppointmentRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/appointments")
public class ServiceAppointmentController {

    private final ServiceAppointmentRepository appointmentRepository;

    public ServiceAppointmentController(ServiceAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public Page<ServiceAppointment> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) AppointmentStatus status,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("appointmentTime").descending());
        String normalizedKeyword = keyword != null && !keyword.isBlank() ? keyword : null;
        return appointmentRepository.search(normalizedKeyword, status, pageable);
    }

    @GetMapping("/{id}")
    public ServiceAppointment detail(@PathVariable Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的预约记录"));
    }

    @PostMapping
    public ResponseEntity<ServiceAppointment> create(@Valid @RequestBody AppointmentRequest request) {
        ServiceAppointment appointment = ServiceAppointment.builder()
                .serviceName(request.getServiceName())
                .quantity(request.getQuantity())
                .totalAmount(request.getTotalAmount())
                .userName(request.getUserName())
                .providerName(request.getProviderName())
                .contactPhone(request.getContactPhone())
                .contactAddress(request.getContactAddress())
                .servicePhone(request.getServicePhone())
                .status(request.getStatus())
                .statusText(resolveStatusText(request.getStatus(), request.getStatusText()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .appointmentTime(request.getAppointmentTime())
                .assigned(Boolean.TRUE.equals(request.getAssigned()))
                .build();
        ServiceAppointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.created(URI.create("/api/appointments/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ServiceAppointment update(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
        ServiceAppointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的预约记录"));
        appointment.setServiceName(request.getServiceName());
        appointment.setQuantity(request.getQuantity());
        appointment.setTotalAmount(request.getTotalAmount());
        appointment.setUserName(request.getUserName());
        appointment.setProviderName(request.getProviderName());
        appointment.setContactPhone(request.getContactPhone());
        appointment.setContactAddress(request.getContactAddress());
        appointment.setServicePhone(request.getServicePhone());
        appointment.setStatus(request.getStatus());
        appointment.setStatusText(resolveStatusText(request.getStatus(), request.getStatusText()));
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setAssigned(Boolean.TRUE.equals(request.getAssigned()));
        return appointmentRepository.save(appointment);
    }

    @PostMapping("/{id}/assign")
    public ServiceAppointment assign(@PathVariable Long id, @Valid @RequestBody AssignAppointmentRequest request) {
        ServiceAppointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的预约记录"));
        appointment.setProviderName(request.getProviderName());
        appointment.setContactPhone(request.getContactPhone());
        appointment.setContactAddress(request.getContactAddress());
        appointment.setServicePhone(request.getServicePhone());
        appointment.setAssigned(true);
        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            appointment.setStatus(AppointmentStatus.PROCESSING);
        }
        appointment.setStatusText(resolveStatusText(appointment.getStatus(), appointment.getStatusText()));
        return appointmentRepository.save(appointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ServiceAppointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的预约记录"));
        appointmentRepository.delete(appointment);
        return ResponseEntity.noContent().build();
    }

    private String resolveStatusText(AppointmentStatus status, String override) {
        if (override != null && !override.isBlank()) {
            return override;
        }
        return switch (status) {
            case PENDING -> "已预约";
            case PROCESSING -> "处理中";
            case COMPLETED -> "已完成";
            case CANCELLED -> "已取消";
        };
    }
}
