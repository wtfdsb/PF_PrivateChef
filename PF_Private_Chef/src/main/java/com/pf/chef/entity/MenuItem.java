package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 单点菜单条目（凉菜/热菜/主食/需预定付押金，管理端可增删改）
 */
@Data
@TableName("menu_item")
public class MenuItem {

    /** 分类常量 */
    public static final String CAT_COLD = "cold";
    public static final String CAT_HOT = "hot";
    public static final String CAT_STAPLE = "staple";
    public static final String CAT_DEPOSIT = "deposit";

    @TableId(type = IdType.AUTO)
    private Long id;

    /** cold / hot / staple / deposit */
    private String category;

    private String name;

    /** 价格（元） */
    private BigDecimal price;

    /** 计价单位：只 / 6只 / 碗 / 条 / 斤 / 份 */
    private String unit;

    private Integer sort;

    /** 1 上架 0 下架 */
    private Integer status;

    private Integer deleted;
    private LocalDateTime createdAt;
}
