package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统公告实体类
 */
@Data
@Entity
@Table(name = "system_notice")
@EqualsAndHashCode(callSuper = true)
public class SystemNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "longtext")
    private String content;

    @Column(name = "type", columnDefinition = "tinyint default 1")
    private Integer type = 1; // 1-系统公告，2-活动通知

    @Column(name = "status", columnDefinition = "tinyint default 1")
    private Integer status = 1; // 0-草稿，1-发布
}
