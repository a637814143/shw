package com.example.housekeeping.repository;

import com.example.housekeeping.entity.CarouselItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarouselItemRepository extends JpaRepository<CarouselItem, Long> {
}
