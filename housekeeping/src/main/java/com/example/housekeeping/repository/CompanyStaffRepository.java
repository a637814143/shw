package com.example.housekeeping.repository;

import com.example.housekeeping.entity.CompanyStaff;
import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyStaffRepository extends JpaRepository<CompanyStaff, Long> {

    List<CompanyStaff> findByCompany(UserAll company);
}
