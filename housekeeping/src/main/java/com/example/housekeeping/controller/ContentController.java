package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.CarouselItem;
import com.example.housekeeping.domain.entity.HomeTip;
import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.SystemAnnouncement;
import com.example.housekeeping.domain.enums.AnnouncementTarget;
import com.example.housekeeping.dto.AnnouncementRequest;
import com.example.housekeeping.dto.CarouselRequest;
import com.example.housekeeping.dto.HomeTipRequest;
import com.example.housekeeping.repository.CarouselItemRepository;
import com.example.housekeeping.repository.HomeTipRepository;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.SystemAnnouncementRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final SystemAnnouncementRepository systemAnnouncementRepository;
    private final HomeTipRepository homeTipRepository;
    private final CarouselItemRepository carouselItemRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;

    public ContentController(SystemAnnouncementRepository systemAnnouncementRepository,
            HomeTipRepository homeTipRepository,
            CarouselItemRepository carouselItemRepository,
            HousekeepingServiceRepository housekeepingServiceRepository) {
        this.systemAnnouncementRepository = systemAnnouncementRepository;
        this.homeTipRepository = homeTipRepository;
        this.carouselItemRepository = carouselItemRepository;
        this.housekeepingServiceRepository = housekeepingServiceRepository;
    }

    // Announcements
    @GetMapping("/announcements")
    public List<SystemAnnouncement> listAnnouncements(
            @RequestParam(name = "target", required = false) AnnouncementTarget target,
            @RequestParam(name = "enabled", defaultValue = "false") boolean onlyEnabled) {
        if (onlyEnabled) {
            if (target != null && target != AnnouncementTarget.ALL) {
                return systemAnnouncementRepository.findByTargetOrTargetOrderByPinnedDescPublishedAtDesc(target, AnnouncementTarget.ALL);
            }
            return systemAnnouncementRepository.findByEnabledTrueOrderByPinnedDescPublishedAtDesc();
        }
        return systemAnnouncementRepository.findAll();
    }

    @PostMapping("/announcements")
    public SystemAnnouncement createAnnouncement(@RequestBody AnnouncementRequest request) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        applyAnnouncement(request, announcement);
        return systemAnnouncementRepository.save(announcement);
    }

    @PutMapping("/announcements/{id}")
    public SystemAnnouncement updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        SystemAnnouncement announcement = systemAnnouncementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在"));
        applyAnnouncement(request, announcement);
        return announcement;
    }

    @DeleteMapping("/announcements/{id}")
    public void deleteAnnouncement(@PathVariable Long id) {
        if (!systemAnnouncementRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在");
        }
        systemAnnouncementRepository.deleteById(id);
    }

    private void applyAnnouncement(AnnouncementRequest request, SystemAnnouncement announcement) {
        announcement.setTitle(request.title());
        announcement.setContent(request.content());
        announcement.setTarget(request.target());
        announcement.setPinned(request.pinned());
        announcement.setEnabled(request.enabled());
        announcement.setPublishedBy(request.publishedBy());
        if (request.enabled()) {
            announcement.setPublishedAt(LocalDateTime.now());
        }
    }

    // Tips
    @GetMapping("/tips")
    public List<HomeTip> listTips() {
        return homeTipRepository.findAll();
    }

    @GetMapping("/tips/featured")
    public List<HomeTip> listFeaturedTips() {
        return homeTipRepository.findByFeaturedTrueOrderByUpdatedAtDesc();
    }

    @PostMapping("/tips")
    public HomeTip createTip(@RequestBody HomeTipRequest request) {
        HomeTip tip = new HomeTip();
        applyTip(request, tip);
        return homeTipRepository.save(tip);
    }

    @PutMapping("/tips/{id}")
    public HomeTip updateTip(@PathVariable Long id, @RequestBody HomeTipRequest request) {
        HomeTip tip = homeTipRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "居家贴士不存在"));
        applyTip(request, tip);
        return tip;
    }

    @DeleteMapping("/tips/{id}")
    public void deleteTip(@PathVariable Long id) {
        if (!homeTipRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "居家贴士不存在");
        }
        homeTipRepository.deleteById(id);
    }

    private void applyTip(HomeTipRequest request, HomeTip tip) {
        tip.setTitle(request.title());
        tip.setSummary(request.summary());
        tip.setContent(request.content());
        tip.setCoverImageUrl(request.coverImageUrl());
        tip.setFeatured(request.featured());
    }

    // Carousel
    @GetMapping("/carousel")
    public List<CarouselItem> listCarousel() {
        return carouselItemRepository.findAll();
    }

    @GetMapping("/carousel/active")
    public List<CarouselItem> listActiveCarousel() {
        return carouselItemRepository.findByEnabledTrueOrderBySortOrderAsc();
    }

    @PostMapping("/carousel")
    public CarouselItem createCarousel(@RequestBody CarouselRequest request) {
        CarouselItem item = new CarouselItem();
        applyCarousel(request, item);
        return carouselItemRepository.save(item);
    }

    @PutMapping("/carousel/{id}")
    public CarouselItem updateCarousel(@PathVariable Long id, @RequestBody CarouselRequest request) {
        CarouselItem item = carouselItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "轮播图不存在"));
        applyCarousel(request, item);
        return item;
    }

    @DeleteMapping("/carousel/{id}")
    public void deleteCarousel(@PathVariable Long id) {
        if (!carouselItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "轮播图不存在");
        }
        carouselItemRepository.deleteById(id);
    }

    private void applyCarousel(CarouselRequest request, CarouselItem item) {
        item.setTitle(request.title());
        item.setImageUrl(request.imageUrl());
        item.setLinkUrl(request.linkUrl());
        item.setSortOrder(request.sortOrder());
        item.setEnabled(request.enabled());
        if (request.serviceId() != null) {
            HousekeepingService service = housekeepingServiceRepository.findById(request.serviceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "关联服务不存在"));
            item.setService(service);
        } else {
            item.setService(null);
        }
    }
}
