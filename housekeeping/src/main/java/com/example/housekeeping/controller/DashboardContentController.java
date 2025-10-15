package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.DashboardOfferItemRequest;
import com.example.housekeeping.dto.DashboardOfferItemResponse;
import com.example.housekeeping.dto.DashboardReviewItemRequest;
import com.example.housekeeping.dto.DashboardReviewItemResponse;
import com.example.housekeeping.dto.DashboardServiceItemRequest;
import com.example.housekeeping.dto.DashboardServiceItemResponse;
import com.example.housekeeping.dto.DashboardTipItemRequest;
import com.example.housekeeping.dto.DashboardTipItemResponse;
import com.example.housekeeping.service.DashboardContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端仪表盘内容控制器
 */
@RestController
@RequestMapping("/api/user/dashboard")
public class DashboardContentController {

    @Autowired
    private DashboardContentService dashboardContentService;

    @GetMapping("/services")
    public Result<List<DashboardServiceItemResponse>> listServices() {
        return Result.success(dashboardContentService.listServices());
    }

    @PostMapping("/services")
    public Result<DashboardServiceItemResponse> createService(@Valid @RequestBody DashboardServiceItemRequest request) {
        return Result.success("创建成功", dashboardContentService.createService(request));
    }

    @PutMapping("/services/{id}")
    public Result<DashboardServiceItemResponse> updateService(@PathVariable Long id,
                                                              @Valid @RequestBody DashboardServiceItemRequest request) {
        return Result.success("更新成功", dashboardContentService.updateService(id, request));
    }

    @DeleteMapping("/services/{id}")
    public Result<Void> deleteService(@PathVariable Long id) {
        dashboardContentService.deleteService(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/tips")
    public Result<List<DashboardTipItemResponse>> listTips() {
        return Result.success(dashboardContentService.listTips());
    }

    @PostMapping("/tips")
    public Result<DashboardTipItemResponse> createTip(@Valid @RequestBody DashboardTipItemRequest request) {
        return Result.success("创建成功", dashboardContentService.createTip(request));
    }

    @PutMapping("/tips/{id}")
    public Result<DashboardTipItemResponse> updateTip(@PathVariable Long id,
                                                      @Valid @RequestBody DashboardTipItemRequest request) {
        return Result.success("更新成功", dashboardContentService.updateTip(id, request));
    }

    @DeleteMapping("/tips/{id}")
    public Result<Void> deleteTip(@PathVariable Long id) {
        dashboardContentService.deleteTip(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/reviews")
    public Result<List<DashboardReviewItemResponse>> listReviews() {
        return Result.success(dashboardContentService.listReviews());
    }

    @PostMapping("/reviews")
    public Result<DashboardReviewItemResponse> createReview(@Valid @RequestBody DashboardReviewItemRequest request) {
        return Result.success("创建成功", dashboardContentService.createReview(request));
    }

    @PutMapping("/reviews/{id}")
    public Result<DashboardReviewItemResponse> updateReview(@PathVariable Long id,
                                                            @Valid @RequestBody DashboardReviewItemRequest request) {
        return Result.success("更新成功", dashboardContentService.updateReview(id, request));
    }

    @DeleteMapping("/reviews/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        dashboardContentService.deleteReview(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/offers")
    public Result<List<DashboardOfferItemResponse>> listOffers() {
        return Result.success(dashboardContentService.listOffers());
    }

    @PostMapping("/offers")
    public Result<DashboardOfferItemResponse> createOffer(@Valid @RequestBody DashboardOfferItemRequest request) {
        return Result.success("创建成功", dashboardContentService.createOffer(request));
    }

    @PutMapping("/offers/{id}")
    public Result<DashboardOfferItemResponse> updateOffer(@PathVariable Long id,
                                                          @Valid @RequestBody DashboardOfferItemRequest request) {
        return Result.success("更新成功", dashboardContentService.updateOffer(id, request));
    }

    @DeleteMapping("/offers/{id}")
    public Result<Void> deleteOffer(@PathVariable Long id) {
        dashboardContentService.deleteOffer(id);
        return Result.success("删除成功", null);
    }
}
