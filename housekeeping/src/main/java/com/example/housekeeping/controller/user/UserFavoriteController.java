package com.example.housekeeping.controller.user;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.FavoriteRequest;
import com.example.housekeeping.entity.Favorite;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.FavoriteRepository;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final HousekeepingServiceRepository serviceRepository;

    @GetMapping
    public ApiResponse<Page<Favorite>> list(@RequestParam Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return ApiResponse.success(favoriteRepository.findByUser(user, pageable));
    }

    @PostMapping
    public ApiResponse<Favorite> add(@RequestBody @Valid FavoriteRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        HousekeepingService service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new BusinessException("服务不存在"));
        return favoriteRepository.findByUserAndService(user, service)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.success(favoriteRepository.save(createFavorite(user, service))));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        favoriteRepository.deleteById(id);
        return ApiResponse.success();
    }

    private Favorite createFavorite(User user, HousekeepingService service) {
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setService(service);
        return favorite;
    }
}
