package com.liang.dto;

public class LoginUser {
    private final Integer id;
    private final String username;

    public LoginUser(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
