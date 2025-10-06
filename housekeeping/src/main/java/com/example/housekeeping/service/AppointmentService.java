package com.example.housekeeping.service;

import com.example.housekeeping.domain.entity.ServiceAppointment;
import com.example.housekeeping.dto.AppointmentAssignmentRequest;
import com.example.housekeeping.dto.AppointmentCreateRequest;
import com.example.housekeeping.dto.AppointmentStatusUpdateRequest;
import java.util.List;

public interface AppointmentService {
    ServiceAppointment createAppointment(Long userId, AppointmentCreateRequest request);

    ServiceAppointment getAppointment(Long id);

    List<ServiceAppointment> getAppointmentsForUser(Long userId);

    List<ServiceAppointment> getAppointmentsForProvider(Long providerId);

    List<ServiceAppointment> getAllAppointments();

    ServiceAppointment assignProvider(Long appointmentId, AppointmentAssignmentRequest request, String adminName);

    ServiceAppointment updateStatus(Long appointmentId, AppointmentStatusUpdateRequest request, String actor);
}
