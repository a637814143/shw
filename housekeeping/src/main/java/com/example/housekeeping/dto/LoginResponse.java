package com.example.housekeeping.dto;

import java.math.BigDecimal;

public class LoginResponse {

    private final String username;
    private final String usertype;
    private final BigDecimal usermoney;

    public LoginResponse(String username, String usertype, BigDecimal usermoney) {
        this.username = username;
        this.usertype = usertype;
        this.usermoney = usermoney;
    }

    public String getUsername() {
        return username;
    }

    public String getUsertype() {
        return usertype;
    }

    public BigDecimal getUsermoney() {
        return usermoney;
    }
}
