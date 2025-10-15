package com.example.housekeeping.service;

import com.example.housekeeping.dto.DashboardOfferItemRequest;
import com.example.housekeeping.dto.DashboardReviewItemRequest;
import com.example.housekeeping.dto.DashboardServiceItemRequest;
import com.example.housekeeping.dto.DashboardTipItemRequest;
import com.example.housekeeping.entity.DashboardOfferItem;
import com.example.housekeeping.entity.DashboardReviewItem;
import com.example.housekeeping.entity.DashboardServiceItem;
import com.example.housekeeping.entity.DashboardTipItem;
import com.example.housekeeping.repository.DashboardOfferItemRepository;
import com.example.housekeeping.repository.DashboardReviewItemRepository;
import com.example.housekeeping.repository.DashboardServiceItemRepository;
import com.example.housekeeping.repository.DashboardTipItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户主界面内容服务
 */
@Service
public class DashboardContentService {

    @Autowired
    private DashboardServiceItemRepository serviceItemRepository;

    @Autowired
    private DashboardTipItemRepository tipItemRepository;

    @Autowired
    private DashboardReviewItemRepository reviewItemRepository;

    @Autowired
    private DashboardOfferItemRepository offerItemRepository;

    public List<DashboardServiceItem> listServices() {
        return serviceItemRepository.findAll();
    }

    public DashboardServiceItem createService(DashboardServiceItemRequest request) {
        DashboardServiceItem item = new DashboardServiceItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIcon(request.getIcon());
        return serviceItemRepository.save(item);
    }

    public DashboardServiceItem updateService(Long id, DashboardServiceItemRequest request) {
        DashboardServiceItem item = serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("服务条目不存在"));
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIcon(request.getIcon());
        return serviceItemRepository.save(item);
    }

    public void deleteService(Long id) {
        serviceItemRepository.deleteById(id);
    }

    public List<DashboardTipItem> listTips() {
        return tipItemRepository.findAll();
    }

    public DashboardTipItem createTip(DashboardTipItemRequest request) {
        DashboardTipItem item = new DashboardTipItem();
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        return tipItemRepository.save(item);
    }

    public DashboardTipItem updateTip(Long id, DashboardTipItemRequest request) {
        DashboardTipItem item = tipItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("贴士不存在"));
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        return tipItemRepository.save(item);
    }

    public void deleteTip(Long id) {
        tipItemRepository.deleteById(id);
    }

    public List<DashboardReviewItem> listReviews() {
        return reviewItemRepository.findAll();
    }

    public DashboardReviewItem createReview(DashboardReviewItemRequest request) {
        DashboardReviewItem item = new DashboardReviewItem();
        item.setAuthor(request.getAuthor());
        item.setServiceName(request.getServiceName());
        item.setRating(request.getRating());
        item.setContent(request.getContent());
        return reviewItemRepository.save(item);
    }

    public DashboardReviewItem updateReview(Long id, DashboardReviewItemRequest request) {
        DashboardReviewItem item = reviewItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评价不存在"));
        item.setAuthor(request.getAuthor());
        item.setServiceName(request.getServiceName());
        item.setRating(request.getRating());
        item.setContent(request.getContent());
        return reviewItemRepository.save(item);
    }

    public void deleteReview(Long id) {
        reviewItemRepository.deleteById(id);
    }

    public List<DashboardOfferItem> listOffers() {
        return offerItemRepository.findAll();
    }

    public DashboardOfferItem createOffer(DashboardOfferItemRequest request) {
        DashboardOfferItem item = new DashboardOfferItem();
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setTag(request.getTag());
        return offerItemRepository.save(item);
    }

    public DashboardOfferItem updateOffer(Long id, DashboardOfferItemRequest request) {
        DashboardOfferItem item = offerItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("特惠信息不存在"));
        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setTag(request.getTag());
        return offerItemRepository.save(item);
    }

    public void deleteOffer(Long id) {
        offerItemRepository.deleteById(id);
    }
}
