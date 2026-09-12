package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 档期 —— 私厨生意的稀缺性就体现在这里
 */
@Data
@TableName("schedule_slot")
public class ScheduleSlot {

    /** 档期状态 */
    public static final String STATUS_OPEN = "open";
    public static final String STATUS_FULL = "full";
    public static final String STATUS_CLOSED = "closed";

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 日期；前端字段名是 date */
    @JsonProperty("date")
    private LocalDate slotDate;

    /** 午宴 / 晚宴 */
    private String meal;

    /** open 可约 / full 已满 / closed 关闭 */
    private String status;

    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
