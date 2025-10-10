package com.example.housekeeping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "service_provider")
@Getter
@Setter
public class ServiceProvider extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    private String address;

    @Column(name = "id_card", length = 18)
    private String idCard;

    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "certification_status")
    private Integer certificationStatus;

    @Column(name = "certification_time")
    private java.time.LocalDateTime certificationTime;

    private BigDecimal balance;

    private Integer status;
}
