package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.entity.ServiceReview;
import com.example.housekeeping.repository.ServiceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ServiceReviewRepository reviewRepository;

    @GetMapping
    public ApiResponse<Page<ServiceReview>> list(Pageable pageable) {
        return ApiResponse.success(reviewRepository.findAll(pageable));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return ApiResponse.success();
    }
}
