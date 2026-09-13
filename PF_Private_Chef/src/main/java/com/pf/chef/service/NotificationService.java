package com.pf.chef.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 开发者微信推送 —— 多通道
 *
 * 新预约提交后，把订单摘要推到开发者的微信里，厨师不用守着电脑也能第一时间知道来单。
 *
 * 通道优先级（配置了哪个用哪个）：
 *   1. PushPlus（推送加）：实名免费 200 条/天，环境变量 PF_PUSHPLUS_TOKEN
 *      https://www.pushplus.plus 微信扫码登录 → 实名 → 复制 token
 *   2. Server酱：免费 5 条/天，环境变量 PF_NOTIFY_KEY
 *      https://sct.ftqq.com 微信扫码登录 → 复制 SendKey
 *
 * 两个都没配置时自动降级为跳过，不影响主流程。
 */
@Slf4j
@Service
public class NotificationService {

    private static final String SERVERCHAN_URL = "https://sctapi.ftqq.com/{key}.send";
    private static final String PUSHPLUS_URL = "https://www.pushplus.plus/send";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Server酱 SendKey；空 = 不启用 */
    @Value("${pf.notify.key:}")
    private String sendKey;

    /** PushPlus token；空 = 不启用 */
    @Value("${pf.notify.pushplus-token:}")
    private String pushplusToken;

    public boolean enabled() {
        return (sendKey != null && !sendKey.isBlank()) || (pushplusToken != null && !pushplusToken.isBlank());
    }

    /**
     * 推送到开发者微信
     *
     * 策略：PushPlus 优先（额度高），失败自动回退 Server酱；
     * 都没配置或都失败则返回 false，不影响业务。
     */
    @SuppressWarnings("unchecked")
    public boolean push(String title, String desp) {
        boolean hasPushplus = pushplusToken != null && !pushplusToken.isBlank();
        boolean hasServerchan = sendKey != null && !sendKey.isBlank();

        if (hasPushplus && pushPushplus(title, desp)) {
            return true;
        }
        if (hasServerchan && pushServerchan(title, desp)) {
            return true;
        }
        if (!hasPushplus && !hasServerchan) {
            log.info("通知跳过：未配置 PF_PUSHPLUS_TOKEN / PF_NOTIFY_KEY");
        } else {
            log.warn("通知发送失败：所有已配置通道均未成功（PushPlus={} Server酱={}）", hasPushplus, hasServerchan);
        }
        return false;
    }

    /** PushPlus 通道（https://www.pushplus.plus/doc/） */
    private boolean pushPushplus(String title, String desp) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("token", pushplusToken);
            payload.put("title", title);
            payload.put("content", desp);
            payload.put("template", "markdown");
            String raw = restClient.post()
                    .uri(PUSHPLUS_URL)
                    .body(payload)
                    .retrieve()
                    .body(String.class);
            Map<String, Object> resp = raw == null || raw.isBlank()
                    ? null : objectMapper.readValue(raw, Map.class);
            int code = resp != null && resp.get("code") instanceof Number n ? n.intValue() : -1;
            if (code == 200) {
                log.info("PushPlus 推送成功: {}", title);
                return true;
            }
            log.warn("PushPlus 推送失败: code={} resp={}", code, raw);
            return false;
        } catch (Exception e) {
            log.error("PushPlus 推送异常", e);
            return false;
        }
    }

    /** Server酱 通道（https://sct.ftqq.com） */
    private boolean pushServerchan(String title, String desp) {
        try {
            Map<String, String> form = new LinkedHashMap<>();
            form.put("title", title);
            form.put("desp", desp);
            String raw = restClient.post()
                    .uri(SERVERCHAN_URL, sendKey)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(toForm(form))
                    .retrieve()
                    .body(String.class);
            Map<String, Object> resp = raw == null || raw.isBlank()
                    ? null : objectMapper.readValue(raw, Map.class);
            int code = resp != null && resp.get("code") instanceof Number n ? n.intValue() : -1;
            if (code == 0) {
                log.info("Server酱推送成功: {}", title);
                return true;
            }
            log.warn("Server酱推送失败: code={} resp={}", code, raw);
            return false;
        } catch (Exception e) {
            log.error("Server酱推送异常", e);
            return false;
        }
    }

    /** 异步推送，不阻塞主业务流程 */
    public void pushAsync(String title, String desp) {
        boolean any = (sendKey != null && !sendKey.isBlank())
                || (pushplusToken != null && !pushplusToken.isBlank());
        if (!any) {
            return;
        }
        CompletableFuture.runAsync(() -> push(title, desp));
    }

    private String toForm(Map<String, String> m) {
        return m.entrySet().stream()
                .map(e -> urlEncode(e.getKey()) + "=" + urlEncode(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
