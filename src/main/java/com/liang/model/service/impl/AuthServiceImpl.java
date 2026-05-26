package com.liang.model.service.impl;

import com.liang.dto.LoginUser;
import com.liang.mapper.UserMapper;
import com.liang.model.User;
import com.liang.model.service.AuthService;
import com.liang.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

public class AuthServiceImpl implements AuthService {
    @Override
    public LoginUser login(String username, String password) {
        String cleanUsername = normalize(username, "用户名");
        String cleanPassword = normalize(password, "密码");

        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectByUsername(resolveUserTable(mapper), cleanUsername);
            if (user == null || !cleanPassword.equals(user.getPassword())) {
                throw new IllegalArgumentException("用户名或密码错误");
            }
            return new LoginUser(user.getId(), user.getUsername());
        }
    }

    @Override
    public LoginUser register(String username, String password) {
        String cleanUsername = normalize(username, "用户名");
        String cleanPassword = normalize(password, "密码");
        if (cleanUsername.length() > 45) {
            throw new IllegalArgumentException("用户名长度不能超过 45");
        }
        if (cleanPassword.length() > 45) {
            throw new IllegalArgumentException("密码长度不能超过 45");
        }

        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            String tableName = resolveUserTable(mapper);
            if (mapper.selectByUsername(tableName, cleanUsername) != null) {
                throw new IllegalArgumentException("用户名已存在");
            }
            mapper.insertUser(tableName, cleanUsername, cleanPassword);
            session.commit();
            User user = mapper.selectByUsername(tableName, cleanUsername);
            return new LoginUser(user.getId(), user.getUsername());
        }
    }

    private String normalize(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        return value.trim();
    }

    private String resolveUserTable(UserMapper mapper) {
        String tableName = mapper.findUserTableName();
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalStateException("未找到用户表，请确认表名为 user、users 或 t_user");
        }
        return quoteIdentifier(tableName);
    }

    private String quoteIdentifier(String identifier) {
        if (!identifier.matches("[A-Za-z0-9_]+")) {
            throw new IllegalStateException("用户表名不合法");
        }
        return "`" + identifier + "`";
    }
}
