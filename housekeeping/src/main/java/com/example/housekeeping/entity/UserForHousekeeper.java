package com.example.housekeeping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "user_for_housekeeper")
public class UserForHousekeeper {

    @Id
    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "usertype", nullable = false, length = 32)
    private String usertype;

    @Column(name = "usermoney")
    private BigDecimal usermoney;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public BigDecimal getUsermoney() {
        return usermoney;
    }

    public void setUsermoney(BigDecimal usermoney) {
        this.usermoney = usermoney;
    }
}
