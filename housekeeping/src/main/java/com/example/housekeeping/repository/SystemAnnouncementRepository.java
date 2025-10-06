package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.SystemAnnouncement;
import com.example.housekeeping.domain.enums.AnnouncementTarget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemAnnouncementRepository extends JpaRepository<SystemAnnouncement, Long> {
    List<SystemAnnouncement> findByEnabledTrueOrderByPinnedDescPublishedAtDesc();
    List<SystemAnnouncement> findByTargetOrTargetOrderByPinnedDescPublishedAtDesc(
            AnnouncementTarget target, AnnouncementTarget fallback);
}
