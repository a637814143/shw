package com.example.housekeeping.repository;

import com.example.housekeeping.model.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<ServiceItem> findByNameIgnoreCase(String name);
}

