package com.pf.chef.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置（application.yml 的 wx.*）
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WxProperties {

    /** 小程序 AppID */
    private String appid;

    /** 小程序 AppSecret，不要提交到仓库 */
    private String secret;

    /**
     * 开发期是否允许伪造登录：为 true 且 appid 为空时，
     * /api/auth/login 会直接返回一个 mock openid，方便前后端联调。
     * ⚠️ 生产环境必须是 false。
     */
    private boolean mockLogin = true;

    public boolean isConfigured() {
        return appid != null && !appid.isBlank() && secret != null && !secret.isBlank();
    }
}
