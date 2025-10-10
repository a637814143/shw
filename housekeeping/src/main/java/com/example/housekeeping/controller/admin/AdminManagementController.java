package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.Admin;
import com.example.housekeeping.repository.AdminRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/admins")
@RequiredArgsConstructor
public class AdminManagementController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ApiResponse<Page<Admin>> list(Pageable pageable) {
        return ApiResponse.success(adminRepository.findAll(pageable));
    }

    @PostMapping
    public ApiResponse<Admin> create(@RequestBody @Valid Admin request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return ApiResponse.success(adminRepository.save(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Admin> update(@PathVariable Long id, @RequestBody @Valid Admin request) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        admin.setRealName(request.getRealName());
        admin.setPhone(request.getPhone());
        admin.setEmail(request.getEmail());
        admin.setAvatar(request.getAvatar());
        admin.setStatus(request.getStatus());
        return ApiResponse.success(adminRepository.save(admin));
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestParam String password) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        admin.setPassword(passwordEncoder.encode(password));
        adminRepository.save(admin);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminRepository.deleteById(id);
        return ApiResponse.success();
    }
}
