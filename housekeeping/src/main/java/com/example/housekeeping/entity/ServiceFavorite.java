package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 用户收藏的服务。
 */
@Getter
@Setter
@Entity
@Table(name = "service_favorite",
    uniqueConstraints = @UniqueConstraint(name = "uk_favorite_user_service", columnNames = {"user_id", "service_id"}))
public class ServiceFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private HousekeepService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAll user;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
