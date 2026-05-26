package com.liang.mapper;

import com.liang.model.CartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    String findCartTableName();

    int createDefaultCartTable();

    CartItem selectByUserAndProduct(@Param("tableName") String tableName,
                                    @Param("userId") Integer userId,
                                    @Param("productId") Integer productId);

    CartItem selectByIdAndUser(@Param("tableName") String tableName,
                               @Param("userId") Integer userId,
                               @Param("cartItemId") Long cartItemId);

    List<CartItem> selectPage(@Param("tableName") String tableName,
                              @Param("userId") Integer userId,
                              @Param("offset") Integer offset,
                              @Param("pageSize") Integer pageSize);

    Long countByUser(@Param("tableName") String tableName,
                     @Param("userId") Integer userId);

    int insertItem(@Param("tableName") String tableName,
                   @Param("userId") Integer userId,
                   @Param("productId") Integer productId,
                   @Param("quantity") Integer quantity);

    int updateQuantity(@Param("tableName") String tableName,
                       @Param("userId") Integer userId,
                       @Param("cartItemId") Long cartItemId,
                       @Param("quantity") Integer quantity);

    int updateSelected(@Param("tableName") String tableName,
                       @Param("userId") Integer userId,
                       @Param("cartItemId") Long cartItemId,
                       @Param("selected") Boolean selected);

    int deleteByIdAndUser(@Param("tableName") String tableName,
                          @Param("userId") Integer userId,
                          @Param("cartItemId") Long cartItemId);
}
