package com.example.housekeeping.dto;

/**
 * 可用于评价的服务选项。
 */
public class ReviewableServiceOption {

    private final Long id;
    private final String name;
    private final String companyName;

    public ReviewableServiceOption(Long id, String name, String companyName) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }
}
