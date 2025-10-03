package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 家政服务实体类
 */
@Data
@Entity
@Table(name = "housekeeping_service")
@EqualsAndHashCode(callSuper = true)
public class HousekeepingService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "content", columnDefinition = "longtext")
    private String content;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "unit", length = 20, columnDefinition = "varchar(20) default '次'")
    private String unit = "次";

    @Column(name = "duration")
    private Integer duration; // 服务时长（分钟）

    @Column(name = "images", columnDefinition = "text")
    private String images; // JSON数组

    @Column(name = "tags")
    private String tags;

    @Column(name = "rating", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count", columnDefinition = "int default 0")
    private Integer reviewCount = 0;

    @Column(name = "booking_count", columnDefinition = "int default 0")
    private Integer bookingCount = 0;

    @Column(name = "status", columnDefinition = "tinyint default 1")
    private Integer status = 1; // 0-下架，1-上架

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private ServiceCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private ServiceProvider provider;

    // 手动添加getter和setter方法（Lombok可能未正确生成）
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }
}
