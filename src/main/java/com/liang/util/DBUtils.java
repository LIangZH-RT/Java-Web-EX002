package com.liang.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {
    private static DataSource ds;


    /*
    * 静态声名 保证只运行一次
    *
    */
    static {
        try{
            Properties pro = new Properties();
            InputStream is = DBUtils.class.getClassLoader().getResourceAsStream("db.property");

            pro.load(is);
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception  e) {
            System.out.println("数据库连接池初始化失败");
            throw new RuntimeException(e);

        }
    }

    public static DataSource getDataSource(){
        return ds;
    }

    public static Connection getCon() throws SQLException {

        return ds.getConnection();
    }


}
