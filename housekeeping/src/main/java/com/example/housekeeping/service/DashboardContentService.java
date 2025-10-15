package com.example.housekeeping.service;

import com.example.housekeeping.dto.DashboardOfferItemRequest;
import com.example.housekeeping.dto.DashboardOfferItemResponse;
import com.example.housekeeping.dto.DashboardReviewItemRequest;
import com.example.housekeeping.dto.DashboardReviewItemResponse;
import com.example.housekeeping.dto.DashboardServiceItemRequest;
import com.example.housekeeping.dto.DashboardServiceItemResponse;
import com.example.housekeeping.dto.DashboardTipItemRequest;
import com.example.housekeeping.dto.DashboardTipItemResponse;
import com.example.housekeeping.entity.HousekeepItem;
import com.example.housekeeping.enums.HousekeepItemType;
import com.example.housekeeping.repository.HousekeepItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户主界面内容服务
 */
@Service
public class DashboardContentService {

    @Autowired
    private HousekeepItemRepository housekeepItemRepository;

    public List<DashboardServiceItemResponse> listServices() {
        return housekeepItemRepository.findByItemTypeOrderByIdAsc(HousekeepItemType.SERVICE.getValue())
                .stream()
                .map(item -> new DashboardServiceItemResponse(item.getId(), item.getTitle(), item.getContent(), item.getIcon()))
                .collect(Collectors.toList());
    }

    public DashboardServiceItemResponse createService(DashboardServiceItemRequest request) {
        HousekeepItem item = new HousekeepItem();
        item.setItemType(HousekeepItemType.SERVICE.getValue());
        item.setTitle(request.getName());
        item.setContent(request.getDescription());
        item.setIcon(request.getIcon());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardServiceItemResponse(saved.getId(), saved.getTitle(), saved.getContent(), saved.getIcon());
    }

    public DashboardServiceItemResponse updateService(Long id, DashboardServiceItemRequest request) {
        HousekeepItem item = housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.SERVICE.getValue())
                .orElseThrow(() -> new RuntimeException("服务条目不存在"));
        item.setTitle(request.getName());
        item.setContent(request.getDescription());
        item.setIcon(request.getIcon());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardServiceItemResponse(saved.getId(), saved.getTitle(), saved.getContent(), saved.getIcon());
    }

    public void deleteService(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.SERVICE.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }

    public List<DashboardTipItemResponse> listTips() {
        return housekeepItemRepository.findByItemTypeOrderByIdAsc(HousekeepItemType.TIP.getValue())
                .stream()
                .map(item -> new DashboardTipItemResponse(item.getId(), item.getTitle(), item.getContent()))
                .collect(Collectors.toList());
    }

    public DashboardTipItemResponse createTip(DashboardTipItemRequest request) {
        HousekeepItem item = new HousekeepItem();
        item.setItemType(HousekeepItemType.TIP.getValue());
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardTipItemResponse(saved.getId(), saved.getTitle(), saved.getContent());
    }

    public DashboardTipItemResponse updateTip(Long id, DashboardTipItemRequest request) {
        HousekeepItem item = housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.TIP.getValue())
                .orElseThrow(() -> new RuntimeException("贴士不存在"));
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardTipItemResponse(saved.getId(), saved.getTitle(), saved.getContent());
    }

    public void deleteTip(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.TIP.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }

    public List<DashboardReviewItemResponse> listReviews() {
        return housekeepItemRepository.findByItemTypeOrderByIdAsc(HousekeepItemType.REVIEW.getValue())
                .stream()
                .map(item -> new DashboardReviewItemResponse(
                        item.getId(),
                        item.getAuthor(),
                        item.getServiceName(),
                        item.getRating() == null ? 0.0 : item.getRating().doubleValue(),
                        item.getContent()))
                .collect(Collectors.toList());
    }

    public DashboardReviewItemResponse createReview(DashboardReviewItemRequest request) {
        HousekeepItem item = new HousekeepItem();
        item.setItemType(HousekeepItemType.REVIEW.getValue());
        item.setAuthor(request.getAuthor());
        item.setServiceName(request.getServiceName());
        item.setRating(BigDecimal.valueOf(request.getRating()));
        item.setContent(request.getContent());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardReviewItemResponse(
                saved.getId(),
                saved.getAuthor(),
                saved.getServiceName(),
                saved.getRating() == null ? 0.0 : saved.getRating().doubleValue(),
                saved.getContent());
    }

    public DashboardReviewItemResponse updateReview(Long id, DashboardReviewItemRequest request) {
        HousekeepItem item = housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.REVIEW.getValue())
                .orElseThrow(() -> new RuntimeException("评价不存在"));
        item.setAuthor(request.getAuthor());
        item.setServiceName(request.getServiceName());
        item.setRating(BigDecimal.valueOf(request.getRating()));
        item.setContent(request.getContent());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardReviewItemResponse(
                saved.getId(),
                saved.getAuthor(),
                saved.getServiceName(),
                saved.getRating() == null ? 0.0 : saved.getRating().doubleValue(),
                saved.getContent());
    }

    public void deleteReview(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.REVIEW.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }

    public List<DashboardOfferItemResponse> listOffers() {
        return housekeepItemRepository.findByItemTypeOrderByIdAsc(HousekeepItemType.OFFER.getValue())
                .stream()
                .map(item -> new DashboardOfferItemResponse(item.getId(), item.getTitle(), item.getContent(), item.getTag()))
                .collect(Collectors.toList());
    }

    public DashboardOfferItemResponse createOffer(DashboardOfferItemRequest request) {
        HousekeepItem item = new HousekeepItem();
        item.setItemType(HousekeepItemType.OFFER.getValue());
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setTag(request.getTag());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardOfferItemResponse(saved.getId(), saved.getTitle(), saved.getContent(), saved.getTag());
    }

    public DashboardOfferItemResponse updateOffer(Long id, DashboardOfferItemRequest request) {
        HousekeepItem item = housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.OFFER.getValue())
                .orElseThrow(() -> new RuntimeException("特惠信息不存在"));
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setTag(request.getTag());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardOfferItemResponse(saved.getId(), saved.getTitle(), saved.getContent(), saved.getTag());
    }

    public void deleteOffer(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.OFFER.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }
}
