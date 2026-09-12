package com.pf.chef.common;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 极简 token 工具：HMAC-SHA256 签名，格式 base64(openid|expireAt|signature)
 *
 * 说明：MVP 阶段够用。若后续要多端/多角色，建议换成 JWT（jjwt）或 Spring Security。
 */
public final class TokenUtil {

    /** ⚠️ 生产环境务必改成配置项 + 环境变量，不要用这个默认值 */
    private static final String SECRET = System.getenv().getOrDefault("PF_TOKEN_SECRET", "pf-private-chef-dev-secret");

    private TokenUtil() {
    }

    /** 签发 token，默认 30 天有效 */
    public static String issue(String openid) {
        long expireAt = System.currentTimeMillis() + 30L * 24 * 3600 * 1000;
        String body = openid + "|" + expireAt;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((body + "|" + sign(body)).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 校验 token
     * @return 合法则返回 openid，否则返回 null
     */
    public static String verify(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = raw.split("\\|");
            if (parts.length != 3) {
                return null;
            }
            String openid = parts[0];
            long expireAt = Long.parseLong(parts[1]);
            if (System.currentTimeMillis() > expireAt) {
                return null;
            }
            String expect = sign(openid + "|" + expireAt);
            return expect.equals(parts[2]) ? openid : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String sign(String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("token 签名失败", e);
        }
    }
}
