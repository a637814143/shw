package com.example.housekeeping.service;

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

/**
 * 账号资料编辑相关业务逻辑。
 */
@Service
public class AccountProfileService {

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
        return new AccountProfileResponse(
            account.getId(),
            account.getUsername(),
            displayName,
            role.getCode(),
            account.getMoney(),
            account.getLoyaltyPoints() == null ? 0 : account.getLoyaltyPoints(),
            normalizeResponseValue(account.getContactNumber()),
            normalizeResponseValue(account.getAddress()),
            normalizeResponseValue(account.getCompanyPhone()),
            normalizeResponseValue(account.getCompanyAddress()),
            normalizeResponseValue(account.getCompanyDescription())
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

        account.setContactNumber(normalizeOptional(request.getContactNumber()));
        account.setAddress(normalizeOptional(request.getAddress()));

        if (AccountRole.COMPANY.getLabel().equals(account.getUserType())) {
            account.setCompanyPhone(normalizeOptional(request.getCompanyPhone()));
            account.setCompanyAddress(normalizeOptional(request.getCompanyAddress()));
            account.setCompanyDescription(normalizeOptional(request.getCompanyDescription()));
        } else {
            account.setCompanyPhone(null);
            account.setCompanyAddress(null);
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

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeResponseValue(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
