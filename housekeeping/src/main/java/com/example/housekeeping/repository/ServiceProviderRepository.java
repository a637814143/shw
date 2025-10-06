package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.ServiceProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    Optional<ServiceProvider> findByUsername(String username);
}
