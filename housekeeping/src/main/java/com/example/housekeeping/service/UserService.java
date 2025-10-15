package com.example.housekeeping.service;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * 用户管理服务类
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 分页查询用户
     */
    public PageResult<User> getUsers(int page, int size, String username, String realName, String phone, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<User> userPage = userRepository.findByConditions(username, realName, phone, status, pageable);
        return PageResult.of(userPage);
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    /**
     * 创建用户
     */
    public User createUser(User user) {
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
     * 更新用户
     */
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        
        // 检查用户名是否已被其他用户使用
        if (userRepository.existsByUsernameAndIdNot(user.getUsername(), id)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已被其他用户使用
        if (userRepository.existsByPhoneAndIdNot(user.getPhone(), id)) {
            throw new RuntimeException("手机号已存在");
        }
        
        existingUser.setUsername(user.getUsername());
        existingUser.setRealName(user.getRealName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setStatus(user.getStatus());
        
        // 如果提供了新密码，则更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * 启用/禁用用户
     */
    public void toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        userRepository.save(user);
    }
    
    /**
     * 重置用户密码
     */
    public void resetUserPassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
