package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.HousekeepingTip;
import com.example.housekeeping.repository.HousekeepingTipRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tips")
@RequiredArgsConstructor
public class AdminTipController {

    private final HousekeepingTipRepository tipRepository;

    @GetMapping
    public ApiResponse<Page<HousekeepingTip>> list(Pageable pageable) {
        return ApiResponse.success(tipRepository.findAll(pageable));
    }

    @PostMapping
    public ApiResponse<HousekeepingTip> create(@RequestBody @Valid HousekeepingTip tip) {
        return ApiResponse.success(tipRepository.save(tip));
    }

    @PutMapping("/{id}")
    public ApiResponse<HousekeepingTip> update(@PathVariable Long id, @RequestBody @Valid HousekeepingTip request) {
        HousekeepingTip tip = tipRepository.findById(id)
                .orElseThrow(() -> new BusinessException("贴士不存在"));
        tip.setTitle(request.getTitle());
        tip.setContent(request.getContent());
        tip.setSummary(request.getSummary());
        tip.setCoverImage(request.getCoverImage());
        tip.setTags(request.getTags());
        tip.setStatus(request.getStatus());
        return ApiResponse.success(tipRepository.save(tip));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tipRepository.deleteById(id);
        return ApiResponse.success();
    }
}
