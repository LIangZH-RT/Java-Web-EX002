package com.liang.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public final class MyBatisUtil {
    private static final SqlSessionFactory SESSION_FACTORY = createSessionFactory();

    private MyBatisUtil() {
    }

    public static SqlSession openSession() {
        return SESSION_FACTORY.openSession();
    }

    private static SqlSessionFactory createSessionFactory() {
        try (InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml")) {
            return new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
