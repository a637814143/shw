package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统公告实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "system_notice")
public class SystemNotice extends BaseEntity {
    
    @NotBlank(message = "标题不能为空")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @NotBlank(message = "内容不能为空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "type")
    private Integer type = 1; // 1-系统公告，2-活动公告
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
}
