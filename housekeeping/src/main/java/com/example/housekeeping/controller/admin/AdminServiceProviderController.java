package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.repository.ServiceProviderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/providers")
@RequiredArgsConstructor
public class AdminServiceProviderController {

    private final ServiceProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ApiResponse<Page<ServiceProvider>> list(Pageable pageable) {
        return ApiResponse.success(providerRepository.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceProvider> detail(@PathVariable Long id) {
        return ApiResponse.success(providerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务者不存在")));
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceProvider> update(@PathVariable Long id, @RequestBody @Valid ServiceProvider request) {
        ServiceProvider provider = providerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        provider.setRealName(request.getRealName());
        provider.setPhone(request.getPhone());
        provider.setEmail(request.getEmail());
        provider.setAvatar(request.getAvatar());
        provider.setGender(request.getGender());
        provider.setBirthday(request.getBirthday());
        provider.setAddress(request.getAddress());
        provider.setIdCard(request.getIdCard());
        provider.setWorkExperience(request.getWorkExperience());
        provider.setSkills(request.getSkills());
        provider.setCertificationStatus(request.getCertificationStatus());
        provider.setCertificationTime(request.getCertificationTime());
        provider.setBalance(request.getBalance());
        provider.setStatus(request.getStatus());
        return ApiResponse.success(providerRepository.save(provider));
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestParam String password) {
        ServiceProvider provider = providerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        provider.setPassword(passwordEncoder.encode(password));
        providerRepository.save(provider);
        return ApiResponse.success();
    }
}
