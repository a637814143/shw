package com.example.housekeeping.service;

import com.example.housekeeping.entity.Admin;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.AdminRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserRepository;
import com.example.housekeeping.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, 
                      ServiceProviderRepository serviceProviderRepository,
                      AdminRepository adminRepository,
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> userLogin(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameAndStatus(username, 1);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        result.put("userType", "USER");
        
        return result;
    }

    /**
     * 服务者登录
     */
    public Map<String, Object> providerLogin(String username, String password) {
        Optional<ServiceProvider> providerOpt = serviceProviderRepository.findByUsernameAndStatus(username, 1);
        if (providerOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        ServiceProvider provider = providerOpt.get();
        if (!passwordEncoder.matches(password, provider.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(provider.getId(), provider.getUsername(), "PROVIDER");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", provider);
        result.put("userType", "PROVIDER");
        
        return result;
    }

    /**
     * 管理员登录
     */
    public Map<String, Object> adminLogin(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsernameAndStatus(username, 1);
        if (adminOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        Admin admin = adminOpt.get();
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "ADMIN");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", admin);
        result.put("userType", "ADMIN");
        
        return result;
    }

    /**
     * 用户注册
     */
    public User userRegister(String username, String password, String phone) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("手机号已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setStatus(1);

        return userRepository.save(user);
    }

    /**
     * 服务者注册
     */
    public ServiceProvider providerRegister(String username, String password, String phone) {
        // 检查用户名是否已存在
        if (serviceProviderRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (serviceProviderRepository.existsByPhone(phone)) {
            throw new RuntimeException("手机号已存在");
        }

        ServiceProvider provider = new ServiceProvider();
        provider.setUsername(username);
        provider.setPassword(passwordEncoder.encode(password));
        provider.setPhone(phone);
        provider.setStatus(1);
        provider.setCertificationStatus(0);

        return serviceProviderRepository.save(provider);
    }

    /**
     * 管理员注册
     */
    public Admin adminRegister(String username, String password, String phone) {
        // 检查用户名是否已存在
        if (adminRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (adminRepository.existsByPhone(phone)) {
            throw new RuntimeException("手机号已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setPhone(phone);
        admin.setStatus(1);

        return adminRepository.save(admin);
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String userType, String oldPassword, String newPassword) {
        switch (userType) {
            case "USER":
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
                if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                    throw new RuntimeException("原密码错误");
                }
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                break;
                
            case "PROVIDER":
                ServiceProvider provider = serviceProviderRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("服务者不存在"));
                if (!passwordEncoder.matches(oldPassword, provider.getPassword())) {
                    throw new RuntimeException("原密码错误");
                }
                provider.setPassword(passwordEncoder.encode(newPassword));
                serviceProviderRepository.save(provider);
                break;
                
            case "ADMIN":
                Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("管理员不存在"));
                if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
                    throw new RuntimeException("原密码错误");
                }
                admin.setPassword(passwordEncoder.encode(newPassword));
                adminRepository.save(admin);
                break;
                
            default:
                throw new RuntimeException("用户类型错误");
        }
    }
}
