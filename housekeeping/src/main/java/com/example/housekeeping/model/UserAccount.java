package com.example.housekeeping.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 简化账号实体，对应 user_family 表
 */
@Entity
@Table(name = "user_family")
public class UserAccount {

    @Id
    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String types;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal money = BigDecimal.ZERO;

    public UserAccount() {
    }

    public UserAccount(String userName, String password, String types, BigDecimal money) {
        this.userName = userName;
        this.password = password;
        this.types = types;
        this.money = money == null ? BigDecimal.ZERO : money;
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
        this.money = money == null ? BigDecimal.ZERO : money;
    }

    public void increaseMoney(BigDecimal amount) {
        if (amount != null) {
            this.money = this.money.add(amount);
        }
    }

    public void decreaseMoney(BigDecimal amount) {
        if (amount != null) {
            this.money = this.money.subtract(amount);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}
