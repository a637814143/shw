package com.example.housekeeping.service;

import com.example.housekeeping.dto.AuthResponse;
import com.example.housekeeping.dto.CurrentUserResponse;
import com.example.housekeeping.entity.Admin;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.AdminRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String ROLE_USER = "USER";
    private static final String ROLE_PROVIDER = "PROVIDER";
    private static final String ROLE_ADMIN = "ADMIN";

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
     * 普通用户登录
     */
    public AuthResponse userLogin(String username, String password) {
        User user = userRepository.findByUsernameAndStatus(normalizeUsername(username), 1)
            .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        verifyPassword(password, user.getPassword());
        return buildAuthResponse(user.getId(), user.getUsername(), ROLE_USER, toUserProfile(user));
    }

    /**
     * 家政服务人员登录
     */
    public AuthResponse providerLogin(String username, String password) {
        ServiceProvider provider = serviceProviderRepository.findByUsernameAndStatus(normalizeUsername(username), 1)
            .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        verifyPassword(password, provider.getPassword());
        return buildAuthResponse(provider.getId(), provider.getUsername(), ROLE_PROVIDER, toProviderProfile(provider));
    }

    /**
     * 管理员登录
     */
    public AuthResponse adminLogin(String username, String password) {
        Admin admin = adminRepository.findByUsernameAndStatus(normalizeUsername(username), 1)
            .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        verifyPassword(password, admin.getPassword());
        return buildAuthResponse(admin.getId(), admin.getUsername(), ROLE_ADMIN, toAdminProfile(admin));
    }

    /**
     * 普通用户注册
     */
    @Transactional
    public Map<String, Object> userRegister(String username, String password, String phone) {
        String normalizedUsername = normalizeUsername(username);
        String normalizedPhone = normalizePhone(phone);
        validatePasswordStrength(password);

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(normalizedPhone);
        user.setStatus(1);

        User saved = userRepository.save(user);
        return toUserProfile(saved);
    }

    /**
     * 服务人员注册
     */
    @Transactional
    public Map<String, Object> providerRegister(String username, String password, String phone) {
        String normalizedUsername = normalizeUsername(username);
        String normalizedPhone = normalizePhone(phone);
        validatePasswordStrength(password);

        if (serviceProviderRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (serviceProviderRepository.existsByPhone(normalizedPhone)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        ServiceProvider provider = new ServiceProvider();
        provider.setUsername(normalizedUsername);
        provider.setPassword(passwordEncoder.encode(password));
        provider.setPhone(normalizedPhone);
        provider.setStatus(1);
        provider.setCertificationStatus(0);

        ServiceProvider saved = serviceProviderRepository.save(provider);
        return toProviderProfile(saved);
    }

    /**
     * 管理员注册
     */
    @Transactional
    public Map<String, Object> adminRegister(String username, String password, String phone) {
        String normalizedUsername = normalizeUsername(username);
        String normalizedPhone = normalizePhone(phone);
        validatePasswordStrength(password);

        if (adminRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (adminRepository.existsByPhone(normalizedPhone)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(normalizedUsername);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setPhone(normalizedPhone);
        admin.setStatus(1);

        Admin saved = adminRepository.save(admin);
        return toAdminProfile(saved);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String userType, String oldPassword, String newPassword) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        String normalizedType = Optional.ofNullable(userType)
            .map(type -> type.trim().toUpperCase(Locale.ROOT))
            .orElseThrow(() -> new IllegalArgumentException("用户类型不能为空"));
        validatePasswordStrength(newPassword);

        switch (normalizedType) {
            case ROLE_USER -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                verifyPassword(oldPassword, user.getPassword());
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            }
            case ROLE_PROVIDER -> {
                ServiceProvider provider = serviceProviderRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("服务人员不存在"));
                verifyPassword(oldPassword, provider.getPassword());
                provider.setPassword(passwordEncoder.encode(newPassword));
                serviceProviderRepository.save(provider);
            }
            case ROLE_ADMIN -> {
                Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("管理员不存在"));
                verifyPassword(oldPassword, admin.getPassword());
                admin.setPassword(passwordEncoder.encode(newPassword));
                adminRepository.save(admin);
            }
            default -> throw new IllegalArgumentException("用户类型错误");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    public CurrentUserResponse loadCurrentUser(Long userId, String userType) {
        if (userId == null || !StringUtils.hasText(userType)) {
            throw new IllegalArgumentException("未找到登录信息");
        }
        String normalizedType = userType.trim().toUpperCase(Locale.ROOT);
        return switch (normalizedType) {
            case ROLE_USER -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                yield new CurrentUserResponse(user.getId(), user.getUsername(), ROLE_USER, toUserProfile(user));
            }
            case ROLE_PROVIDER -> {
                ServiceProvider provider = serviceProviderRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("服务人员不存在"));
                yield new CurrentUserResponse(provider.getId(), provider.getUsername(), ROLE_PROVIDER, toProviderProfile(provider));
            }
            case ROLE_ADMIN -> {
                Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("管理员不存在"));
                yield new CurrentUserResponse(admin.getId(), admin.getUsername(), ROLE_ADMIN, toAdminProfile(admin));
            }
            default -> throw new IllegalArgumentException("不支持的用户类型");
        };
    }

    private AuthResponse buildAuthResponse(Long userId, String username, String userType, Map<String, Object> profile) {
        String token = jwtUtil.generateToken(userId, username, userType);
        long expiresAt = System.currentTimeMillis() + Optional.ofNullable(jwtUtil.getExpiration()).orElse(3600_000L);
        return new AuthResponse(token, expiresAt, userType, profile);
    }

    private void verifyPassword(String rawPassword, String encodedPassword) {
        String password = Optional.ofNullable(rawPassword).orElse("");
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
    }

    private void validatePasswordStrength(String password) {
        String value = Optional.ofNullable(password).orElse("");
        if (value.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("密码长度至少为" + MIN_PASSWORD_LENGTH + "位");
        }
    }

    private String normalizeUsername(String username) {
        String value = Optional.ofNullable(username).map(String::trim).orElse("");
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return value;
    }

    private String normalizePhone(String phone) {
        String value = Optional.ofNullable(phone).map(String::trim).orElse("");
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("请输入有效的手机号");
        }
        return value;
    }

    private Map<String, Object> toUserProfile(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("realName", user.getRealName());
        profile.put("phone", user.getPhone());
        profile.put("email", user.getEmail());
        profile.put("avatar", user.getAvatar());
        profile.put("gender", user.getGender());
        profile.put("birthday", user.getBirthday());
        profile.put("address", user.getAddress());
        profile.put("balance", user.getBalance());
        profile.put("status", user.getStatus());
        profile.put("createTime", user.getCreateTime());
        profile.put("updateTime", user.getUpdateTime());
        return profile;
    }

    private Map<String, Object> toProviderProfile(ServiceProvider provider) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", provider.getId());
        profile.put("username", provider.getUsername());
        profile.put("realName", provider.getRealName());
        profile.put("phone", provider.getPhone());
        profile.put("email", provider.getEmail());
        profile.put("avatar", provider.getAvatar());
        profile.put("gender", provider.getGender());
        profile.put("birthday", provider.getBirthday());
        profile.put("address", provider.getAddress());
        profile.put("idCard", provider.getIdCard());
        profile.put("workExperience", provider.getWorkExperience());
        profile.put("skills", provider.getSkills());
        profile.put("certificationStatus", provider.getCertificationStatus());
        profile.put("certificationTime", provider.getCertificationTime());
        profile.put("balance", provider.getBalance());
        profile.put("status", provider.getStatus());
        profile.put("createTime", provider.getCreateTime());
        profile.put("updateTime", provider.getUpdateTime());
        return profile;
    }

    private Map<String, Object> toAdminProfile(Admin admin) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", admin.getId());
        profile.put("username", admin.getUsername());
        profile.put("realName", admin.getRealName());
        profile.put("phone", admin.getPhone());
        profile.put("email", admin.getEmail());
        profile.put("avatar", admin.getAvatar());
        profile.put("status", admin.getStatus());
        profile.put("createTime", admin.getCreateTime());
        profile.put("updateTime", admin.getUpdateTime());
        return profile;
    }
}
