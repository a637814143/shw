package com.example.housekeeping.repository;

import com.example.housekeeping.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 根据用户名和状态查找用户
     */
    Optional<User> findByUsernameAndStatus(String username, Integer status);
    
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
     * 根据条件分页查询用户
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:realName IS NULL OR u.realName LIKE %:realName%) AND " +
           "(:phone IS NULL OR u.phone LIKE %:phone%) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findByConditions(@Param("username") String username,
                               @Param("realName") String realName,
                               @Param("phone") String phone,
                               @Param("status") Integer status,
                               Pageable pageable);
    
    /**
     * 统计用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    long countActiveUsers();
}
