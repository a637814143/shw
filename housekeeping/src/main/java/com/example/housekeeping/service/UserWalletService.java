package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountProfileResponse;
import com.example.housekeeping.dto.PointsExchangeRequest;
import com.example.housekeeping.dto.WalletRechargeRequest;
import com.example.housekeeping.entity.AccountTransaction;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.AccountTransactionType;
import com.example.housekeeping.repository.AccountTransactionRepository;
import com.example.housekeeping.repository.UserAllRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * 用户钱包操作。
 */
@Service
public class UserWalletService {

    private static final BigDecimal POINT_TO_CNY_RATIO = new BigDecimal("0.2"); // 5:1 => 1 point = 0.2 CNY

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private UserAllRepository userAllRepository;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private AccountProfileService accountProfileService;

    @Transactional
    public AccountProfileResponse recharge(WalletRechargeRequest request) {
        UserAll user = ensureUser();
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额不合法");
        }

        user.setMoney(user.getMoney().add(amount));
        UserAll saved = userAllRepository.save(user);

        recordTransaction(user, AccountTransactionType.RECHARGE, amount, "用户自助充值");
        return accountProfileService.buildResponse(saved);
    }

    @Transactional
    public AccountProfileResponse exchangePoints(PointsExchangeRequest request) {
        UserAll user = ensureUser();
        int points = request.getPoints();
        if (points <= 0) {
            throw new RuntimeException("兑换积分需大于0");
        }
        if (points % 5 != 0) {
            throw new RuntimeException("请以5的倍数兑换，当前比例为5积分兑换1元");
        }
        int currentPoints = user.getLoyaltyPoints() == null ? 0 : user.getLoyaltyPoints();
        if (currentPoints < points) {
            throw new RuntimeException("积分不足");
        }

        BigDecimal amount = new BigDecimal(points).multiply(POINT_TO_CNY_RATIO).setScale(2, RoundingMode.HALF_UP);
        user.setLoyaltyPoints(currentPoints - points);
        user.setMoney(user.getMoney().add(amount));
        UserAll saved = userAllRepository.save(user);

        recordTransaction(user, AccountTransactionType.ADJUST, amount, "积分兑换余额");
        return accountProfileService.buildResponse(saved);
    }

    private UserAll ensureUser() {
        UserAll user = accountLookupService.getCurrentAccount();
        if (!AccountRole.USER.getLabel().equals(user.getUserType())) {
            throw new RuntimeException("仅普通用户可操作钱包");
        }
        return user;
    }

    private void recordTransaction(UserAll user, AccountTransactionType type, BigDecimal amount, String note) {
        AccountTransaction txn = new AccountTransaction();
        txn.setUser(user);
        txn.setType(type);
        txn.setAmount(amount);
        txn.setNote(note);
        txn.setCreatedAt(Instant.now());
        accountTransactionRepository.save(txn);
    }

}
