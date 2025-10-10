package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceBooking;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {

    Page<ServiceBooking> findByUser(User user, Pageable pageable);

    Page<ServiceBooking> findByProvider(ServiceProvider provider, Pageable pageable);

    long countByStatus(BookingStatus status);

    long countByProvider(ServiceProvider provider);

    long countByUser(User user);

    @Query("select date(b.createTime) as day, count(b) from ServiceBooking b where b.createTime between :start and :end group by date(b.createTime) order by day")
    List<Object[]> countBookingsByDay(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}
