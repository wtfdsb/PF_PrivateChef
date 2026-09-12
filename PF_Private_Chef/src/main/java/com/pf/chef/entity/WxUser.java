package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 微信用户
 */
@Data
@TableName("wx_user")
public class WxUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;
    private String unionid;

    /** 手机号，需前端 getPhoneNumber 授权后写入 */
    private String phone;

    private String nickname;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
