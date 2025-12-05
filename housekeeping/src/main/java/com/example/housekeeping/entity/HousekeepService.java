package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 家政公司发布的服务信息。
 */
@Getter
@Setter
@Entity
@Table(name = "housekeep_service")
public class HousekeepService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private UserAll company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "unit", nullable = false, length = 50)
    private String unit;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "contact", nullable = false, length = 100)
    private String contact;

    @Column(name = "service_time", nullable = false, length = 100)
    private String serviceTime = "2小时";

    @Column(name = "description", length = 500)
    private String description;
}
