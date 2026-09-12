package com.pf.chef.service;

import com.pf.chef.common.BizException;
import com.pf.chef.config.WxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 微信客服消息（官方「发送客服消息」接口）
 *
 * 用途：客户在 48 小时内点过小程序「在线客服」并发过消息后，
 * 服务端可以向该客户的微信主动推送文本（预约确认、改期提醒、完成感谢等）。
 *
 * 关键限制（微信官方规定）：
 *   1. 客户必须先主动给客服发过消息，且距今不超过 48 小时（errcode 45015/45047）
 *   2. 需要配置 WX_APPID / WX_SECRET（未配置时自动降级为跳过，不影响主流程）
 *
 * 参考文档：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/kf-mgnt/kf-message/sendCustomMessage.html
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxKfService {

    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send";

    private final WxProperties wx;
    private final RestClient restClient = RestClient.create();

    /** access_token 缓存（7200s 有效，提前 5 分钟刷新） */
    private volatile String accessToken;
    private volatile long tokenExpireAt;

    /** 是否可用：appid/secret 已配置 */
    public boolean available() {
        return wx.isConfigured();
    }

    /** 获取 access_token（带缓存） */
    @SuppressWarnings("unchecked")
    public synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireAt) {
            return accessToken;
        }
        if (!wx.isConfigured()) {
            throw new BizException("未配置 WX_APPID / WX_SECRET，客服消息不可用");
        }
        try {
            Map<String, Object> body = restClient.get()
                    .uri(TOKEN_URL + "?grant_type=client_credential&appid={a}&secret={s}",
                            wx.getAppid(), wx.getSecret())
                    .retrieve()
                    .body(Map.class);
            if (body == null || body.get("access_token") == null) {
                log.warn("获取 access_token 失败: {}", body);
                throw new BizException("获取微信 access_token 失败：" + (body == null ? "无响应" : body.get("errmsg")));
            }
            accessToken = String.valueOf(body.get("access_token"));
            int expires = body.get("expires_in") instanceof Number n ? n.intValue() : 7200;
            tokenExpireAt = System.currentTimeMillis() + (expires - 300) * 1000L;
            return accessToken;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取 access_token 异常", e);
            throw new BizException("微信客服服务暂时不可用");
        }
    }

    /**
     * 发送文本客服消息
     *
     * @return 微信 errcode：0 成功；45015/45047 超出 48 小时窗口；
     *         -1 未配置或非真实 openid（本地降级跳过）；-2 网络异常
     */
    @SuppressWarnings("unchecked")
    public int sendText(String openid, String content) {
        if (openid == null || openid.isBlank() || openid.startsWith("mock_")) {
            log.info("客服消息跳过：无真实 openid");
            return -1;
        }
        if (!wx.isConfigured()) {
            log.warn("客服消息跳过：未配置 WX_APPID / WX_SECRET");
            return -1;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("touser", openid);
        payload.put("msgtype", "text");
        Map<String, Object> text = new LinkedHashMap<>();
        text.put("content", content);
        payload.put("text", text);

        try {
            Map<String, Object> resp = restClient.post()
                    .uri(SEND_URL + "?access_token={t}", getAccessToken())
                    .body(payload)
                    .retrieve()
                    .body(Map.class);
            Object code = resp == null ? null : resp.get("errcode");
            int errcode = code instanceof Number n ? n.intValue() : -1;
            if (errcode == 0) {
                log.info("客服消息已发送 -> {}", openid);
            } else if (errcode == 45015 || errcode == 45047) {
                log.info("客服消息未发送（超出 48 小时窗口）-> {} errcode={}", openid, errcode);
            } else {
                log.warn("客服消息发送失败 -> {} errcode={} errmsg={}",
                        openid, errcode, resp == null ? "" : resp.get("errmsg"));
            }
            return errcode;
        } catch (Exception e) {
            log.error("客服消息发送异常 -> {}", openid, e);
            return -2;
        }
    }

    /** 异步发送：不阻塞主业务流程，失败只记日志 */
    public void sendTextAsync(String openid, String content) {
        if (openid == null || openid.isBlank() || !wx.isConfigured()) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                sendText(openid, content);
            } catch (Exception e) {
                log.error("异步客服消息异常", e);
            }
        });
    }
}
