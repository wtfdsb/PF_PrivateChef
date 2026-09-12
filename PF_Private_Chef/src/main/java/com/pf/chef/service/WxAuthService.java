package com.pf.chef.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.common.BizException;
import com.pf.chef.common.TokenUtil;
import com.pf.chef.config.WxProperties;
import com.pf.chef.entity.WxUser;
import com.pf.chef.mapper.WxUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 微信登录
 *
 * 正式流程：wx.login() 拿到 code → 后端换 openid → 签发 token
 * 开发流程：未配置 appid/secret 且 wx.mock-login=true 时，用 mock openid，前后端可先联调
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxAuthService {

    private static final String JSCODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session";

    private final WxProperties wx;
    private final WxUserMapper wxUserMapper;
    private final RestClient restClient = RestClient.create();

    /**
     * 用 code 换登录态
     *
     * @param code wx.login() 返回的 code
     * @return { token, openid }
     */
    public Map<String, Object> login(String code) {
        String openid = resolveOpenid(code);
        WxUser user = upsertUser(openid);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", TokenUtil.issue(openid));
        result.put("openid", openid);
        result.put("userId", user.getId());
        return result;
    }

    @SuppressWarnings("unchecked")
    private String resolveOpenid(String code) {
        if (wx.isConfigured()) {
            try {
                Map<String, Object> body = restClient.get()
                        .uri(JSCODE2SESSION + "?appid={a}&secret={s}&js_code={c}&grant_type=authorization_code",
                                wx.getAppid(), wx.getSecret(), code)
                        .retrieve()
                        .body(Map.class);
                if (body == null || body.get("openid") == null) {
                    log.warn("code2session 失败: {}", body);
                    throw new BizException("微信登录失败：" + (body == null ? "无响应" : body.get("errmsg")));
                }
                return String.valueOf(body.get("openid"));
            } catch (BizException e) {
                throw e;
            } catch (Exception e) {
                log.error("调用微信接口异常", e);
                throw new BizException("微信登录服务暂时不可用");
            }
        }

        if (!wx.isMockLogin()) {
            throw new BizException("微信登录未配置，请设置 WX_APPID / WX_SECRET");
        }
        log.warn("mock-login 生效，返回伪造 openid —— 生产环境必须关闭 wx.mock-login");
        return "mock_openid_" + (code == null || code.isBlank() ? "anonymous" : code);
    }

    /** 首次登录自动建档，之后更新 unionid（如果有） */
    private WxUser upsertUser(String openid) {
        WxUser db = wxUserMapper.selectOne(
                Wrappers.<WxUser>lambdaQuery().eq(WxUser::getOpenid, openid).last("limit 1"));
        if (db != null) {
            return db;
        }
        WxUser user = new WxUser();
        user.setOpenid(openid);
        wxUserMapper.insert(user);
        return user;
    }

    /** 按 openid 查用户 */
    public WxUser findByOpenid(String openid) {
        if (openid == null) {
            return null;
        }
        return wxUserMapper.selectOne(
                Wrappers.<WxUser>lambdaQuery().eq(WxUser::getOpenid, openid).last("limit 1"));
    }
}
