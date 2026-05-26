package com.liang.mapper;

import com.liang.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    String findUserTableName();

    User selectByUsername(@Param("tableName") String tableName,
                          @Param("username") String username);

    int insertUser(@Param("tableName") String tableName,
                   @Param("username") String username,
                   @Param("password") String password);
}
