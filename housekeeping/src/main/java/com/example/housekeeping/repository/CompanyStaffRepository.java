package com.example.housekeeping.repository;

import com.example.housekeeping.entity.CompanyStaff;
import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyStaffRepository extends JpaRepository<CompanyStaff, Long> {

    List<CompanyStaff> findByCompany(UserAll company);

    @Query("""
        SELECT s FROM CompanyStaff s
        WHERE s.company = :company AND (
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.role, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.notes, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        ORDER BY s.createdAt DESC
        """)
    List<CompanyStaff> searchByCompanyAndKeyword(@Param("company") UserAll company,
                                                 @Param("keyword") String keyword);
}
