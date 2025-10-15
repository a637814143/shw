package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousekeepItemRepository extends JpaRepository<HousekeepItem, Long> {

    List<HousekeepItem> findByItemTypeOrderByIdAsc(String itemType);

    Optional<HousekeepItem> findByIdAndItemType(Long id, String itemType);
}
