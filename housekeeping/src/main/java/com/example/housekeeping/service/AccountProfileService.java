package com.example.housekeeping.service;

import com.example.housekeeping.common.AvatarConstants;
import com.example.housekeeping.dto.AccountProfileResponse;
import com.example.housekeeping.dto.AccountProfileUpdateRequest;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.UserAllRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * 账号资料编辑相关业务逻辑。
 */
@Service
public class AccountProfileService {

    private static final int MAX_AVATAR_BYTES = 512 * 1024;

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private UserAllRepository userAllRepository;

    public AccountProfileResponse buildResponse(UserAll account) {
        AccountRole role = AccountRole.fromValue(account.getUserType());
        String displayName = account.getDisplayName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = account.getUsername();
        }
        String avatar = account.getAvatarBase64();
        if (avatar == null || avatar.trim().isEmpty()) {
            avatar = AvatarConstants.DEFAULT_AVATAR_BASE64;
        }
        return new AccountProfileResponse(
            account.getId(),
            account.getUsername(),
            displayName,
            role.getCode(),
            account.getMoney(),
            account.getLoyaltyPoints() == null ? 0 : account.getLoyaltyPoints(),
            avatar
        );
    }

    @Transactional
    public AccountProfileResponse updateProfile(AccountProfileUpdateRequest request) {
        UserAll account = accountLookupService.getCurrentAccount();

        String normalizedName = request.getDisplayName().trim();
        if (normalizedName.isEmpty()) {
            throw new RuntimeException("展示名称不能为空");
        }
        account.setDisplayName(normalizedName);

        String normalizedAvatar = normalizeAvatar(request.getAvatarBase64());
        account.setAvatarBase64(normalizedAvatar);

        UserAll saved = userAllRepository.save(account);
        return buildResponse(saved);
    }

    public AccountProfileResponse currentProfile() {
        UserAll account = accountLookupService.getCurrentAccount();
        return buildResponse(account);
    }

    private String normalizeAvatar(String raw) {
        String candidate = raw == null ? "" : raw.trim();
        if (candidate.isEmpty()) {
            return AvatarConstants.DEFAULT_AVATAR_BASE64;
        }

        String dataPart = candidate;
        int commaIndex = candidate.indexOf(',');
        if (commaIndex >= 0) {
            dataPart = candidate.substring(commaIndex + 1);
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(dataPart);
            if (decoded.length > MAX_AVATAR_BYTES) {
                throw new RuntimeException("头像文件过大，请控制在 512KB 以内");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("头像数据不是有效的 Base64 编码", e);
        }

        return candidate;
    }
}
