package com.pf.chef.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 提交预约的入参 —— 字段名与前端 pages/booking/booking.js 一一对应
 */
@Data
public class BookingCreateReq {

    @NotBlank(message = "请填写称呼")
    @Size(max = 32, message = "称呼太长了")
    private String name;

    /** 服务项目 code（可选，从服务详情页带过来），如 family/birthday */
    @Size(max = 32)
    private String category;

    @NotBlank(message = "请填写手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请填写正确的手机号")
    private String phone;

    @NotNull(message = "请选择日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "请选择餐次")
    private String meal;

    @NotNull(message = "请填写人数")
    @Min(value = 1, message = "人数至少 1 人")
    @Max(value = 100, message = "人数请控制在 100 人以内")
    private Integer people;

    /** 预算区间，如 "1000-2000" */
    private String budget;

    @NotBlank(message = "请填写场地地址")
    @Size(max = 255, message = "地址太长了")
    private String address;

    /** 口味与忌口：不吃辣 / 海鲜过敏 / 老人孩子多等 */
    @Size(max = 500, message = "口味忌口描述太长了")
    private String taste;

    /** 特殊需求：摆盘仪式 / 酒水代办 / 餐后收拾 / 代采购食材等 */
    @Size(max = 500, message = "特殊需求描述太长了")
    private String needs;

    /** 忌口 / 过敏 / 其他备注（兼容旧字段） */
    @Size(max = 500, message = "备注太长了")
    private String remark;

    /** 介绍人 */
    private String source;

    /** 来源渠道 direct / share */
    private String channel;
}
