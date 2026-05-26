package com.liang.model.service.impl;

import com.liang.dto.PageResult;
import com.liang.mapper.CartMapper;
import com.liang.model.CartItem;
import com.liang.model.Product;
import com.liang.model.service.CartService;
import com.liang.model.service.ProductService;
import com.liang.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class CartServiceImpl implements CartService {
    private static final int DEFAULT_STOCK = 999;

    private final ProductService productService = new ProductServiceImpl();

    @Override
    public PageResult<CartItem> findPage(Integer userId, int page, int pageSize) {
        validateUserId(userId);
        int offset = (page - 1) * pageSize;
        try (SqlSession session = MyBatisUtil.openSession()) {
            CartMapper mapper = session.getMapper(CartMapper.class);
            String tableName = resolveCartTable(session, mapper);
            List<CartItem> records = mapper.selectPage(tableName, userId, offset, pageSize);
            Long total = mapper.countByUser(tableName, userId);
            return new PageResult<CartItem>(records, page, pageSize, total == null ? 0L : total);
        }
    }

    @Override
    public CartItem add(Integer userId, Integer productId, int quantity) {
        validateUserId(userId);
        validateQuantity(quantity);
        Product product = productService.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        try (SqlSession session = MyBatisUtil.openSession()) {
            CartMapper mapper = session.getMapper(CartMapper.class);
            String tableName = resolveCartTable(session, mapper);
            CartItem item = mapper.selectByUserAndProduct(tableName, userId, productId);
            if (item == null) {
                mapper.insertItem(tableName, userId, productId, quantity);
            } else {
                int nextQuantity = item.getQuantity() + quantity;
                validateQuantity(nextQuantity);
                mapper.updateQuantity(tableName, userId, item.getId(), nextQuantity);
            }
            session.commit();
            return mapper.selectByUserAndProduct(tableName, userId, productId);
        }
    }

    @Override
    public CartItem update(Integer userId, Long cartItemId, int quantity) {
        validateUserId(userId);
        validateQuantity(quantity);
        try (SqlSession session = MyBatisUtil.openSession()) {
            CartMapper mapper = session.getMapper(CartMapper.class);
            String tableName = resolveCartTable(session, mapper);
            int affectedRows = mapper.updateQuantity(tableName, userId, cartItemId, quantity);
            if (affectedRows == 0) {
                throw new IllegalArgumentException("购物车商品不存在");
            }
            session.commit();
            return mapper.selectByIdAndUser(tableName, userId, cartItemId);
        }
    }

    @Override
    public CartItem updateSelected(Integer userId, Long cartItemId, boolean selected) {
        validateUserId(userId);
        try (SqlSession session = MyBatisUtil.openSession()) {
            CartMapper mapper = session.getMapper(CartMapper.class);
            String tableName = resolveCartTable(session, mapper);
            int affectedRows = mapper.updateSelected(tableName, userId, cartItemId, selected);
            if (affectedRows == 0) {
                throw new IllegalArgumentException("购物车商品不存在");
            }
            session.commit();
            return mapper.selectByIdAndUser(tableName, userId, cartItemId);
        }
    }

    @Override
    public void delete(Integer userId, Long cartItemId) {
        validateUserId(userId);
        try (SqlSession session = MyBatisUtil.openSession()) {
            CartMapper mapper = session.getMapper(CartMapper.class);
            String tableName = resolveCartTable(session, mapper);
            int affectedRows = mapper.deleteByIdAndUser(tableName, userId, cartItemId);
            if (affectedRows == 0) {
                throw new IllegalArgumentException("购物车商品不存在");
            }
            session.commit();
        }
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("缺少登录用户");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须为正整数");
        }
        if (quantity > DEFAULT_STOCK) {
            throw new IllegalArgumentException("数量不能大于 " + DEFAULT_STOCK);
        }
    }

    private String resolveCartTable(SqlSession session, CartMapper mapper) {
        String tableName = mapper.findCartTableName();
        if (tableName == null || tableName.trim().isEmpty()) {
            mapper.createDefaultCartTable();
            session.commit();
            tableName = mapper.findCartTableName();
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalStateException("未找到购物车表，且自动创建 t_cart_item 失败");
        }
        return quoteIdentifier(tableName);
    }

    private String quoteIdentifier(String identifier) {
        if (!identifier.matches("[A-Za-z0-9_]+")) {
            throw new IllegalStateException("购物车表名不合法");
        }
        return "`" + identifier + "`";
    }
}
