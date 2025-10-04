package com.example.housekeeping.repository;

import com.example.housekeeping.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Optional<UserAccount> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
