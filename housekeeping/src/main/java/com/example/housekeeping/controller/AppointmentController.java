package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.ServiceAppointment;
import com.example.housekeeping.dto.AppointmentAssignmentRequest;
import com.example.housekeeping.dto.AppointmentCreateRequest;
import com.example.housekeeping.dto.AppointmentStatusUpdateRequest;
import com.example.housekeeping.service.AppointmentService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<ServiceAppointment> listAll() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public ServiceAppointment getById(@PathVariable Long id) {
        return appointmentService.getAppointment(id);
    }

    @GetMapping("/user/{userId}")
    public List<ServiceAppointment> getByUser(@PathVariable Long userId) {
        return appointmentService.getAppointmentsForUser(userId);
    }

    @GetMapping("/provider/{providerId}")
    public List<ServiceAppointment> getByProvider(@PathVariable Long providerId) {
        return appointmentService.getAppointmentsForProvider(providerId);
    }

    @PostMapping("/user/{userId}")
    public ServiceAppointment createAppointment(@PathVariable Long userId,
            @RequestBody AppointmentCreateRequest request) {
        return appointmentService.createAppointment(userId, request);
    }

    @PostMapping("/{appointmentId}/assign")
    public ServiceAppointment assignProvider(@PathVariable Long appointmentId,
            @RequestBody AppointmentAssignmentRequest request,
            @RequestParam(name = "adminName", defaultValue = "系统管理员") String adminName) {
        return appointmentService.assignProvider(appointmentId, request, adminName);
    }

    @PatchMapping("/{appointmentId}/status")
    public ServiceAppointment updateStatus(@PathVariable Long appointmentId,
            @RequestBody AppointmentStatusUpdateRequest request,
            @RequestParam(name = "actor", defaultValue = "系统用户") String actor) {
        return appointmentService.updateStatus(appointmentId, request, actor);
    }
}
