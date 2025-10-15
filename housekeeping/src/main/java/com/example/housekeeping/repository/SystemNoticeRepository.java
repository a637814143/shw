package com.example.housekeeping.repository;

import com.example.housekeeping.entity.SystemNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统公告数据访问层
 */
@Repository
public interface SystemNoticeRepository extends JpaRepository<SystemNotice, Long> {
    
    /**
     * 根据状态查找公告
     */
    List<SystemNotice> findByStatusOrderByCreateTimeDesc(Integer status);
    
    /**
     * 根据类型和状态查找公告
     */
    List<SystemNotice> findByTypeAndStatusOrderByCreateTimeDesc(Integer type, Integer status);
    
    /**
     * 根据条件分页查询公告
     */
    @Query("SELECT sn FROM SystemNotice sn WHERE " +
           "(:title IS NULL OR sn.title LIKE %:title%) AND " +
           "(:type IS NULL OR sn.type = :type) AND " +
           "(:status IS NULL OR sn.status = :status)")
    Page<SystemNotice> findByConditions(@Param("title") String title,
                                       @Param("type") Integer type,
                                       @Param("status") Integer status,
                                       Pageable pageable);
}
