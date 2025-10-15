package com.example.housekeeping.service;

import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.UserRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户认证服务类
 */
@Service
public class UserAuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsernameAndStatus(loginRequest.getUsername(), 1);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误，或账号已被禁用");
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), "USER");
        
        return new LoginResponse(token, user.getUsername(), user.getRealName(), "USER");
    }
    
    /**
     * 根据用户名获取用户信息
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("用户不存在或已被禁用"));
    }
    
    /**
     * 修改密码
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = getUserByUsername(username);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * 更新个人信息
     */
    public void updateProfile(String username, String realName, String phone, String email, String avatar) {
        User user = getUserByUsername(username);
        
        // 检查手机号是否已被其他用户使用
        if (userRepository.existsByPhoneAndIdNot(phone, user.getId())) {
            throw new RuntimeException("手机号已存在");
        }
        
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAvatar(avatar);
        
        userRepository.save(user);
    }
}

