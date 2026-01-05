package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceReview;
import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {

    List<ServiceReview> findByServiceOrderByCreatedAtDesc(HousekeepService service);

    List<ServiceReview> findByServiceInOrderByCreatedAtDesc(List<HousekeepService> services);

    List<ServiceReview> findByUserOrderByCreatedAtDesc(UserAll user);

    void deleteByUser(UserAll user);
}
