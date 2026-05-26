package com.liang.model.service;

import com.liang.dto.PageResult;
import com.liang.model.Product;

public interface ProductService {
    PageResult<Product> findPage(int page, int pageSize);

    Product findById(Integer productId);
}
