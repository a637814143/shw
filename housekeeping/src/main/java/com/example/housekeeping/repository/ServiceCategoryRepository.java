package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 服务分类数据访问层
 */
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    /**
     * 根据状态查询分类
     */
    List<ServiceCategory> findByStatusOrderBySortOrder(Integer status);

    /**
     * 根据名称查询分类
     */
    ServiceCategory findByName(String name);

    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 查询所有启用的分类，按排序字段排序
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE sc.status = 1 ORDER BY sc.sortOrder ASC, sc.id ASC")
    List<ServiceCategory> findAllEnabledOrderBySortOrder();
}
