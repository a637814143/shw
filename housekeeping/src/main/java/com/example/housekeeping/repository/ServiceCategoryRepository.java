package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 服务分类数据访问层
 */
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    
    /**
     * 根据名称查找分类
     */
    Optional<ServiceCategory> findByName(String name);
    
    /**
     * 根据名称和状态查找分类
     */
    Optional<ServiceCategory> findByNameAndStatus(String name, Integer status);
    
    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 根据状态查找所有分类
     */
    List<ServiceCategory> findByStatusOrderBySortOrderAsc(Integer status);
    
    /**
     * 根据条件分页查询分类
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE " +
           "(:name IS NULL OR sc.name LIKE %:name%) AND " +
           "(:status IS NULL OR sc.status = :status)")
    Page<ServiceCategory> findByConditions(@Param("name") String name,
                                          @Param("status") Integer status,
                                          Pageable pageable);
}
