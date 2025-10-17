package com.example.housekeeping.dto;

import com.example.housekeeping.enums.ServiceOrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 更新订单进度的请求。
 */
public class OrderProgressUpdateRequest {

    @NotNull(message = "状态不能为空")
    private ServiceOrderStatus status;

    @Size(max = 500, message = "备注过长")
    private String progressNote;

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceOrderStatus status) {
        this.status = status;
    }

    public String getProgressNote() {
        return progressNote;
    }

    public void setProgressNote(String progressNote) {
        this.progressNote = progressNote;
    }
}
