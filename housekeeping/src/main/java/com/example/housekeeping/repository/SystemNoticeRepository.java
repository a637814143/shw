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
     * 根据状态查询公告
     */
    List<SystemNotice> findByStatusOrderByCreateTimeDesc(Integer status);

    /**
     * 根据状态分页查询公告
     */
    Page<SystemNotice> findByStatusOrderByCreateTimeDesc(Integer status, Pageable pageable);

    /**
     * 根据类型查询公告
     */
    List<SystemNotice> findByTypeAndStatusOrderByCreateTimeDesc(Integer type, Integer status);

    /**
     * 根据类型分页查询公告
     */
    Page<SystemNotice> findByTypeAndStatusOrderByCreateTimeDesc(Integer type, Integer status, Pageable pageable);

    /**
     * 根据关键词搜索公告
     */
    @Query("SELECT sn FROM SystemNotice sn WHERE sn.status = 1 AND " +
           "(sn.title LIKE %:keyword% OR sn.content LIKE %:keyword%)")
    Page<SystemNotice> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询最新公告
     */
    @Query("SELECT sn FROM SystemNotice sn WHERE sn.status = 1 ORDER BY sn.createTime DESC")
    Page<SystemNotice> findLatestNotices(Pageable pageable);

    /**
     * 统计公告数量
     */
    @Query("SELECT COUNT(sn) FROM SystemNotice sn WHERE sn.status = 1")
    long countPublishedNotices();

    /**
     * 统计各类型公告数量
     */
    @Query("SELECT sn.type, COUNT(sn) FROM SystemNotice sn WHERE sn.status = 1 GROUP BY sn.type")
    List<Object[]> countNoticesByType();
}
