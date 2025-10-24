package com.example.housekeeping.entity;

import com.example.housekeeping.enums.StaffStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 家政公司人员信息。
 */
@Getter
@Setter
@Entity
@Table(name = "company_staff")
public class CompanyStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private UserAll company;

    @Column(name = "staff_name", nullable = false, length = 100)
    private String name;

    @Column(name = "contact", nullable = false, length = 100)
    private String contact;

    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "notes", length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private StaffStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
