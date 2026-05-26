package com.liang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liang.dto.LoginUser;
import com.liang.dto.Result;
import com.liang.model.service.CartService;
import com.liang.model.service.impl.CartServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {
        "/api/cart/list",
        "/api/cart/add",
        "/api/cart/update",
        "/api/cart/select",
        "/api/cart/delete"
})
public class CartServlet extends HttpServlet {
    private final CartService cartService = new CartServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareJsonResponse(response);
        try {
            if (request.getServletPath().endsWith("/list")) {
                int page = readPositiveParameter(request, "page", 1);
                int pageSize = readPositiveParameter(request, "pageSize", 8);
                if (pageSize > 100) {
                    throw new IllegalArgumentException("pageSize 不能大于 100");
                }
                writeJson(response, Result.success(cartService.findPage(currentUserId(request), page, pageSize)));
                return;
            }
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            writeJson(response, Result.error("不支持的请求方式"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, Result.error(e.getMessage()));
        } catch (RuntimeException e) {
            log("Failed to handle cart query.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, Result.error("购物车数据加载失败"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareJsonResponse(response);
        try {
            String path = request.getServletPath();
            Integer userId = currentUserId(request);
            if (path.endsWith("/add")) {
                int productId = readPositiveParameter(request, "productId", null);
                int quantity = readPositiveParameter(request, "quantity", 1);
                writeJson(response, Result.success(cartService.add(userId, productId, quantity)));
            } else if (path.endsWith("/update")) {
                long cartItemId = readPositiveLongParameter(request, "cartItemId", null);
                int quantity = readPositiveParameter(request, "quantity", null);
                writeJson(response, Result.success(cartService.update(userId, cartItemId, quantity)));
            } else if (path.endsWith("/select")) {
                long cartItemId = readPositiveLongParameter(request, "cartItemId", null);
                boolean selected = readBooleanParameter(request, "selected");
                writeJson(response, Result.success(cartService.updateSelected(userId, cartItemId, selected)));
            } else if (path.endsWith("/delete")) {
                long cartItemId = readPositiveLongParameter(request, "cartItemId", null);
                cartService.delete(userId, cartItemId);
                writeJson(response, Result.success(null));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJson(response, Result.error("接口不存在"));
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, Result.error(e.getMessage()));
        } catch (RuntimeException e) {
            log("Failed to handle cart operation.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, Result.error("购物车操作失败"));
        }
    }

    private Integer currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute(AuthServlet.LOGIN_USER_SESSION_KEY);
        if (loginUser == null) {
            throw new IllegalArgumentException("请先登录");
        }
        return loginUser.getId();
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

    private long readPositiveLongParameter(HttpServletRequest request, String name, Long defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new IllegalArgumentException("缺少参数: " + name);
        }
        try {
            long parsed = Long.parseLong(value);
            if (parsed <= 0) {
                throw new IllegalArgumentException(name + " 必须为正整数");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " 必须为正整数");
        }
    }

    private boolean readBooleanParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("缺少参数: " + name);
        }
        String normalized = value.trim().toLowerCase();
        if ("1".equals(normalized) || "true".equals(normalized)) {
            return true;
        }
        if ("0".equals(normalized) || "false".equals(normalized)) {
            return false;
        }
        throw new IllegalArgumentException(name + " 必须为 true/false 或 1/0");
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
    }

    private void writeJson(HttpServletResponse response, Result<?> result) throws IOException {
        objectMapper.writeValue(response.getWriter(), result);
    }
}
