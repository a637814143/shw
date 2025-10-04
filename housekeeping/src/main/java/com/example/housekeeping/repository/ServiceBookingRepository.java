package com.example.housekeeping.repository;

import com.example.housekeeping.model.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findAllByCreatedByOrderByServiceDateDesc(String createdBy);

    List<ServiceBooking> findAllByOrderByServiceDateDesc();
}
