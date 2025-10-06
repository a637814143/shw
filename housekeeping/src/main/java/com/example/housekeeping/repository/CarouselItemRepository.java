package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.CarouselItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarouselItemRepository extends JpaRepository<CarouselItem, Long> {
    List<CarouselItem> findByEnabledTrueOrderBySortOrderAsc();
}
