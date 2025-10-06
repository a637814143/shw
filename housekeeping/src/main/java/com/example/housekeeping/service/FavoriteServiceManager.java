package com.example.housekeeping.service;

import com.example.housekeeping.domain.entity.FavoriteService;
import java.util.List;

public interface FavoriteServiceManager {
    FavoriteService addFavorite(Long userId, Long serviceId);

    void removeFavorite(Long userId, Long serviceId);

    List<FavoriteService> getFavoritesForUser(Long userId);
}
