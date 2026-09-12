package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 厨师名片（单店，实际只有一条记录）
 */
@Data
@TableName(value = "chef_profile", autoResultMap = true)
public class ChefProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String title;
    private Integer years;
    private String city;

    /** 服务范围，如「平顶山全市（新华、卫东...）」 */
    private String area;

    /** 擅长菜系，DB 里存 JSON 数组 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> cuisines;

    private String intro;

    /** 对外电话，首页「电话咨询」用 */
    private String phone;

    /** 是否持健康证 */
    private Boolean healthCert;

    private Integer statBanquets;

    /** 复购率，DB 存百分数（68 表示 68%） */
    private Integer statRepeatRate;

    private Integer statMaxPeople;

    private Integer status;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 前端约定的嵌套结构：chef.stats.{banquets, repeatRate, maxPeople}
     * repeatRate 输出成小数（0.68），前端直接 *100 显示。
     */
    @JsonProperty("stats")
    public Map<String, Object> getStats() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("banquets", statBanquets == null ? 0 : statBanquets);
        m.put("repeatRate", statRepeatRate == null ? 0d : statRepeatRate / 100d);
        m.put("maxPeople", statMaxPeople == null ? 0 : statMaxPeople);
        return m;
    }

    /**
     * 服务流程 —— 固定四步，属于展示性文案，不占数据库字段。
     */
    @JsonProperty("flow")
    public List<Map<String, String>> getFlow() {
        return List.of(
                step("01", "沟通需求", "人数、口味、预算、场地与时间"),
                step("02", "确认菜单", "按预算出菜单，文字确认后定档"),
                step("03", "采买备料", "食材实报实销，小票留存"),
                step("04", "上门烹饪", "现场掌勺、上菜、收尾清理"));
    }

    private static Map<String, String> step(String step, String title, String desc) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("step", step);
        m.put("title", title);
        m.put("desc", desc);
        return m;
    }
}
