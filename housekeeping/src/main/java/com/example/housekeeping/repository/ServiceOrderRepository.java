package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.ServiceOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    List<ServiceOrder> findByUserOrderByCreatedAtDesc(UserAll user);

    List<ServiceOrder> findByServiceInAndStatus(List<HousekeepService> services, ServiceOrderStatus status);

    List<ServiceOrder> findByServiceIn(List<HousekeepService> services);

    List<ServiceOrder> findByHandledBy(UserAll handler);

    List<ServiceOrder> findByAssignedStaffIn(@Param("staff") List<com.example.housekeeping.entity.CompanyStaff> staff);
}
