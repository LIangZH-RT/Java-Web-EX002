package com.liang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liang.dto.Result;
import com.liang.model.Product;
import com.liang.model.service.ProductService;
import com.liang.model.service.impl.ProductServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/products", "/api/products/detail"})
public class ProductServlet extends HttpServlet {
    private final ProductService productService = new ProductServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            if (request.getServletPath().endsWith("/detail")) {
                writeProductDetail(request, response);
            } else {
                writeProductPage(request, response);
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, Result.error(e.getMessage()));
        } catch (RuntimeException e) {
            log("Failed to query product data.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, Result.error("商品数据加载失败"));
        }
    }

    private void writeProductPage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int page = readPositiveParameter(request, "page", 1);
        int pageSize = readPositiveParameter(request, "pageSize", 12);
        if (pageSize > 100) {
            throw new IllegalArgumentException("pageSize 不能大于 100");
        }
        writeJson(response, Result.success(productService.findPage(page, pageSize)));
    }

    private void writeProductDetail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int productId = readPositiveParameter(request, "productId", null);
        Product product = productService.findById(productId);
        if (product == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(response, Result.error("商品不存在"));
            return;
        }
        writeJson(response, Result.success(product));
    }

    private int readPositiveParameter(HttpServletRequest request, String name, Integer defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new IllegalArgumentException("缺少参数: " + name);
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new IllegalArgumentException(name + " 必须为正整数");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " 必须为正整数");
        }
    }

    private void writeJson(HttpServletResponse response, Result<?> result) throws IOException {
        objectMapper.writeValue(response.getWriter(), result);
    }
}
