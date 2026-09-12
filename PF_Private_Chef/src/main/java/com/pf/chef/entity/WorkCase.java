package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宴席作品案例 —— 转化率最高的内容，客户看的是"这个人懂我的场合"
 */
@Data
@TableName(value = "work_case", autoResultMap = true)
public class WorkCase {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商务宴请 / 家宴 / 生日宴 / 满月宴 / 同学聚会 / 乔迁宴 */
    private String scene;

    private Integer people;

    /** 举办日期；前端字段名是 date，这里映射一下 */
    @JsonProperty("date")
    private LocalDate caseDate;

    private String title;

    /** 菜单（长文本） */
    private String menu;

    /** 这一场的关键点 */
    private String highlight;

    /** 参考价（元） */
    private Integer refPrice;

    /** 现场照片地址数组 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private Integer sort;
    private Integer status;
    private Integer deleted;
    private LocalDateTime createdAt;
}
