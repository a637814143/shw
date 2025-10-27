package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    Optional<ServiceCategory> findByNameIgnoreCase(String name);

    List<ServiceCategory> findAllByOrderByNameAsc();
}
