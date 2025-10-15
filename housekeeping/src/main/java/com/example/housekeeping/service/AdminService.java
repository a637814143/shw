package com.example.housekeeping.service;

import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.Admin;
import com.example.housekeeping.repository.AdminRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 管理员服务类
 */
@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 管理员登录
     */
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Admin> adminOpt = adminRepository.findByUsernameAndStatus(loginRequest.getUsername(), 1);
        
        if (adminOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        Admin admin = adminOpt.get();
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN");
        
        return new LoginResponse(token, admin.getUsername(), admin.getRealName(), "ADMIN");
    }
    
    /**
     * 根据用户名获取管理员信息
     */
    public Admin getAdminByUsername(String username) {
        return adminRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
    }
    
    /**
     * 修改密码
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        Admin admin = getAdminByUsername(username);
        
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }
    
    /**
     * 更新个人信息
     */
    public void updateProfile(String username, String realName, String phone, String email) {
        Admin admin = getAdminByUsername(username);
        admin.setRealName(realName);
        admin.setPhone(phone);
        admin.setEmail(email);
        adminRepository.save(admin);
    }
}
