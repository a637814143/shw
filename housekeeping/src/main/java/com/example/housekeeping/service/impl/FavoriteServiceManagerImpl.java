package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.FavoriteService;
import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.UserAccount;
import com.example.housekeeping.repository.FavoriteServiceRepository;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.service.FavoriteServiceManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceManagerImpl implements FavoriteServiceManager {

    private final FavoriteServiceRepository favoriteServiceRepository;
    private final UserAccountRepository userAccountRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;

    @Override
    public FavoriteService addFavorite(Long userId, Long serviceId) {
        FavoriteService existing = favoriteServiceRepository.findByUserIdAndServiceId(userId, serviceId).orElse(null);
        if (existing != null) {
            return existing;
        }
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        HousekeepingService service = housekeepingServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "服务不存在"));
        FavoriteService favorite = new FavoriteService();
        favorite.setUser(user);
        favorite.setService(service);
        return favoriteServiceRepository.save(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long serviceId) {
        favoriteServiceRepository.findByUserIdAndServiceId(userId, serviceId)
                .ifPresentOrElse(favoriteServiceRepository::delete,
                        () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "收藏记录不存在"); });
    }

    @Override
    public List<FavoriteService> getFavoritesForUser(Long userId) {
        return favoriteServiceRepository.findByUserId(userId);
    }
}
