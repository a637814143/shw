package com.example.housekeeping.repository;

import com.example.housekeeping.entity.UserForHousekeeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserForHousekeeperRepository extends JpaRepository<UserForHousekeeper, String> {
    Optional<UserForHousekeeper> findByUsername(String username);

    long countByUsertypeIgnoreCase(String usertype);
}
