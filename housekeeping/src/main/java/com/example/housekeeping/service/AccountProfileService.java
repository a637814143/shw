package com.example.housekeeping.service;

import com.example.housekeeping.common.AvatarConstants;
import com.example.housekeeping.dto.AccountPasswordUpdateRequest;
import com.example.housekeeping.dto.AccountProfileResponse;
import com.example.housekeeping.dto.AccountProfileUpdateRequest;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.UserAllRepository;
import com.example.housekeeping.util.LegacyPasswordEncoder;
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

    @Autowired
    private LegacyPasswordEncoder legacyPasswordEncoder;

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
            avatar,
            account.getContactPhone(),
            account.getContactAddress(),
            role == AccountRole.COMPANY ? account.getCompanyDescription() : null
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

        account.setContactPhone(normalizeOptional(request.getContactPhone()));
        account.setContactAddress(normalizeOptional(request.getContactAddress()));

        AccountRole role = AccountRole.fromValue(account.getUserType());
        if (role == AccountRole.COMPANY) {
            account.setCompanyDescription(normalizeOptional(request.getCompanyDescription()));
        } else {
            account.setCompanyDescription(null);
        }

        UserAll saved = userAllRepository.save(account);
        return buildResponse(saved);
    }

    public AccountProfileResponse currentProfile() {
        UserAll account = accountLookupService.getCurrentAccount();
        return buildResponse(account);
    }

    @Transactional
    public void updatePassword(AccountPasswordUpdateRequest request) {
        UserAll account = accountLookupService.getCurrentAccount();

        String normalizedCurrent = request.getCurrentPassword() == null
            ? ""
            : request.getCurrentPassword().trim();
        String normalizedNew = request.getNewPassword() == null
            ? ""
            : request.getNewPassword().trim();

        if (normalizedCurrent.isEmpty() || normalizedNew.isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        String encodedCurrent = legacyPasswordEncoder.encode(normalizedCurrent);
        if (!encodedCurrent.equals(account.getPasswd())) {
            throw new RuntimeException("原密码不正确");
        }

        if (normalizedCurrent.equals(normalizedNew)) {
            throw new RuntimeException("新密码不能与原密码相同");
        }

        account.setPasswd(legacyPasswordEncoder.encode(normalizedNew));
        userAllRepository.save(account);
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

    private String normalizeOptional(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
