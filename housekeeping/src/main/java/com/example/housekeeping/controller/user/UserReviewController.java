package com.example.housekeeping.controller.user;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.ReviewCreateRequest;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.ServiceBooking;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.ServiceReview;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ServiceBookingRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.ServiceReviewRepository;
import com.example.housekeeping.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/reviews")
@RequiredArgsConstructor
public class UserReviewController {

    private final ServiceReviewRepository reviewRepository;
    private final ServiceBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HousekeepingServiceRepository serviceRepository;
    private final ServiceProviderRepository providerRepository;

    @GetMapping
    public ApiResponse<Page<ServiceReview>> list(@RequestParam Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return ApiResponse.success(reviewRepository.findByUser(user, pageable));
    }

    @PostMapping
    public ApiResponse<ServiceReview> create(@RequestBody @Valid ReviewCreateRequest request) {
        ServiceBooking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BusinessException("预约不存在"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        HousekeepingService service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new BusinessException("服务不存在"));
        ServiceProvider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        ServiceReview review = new ServiceReview();
        review.setBooking(booking);
        review.setUser(user);
        review.setService(service);
        review.setProvider(provider);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        return ApiResponse.success(reviewRepository.save(review));
    }
}
