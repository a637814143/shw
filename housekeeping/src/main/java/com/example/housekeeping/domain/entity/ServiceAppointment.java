package com.example.housekeeping.domain.entity;

import com.example.housekeeping.domain.enums.AppointmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "service_appointments")
public class ServiceAppointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private HousekeepingService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ServiceProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING_REVIEW;

    private LocalDateTime scheduledAt;

    private LocalDateTime actualStartAt;

    private LocalDateTime actualEndAt;

    private String address;

    private String contactName;

    private String contactPhone;

    private String userNotes;

    private String adminNotes;

    private String auditRemark;

    private String auditBy;

    private LocalDateTime auditAt;

    @Column(nullable = false)
    private BigDecimal price;
}
