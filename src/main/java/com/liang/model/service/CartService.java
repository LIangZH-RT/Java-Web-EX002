package com.liang.model.service;

import com.liang.dto.PageResult;
import com.liang.model.CartItem;

public interface CartService {
    PageResult<CartItem> findPage(Integer userId, int page, int pageSize);

    CartItem add(Integer userId, Integer productId, int quantity);

    CartItem update(Integer userId, Long cartItemId, int quantity);

    CartItem updateSelected(Integer userId, Long cartItemId, boolean selected);

    void delete(Integer userId, Long cartItemId);
}
