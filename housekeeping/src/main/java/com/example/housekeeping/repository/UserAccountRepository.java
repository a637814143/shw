package com.example.housekeeping.repository;

import com.example.housekeeping.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    boolean existsByUserNameIgnoreCase(String userName);

    Optional<UserAccount> findByUserNameIgnoreCase(String userName);
}

