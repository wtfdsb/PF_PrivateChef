package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务项目详情 —— 首页「服务项目」每个入口的内容页数据
 */
@Data
@TableName(value = "service_item", autoResultMap = true)
public class ServiceItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 唯一标识：family / birthday / business / festival / seafood / sashimi / hotpot */
    private String code;

    private String name;

    /** 一句话副标题，首页网格用 */
    private String subtitle;

    private String intro;

    /** 适合场景，JSON 数组 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> scenes;

    /** 常做的菜，JSON 数组 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> menu;

    /** 价格说明 */
    private String priceNote;

    /** 需要客户准备的，JSON 数组 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> prep;

    /** 小提示 */
    private String tips;

    private Integer sort;
    private Integer status;
    private Integer deleted;
    private LocalDateTime createdAt;
}
