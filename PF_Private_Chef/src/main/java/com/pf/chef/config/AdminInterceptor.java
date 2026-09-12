package com.pf.chef.config;

import com.pf.chef.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台接口鉴权：所有 /api/admin/**（除登录接口）必须携带管理员 token。
 *
 * token 通过 Authorization: Bearer <token> 传递，
 * 由 AdminAuthService.verifyToken 校验主体是否为 admin:*。
 */
@Slf4j
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final AdminAuthService adminAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // CORS 预检直接放行（跨域配置在 WebConfig）
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (adminAuthService.verifyToken(token) != null) {
            return true;
        }
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
        } catch (Exception e) {
            log.warn("写出 401 响应失败", e);
        }
        return false;
    }
}
