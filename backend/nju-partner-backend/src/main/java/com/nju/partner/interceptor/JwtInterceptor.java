package com.nju.partner.interceptor;

import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if ((authHeader == null || authHeader.isBlank()) && isPublicReadRequest(request)) {
            return true;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        // store current user id for downstream use
        BaseContext.setCurrentUserId(userId);
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentUserId();
    }

    private boolean isPublicReadRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String uri = request.getRequestURI();
        return uri.equals("/api/posts")
                || uri.matches("^/api/posts/\\d+$")
                || uri.matches("^/api/posts/\\d+/comments$");
    }
}

