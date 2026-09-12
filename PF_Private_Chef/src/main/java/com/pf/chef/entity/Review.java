package com.pf.chef.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户评价 —— 必须 authorized=1 才对外展示
 */
@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String scene;
    private Integer score;
    private String content;

    /** 客户是否授权展示（合规要求，默认 0） */
    private Boolean authorized;

    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
}
