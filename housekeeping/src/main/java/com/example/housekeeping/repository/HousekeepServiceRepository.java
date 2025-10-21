package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeepServiceRepository extends JpaRepository<HousekeepService, Long> {

    List<HousekeepService> findByCompany(UserAll company);

    @Query("""
        SELECT s FROM HousekeepService s
        WHERE s.company = :company AND (
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """)
    List<HousekeepService> searchByCompanyAndKeyword(@Param("company") UserAll company,
                                                     @Param("keyword") String keyword);

    @Query("""
        SELECT s FROM HousekeepService s
        WHERE
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<HousekeepService> searchByKeyword(@Param("keyword") String keyword);
}
