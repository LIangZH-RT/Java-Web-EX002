package com.liang.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liang.controller.AuthServlet;
import com.liang.dto.Result;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginFilter implements Filter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        Object loginUser = session == null ? null : session.getAttribute(AuthServlet.LOGIN_USER_SESSION_KEY);
        if (loginUser == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(httpResponse.getWriter(), Result.error("请先登录后再操作购物车"));
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
