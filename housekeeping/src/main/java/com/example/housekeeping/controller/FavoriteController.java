package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.FavoriteService;
import com.example.housekeeping.service.FavoriteServiceManager;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteServiceManager favoriteServiceManager;

    public FavoriteController(FavoriteServiceManager favoriteServiceManager) {
        this.favoriteServiceManager = favoriteServiceManager;
    }

    @PostMapping("/user/{userId}/service/{serviceId}")
    public FavoriteService addFavorite(@PathVariable Long userId, @PathVariable Long serviceId) {
        return favoriteServiceManager.addFavorite(userId, serviceId);
    }

    @DeleteMapping("/user/{userId}/service/{serviceId}")
    public void removeFavorite(@PathVariable Long userId, @PathVariable Long serviceId) {
        favoriteServiceManager.removeFavorite(userId, serviceId);
    }

    @GetMapping("/user/{userId}")
    public List<FavoriteService> listFavorites(@PathVariable Long userId) {
        return favoriteServiceManager.getFavoritesForUser(userId);
    }
}
