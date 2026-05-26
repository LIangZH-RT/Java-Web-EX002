package com.liang.model.service;

import com.liang.dto.LoginUser;

public interface AuthService {
    LoginUser login(String username, String password);

    LoginUser register(String username, String password);
}
