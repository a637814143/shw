package com.example.housekeeping.service;

import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户服务
 */
@Service
public class UserService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据ID查找用户
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名查找用户
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据手机号查找用户
     */
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    /**
     * 保存用户
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long userId, User updateUser) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (updateUser.getRealName() != null) {
            user.setRealName(updateUser.getRealName());
        }
        if (updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.getAvatar() != null) {
            user.setAvatar(updateUser.getAvatar());
        }
        if (updateUser.getGender() != null) {
            user.setGender(updateUser.getGender());
        }
        if (updateUser.getBirthday() != null) {
            user.setBirthday(updateUser.getBirthday());
        }
        if (updateUser.getAddress() != null) {
            user.setAddress(updateUser.getAddress());
        }
        if (updateUser.getPhone() != null && !updateUser.getPhone().equals(user.getPhone())) {
            String phone = updateUser.getPhone().trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new RuntimeException("手机号格式不正确");
            }
            if (userRepository.existsByPhone(phone)) {
                throw new RuntimeException("手机号已被使用");
            }
            user.setPhone(phone);
        }

        return userRepository.save(user);
    }

    /**
     * 分页查询用户
     */
    public Page<User> findUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 根据状态分页查询用户
     */
    public Page<User> findByStatus(Integer status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    /**
     * 根据关键词搜索用户
     */
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findByKeyword(keyword, pageable);
    }

    /**
     * 启用/禁用用户
     */
    public void updateUserStatus(Long userId, Integer status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setStatus(status);
        userRepository.save(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }

    /**
     * 统计用户数量
     */
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * 统计启用用户数量
     */
    public long countActiveUsers() {
        return userRepository.findByStatus(1, Pageable.unpaged()).getTotalElements();
    }
}
