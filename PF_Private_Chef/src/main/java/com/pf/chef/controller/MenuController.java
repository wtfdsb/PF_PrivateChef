package com.pf.chef.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.common.R;
import com.pf.chef.entity.MenuItem;
import com.pf.chef.mapper.MenuItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 单点菜单（公开查询；管理端增删改在 AdminController）
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemMapper menuItemMapper;

    /** 单点菜单列表：按分类顺序 + 菜序返回；可指定分类（cold/hot/staple/deposit） */
    @GetMapping
    public R<List<MenuItem>> list(@RequestParam(required = false) String category) {
        return R.ok(menuItemMapper.selectList(
                Wrappers.<MenuItem>lambdaQuery()
                        .eq(MenuItem::getStatus, 1)
                        .eq(category != null && !category.isBlank(), MenuItem::getCategory, category)
                        .orderByAsc(MenuItem::getCategory)
                        .orderByAsc(MenuItem::getSort)
                        .orderByAsc(MenuItem::getId)));
    }
}
