package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.Admin;
import com.example.housekeeping.domain.entity.ServiceProvider;
import com.example.housekeeping.domain.entity.UserAccount;
import com.example.housekeeping.domain.enums.RoleType;
import com.example.housekeeping.dto.AuthRequest;
import com.example.housekeeping.dto.AuthResponse;
import com.example.housekeeping.dto.RegisterRequest;
import com.example.housekeeping.dto.UpdatePasswordRequest;
import com.example.housekeeping.dto.UpdateProfileRequest;
import com.example.housekeeping.repository.AdminRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.service.AuthService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final UserAccountRepository userAccountRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(AuthRequest request) {
        return switch (request.role()) {
            case ADMIN -> adminRepository.findByUsername(request.username())
                    .filter(admin -> passwordEncoder.matches(request.password(), admin.getPassword()))
                    .map(admin -> {
                        admin.setLastLoginAt(LocalDateTime.now());
                        return buildResponse(admin.getId(), RoleType.ADMIN, admin.getUsername(), admin.getFullName());
                    })
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "管理员账号或密码不正确"));
            case USER -> userAccountRepository.findByUsername(request.username())
                    .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                    .map(user -> {
                        if (!user.isEnabled()) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "账号已被禁用");
                        }
                        user.setLastLoginAt(LocalDateTime.now());
                        return buildResponse(user.getId(), RoleType.USER, user.getUsername(), user.getFullName());
                    })
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户账号或密码不正确"));
            case PROVIDER -> serviceProviderRepository.findByUsername(request.username())
                    .filter(provider -> passwordEncoder.matches(request.password(), provider.getPassword()))
                    .map(provider -> {
                        if (!provider.isEnabled()) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "账号已被禁用");
                        }
                        provider.setLastLoginAt(LocalDateTime.now());
                        return buildResponse(provider.getId(), RoleType.PROVIDER, provider.getUsername(), provider.getFullName());
                    })
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "服务者账号或密码不正确"));
        };
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        boolean usernameExists = adminRepository.findByUsername(request.username()).isPresent()
                || userAccountRepository.findByUsername(request.username()).isPresent()
                || serviceProviderRepository.findByUsername(request.username()).isPresent();
        if (usernameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "账号已存在");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        LocalDateTime now = LocalDateTime.now();
        return switch (request.role()) {
            case ADMIN -> {
                Admin admin = new Admin();
                admin.setUsername(request.username());
                admin.setPassword(encodedPassword);
                admin.setFullName(Optional.ofNullable(request.fullName()).orElse("管理员"));
                admin.setEmail(request.email());
                admin.setPhone(request.phone());
                admin.setLastLoginAt(now);
                Admin saved = adminRepository.save(admin);
                yield buildResponse(saved.getId(), RoleType.ADMIN, saved.getUsername(), saved.getFullName());
            }
            case USER -> {
                UserAccount user = new UserAccount();
                user.setUsername(request.username());
                user.setPassword(encodedPassword);
                user.setFullName(request.fullName());
                user.setEmail(request.email());
                user.setPhone(request.phone());
                user.setAddress(request.address());
                user.setLastLoginAt(now);
                UserAccount saved = userAccountRepository.save(user);
                yield buildResponse(saved.getId(), RoleType.USER, saved.getUsername(), saved.getFullName());
            }
            case PROVIDER -> {
                ServiceProvider provider = new ServiceProvider();
                provider.setUsername(request.username());
                provider.setPassword(encodedPassword);
                provider.setFullName(request.fullName());
                provider.setEmail(request.email());
                provider.setPhone(request.phone());
                provider.setServiceArea(request.address());
                provider.setLastLoginAt(now);
                ServiceProvider saved = serviceProviderRepository.save(provider);
                yield buildResponse(saved.getId(), RoleType.PROVIDER, saved.getUsername(), saved.getFullName());
            }
        };
    }

    @Override
    public void updatePassword(RoleType role, Long id, UpdatePasswordRequest request) {
        switch (role) {
            case ADMIN -> adminRepository.findById(id).ifPresentOrElse(admin -> {
                ensurePasswordMatches(request.oldPassword(), admin.getPassword());
                admin.setPassword(passwordEncoder.encode(request.newPassword()));
            }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "管理员不存在"); });
            case USER -> userAccountRepository.findById(id).ifPresentOrElse(user -> {
                ensurePasswordMatches(request.oldPassword(), user.getPassword());
                user.setPassword(passwordEncoder.encode(request.newPassword()));
            }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"); });
            case PROVIDER -> serviceProviderRepository.findById(id).ifPresentOrElse(provider -> {
                ensurePasswordMatches(request.oldPassword(), provider.getPassword());
                provider.setPassword(passwordEncoder.encode(request.newPassword()));
            }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "服务者不存在"); });
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的角色类型");
        }
    }

    @Override
    public void updateProfile(RoleType role, Long id, UpdateProfileRequest request) {
        switch (role) {
            case ADMIN -> adminRepository.findById(id).ifPresentOrElse(admin -> {
                applyCommonProfile(admin, request);
            }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "管理员不存在"); });
            case USER -> userAccountRepository.findById(id).ifPresentOrElse(user -> {
                applyCommonProfile(user, request);
                user.setAddress(request.address());
            }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"); });
            case PROVIDER -> serviceProviderRepository.findById(id).ifPresentOrElse(provider -> {
                applyCommonProfile(provider, request);
                provider.setBiography(request.biography());
                provider.setServiceArea(request.serviceArea());
                provider.setSkills(request.skills());
            }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "服务者不存在"); });
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的角色类型");
        }
    }

    private void ensurePasswordMatches(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "原密码不正确");
        }
    }

    private void applyCommonProfile(Admin admin, UpdateProfileRequest request) {
        admin.setFullName(request.fullName());
        admin.setPhone(request.phone());
        admin.setEmail(request.email());
        admin.setAvatarUrl(request.avatarUrl());
    }

    private void applyCommonProfile(UserAccount user, UpdateProfileRequest request) {
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setAvatarUrl(request.avatarUrl());
    }

    private void applyCommonProfile(ServiceProvider provider, UpdateProfileRequest request) {
        provider.setFullName(request.fullName());
        provider.setPhone(request.phone());
        provider.setEmail(request.email());
        provider.setAvatarUrl(request.avatarUrl());
    }

    private AuthResponse buildResponse(Long id, RoleType role, String username, String displayName) {
        return new AuthResponse(id, role, username, displayName, LocalDateTime.now());
    }
}
