package com.example.housekeeping.repository;

import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAllRepository extends JpaRepository<UserAll, Long> {

    Optional<UserAll> findByUsername(String username);

    boolean existsByUsername(String username);

    List<UserAll> findByUserType(String userType);

    List<UserAll> findByUserTypeOrderByIdAsc(String userType);

    Optional<UserAll> findFirstByUserTypeOrderByIdAsc(String userType);

    @Query("""
        SELECT u FROM UserAll u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(u.displayName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(u.contactPhone, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(COALESCE(u.contactAddress, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY u.id ASC
        """)
    List<UserAll> searchByKeyword(@Param("keyword") String keyword);

    @Query("""
        SELECT u FROM UserAll u
        WHERE u.userType = :userType AND (
            LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(u.displayName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(u.contactPhone, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(u.contactAddress, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        ORDER BY u.id ASC
        """)
    List<UserAll> searchByUserTypeAndKeyword(@Param("userType") String userType,
                                             @Param("keyword") String keyword);

    long countByUserType(String userType);
}
