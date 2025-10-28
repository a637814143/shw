package com.example.housekeeping.dto;

/**
 * 服务分类响应数据。
 */
public class ServiceCategoryResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Long serviceCount;
    private final Long totalStaffCount;
    private final Long availableStaffCount;

    public ServiceCategoryResponse(Long id, String name, String description,
                                   Long serviceCount, Long totalStaffCount, Long availableStaffCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.serviceCount = serviceCount;
        this.totalStaffCount = totalStaffCount;
        this.availableStaffCount = availableStaffCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getServiceCount() {
        return serviceCount;
    }

    public Long getTotalStaffCount() {
        return totalStaffCount;
    }

    public Long getAvailableStaffCount() {
        return availableStaffCount;
    }
}
