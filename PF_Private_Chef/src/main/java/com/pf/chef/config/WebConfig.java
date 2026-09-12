package com.pf.chef.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：跨域 + 拦截器
 *
 * 注意：小程序请求不受同源策略限制，CORS 主要是给本地 H5 调试和后台页面用的。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 上线前改成 true，强制登录 */
    private static final boolean REQUIRE_LOGIN = false;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(REQUIRE_LOGIN))
                .addPathPatterns("/api/**");
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
}
