package com.example.housekeeping.dto.request;

import com.example.housekeeping.enums.WithdrawStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawProcessRequest {

    @NotNull
    private WithdrawStatus status;

    private String remark;
}
