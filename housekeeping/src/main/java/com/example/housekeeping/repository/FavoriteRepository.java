package com.example.housekeeping.repository;

import com.example.housekeeping.entity.Favorite;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndService(User user, HousekeepingService service);

    long countByService(HousekeepingService service);

    Page<Favorite> findByUser(User user, Pageable pageable);
}
