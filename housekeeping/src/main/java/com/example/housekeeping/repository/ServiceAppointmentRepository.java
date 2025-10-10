package com.example.housekeeping.repository;

import com.example.housekeeping.entity.AppointmentStatus;
import com.example.housekeeping.entity.ServiceAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, Long> {

    @Query("SELECT a FROM ServiceAppointment a WHERE (:keyword IS NULL OR LOWER(a.serviceName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<ServiceAppointment> search(@Param("keyword") String keyword,
                                    @Param("status") AppointmentStatus status,
                                    Pageable pageable);
}
