package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.HomeTip;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeTipRepository extends JpaRepository<HomeTip, Long> {
    List<HomeTip> findByFeaturedTrueOrderByUpdatedAtDesc();
}
