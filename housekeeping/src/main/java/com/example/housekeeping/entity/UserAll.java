package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 统一用户账号表，对应 user_all
 */
@Getter
@Setter
@Entity
@Table(name = "user_all")
public class UserAll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "passwd", nullable = false, length = 255)
    private String passwd;

    @Column(name = "money", nullable = false, precision = 12, scale = 2)
    private BigDecimal money;

    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints;

    @Column(name = "usertype", nullable = false, length = 32)
    private String userType;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "contact_number", length = 100)
    private String contactNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "company_phone", length = 100)
    private String companyPhone;

    @Column(name = "company_address", length = 255)
    private String companyAddress;

    @Column(name = "company_description", length = 1000)
    private String companyDescription;
}
