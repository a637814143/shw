package com.example.housekeeping.service;

import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.Provider;
import com.example.housekeeping.repository.ProviderRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 家政人员认证服务类
 */
@Service
public class ProviderAuthService {
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 家政人员注册
     */
    public Provider register(Provider provider) {
        // 检查用户名是否已存在
        if (providerRepository.existsByUsername(provider.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (providerRepository.existsByPhone(provider.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 加密密码
        provider.setPassword(passwordEncoder.encode(provider.getPassword()));
        
        // 设置初始状态为待审核
        provider.setStatus(0);
        
        return providerRepository.save(provider);
    }
    
    /**
     * 家政人员登录
     */
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Provider> providerOpt = providerRepository.findByUsernameAndStatus(loginRequest.getUsername(), 1);
        
        if (providerOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误，或账号未通过审核");
        }
        
        Provider provider = providerOpt.get();
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), provider.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(provider.getUsername(), "PROVIDER");
        
        return new LoginResponse(token, provider.getUsername(), provider.getRealName(), "PROVIDER");
    }
    
    /**
     * 根据用户名获取家政人员信息
     */
    public Provider getProviderByUsername(String username) {
        return providerRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("家政人员不存在或未通过审核"));
    }
    
    /**
     * 修改密码
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        Provider provider = getProviderByUsername(username);
        
        if (!passwordEncoder.matches(oldPassword, provider.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        provider.setPassword(passwordEncoder.encode(newPassword));
        providerRepository.save(provider);
    }
    
    /**
     * 更新个人信息
     */
    public void updateProfile(String username, String realName, String phone, String email, String avatar) {
        Provider provider = getProviderByUsername(username);
        
        // 检查手机号是否已被其他家政人员使用
        if (providerRepository.existsByPhoneAndIdNot(phone, provider.getId())) {
            throw new RuntimeException("手机号已存在");
        }
        
        provider.setRealName(realName);
        provider.setPhone(phone);
        provider.setEmail(email);
        provider.setAvatar(avatar);
        
        providerRepository.save(provider);
    }
    
    /**
     * 提交认证材料
     */
    public void submitCertification(String username, String idCard, String idCardFront, 
                                   String idCardBack, String certificate, String experience) {
        Provider provider = getProviderByUsername(username);
        
        provider.setIdCard(idCard);
        provider.setIdCardFront(idCardFront);
        provider.setIdCardBack(idCardBack);
        provider.setCertificate(certificate);
        provider.setExperience(experience);
        provider.setStatus(0); // 重新提交审核
        
        providerRepository.save(provider);
    }
}

