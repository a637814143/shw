package com.example.housekeeping.service;

import com.example.housekeeping.dto.ServiceFavoriteResponse;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceFavorite;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import com.example.housekeeping.repository.ServiceFavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收藏服务。
 */
@Service
public class ServiceFavoriteService {

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private ServiceFavoriteRepository serviceFavoriteRepository;

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Transactional
    public void addFavorite(Long serviceId) {
        UserAll user = ensureUser();
        HousekeepService service = housekeepServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        if (serviceFavoriteRepository.findByUserAndService(user, service).isPresent()) {
            throw new RuntimeException("已收藏该服务");
        }

        ServiceFavorite favorite = new ServiceFavorite();
        favorite.setUser(user);
        favorite.setService(service);
        favorite.setCreatedAt(Instant.now());
        serviceFavoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long serviceId) {
        UserAll user = ensureUser();
        HousekeepService service = housekeepServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        serviceFavoriteRepository.findByUserAndService(user, service)
            .ifPresent(serviceFavoriteRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<ServiceFavoriteResponse> listFavorites() {
        UserAll user = ensureUser();
        return serviceFavoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
            .map(favorite -> new ServiceFavoriteResponse(
                favorite.getId(),
                user.getUsername(),
                favorite.getService().getId(),
                favorite.getService().getName(),
                favorite.getService().getCompany().getUsername(),
                favorite.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    private UserAll ensureUser() {
        UserAll user = accountLookupService.getCurrentAccount();
        if (!AccountRole.USER.getLabel().equals(user.getUserType())) {
            throw new RuntimeException("仅普通用户可操作收藏");
        }
        return user;
    }
}
