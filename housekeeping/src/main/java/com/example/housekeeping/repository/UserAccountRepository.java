package com.example.housekeeping.repository;

import com.example.housekeeping.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    boolean existsByUserName(String userName);
}
