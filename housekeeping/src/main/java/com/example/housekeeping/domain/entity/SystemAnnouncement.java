package com.example.housekeeping.domain.entity;

import com.example.housekeeping.domain.enums.AnnouncementTarget;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "system_announcements")
public class SystemAnnouncement extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementTarget target = AnnouncementTarget.ALL;

    private String publishedBy;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private boolean pinned = false;

    @Column(nullable = false)
    private boolean enabled = true;
}
