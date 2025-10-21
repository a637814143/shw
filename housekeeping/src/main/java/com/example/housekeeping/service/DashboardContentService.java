package com.example.housekeeping.service;

import com.example.housekeeping.dto.DashboardAnnouncementRequest;
import com.example.housekeeping.dto.DashboardAnnouncementResponse;
import com.example.housekeeping.dto.DashboardCarouselItemRequest;
import com.example.housekeeping.dto.DashboardCarouselItemResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户主界面内容服务
 */
@Service
public class DashboardContentService {

    @Autowired
    private HousekeepItemRepository housekeepItemRepository;

    @Transactional(readOnly = true)
    public List<DashboardServiceItemResponse> listServices(String keyword) {
        return loadItems(HousekeepItemType.SERVICE.getValue(), keyword,
            item -> new DashboardServiceItemResponse(item.getId(), item.getTitle(), item.getContent(), item.getIcon()));
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

    @Transactional
    public void deleteService(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.SERVICE.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }

    @Transactional
    public void deleteServices(List<Long> ids) {
        deleteItems(HousekeepItemType.SERVICE.getValue(), ids);
    }

    @Transactional(readOnly = true)
    public List<DashboardTipItemResponse> listTips(String keyword) {
        return loadItems(HousekeepItemType.TIP.getValue(), keyword,
            item -> new DashboardTipItemResponse(item.getId(), item.getTitle(), item.getContent()));
    }

    @Transactional(readOnly = true)
    public List<DashboardCarouselItemResponse> listCarousels(String keyword) {
        return loadItems(HousekeepItemType.CAROUSEL.getValue(), keyword,
            item -> new DashboardCarouselItemResponse(item.getId(), item.getTitle(), item.getContent(), item.getTag()));
    }

    public DashboardCarouselItemResponse createCarousel(DashboardCarouselItemRequest request) {
        HousekeepItem item = new HousekeepItem();
        item.setItemType(HousekeepItemType.CAROUSEL.getValue());
        item.setTitle(request.getTitle());
        item.setContent(request.getImageUrl());
        item.setTag(request.getServiceLink());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardCarouselItemResponse(saved.getId(), saved.getTitle(), saved.getContent(), saved.getTag());
    }

    public DashboardCarouselItemResponse updateCarousel(Long id, DashboardCarouselItemRequest request) {
        HousekeepItem item = housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.CAROUSEL.getValue())
            .orElseThrow(() -> new RuntimeException("轮播图不存在"));
        item.setTitle(request.getTitle());
        item.setContent(request.getImageUrl());
        item.setTag(request.getServiceLink());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardCarouselItemResponse(saved.getId(), saved.getTitle(), saved.getContent(), saved.getTag());
    }

    @Transactional
    public void deleteCarousel(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.CAROUSEL.getValue())
            .ifPresent(housekeepItemRepository::delete);
    }

    @Transactional
    public void deleteCarousels(List<Long> ids) {
        deleteItems(HousekeepItemType.CAROUSEL.getValue(), ids);
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

    @Transactional
    public void deleteTip(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.TIP.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }

    @Transactional
    public void deleteTips(List<Long> ids) {
        deleteItems(HousekeepItemType.TIP.getValue(), ids);
    }

    @Transactional(readOnly = true)
    public List<DashboardAnnouncementResponse> listAnnouncements(String keyword) {
        return loadItems(HousekeepItemType.ANNOUNCEMENT.getValue(), keyword,
            item -> new DashboardAnnouncementResponse(item.getId(), item.getTitle(), item.getContent()));
    }

    public DashboardAnnouncementResponse createAnnouncement(DashboardAnnouncementRequest request) {
        HousekeepItem item = new HousekeepItem();
        item.setItemType(HousekeepItemType.ANNOUNCEMENT.getValue());
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardAnnouncementResponse(saved.getId(), saved.getTitle(), saved.getContent());
    }

    public DashboardAnnouncementResponse updateAnnouncement(Long id, DashboardAnnouncementRequest request) {
        HousekeepItem item = housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.ANNOUNCEMENT.getValue())
            .orElseThrow(() -> new RuntimeException("公告不存在"));
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        HousekeepItem saved = housekeepItemRepository.save(item);
        return new DashboardAnnouncementResponse(saved.getId(), saved.getTitle(), saved.getContent());
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.ANNOUNCEMENT.getValue())
            .ifPresent(housekeepItemRepository::delete);
    }

    @Transactional
    public void deleteAnnouncements(List<Long> ids) {
        deleteItems(HousekeepItemType.ANNOUNCEMENT.getValue(), ids);
    }

    @Transactional(readOnly = true)
    public List<DashboardReviewItemResponse> listReviews(String keyword) {
        return loadItems(HousekeepItemType.REVIEW.getValue(), keyword,
            item -> new DashboardReviewItemResponse(
                    item.getId(),
                    item.getAuthor(),
                    item.getServiceName(),
                    item.getRating() == null ? 0.0 : item.getRating().doubleValue(),
                    item.getContent()));
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

    @Transactional
    public void deleteReview(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.REVIEW.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }

    @Transactional
    public void deleteReviews(List<Long> ids) {
        deleteItems(HousekeepItemType.REVIEW.getValue(), ids);
    }

    @Transactional(readOnly = true)
    public List<DashboardOfferItemResponse> listOffers(String keyword) {
        return loadItems(HousekeepItemType.OFFER.getValue(), keyword,
            item -> new DashboardOfferItemResponse(item.getId(), item.getTitle(), item.getContent(), item.getTag()));
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

    @Transactional
    public void deleteOffer(Long id) {
        housekeepItemRepository.findByIdAndItemType(id, HousekeepItemType.OFFER.getValue())
                .ifPresent(housekeepItemRepository::delete);
    }

    @Transactional
    public void deleteOffers(List<Long> ids) {
        deleteItems(HousekeepItemType.OFFER.getValue(), ids);
    }

    private <T> List<T> loadItems(String itemType, String keyword, Function<HousekeepItem, T> mapper) {
        String normalized = normalizeKeyword(keyword);
        List<HousekeepItem> items = normalized == null
            ? housekeepItemRepository.findByItemTypeOrderByIdAsc(itemType)
            : housekeepItemRepository.searchByItemTypeAndKeyword(itemType, normalized);
        return items.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }

    private void deleteItems(String itemType, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<Long> distinct = ids.stream()
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        if (distinct.isEmpty()) {
            return;
        }
        List<HousekeepItem> items = housekeepItemRepository.findByItemTypeAndIdIn(itemType, distinct);
        if (items.size() != distinct.size()) {
            throw new RuntimeException("部分内容不存在或已被删除");
        }
        housekeepItemRepository.deleteAll(items);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
