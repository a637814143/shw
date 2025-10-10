package com.example.housekeeping.controller;

import com.example.housekeeping.entity.ProviderCertificationStatus;
import com.example.housekeeping.repository.HousekeepingServiceItemRepository;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import com.example.housekeeping.repository.ServiceAppointmentRepository;
import com.example.housekeeping.repository.UserForHousekeeperRepository;
import com.example.housekeeping.repository.WithdrawalRecordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserForHousekeeperRepository userRepository;
    private final ServiceAppointmentRepository appointmentRepository;
    private final HousekeepingServiceItemRepository serviceRepository;
    private final WithdrawalRecordRepository withdrawalRepository;
    private final ProviderCertificationRepository certificationRepository;

    public DashboardController(UserForHousekeeperRepository userRepository,
                               ServiceAppointmentRepository appointmentRepository,
                               HousekeepingServiceItemRepository serviceRepository,
                               WithdrawalRecordRepository withdrawalRepository,
                               ProviderCertificationRepository certificationRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.certificationRepository = certificationRepository;
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", userRepository.count());
        response.put("totalCustomers", userRepository.countByUsertypeIgnoreCase("user"));
        response.put("totalProviders", userRepository.countByUsertypeIgnoreCase("provider"));
        response.put("totalAdmins", userRepository.countByUsertypeIgnoreCase("admin"));
        response.put("totalAppointments", appointmentRepository.count());
        response.put("totalServices", serviceRepository.count());
        BigDecimal totalWithdrawal = withdrawalRepository.totalAmount();
        response.put("totalWithdrawAmount", totalWithdrawal);
        response.put("pendingCertifications", certificationRepository.countByStatus(ProviderCertificationStatus.PENDING));
        response.put("approvedCertifications", certificationRepository.countByStatus(ProviderCertificationStatus.APPROVED));
        return response;
    }
}
