package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 家政服务评价。
 */
@Getter
@Setter
@Entity
@Table(name = "service_review")
public class ServiceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private HousekeepService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAll user;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
