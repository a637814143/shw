package com.example.housekeeping.service;

import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.util.HtmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 家政服务服务
 */
@Service
public class HousekeepingServiceService {

    private static final Logger log = LoggerFactory.getLogger(HousekeepingServiceService.class);
    private final HousekeepingServiceRepository housekeepingServiceRepository;

    public HousekeepingServiceService(HousekeepingServiceRepository housekeepingServiceRepository) {
        this.housekeepingServiceRepository = housekeepingServiceRepository;
    }

    /**
     * 根据ID查找服务
     */
    public Optional<HousekeepingService> findById(Long id) {
        return housekeepingServiceRepository.findById(id);
    }

    /**
     * 保存服务
     */
    public HousekeepingService save(HousekeepingService service) {
        // 清理HTML内容
        if (service.getContent() != null) {
            service.setContent(HtmlUtil.cleanHtml(service.getContent()));
        }
        
        return housekeepingServiceRepository.save(service);
    }

    /**
     * 创建服务
     */
    public HousekeepingService createService(HousekeepingService service) {
        // 清理HTML内容
        if (service.getContent() != null) {
            service.setContent(HtmlUtil.cleanHtml(service.getContent()));
        }
        
        return housekeepingServiceRepository.save(service);
    }

    /**
     * 更新服务
     */
    public HousekeepingService updateService(Long serviceId, HousekeepingService updateService) {
        HousekeepingService service = housekeepingServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));

        if (updateService.getName() != null) {
            service.setName(updateService.getName());
        }
        if (updateService.getDescription() != null) {
            service.setDescription(updateService.getDescription());
        }
        if (updateService.getContent() != null) {
            service.setContent(HtmlUtil.cleanHtml(updateService.getContent()));
        }
        if (updateService.getCategoryId() != null) {
            service.setCategoryId(updateService.getCategoryId());
        }
        if (updateService.getPrice() != null) {
            service.setPrice(updateService.getPrice());
        }
        if (updateService.getUnit() != null) {
            service.setUnit(updateService.getUnit());
        }
        if (updateService.getDuration() != null) {
            service.setDuration(updateService.getDuration());
        }
        if (updateService.getImages() != null) {
            service.setImages(updateService.getImages());
        }
        if (updateService.getTags() != null) {
            service.setTags(updateService.getTags());
        }
        if (updateService.getStatus() != null) {
            service.setStatus(updateService.getStatus());
        }

        return housekeepingServiceRepository.save(service);
    }

    /**
     * 分页查询服务
     */
    public Page<HousekeepingService> findServices(Pageable pageable) {
        return housekeepingServiceRepository.findAll(pageable);
    }

    /**
     * 根据状态查询服务
     */
    public List<HousekeepingService> findByStatus(Integer status) {
        return housekeepingServiceRepository.findByStatus(status);
    }

    /**
     * 根据状态分页查询服务
     */
    public Page<HousekeepingService> findByStatus(Integer status, Pageable pageable) {
        return housekeepingServiceRepository.findByStatus(status, pageable);
    }

    /**
     * 根据分类ID查询服务
     */
    public List<HousekeepingService> findByCategoryId(Long categoryId) {
        return housekeepingServiceRepository.findByCategoryIdAndStatus(categoryId, 1);
    }

    /**
     * 根据分类ID分页查询服务
     */
    public Page<HousekeepingService> findByCategoryId(Long categoryId, Pageable pageable) {
        return housekeepingServiceRepository.findByCategoryIdAndStatus(categoryId, 1, pageable);
    }

    /**
     * 根据服务者ID查询服务
     */
    public List<HousekeepingService> findByProviderId(Long providerId) {
        return housekeepingServiceRepository.findByProviderIdAndStatus(providerId, 1);
    }

    /**
     * 根据服务者ID分页查询服务
     */
    public Page<HousekeepingService> findByProviderId(Long providerId, Pageable pageable) {
        return housekeepingServiceRepository.findByProviderIdAndStatus(providerId, 1, pageable);
    }

    /**
     * 根据价格范围查询服务
     */
    public Page<HousekeepingService> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return housekeepingServiceRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    /**
     * 根据关键词搜索服务
     */
    public Page<HousekeepingService> searchServices(String keyword, Pageable pageable) {
        return housekeepingServiceRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 查询热门服务
     */
    public Page<HousekeepingService> findPopularServices(Pageable pageable) {
        return housekeepingServiceRepository.findPopularServices(pageable);
    }

    /**
     * 查询高评分服务
     */
    public Page<HousekeepingService> findHighRatedServices(BigDecimal minRating, Pageable pageable) {
        return housekeepingServiceRepository.findHighRatedServices(minRating, pageable);
    }

    /**
     * 启用/禁用服务
     */
    public void updateServiceStatus(Long serviceId, Integer status) {
        HousekeepingService service = housekeepingServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        
        service.setStatus(status);
        housekeepingServiceRepository.save(service);
    }

    /**
     * 更新服务评分
     */
    public void updateServiceRating(Long serviceId) {
        HousekeepingService service = housekeepingServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        
        // 这里应该调用评价服务来计算平均评分
        // 暂时使用默认值
        service.setRating(BigDecimal.ZERO);
        service.setReviewCount(0);
        
        housekeepingServiceRepository.save(service);
    }

    /**
     * 增加预约数量
     */
    public void incrementBookingCount(Long serviceId) {
        HousekeepingService service = housekeepingServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        
        service.setBookingCount(service.getBookingCount() + 1);
        housekeepingServiceRepository.save(service);
    }

    /**
     * 删除服务
     */
    public void deleteService(Long serviceId) {
        if (!housekeepingServiceRepository.existsById(serviceId)) {
            throw new RuntimeException("服务不存在");
        }
        housekeepingServiceRepository.deleteById(serviceId);
    }

    /**
     * 统计服务数量
     */
    public long countServices() {
        return housekeepingServiceRepository.count();
    }

    /**
     * 统计活跃服务数量
     */
    public long countActiveServices() {
        return housekeepingServiceRepository.countActiveServices();
    }

    /**
     * 统计分类下的服务数量
     */
    public long countByCategoryId(Long categoryId) {
        return housekeepingServiceRepository.countByCategoryId(categoryId);
    }
}
