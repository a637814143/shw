package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.FavoriteService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteServiceRepository extends JpaRepository<FavoriteService, Long> {
    List<FavoriteService> findByUserId(Long userId);
    Optional<FavoriteService> findByUserIdAndServiceId(Long userId, Long serviceId);
    void deleteByUserIdAndServiceId(Long userId, Long serviceId);
}
