package com.example.housekeeping.service;

import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.repository.ServiceProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 家政服务者服务
 */
@Service
public class ServiceProviderService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private final ServiceProviderRepository serviceProviderRepository;

    public ServiceProviderService(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    /**
     * 根据ID查找服务者
     */
    public Optional<ServiceProvider> findById(Long id) {
        return serviceProviderRepository.findById(id);
    }

    /**
     * 根据用户名查找服务者
     */
    public Optional<ServiceProvider> findByUsername(String username) {
        return serviceProviderRepository.findByUsername(username);
    }

    /**
     * 根据手机号查找服务者
     */
    public Optional<ServiceProvider> findByPhone(String phone) {
        return serviceProviderRepository.findByPhone(phone);
    }

    /**
     * 保存服务者
     */
    public ServiceProvider save(ServiceProvider serviceProvider) {
        return serviceProviderRepository.save(serviceProvider);
    }

    /**
     * 更新服务者信息
     */
    public ServiceProvider updateProvider(Long providerId, ServiceProvider updateProvider) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
            .orElseThrow(() -> new RuntimeException("服务者不存在"));

        if (updateProvider.getRealName() != null) {
            provider.setRealName(updateProvider.getRealName());
        }
        if (updateProvider.getEmail() != null) {
            provider.setEmail(updateProvider.getEmail());
        }
        if (updateProvider.getAvatar() != null) {
            provider.setAvatar(updateProvider.getAvatar());
        }
        if (updateProvider.getGender() != null) {
            provider.setGender(updateProvider.getGender());
        }
        if (updateProvider.getBirthday() != null) {
            provider.setBirthday(updateProvider.getBirthday());
        }
        if (updateProvider.getAddress() != null) {
            provider.setAddress(updateProvider.getAddress());
        }
        if (updateProvider.getIdCard() != null) {
            provider.setIdCard(updateProvider.getIdCard());
        }
        if (updateProvider.getWorkExperience() != null) {
            provider.setWorkExperience(updateProvider.getWorkExperience());
        }
        if (updateProvider.getSkills() != null) {
            provider.setSkills(updateProvider.getSkills());
        }
        if (updateProvider.getPhone() != null && !updateProvider.getPhone().equals(provider.getPhone())) {
            String phone = updateProvider.getPhone().trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new RuntimeException("手机号格式不正确");
            }
            if (serviceProviderRepository.existsByPhone(phone)) {
                throw new RuntimeException("手机号已被使用");
            }
            provider.setPhone(phone);
        }

        return serviceProviderRepository.save(provider);
    }

    /**
     * 分页查询服务者
     */
    public Page<ServiceProvider> findProviders(Pageable pageable) {
        return serviceProviderRepository.findAll(pageable);
    }

    /**
     * 根据状态分页查询服务者
     */
    public Page<ServiceProvider> findByStatus(Integer status, Pageable pageable) {
        return serviceProviderRepository.findByStatus(status, pageable);
    }

    /**
     * 根据认证状态查询服务者
     */
    public List<ServiceProvider> findByCertificationStatus(Integer certificationStatus) {
        return serviceProviderRepository.findByCertificationStatus(certificationStatus);
    }

    /**
     * 根据认证状态分页查询服务者
     */
    public Page<ServiceProvider> findByCertificationStatus(Integer certificationStatus, Pageable pageable) {
        return serviceProviderRepository.findByCertificationStatus(certificationStatus, pageable);
    }

    /**
     * 根据关键词搜索服务者
     */
    public Page<ServiceProvider> searchProviders(String keyword, Pageable pageable) {
        return serviceProviderRepository.findByKeyword(keyword, pageable);
    }

    /**
     * 启用/禁用服务者
     */
    public void updateProviderStatus(Long providerId, Integer status) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
            .orElseThrow(() -> new RuntimeException("服务者不存在"));
        
        provider.setStatus(status);
        serviceProviderRepository.save(provider);
    }

    /**
     * 更新认证状态
     */
    public void updateCertificationStatus(Long providerId, Integer certificationStatus) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
            .orElseThrow(() -> new RuntimeException("服务者不存在"));
        
        provider.setCertificationStatus(certificationStatus);
        if (certificationStatus == 1) {
            provider.setCertificationTime(LocalDateTime.now());
        }
        serviceProviderRepository.save(provider);
    }

    /**
     * 删除服务者
     */
    public void deleteProvider(Long providerId) {
        if (!serviceProviderRepository.existsById(providerId)) {
            throw new RuntimeException("服务者不存在");
        }
        serviceProviderRepository.deleteById(providerId);
    }

    /**
     * 统计服务者数量
     */
    public long countProviders() {
        return serviceProviderRepository.count();
    }

    /**
     * 统计已认证服务者数量
     */
    public long countCertifiedProviders() {
        return serviceProviderRepository.countCertifiedProviders();
    }

    /**
     * 统计启用服务者数量
     */
    public long countActiveProviders() {
        return serviceProviderRepository.findByStatus(1, Pageable.unpaged()).getTotalElements();
    }
}
