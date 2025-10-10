package com.example.housekeeping.controller;

import com.example.housekeeping.dto.TipRequest;
import com.example.housekeeping.entity.HousekeepingTip;
import com.example.housekeeping.exception.ResourceNotFoundException;
import com.example.housekeeping.repository.HousekeepingTipRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/tips")
public class TipController {

    private final HousekeepingTipRepository tipRepository;

    public TipController(HousekeepingTipRepository tipRepository) {
        this.tipRepository = tipRepository;
    }

    @GetMapping
    public Page<HousekeepingTip> list(@RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("publishTime").descending());
        String normalizedKeyword = keyword != null && !keyword.isBlank() ? keyword : null;
        return tipRepository.search(normalizedKeyword, pageable);
    }

    @GetMapping("/{id}")
    public HousekeepingTip detail(@PathVariable Long id) {
        return tipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的贴士"));
    }

    @PostMapping
    public ResponseEntity<HousekeepingTip> create(@Valid @RequestBody TipRequest request) {
        HousekeepingTip tip = HousekeepingTip.builder()
                .title(request.getTitle())
                .intro(request.getIntro())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .publishTime(request.getPublishTime())
                .views(request.getViews())
                .build();
        HousekeepingTip saved = tipRepository.save(tip);
        return ResponseEntity.created(URI.create("/api/tips/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public HousekeepingTip update(@PathVariable Long id, @Valid @RequestBody TipRequest request) {
        HousekeepingTip tip = tipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的贴士"));
        tip.setTitle(request.getTitle());
        tip.setIntro(request.getIntro());
        tip.setContent(request.getContent());
        tip.setImageUrl(request.getImageUrl());
        tip.setPublishTime(request.getPublishTime());
        tip.setViews(request.getViews());
        return tipRepository.save(tip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        HousekeepingTip tip = tipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的贴士"));
        tipRepository.delete(tip);
        return ResponseEntity.noContent().build();
    }
}
