package com.example.housekeeping.repository;

import com.example.housekeeping.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员数据访问层
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * 根据用户名查找管理员
     */
    Optional<Admin> findByUsername(String username);
    
    /**
     * 根据用户名和状态查找管理员
     */
    Optional<Admin> findByUsernameAndStatus(String username, Integer status);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查用户名是否存在（排除指定ID）
     */
    boolean existsByUsernameAndIdNot(String username, Long id);
}
