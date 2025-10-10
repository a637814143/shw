package com.example.housekeeping.service.impl;

import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.LoginRequest;
import com.example.housekeeping.dto.request.RegisterRequest;
import com.example.housekeeping.dto.response.LoginResponse;
import com.example.housekeeping.entity.Admin;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.AdminRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserRepository;
import com.example.housekeeping.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse loginAdmin(LoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return new LoginResponse(admin.getId(), admin.getUsername(), "ADMIN");
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse loginProvider(LoginRequest request) {
        ServiceProvider provider = serviceProviderRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("家政服务者不存在"));
        if (!passwordEncoder.matches(request.getPassword(), provider.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return new LoginResponse(provider.getId(), provider.getUsername(), "PROVIDER");
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return new LoginResponse(user.getId(), user.getUsername(), "USER");
    }

    @Override
    public LoginResponse registerUser(RegisterRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new BusinessException("用户名已存在");
        });
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setBalance(BigDecimal.ZERO);
        User saved = userRepository.save(user);
        return new LoginResponse(saved.getId(), saved.getUsername(), "USER");
    }

    @Override
    public LoginResponse registerProvider(RegisterRequest request) {
        serviceProviderRepository.findByUsername(request.getUsername()).ifPresent(p -> {
            throw new BusinessException("用户名已存在");
        });
        ServiceProvider provider = new ServiceProvider();
        provider.setUsername(request.getUsername());
        provider.setPassword(passwordEncoder.encode(request.getPassword()));
        provider.setPhone(request.getPhone());
        provider.setEmail(request.getEmail());
        provider.setStatus(1);
        provider.setCertificationStatus(0);
        provider.setBalance(BigDecimal.ZERO);
        ServiceProvider saved = serviceProviderRepository.save(provider);
        return new LoginResponse(saved.getId(), saved.getUsername(), "PROVIDER");
    }
}
