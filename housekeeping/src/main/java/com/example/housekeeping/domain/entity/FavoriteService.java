package com.example.housekeeping.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "favorite_services", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "service_id"})
})
public class FavoriteService extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private HousekeepingService service;

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public HousekeepingService getService() {
        return service;
    }

    public void setService(HousekeepingService service) {
        this.service = service;
    }
}
