package com.example.housekeeping.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 带统计信息的用户订单分页结果。
 */
public class UserOrderPageResponse extends PageResponse<ServiceOrderResponse> {

    private final OrderStatsSummary stats;
    private final List<ServiceOrderResponse> upcomingOrders;
    private final List<ReviewableServiceOption> reviewableServices;

    public UserOrderPageResponse(PageResponse<ServiceOrderResponse> base,
                                 OrderStatsSummary stats,
                                 List<ServiceOrderResponse> upcomingOrders,
                                 List<ReviewableServiceOption> reviewableServices) {
        super(base.getItems(), base.getTotal(), base.getPage(), base.getSize());
        this.stats = stats;
        this.upcomingOrders = upcomingOrders == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(new ArrayList<>(upcomingOrders));
        this.reviewableServices = reviewableServices == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(new ArrayList<>(reviewableServices));
    }

    public OrderStatsSummary getStats() {
        return stats;
    }

    public List<ServiceOrderResponse> getUpcomingOrders() {
        return upcomingOrders;
    }

    public List<ReviewableServiceOption> getReviewableServices() {
        return reviewableServices;
    }
}
