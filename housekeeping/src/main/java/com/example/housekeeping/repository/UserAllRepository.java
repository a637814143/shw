package com.example.housekeeping.repository;

import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAllRepository extends JpaRepository<UserAll, Long> {

    Optional<UserAll> findByUsername(String username);

    boolean existsByUsername(String username);
}
