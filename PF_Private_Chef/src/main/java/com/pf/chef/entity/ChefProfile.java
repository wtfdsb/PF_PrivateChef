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

    /** 品牌名，如「新谷私厨」 */
    private String brand;

    private String title;
    private Integer years;
    private String city;

    /** 服务范围，如「平顶山市区（偏远区域加收上门里程费）」 */
    private String area;

    /** 擅长菜系，DB 里存 JSON 数组 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> cuisines;

    private String intro;

    /** 对外电话，首页「电话咨询」用 */
    private String phone;

    /** 是否持健康证 */
    private Boolean healthCert;

    /** 累计宴席场次 */
    private Integer statBanquets;

    /** 客户评分 0-5 */
    private Double statRating;

    /** 获奖/头衔 */
    private String awards;

    private Integer status;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 首页数据条：场次 / 菜系数 / 评分。
     * 「从业年限」用 years 字段，前端直接取。
     */
    @JsonProperty("stats")
    public Map<String, Object> getStats() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("banquets", statBanquets == null ? 0 : statBanquets);
        m.put("rating", statRating == null ? 5.0 : statRating);
        m.put("cuisines", cuisines == null ? 0 : cuisines.size());
        return m;
    }

    /**
     * 服务项目 —— 固定展示项，属于展示性文案，不占数据库字段。
     * 对齐豆包模板的服务分类。
     */
    @JsonProperty("services")
    public List<Map<String, String>> getServices() {
        return List.of(
                service("家庭聚餐", "团圆饭 · 朋友小聚"),
                service("生日寿宴", "寿面 · 寿桃 · 老规矩"),
                service("商务接待", "有面子 · 有分寸"),
                service("节日宴席", "中秋 · 春节 · 满月"),
                service("精品海鲜", "当日采买 · 清蒸见火候"),
                service("海鲜姿造", "冰盘摆盘 · 主题定制"),
                service("高端火锅", "打边炉 · 海鲜火锅"));
    }

    /**
     * 服务流程 —— 固定四步，属于展示性文案，不占数据库字段。
     */
    @JsonProperty("flow")
    public List<Map<String, String>> getFlow() {
        return List.of(
                step("01", "沟通需求", "人数、口味、预算、场地与时间"),
                step("02", "确认菜单", "按预算出菜单，文字确认后定档"),
                step("03", "采买备料", "食材当日采买，实报实销，小票留存"),
                step("04", "上门烹饪", "现场掌勺、上菜、收尾清理"));
    }

    private static Map<String, String> step(String step, String title, String desc) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("step", step);
        m.put("title", title);
        m.put("desc", desc);
        return m;
    }

    private static Map<String, String> service(String name, String desc) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("desc", desc);
        return m;
    }
}
