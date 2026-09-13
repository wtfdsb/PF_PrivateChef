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
 * 开发者微信推送（Server酱，https://sct.ftqq.com）
 *
 * 新预约提交后，通过 Server酱把订单摘要推到开发者的微信里（服务号消息形式），
 * 厨师不用守着电脑/后台也能第一时间知道来单了。
 *
 * 配置：环境变量 PF_NOTIFY_KEY = Server酱 SendKey（微信登录 sct.ftqq.com 获取）。
 * 未配置时自动降级为跳过，不影响主流程。
 */
@Slf4j
@Service
public class NotificationService {

    private static final String SEND_URL = "https://sctapi.ftqq.com/{key}.send";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Server酱 SendKey；空 = 不推送 */
    @Value("${pf.notify.key:}")
    private String sendKey;

    public boolean enabled() {
        return sendKey != null && !sendKey.isBlank();
    }

    /**
     * 推送到开发者微信
     *
     * @return 是否推送成功；未配置 / 失败均返回 false，不影响业务
     */
    @SuppressWarnings("unchecked")
    public boolean push(String title, String desp) {
        if (!enabled()) {
            log.info("通知跳过：未配置 PF_NOTIFY_KEY（Server酱 SendKey）");
            return false;
        }
        try {
            Map<String, String> form = new LinkedHashMap<>();
            form.put("title", title);
            form.put("desp", desp);
            String raw = restClient.post()
                    .uri(SEND_URL, sendKey)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(toForm(form))
                    .retrieve()
                    .body(String.class);
            Map<String, Object> resp = raw == null || raw.isBlank()
                    ? null : objectMapper.readValue(raw, Map.class);
            int code = resp != null && resp.get("code") instanceof Number n ? n.intValue() : -1;
            if (code == 0) {
                log.info("微信推送成功: {}", title);
                return true;
            }
            log.warn("微信推送失败: code={} resp={}", code, raw);
            return false;
        } catch (Exception e) {
            log.error("微信推送异常", e);
            return false;
        }
    }

    /** 异步推送，不阻塞主业务流程 */
    public void pushAsync(String title, String desp) {
        if (!enabled()) {
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
