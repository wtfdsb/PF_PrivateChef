package com.pf.chef.controller;

import com.pf.chef.common.R;
import com.pf.chef.service.WxAuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 登录
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final WxAuthService wxAuthService;

    /** POST /api/auth/login  body: { "code": "wx.login() 拿到的 code" } */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody(required = false) LoginReq req) {
        String code = req == null ? null : req.getCode();
        return R.ok(wxAuthService.login(code));
    }

    @Data
    public static class LoginReq {
        private String code;
    }
}
