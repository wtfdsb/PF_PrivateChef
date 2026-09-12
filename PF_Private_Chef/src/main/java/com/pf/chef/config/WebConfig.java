package com.pf.chef.config;

import com.pf.chef.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：跨域 + 拦截器
 *
 * - AuthInterceptor（可选登录态解析）→ /api/**
 * - AdminInterceptor（强制管理员登录）→ /api/admin/**（登录接口除外）
 *
 * 注意：小程序请求不受同源策略限制，CORS 主要是给本地 H5 和后台页面用的。
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthService adminAuthService;

    /** 上线前改成 true，强制小程序端登录 */
    private static final boolean REQUIRE_LOGIN = false;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(REQUIRE_LOGIN))
                .addPathPatterns("/api/**");

        registry.addInterceptor(new AdminInterceptor(adminAuthService))
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /** 商家后台入口：访问 /admin 或 /admin/ 转发到 index.html */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/admin").setViewName("forward:/admin/index.html");
        registry.addViewController("/admin/").setViewName("forward:/admin/index.html");
    }
}
