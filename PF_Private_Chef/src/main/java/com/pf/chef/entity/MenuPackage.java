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
 * 参考套餐档位
 */
@Data
@TableName(value = "menu_package", autoResultMap = true)
public class MenuPackage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 人均参考价（旧字段，保留兼容） */
    private Integer perPerson;

    /** 套餐起步价（总价，元）；0 表示面议定制 */
    private Integer startPrice;

    private String tag;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> dishes;

    private String note;

    private Integer sort;
    private Integer status;
    private Integer deleted;
    private LocalDateTime createdAt;
}
