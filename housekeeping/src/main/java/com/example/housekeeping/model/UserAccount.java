package com.example.housekeeping.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "user_family")
public class UserAccount {

    @Id
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 20)
    private String types;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal money;

    public UserAccount() {
    }

    public UserAccount(String userName, String password, String types, BigDecimal money) {
        this.userName = userName;
        this.password = password;
        this.types = types;
        this.money = money;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
