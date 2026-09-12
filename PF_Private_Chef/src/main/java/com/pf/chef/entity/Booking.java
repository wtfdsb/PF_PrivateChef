package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约单 —— 整条业务链的核心
 */
@Data
@TableName("booking")
public class Booking {

    /** 状态机 */
    public static final String PENDING = "pending";     // 待联系确认
    public static final String CONFIRMED = "confirmed"; // 已定档
    public static final String MAKING = "making";       // 制作中
    public static final String DONE = "done";           // 已完成
    public static final String CANCELED = "canceled";   // 已取消

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 预约单号，给客户看 */
    private String bookingNo;

    /** 关联 wx_user.id，未登录时可为空 */
    private Long userId;

    private String name;
    private String phone;

    /** 期望日期；前端字段名是 date */
    @JsonProperty("date")
    private LocalDate slotDate;

    /** 午宴 / 晚宴 */
    private String meal;

    private Integer people;

    /** 预算区间，如 "1000-2000" */
    private String budget;

    private String address;

    /** 忌口 / 过敏 / 场地情况 */
    private String remark;

    /** 介绍人（谁推荐来的） */
    private String source;

    /** 来源渠道：direct / share */
    private String channel;

    private String status;

    /** 商家备注 */
    private String adminRemark;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
