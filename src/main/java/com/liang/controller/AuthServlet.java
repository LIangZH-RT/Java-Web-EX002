package com.liang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liang.dto.LoginUser;
import com.liang.dto.Result;
import com.liang.model.service.AuthService;
import com.liang.model.service.impl.AuthServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {
        "/api/auth/login",
        "/api/auth/logout",
        "/api/auth/current",
        "/api/auth/register"
})
public class AuthServlet extends HttpServlet {
    public static final String LOGIN_USER_SESSION_KEY = "loginUser";

    private final AuthService authService = new AuthServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareJsonResponse(response);
        if (request.getServletPath().endsWith("/current")) {
            HttpSession session = request.getSession(false);
            LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute(LOGIN_USER_SESSION_KEY);
            writeJson(response, Result.success(loginUser));
            return;
        }
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        writeJson(response, Result.error("不支持的请求方式"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareJsonResponse(response);
        try {
            String path = request.getServletPath();
            if (path.endsWith("/login")) {
                login(request, response);
            } else if (path.endsWith("/register")) {
                register(request, response);
            } else if (path.endsWith("/logout")) {
                logout(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJson(response, Result.error("接口不存在"));
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, Result.error(e.getMessage()));
        } catch (RuntimeException e) {
            log("Failed to handle auth request.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, Result.error("登录服务暂时不可用"));
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginUser loginUser = authService.login(request.getParameter("username"), request.getParameter("password"));
        request.getSession(true).setAttribute(LOGIN_USER_SESSION_KEY, loginUser);
        writeJson(response, Result.success(loginUser));
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginUser loginUser = authService.register(request.getParameter("username"), request.getParameter("password"));
        request.getSession(true).setAttribute(LOGIN_USER_SESSION_KEY, loginUser);
        writeJson(response, Result.success(loginUser));
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        writeJson(response, Result.success(null));
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
    }

    private void writeJson(HttpServletResponse response, Result<?> result) throws IOException {
        objectMapper.writeValue(response.getWriter(), result);
    }
}
