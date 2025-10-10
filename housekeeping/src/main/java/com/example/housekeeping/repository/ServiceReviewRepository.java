package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.ServiceReview;
import com.example.housekeeping.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {

    Page<ServiceReview> findByService(HousekeepingService service, Pageable pageable);

    Page<ServiceReview> findByProvider(ServiceProvider provider, Pageable pageable);

    Page<ServiceReview> findByUser(User user, Pageable pageable);
}
