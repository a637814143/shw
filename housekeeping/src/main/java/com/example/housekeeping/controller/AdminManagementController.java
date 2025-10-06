package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.Admin;
import com.example.housekeeping.dto.RegisterRequest;
import com.example.housekeeping.service.AuthService;
import com.example.housekeeping.repository.AdminRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admins")
public class AdminManagementController {

    private final AdminRepository adminRepository;
    private final AuthService authService;

    public AdminManagementController(AdminRepository adminRepository, AuthService authService) {
        this.adminRepository = adminRepository;
        this.authService = authService;
    }

    @GetMapping
    public List<Admin> listAdmins() {
        return adminRepository.findAll();
    }

    @GetMapping("/{id}")
    public Admin getAdmin(@PathVariable Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "管理员不存在"));
    }

    @PostMapping
    public Admin createAdmin(@RequestBody RegisterRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请求参数不能为空");
        }
        RegisterRequest adminRequest = new RegisterRequest(
                request.username(),
                request.password(),
                com.example.housekeeping.domain.enums.RoleType.ADMIN,
                request.fullName(),
                request.phone(),
                request.email(),
                request.address());
        authService.register(adminRequest);
        return adminRepository.findByUsername(adminRequest.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "管理员创建失败"));
    }

    @PutMapping("/{id}/enabled")
    public Admin updateAdminStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        Admin admin = getAdmin(id);
        admin.setEnabled(enabled);
        return admin;
    }
}
