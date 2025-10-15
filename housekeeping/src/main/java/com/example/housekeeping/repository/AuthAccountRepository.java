package com.example.housekeeping.repository;

import com.example.housekeeping.entity.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 登录账号数据访问接口
 */
@Repository
public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {

    Optional<AuthAccount> findByAccount(String account);

    boolean existsByAccount(String account);
}
