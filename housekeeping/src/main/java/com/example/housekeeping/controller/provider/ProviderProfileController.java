package com.example.housekeeping.controller.provider;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.repository.ServiceProviderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/profile")
@RequiredArgsConstructor
public class ProviderProfileController {

    private final ServiceProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;

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
        provider.setWorkExperience(request.getWorkExperience());
        provider.setSkills(request.getSkills());
        return ApiResponse.success(providerRepository.save(provider));
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> changePassword(@PathVariable Long id,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        ServiceProvider provider = providerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        if (!passwordEncoder.matches(oldPassword, provider.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        provider.setPassword(passwordEncoder.encode(newPassword));
        providerRepository.save(provider);
        return ApiResponse.success();
    }
}
