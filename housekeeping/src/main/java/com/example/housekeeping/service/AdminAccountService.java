package com.example.housekeeping.service;

import com.example.housekeeping.dto.UpdateLoyaltyRequest;
import com.example.housekeeping.dto.UpdatePasswordRequest;
import com.example.housekeeping.dto.UpdateWalletRequest;
import com.example.housekeeping.dto.UserAccountResponse;
import com.example.housekeeping.entity.AccountTransaction;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.AccountTransactionType;
import com.example.housekeeping.repository.AccountTransactionRepository;
import com.example.housekeeping.repository.UserAllRepository;
import com.example.housekeeping.util.LegacyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Transactional(readOnly = true)
    public List<UserAccountResponse> listUsers() {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        return userAllRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserAccountResponse> listCompanies() {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        return userAllRepository.findByUserType(AccountRole.COMPANY.getLabel()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserAccountResponse> listAdmins() {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        return userAllRepository.findByUserType(AccountRole.ADMIN.getLabel()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public UserAccountResponse updateWallet(Long userId, UpdateWalletRequest request) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        UserAll target = userAllRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        BigDecimal before = target.getMoney();
        BigDecimal desired = request.getMoney();
        if (desired == null) {
            desired = BigDecimal.ZERO;
        }
        target.setMoney(desired);
        UserAll saved = userAllRepository.save(target);

        if (before != null) {
            BigDecimal delta = desired.subtract(before);
            if (delta.compareTo(BigDecimal.ZERO) != 0) {
                AccountTransaction txn = new AccountTransaction();
                txn.setUser(saved);
                txn.setType(AccountTransactionType.ADJUST);
                txn.setAmount(delta);
                txn.setNote("管理员 " + admin.getUsername() + " 调整余额");
                txn.setCreatedAt(Instant.now());
                accountTransactionRepository.save(txn);
            }
        }

        return mapToResponse(saved);
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

    @Transactional
    public UserAccountResponse updateLoyalty(Long userId, UpdateLoyaltyRequest request) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureAdmin(admin);
        UserAll target = userAllRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        Integer points = request.getLoyaltyPoints();
        target.setLoyaltyPoints(points == null ? 0 : points);
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
            user.getMoney(),
            user.getLoyaltyPoints() == null ? 0 : user.getLoyaltyPoints()
        );
    }
}
