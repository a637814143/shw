package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.HousekeepServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeepServiceRepository extends JpaRepository<HousekeepService, Long> {

    List<HousekeepService> findByCompany(UserAll company);

    List<HousekeepService> findByCompanyAndCategory(UserAll company, ServiceCategory category);

    Page<HousekeepService> findByCompany(UserAll company, Pageable pageable);

    @Query(value = """
        SELECT s FROM HousekeepService s
        WHERE s.company = :company AND (
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.serviceTime, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """,
        countQuery = """
        SELECT COUNT(s) FROM HousekeepService s
        WHERE s.company = :company AND (
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.serviceTime, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """)
    Page<HousekeepService> searchByCompanyAndKeyword(@Param("company") UserAll company,
                                                     @Param("keyword") String keyword,
                                                     Pageable pageable);

    @Query(value = """
        SELECT s FROM HousekeepService s
        WHERE s.company = :company
          AND (:category IS NULL OR s.category = :category)
          AND (
            :keyword IS NULL OR
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.serviceTime, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        """,
        countQuery = """
        SELECT COUNT(s) FROM HousekeepService s
        WHERE s.company = :company
          AND (:category IS NULL OR s.category = :category)
          AND (
            :keyword IS NULL OR
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.serviceTime, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        """)
    Page<HousekeepService> searchByCompanyWithFilters(@Param("company") UserAll company,
                                                      @Param("category") ServiceCategory category,
                                                      @Param("keyword") String keyword,
                                                      Pageable pageable);

    @Query("""
        SELECT s FROM HousekeepService s
        WHERE
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.serviceTime, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<HousekeepService> searchByKeyword(@Param("keyword") String keyword);

    @Query("""
        SELECT s FROM HousekeepService s
        WHERE (:category IS NULL OR s.category = :category)
          AND (:status IS NULL OR s.status = :status)
          AND (
            :keyword IS NULL OR
            LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.unit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.serviceTime, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY s.id DESC
        """)
    List<HousekeepService> searchForAdmin(@Param("category") ServiceCategory category,
                                          @Param("status") HousekeepServiceStatus status,
                                          @Param("keyword") String keyword);

    @Query("""
        SELECT COALESCE(AVG(s.price), 0) FROM HousekeepService s
        WHERE s.company = :company
        """)
    Double findAveragePriceByCompany(@Param("company") UserAll company);

    long countByCategory(ServiceCategory category);
}
