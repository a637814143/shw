package com.example.housekeeping.enums;

/**
 * 第三方扫码支付的结果状态。
 */
public enum PaymentCheckStatus {

    /**
     * 尚未确认支付，可能仍在等待用户操作。
     */
    PENDING,

    /**
     * 用户已经在外部页面完成支付确认。
     */
    CONFIRMED,

    /**
     * 用户明确拒绝或支付失败。
     */
    DECLINED,

    /**
     * 调用外部服务时出现错误。
     */
    ERROR
}

