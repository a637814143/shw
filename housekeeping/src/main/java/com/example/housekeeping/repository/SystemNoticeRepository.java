package com.example.housekeeping.repository;

import com.example.housekeeping.entity.SystemNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemNoticeRepository extends JpaRepository<SystemNotice, Long> {
}
