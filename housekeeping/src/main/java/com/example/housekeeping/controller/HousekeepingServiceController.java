package com.example.housekeeping.controller;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.service.HousekeepingServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 家政服务控制器
 */
@RestController
@RequestMapping("/api/service")
public class HousekeepingServiceController {

    private static final Logger log = LoggerFactory.getLogger(HousekeepingServiceController.class);
    private final HousekeepingServiceService housekeepingServiceService;

    public HousekeepingServiceController(HousekeepingServiceService housekeepingServiceService) {
        this.housekeepingServiceService = housekeepingServiceService;
    }

    /**
     * 获取服务详情（公开接口）
     */
    @GetMapping("/public/{id}")
    public Result<HousekeepingService> getServiceById(@PathVariable Long id) {
        HousekeepingService service = housekeepingServiceService.findById(id)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        return Result.success(service);
    }

    /**
     * 根据分类获取服务列表（公开接口）
     */
    @GetMapping("/public/category/{categoryId}")
    public Result<PageResult<HousekeepingService>> getServicesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<HousekeepingService> servicePage = housekeepingServiceService.findByCategoryId(categoryId, pageable);
        return Result.success(PageResult.of(servicePage));
    }

    /**
     * 搜索服务（公开接口）
     */
    @GetMapping("/public/search")
    public Result<PageResult<HousekeepingService>> searchServices(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<HousekeepingService> servicePage = housekeepingServiceService.searchServices(keyword, pageable);
        return Result.success(PageResult.of(servicePage));
    }

    /**
     * 获取热门服务（公开接口）
     */
    @GetMapping("/public/popular")
    public Result<PageResult<HousekeepingService>> getPopularServices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<HousekeepingService> servicePage = housekeepingServiceService.findPopularServices(pageable);
        return Result.success(PageResult.of(servicePage));
    }

    /**
     * 获取高评分服务（公开接口）
     */
    @GetMapping("/public/high-rated")
    public Result<PageResult<HousekeepingService>> getHighRatedServices(
            @RequestParam(defaultValue = "4.0") BigDecimal minRating,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<HousekeepingService> servicePage = housekeepingServiceService.findHighRatedServices(minRating, pageable);
        return Result.success(PageResult.of(servicePage));
    }

    /**
     * 根据价格范围查询服务（公开接口）
     */
    @GetMapping("/public/price-range")
    public Result<PageResult<HousekeepingService>> getServicesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "price"));
        Page<HousekeepingService> servicePage = housekeepingServiceService.findByPriceRange(minPrice, maxPrice, pageable);
        return Result.success(PageResult.of(servicePage));
    }

    /**
     * 获取服务者的服务列表
     */
    @GetMapping("/provider/list")
    public Result<PageResult<HousekeepingService>> getProviderServices(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long providerId = (Long) request.getAttribute("userId");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<HousekeepingService> servicePage = housekeepingServiceService.findByProviderId(providerId, pageable);
        return Result.success(PageResult.of(servicePage));
    }

    /**
     * 创建服务（服务者）
     */
    @PostMapping
    public Result<HousekeepingService> createService(@RequestBody @Valid HousekeepingService service, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        service.setProviderId(providerId);
        HousekeepingService savedService = housekeepingServiceService.createService(service);
        return Result.success("创建成功", savedService);
    }

    /**
     * 更新服务（服务者）
     */
    @PutMapping("/{id}")
    public Result<HousekeepingService> updateService(@PathVariable Long id, @RequestBody @Valid HousekeepingService updateService, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        
        // 检查服务是否属于当前服务者
        HousekeepingService existingService = housekeepingServiceService.findById(id)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        
        if (!existingService.getProviderId().equals(providerId)) {
            return Result.forbidden("无权限修改此服务");
        }
        
        HousekeepingService service = housekeepingServiceService.updateService(id, updateService);
        return Result.success("更新成功", service);
    }

    /**
     * 更新服务状态（服务者）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateServiceStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request, HttpServletRequest httpRequest) {
        Long providerId = (Long) httpRequest.getAttribute("userId");
        
        // 检查服务是否属于当前服务者
        HousekeepingService existingService = housekeepingServiceService.findById(id)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        
        if (!existingService.getProviderId().equals(providerId)) {
            return Result.forbidden("无权限修改此服务");
        }
        
        Integer status = request.get("status");
        if (status == null) {
            return Result.badRequest("状态不能为空");
        }
        
        housekeepingServiceService.updateServiceStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 删除服务（服务者）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteService(@PathVariable Long id, HttpServletRequest request) {
        Long providerId = (Long) request.getAttribute("userId");
        
        // 检查服务是否属于当前服务者
        HousekeepingService existingService = housekeepingServiceService.findById(id)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        
        if (!existingService.getProviderId().equals(providerId)) {
            return Result.forbidden("无权限删除此服务");
        }
        
        housekeepingServiceService.deleteService(id);
        return Result.success("服务删除成功");
    }

    /**
     * 分页查询所有服务（管理员）
     */
    @GetMapping("/admin/list")
    public Result<PageResult<HousekeepingService>> getAllServices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<HousekeepingService> servicePage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            servicePage = housekeepingServiceService.searchServices(keyword, pageable);
        } else if (status != null) {
            servicePage = housekeepingServiceService.findByStatus(status, pageable);
        } else {
            servicePage = housekeepingServiceService.findServices(pageable);
        }
        
        return Result.success(PageResult.of(servicePage));
    }

    /**
     * 获取服务统计信息（管理员）
     */
    @GetMapping("/admin/statistics")
    public Result<Map<String, Long>> getServiceStatistics() {
        Map<String, Long> statistics = Map.of(
            "totalServices", housekeepingServiceService.countServices(),
            "activeServices", housekeepingServiceService.countActiveServices()
        );
        return Result.success(statistics);
    }
}
