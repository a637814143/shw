package com.example.housekeeping.dto;

/**
 * 订单状态统计信息。
 */
public class OrderStatsSummary {

    private final long total;
    private final long awaiting;
    private final long inProgress;
    private final long refunding;
    private final long completed;

    public OrderStatsSummary(long total, long awaiting, long inProgress, long refunding, long completed) {
        this.total = total;
        this.awaiting = awaiting;
        this.inProgress = inProgress;
        this.refunding = refunding;
        this.completed = completed;
    }

    public long getTotal() {
        return total;
    }

    public long getAwaiting() {
        return awaiting;
    }

    public long getInProgress() {
        return inProgress;
    }

    public long getRefunding() {
        return refunding;
    }

    public long getCompleted() {
        return completed;
    }
}
