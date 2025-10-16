package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeepServiceRepository extends JpaRepository<HousekeepService, Long> {

    List<HousekeepService> findByCompany(UserAll company);
}
