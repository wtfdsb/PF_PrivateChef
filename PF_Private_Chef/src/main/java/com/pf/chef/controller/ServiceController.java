package com.pf.chef.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.common.BizException;
import com.pf.chef.common.R;
import com.pf.chef.entity.ServiceItem;
import com.pf.chef.mapper.ServiceItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 服务项目（首页每个入口的内容页）
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceItemMapper serviceItemMapper;

    /** 服务项目列表（首页网格用：name + subtitle） */
    @GetMapping
    public R<List<ServiceItem>> list() {
        return R.ok(serviceItemMapper.selectList(
                Wrappers.<ServiceItem>lambdaQuery()
                        .eq(ServiceItem::getStatus, 1)
                        .orderByAsc(ServiceItem::getSort)));
    }

    /** 服务项目详情 */
    @GetMapping("/{code}")
    public R<ServiceItem> detail(@PathVariable String code) {
        ServiceItem item = serviceItemMapper.selectOne(
                Wrappers.<ServiceItem>lambdaQuery()
                        .eq(ServiceItem::getCode, code)
                        .eq(ServiceItem::getStatus, 1)
                        .last("limit 1"));
        if (item == null) {
            throw new BizException(404, "该服务项目不存在或已下架");
        }
        return R.ok(item);
    }
}
