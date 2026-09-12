package com.pf.chef.config;

import com.pf.chef.common.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录态解析（当前为"可选鉴权"）
 *
 * 现状：前端还没有登录流程，所以这里只解析不拦截，
 *       解析出的 openid 放进 request attribute 供 controller 使用。
 *
 * ⚠️ 上线前必须收紧：
 *   1. requireLogin 改为 true
 *   2. 或在 startBooking 等写接口上强制校验
 *   3. /api/bookings/mine 不能再靠手机号查询
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_OPENID = "pf_openid";

    /** 是否强制登录（上线前改 true） */
    private final boolean requireLogin;

    /** 不需要登录就能访问的路径前缀 */
    private static final String[] PUBLIC_PATHS = {
            "/api/chef", "/api/cases", "/api/packages", "/api/slots",
            "/api/reviews", "/api/auth/", "/api/bookings"
    };

    public AuthInterceptor(boolean requireLogin) {
        this.requireLogin = requireLogin;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String openid = TokenUtil.verify(token);
        if (openid != null) {
            request.setAttribute(ATTR_OPENID, openid);
        }

        if (!requireLogin) {
            return true;
        }

        String uri = request.getRequestURI();
        for (String p : PUBLIC_PATHS) {
            if (uri.startsWith(p)) {
                return true;
            }
        }
        if (openid == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
            } catch (Exception e) {
                log.warn("写出 401 响应失败", e);
            }
            return false;
        }
        return true;
    }
}
