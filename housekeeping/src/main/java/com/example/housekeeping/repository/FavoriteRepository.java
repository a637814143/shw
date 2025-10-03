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
     * 根据用户ID查询收藏
     */
    List<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据用户ID分页查询收藏
     */
    Page<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据服务ID查询收藏
     */
    List<Favorite> findByServiceId(Long serviceId);

    /**
     * 检查用户是否已收藏该服务
     */
    boolean existsByUserIdAndServiceId(Long userId, Long serviceId);

    /**
     * 统计用户收藏数量
     */
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计服务被收藏数量
     */
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.serviceId = :serviceId")
    long countByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 删除用户的所有收藏
     */
    void deleteByUserId(Long userId);

    /**
     * 删除服务的所有收藏
     */
    void deleteByServiceId(Long serviceId);
}
