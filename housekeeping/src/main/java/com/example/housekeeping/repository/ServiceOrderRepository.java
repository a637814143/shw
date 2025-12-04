package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.ServiceOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    List<ServiceOrder> findByUserOrderByCreatedAtDesc(UserAll user);

    List<ServiceOrder> findByServiceInAndStatus(List<HousekeepService> services, ServiceOrderStatus status);

    List<ServiceOrder> findByServiceIn(List<HousekeepService> services);

    @Query("""
        SELECT COUNT(o) FROM ServiceOrder o
        WHERE o.service.company = :company
          AND (:category IS NULL OR o.service.category = :category)
          AND o.status IN :statuses
          AND o.scheduledAt < :endTime AND o.scheduledEndAt > :startTime
        """)
    long countOverlappingOrders(@Param("company") UserAll company,
                                @Param("category") com.example.housekeeping.entity.ServiceCategory category,
                                @Param("statuses") List<ServiceOrderStatus> statuses,
                                @Param("startTime") java.time.Instant startTime,
                                @Param("endTime") java.time.Instant endTime);

    @Query("""
        SELECT COUNT(o) FROM ServiceOrder o
        WHERE o.assignedStaff = :staff AND o.id <> :excludeId
          AND o.status IN :statuses
        """)
    long countActiveOrdersForStaff(@Param("staff") com.example.housekeeping.entity.CompanyStaff staff,
                                   @Param("excludeId") Long excludeId,
                                   @Param("statuses") List<ServiceOrderStatus> statuses);

    @Query("""
        SELECT COUNT(o) FROM ServiceOrder o
        WHERE o.assignedStaff = :staff
          AND (:excludeId IS NULL OR o.id <> :excludeId)
          AND o.status IN :statuses
          AND o.scheduledAt < :endTime AND o.scheduledEndAt > :startTime
        """)
    long countOverlappingOrdersForStaff(@Param("staff") com.example.housekeeping.entity.CompanyStaff staff,
                                        @Param("excludeId") Long excludeId,
                                        @Param("statuses") List<ServiceOrderStatus> statuses,
                                        @Param("startTime") java.time.Instant startTime,
                                        @Param("endTime") java.time.Instant endTime);
}
