package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceFavorite;
import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceFavoriteRepository extends JpaRepository<ServiceFavorite, Long> {

    List<ServiceFavorite> findByUserOrderByCreatedAtDesc(UserAll user);

    long countByService(HousekeepService service);

    Optional<ServiceFavorite> findByUserAndService(UserAll user, HousekeepService service);
}
