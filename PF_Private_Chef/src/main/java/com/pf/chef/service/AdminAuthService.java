package com.pf.chef.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.common.BizException;
import com.pf.chef.common.TokenUtil;
import com.pf.chef.entity.AdminUser;
import com.pf.chef.mapper.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 商家后台登录
 *
 * 密码存储：SHA-256(salt:password)，单管理员场景足够。
 * 若后续要多账号/改密，再升级 BCrypt 或 Spring Security。
 */
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;

    /** 登录成功返回 {token, username} */
    public Map<String, Object> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BizException(400, "请输入账号和密码");
        }
        AdminUser user = adminUserMapper.selectOne(
                Wrappers.<AdminUser>lambdaQuery()
                        .eq(AdminUser::getUsername, username.trim())
                        .last("limit 1"));
        if (user == null || !hash(user.getSalt(), password).equals(user.getPasswordHash())) {
            throw new BizException(401, "账号或密码错误");
        }
        // token 主体：admin:<id>，由 AdminInterceptor 校验
        String token = TokenUtil.issue("admin:" + user.getId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        return result;
    }

    /** 校验 token，合法则返回 admin 用户 id，否则 null */
    public Long verifyToken(String token) {
        String subject = TokenUtil.verify(token);
        if (subject == null || !subject.startsWith("admin:")) {
            return null;
        }
        try {
            return Long.parseLong(subject.substring("admin:".length()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String hash(String salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("密码哈希失败", e);
        }
    }
}
