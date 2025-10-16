package com.example.housekeeping.service;

import com.example.housekeeping.dto.UpdatePasswordRequest;
import com.example.housekeeping.dto.UpdateWalletRequest;
import com.example.housekeeping.dto.UserAccountResponse;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.UserAllRepository;
import com.example.housekeeping.util.LegacyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员管理其他账号的功能。
 */
@Service
public class AdminAccountService {

    @Autowired
    private UserAllRepository userAllRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private LegacyPasswordEncoder legacyPasswordEncoder;

    @Transactional(readOnly = true)
    public List<UserAccountResponse> listUsers() {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        return userAllRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public UserAccountResponse updateWallet(Long userId, UpdateWalletRequest request) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        UserAll target = userAllRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        target.setMoney(request.getMoney());
        return mapToResponse(userAllRepository.save(target));
    }

    @Transactional
    public UserAccountResponse updatePassword(Long userId, UpdatePasswordRequest request) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        UserAll target = userAllRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        target.setPasswd(legacyPasswordEncoder.encode(request.getPassword()));
        return mapToResponse(userAllRepository.save(target));
    }

    private void ensureAdmin(UserAll account) {
        if (!AccountRole.ADMIN.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("仅管理员可操作");
        }
    }

    private UserAccountResponse mapToResponse(UserAll user) {
        AccountRole role = AccountRole.fromValue(user.getUserType());
        return new UserAccountResponse(
            user.getId(),
            user.getUsername(),
            role.getCode(),
            user.getMoney()
        );
    }
}
