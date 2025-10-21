package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 通用的批量操作请求体，携带一组主键 ID。
 */
public class IdListRequest {

    @NotEmpty(message = "请选择要操作的记录")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
