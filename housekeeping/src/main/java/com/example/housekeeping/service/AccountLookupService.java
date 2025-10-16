package com.example.housekeeping.service;

import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.repository.UserAllRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 帮助根据当前登录态或用户名查询账号信息。
 */
@Service
public class AccountLookupService {

    @Autowired
    private UserAllRepository userAllRepository;

    public UserAll getByUsername(String username) {
        return userAllRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("账号不存在: " + username));
    }

    public UserAll getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("未找到登录信息");
        }
        return getByUsername(authentication.getName());
    }
}
