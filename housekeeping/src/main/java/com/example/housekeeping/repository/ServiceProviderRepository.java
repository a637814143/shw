package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 家政服务者数据访问层
 */
@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    /**
     * 根据用户名查找服务者
     */
    Optional<ServiceProvider> findByUsername(String username);

    /**
     * 根据用户名和状态查找服务者
     */
    Optional<ServiceProvider> findByUsernameAndStatus(String username, Integer status);

    /**
     * 根据手机号查找服务者
     */
    Optional<ServiceProvider> findByPhone(String phone);

    /**
     * 根据手机号和状态查找服务者
     */
    Optional<ServiceProvider> findByPhoneAndStatus(String phone, Integer status);

    /**
     * 根据身份证号查找服务者
     */
    Optional<ServiceProvider> findByIdCard(String idCard);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查身份证号是否存在
     */
    boolean existsByIdCard(String idCard);

    /**
     * 根据认证状态查询服务者
     */
    List<ServiceProvider> findByCertificationStatus(Integer certificationStatus);

    /**
     * 根据认证状态分页查询服务者
     */
    Page<ServiceProvider> findByCertificationStatus(Integer certificationStatus, Pageable pageable);

    /**
     * 根据状态分页查询服务者
     */
    Page<ServiceProvider> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据用户名模糊查询
     */
    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.username LIKE %:keyword% OR sp.realName LIKE %:keyword%")
    Page<ServiceProvider> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 统计已认证的服务者数量
     */
    @Query("SELECT COUNT(sp) FROM ServiceProvider sp WHERE sp.certificationStatus = 1 AND sp.status = 1")
    long countCertifiedProviders();
}
