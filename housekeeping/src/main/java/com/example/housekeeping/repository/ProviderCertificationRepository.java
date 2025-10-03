package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ProviderCertification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 服务者认证材料数据访问层
 */
@Repository
public interface ProviderCertificationRepository extends JpaRepository<ProviderCertification, Long> {

    /**
     * 根据服务者ID查找认证材料
     */
    Optional<ProviderCertification> findByProviderId(Long providerId);

    /**
     * 根据状态查询认证材料
     */
    List<ProviderCertification> findByStatus(Integer status);

    /**
     * 根据状态分页查询认证材料
     */
    Page<ProviderCertification> findByStatus(Integer status, Pageable pageable);

    /**
     * 统计待审核的认证数量
     */
    @Query("SELECT COUNT(pc) FROM ProviderCertification pc WHERE pc.status = 0")
    long countPendingCertifications();

    /**
     * 统计已通过的认证数量
     */
    @Query("SELECT COUNT(pc) FROM ProviderCertification pc WHERE pc.status = 1")
    long countApprovedCertifications();

    /**
     * 统计被拒绝的认证数量
     */
    @Query("SELECT COUNT(pc) FROM ProviderCertification pc WHERE pc.status = 2")
    long countRejectedCertifications();
}
