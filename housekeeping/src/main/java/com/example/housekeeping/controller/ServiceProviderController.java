package com.example.housekeeping.controller;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.service.ServiceProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 家政服务者控制器
 */
@RestController
@RequestMapping("/api/provider")
public class ServiceProviderController {

    private static final Logger log = LoggerFactory.getLogger(ServiceProviderController.class);
    private final ServiceProviderService serviceProviderService;

    public ServiceProviderController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    /**
     * 获取当前服务者信息
     */
    @GetMapping("/profile")
    public Result<ServiceProvider> getProfile(HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        ServiceProvider provider = serviceProviderService.findById(providerId)
            .orElseThrow(() -> new RuntimeException("服务者不存在"));
        return Result.success(provider);
    }

    /**
     * 更新服务者信息
     */
    @PutMapping("/profile")
    public Result<ServiceProvider> updateProfile(@RequestBody @Valid ServiceProvider updateProvider, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        ServiceProvider provider = serviceProviderService.updateProvider(providerId, updateProvider);
        return Result.success("更新成功", provider);
    }

    /**
     * 分页查询服务者（管理员）
     */
    @GetMapping("/list")
    public Result<PageResult<ServiceProvider>> getProviderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer certificationStatus,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ServiceProvider> providerPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            providerPage = serviceProviderService.searchProviders(keyword, pageable);
        } else if (certificationStatus != null) {
            providerPage = serviceProviderService.findByCertificationStatus(certificationStatus, pageable);
        } else if (status != null) {
            providerPage = serviceProviderService.findByStatus(status, pageable);
        } else {
            providerPage = serviceProviderService.findProviders(pageable);
        }
        
        return Result.success(PageResult.of(providerPage));
    }

    /**
     * 根据ID获取服务者信息
     */
    @GetMapping("/{id}")
    public Result<ServiceProvider> getProviderById(@PathVariable Long id) {
        ServiceProvider provider = serviceProviderService.findById(id)
            .orElseThrow(() -> new RuntimeException("服务者不存在"));
        return Result.success(provider);
    }

    /**
     * 更新服务者状态（管理员）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateProviderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status == null) {
            return Result.badRequest("状态不能为空");
        }
        
        serviceProviderService.updateProviderStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 更新认证状态（管理员）
     */
    @PutMapping("/{id}/certification")
    public Result<String> updateCertificationStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer certificationStatus = request.get("certificationStatus");
        if (certificationStatus == null) {
            return Result.badRequest("认证状态不能为空");
        }
        
        serviceProviderService.updateCertificationStatus(id, certificationStatus);
        return Result.success("认证状态更新成功");
    }

    /**
     * 删除服务者（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProvider(@PathVariable Long id) {
        serviceProviderService.deleteProvider(id);
        return Result.success("服务者删除成功");
    }

    /**
     * 获取待认证服务者列表（管理员）
     */
    @GetMapping("/pending-certification")
    public Result<List<ServiceProvider>> getPendingCertificationProviders() {
        List<ServiceProvider> providers = serviceProviderService.findByCertificationStatus(0);
        return Result.success(providers);
    }

    /**
     * 获取服务者统计信息（管理员）
     */
    @GetMapping("/statistics")
    public Result<Map<String, Long>> getProviderStatistics() {
        Map<String, Long> statistics = Map.of(
            "totalProviders", serviceProviderService.countProviders(),
            "certifiedProviders", serviceProviderService.countCertifiedProviders(),
            "activeProviders", serviceProviderService.countActiveProviders()
        );
        return Result.success(statistics);
    }
}
