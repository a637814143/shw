package com.example.housekeeping.repository;

import com.example.housekeeping.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏数据访问层
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户ID和服务ID查找收藏
     */
    Optional<Favorite> findByUserIdAndServiceId(Long userId, Long serviceId);
    
    /**
     * 根据用户ID查找收藏
     */
    List<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据服务ID查找收藏
     */
    List<Favorite> findByServiceIdOrderByCreateTimeDesc(Long serviceId);
    
    /**
     * 根据条件分页查询收藏
     */
    @Query("SELECT f FROM Favorite f WHERE " +
           "(:userId IS NULL OR f.userId = :userId) AND " +
           "(:serviceId IS NULL OR f.serviceId = :serviceId)")
    Page<Favorite> findByConditions(@Param("userId") Long userId,
                                   @Param("serviceId") Long serviceId,
                                   Pageable pageable);
    
    /**
     * 检查用户是否已收藏该服务
     */
    boolean existsByUserIdAndServiceId(Long userId, Long serviceId);
}
