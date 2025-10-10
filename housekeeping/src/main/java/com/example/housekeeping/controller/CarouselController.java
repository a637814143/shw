package com.example.housekeeping.controller;

import com.example.housekeeping.dto.CarouselRequest;
import com.example.housekeeping.entity.CarouselItem;
import com.example.housekeeping.exception.ResourceNotFoundException;
import com.example.housekeeping.repository.CarouselItemRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/carousels")
public class CarouselController {

    private final CarouselItemRepository carouselRepository;

    public CarouselController(CarouselItemRepository carouselRepository) {
        this.carouselRepository = carouselRepository;
    }

    @GetMapping
    public List<CarouselItem> list() {
        return carouselRepository.findAll(Sort.by("id").ascending());
    }

    @GetMapping("/{id}")
    public CarouselItem detail(@PathVariable Long id) {
        return carouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的轮播图"));
    }

    @PostMapping
    public ResponseEntity<CarouselItem> create(@Valid @RequestBody CarouselRequest request) {
        CarouselItem item = CarouselItem.builder()
                .imageUrl(request.getImageUrl())
                .serviceName(request.getServiceName())
                .linkUrl(request.getLinkUrl())
                .build();
        CarouselItem saved = carouselRepository.save(item);
        return ResponseEntity.created(URI.create("/api/carousels/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public CarouselItem update(@PathVariable Long id, @Valid @RequestBody CarouselRequest request) {
        CarouselItem item = carouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的轮播图"));
        item.setImageUrl(request.getImageUrl());
        item.setServiceName(request.getServiceName());
        item.setLinkUrl(request.getLinkUrl());
        return carouselRepository.save(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        CarouselItem item = carouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的轮播图"));
        carouselRepository.delete(item);
        return ResponseEntity.noContent().build();
    }
}
