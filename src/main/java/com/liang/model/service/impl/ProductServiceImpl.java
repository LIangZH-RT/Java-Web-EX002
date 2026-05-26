package com.liang.model.service.impl;

import com.liang.dto.PageResult;
import com.liang.mapper.ProductMapper;
import com.liang.model.Product;
import com.liang.model.service.ProductService;
import com.liang.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    @Override
    public PageResult<Product> findPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> records = mapper.selectPage(offset, pageSize);
            Long total = mapper.countAll();
            return new PageResult<Product>(records, page, pageSize, total == null ? 0L : total);
        }
    }

    @Override
    public Product findById(Integer productId) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            return session.getMapper(ProductMapper.class).selectById(productId);
        }
    }
}
