package com.example.housekeeping.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 家政公司服务分页返回结果。
 */
public class CompanyServicePageResponse {

    private final List<HousekeepServiceResponse> items;
    private final long total;
    private final int page;
    private final int size;
    private final BigDecimal averagePrice;

    public CompanyServicePageResponse(List<HousekeepServiceResponse> items,
                                      long total,
                                      int page,
                                      int size,
                                      BigDecimal averagePrice) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
        this.averagePrice = averagePrice;
    }

    public List<HousekeepServiceResponse> getItems() {
        return items;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
}
