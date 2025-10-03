package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 居家贴士实体类
 */
@Data
@Entity
@Table(name = "housekeeping_tip")
@EqualsAndHashCode(callSuper = true)
public class HousekeepingTip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "longtext")
    private String content;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "tags")
    private String tags;

    @Column(name = "view_count", columnDefinition = "int default 0")
    private Integer viewCount = 0;

    @Column(name = "like_count", columnDefinition = "int default 0")
    private Integer likeCount = 0;

    @Column(name = "status", columnDefinition = "tinyint default 1")
    private Integer status = 1; // 0-草稿，1-发布
}
