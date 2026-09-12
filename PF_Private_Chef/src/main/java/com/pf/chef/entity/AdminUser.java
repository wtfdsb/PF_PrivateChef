package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家后台管理员（单账号场景，密码加盐 SHA-256 存储）
 */
@Data
@TableName("admin_user")
public class AdminUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** SHA-256(salt:password) 十六进制；序列化时不下发 */
    @JsonIgnore
    private String passwordHash;

    @JsonIgnore
    private String salt;

    private LocalDateTime createdAt;
}
