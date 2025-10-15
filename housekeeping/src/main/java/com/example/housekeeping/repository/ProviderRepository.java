package com.example.housekeeping.repository;

import com.example.housekeeping.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 家政服务者数据访问层
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    
    /**
     * 根据用户名查找服务者
     */
    Optional<Provider> findByUsername(String username);
    
    /**
     * 根据手机号查找服务者
     */
    Optional<Provider> findByPhone(String phone);
    
    /**
     * 根据用户名和状态查找服务者
     */
    Optional<Provider> findByUsernameAndStatus(String username, Integer status);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 检查用户名是否存在（排除指定ID）
     */
    boolean existsByUsernameAndIdNot(String username, Long id);
    
    /**
     * 检查手机号是否存在（排除指定ID）
     */
    boolean existsByPhoneAndIdNot(String phone, Long id);
    
    /**
     * 根据条件分页查询服务者
     */
    @Query("SELECT p FROM Provider p WHERE " +
           "(:username IS NULL OR p.username LIKE %:username%) AND " +
           "(:realName IS NULL OR p.realName LIKE %:realName%) AND " +
           "(:phone IS NULL OR p.phone LIKE %:phone%) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Provider> findByConditions(@Param("username") String username,
                                   @Param("realName") String realName,
                                   @Param("phone") String phone,
                                   @Param("status") Integer status,
                                   Pageable pageable);
    
    /**
     * 统计服务者数量
     */
    @Query("SELECT COUNT(p) FROM Provider p WHERE p.status = 1")
    long countActiveProviders();
    
    /**
     * 统计待审核服务者数量
     */
    @Query("SELECT COUNT(p) FROM Provider p WHERE p.status = 0")
    long countPendingProviders();
}
