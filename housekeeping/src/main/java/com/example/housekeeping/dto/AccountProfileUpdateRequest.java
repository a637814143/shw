package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 编辑个人资料请求。
 */
public class AccountProfileUpdateRequest {

    @NotBlank(message = "展示名称不能为空")
    @Size(max = 100, message = "展示名称长度不得超过100个字符")
    private String displayName;

    @Size(max = 524288, message = "头像数据过大，请控制在512KB以内")
    private String avatarBase64;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }
}
