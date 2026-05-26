package com.liang.mapper;

import com.liang.model.Product;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProductMapper {
    Product selectById(@Param("id") Integer id);

    List<Product> selectPage(@Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize);

    Long countAll();
}
