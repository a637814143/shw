package com.example.housekeeping.repository;

import com.example.housekeeping.entity.AccountTransaction;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByUserOrderByCreatedAtDesc(UserAll user);

    List<AccountTransaction> findTop50ByOrderByCreatedAtDesc();

    List<AccountTransaction> findByTypeAndCreatedAtBetween(AccountTransactionType type, Instant start, Instant end);
}
