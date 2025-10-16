package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 家政服务内容表，对应 housekeep
 */
@Getter
@Setter
@Entity
@Table(name = "housekeep")
public class HousekeepItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    @Column(name = "title", length = 200)
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "icon", length = 32)
    private String icon;

    @Column(name = "tag", length = 50)
    private String tag;

    @Column(name = "author", length = 100)
    private String author;

    @Column(name = "service_name", length = 200)
    private String serviceName;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;
}
