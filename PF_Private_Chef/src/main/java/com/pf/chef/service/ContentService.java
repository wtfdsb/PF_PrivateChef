package com.pf.chef.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.common.BizException;
import com.pf.chef.entity.ChefProfile;
import com.pf.chef.entity.MenuPackage;
import com.pf.chef.entity.Review;
import com.pf.chef.entity.WorkCase;
import com.pf.chef.mapper.ChefProfileMapper;
import com.pf.chef.mapper.MenuPackageMapper;
import com.pf.chef.mapper.ReviewMapper;
import com.pf.chef.mapper.WorkCaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 展示内容读取：厨师名片、作品案例、参考套餐、客户评价
 *
 * 这些都是只读、低变更数据，单店量级下直查数据库完全够用。
 */
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ChefProfileMapper chefProfileMapper;
    private final WorkCaseMapper workCaseMapper;
    private final MenuPackageMapper menuPackageMapper;
    private final ReviewMapper reviewMapper;

    /** 厨师名片（单条） */
    public ChefProfile getChef() {
        ChefProfile chef = chefProfileMapper.selectOne(
                Wrappers.<ChefProfile>lambdaQuery()
                        .eq(ChefProfile::getStatus, 1)
                        .orderByAsc(ChefProfile::getId)
                        .last("limit 1"));
        if (chef == null) {
            throw new BizException(500, "厨师名片未配置，请先在 chef_profile 表插入数据");
        }
        return chef;
    }

    /** 作品案例列表 */
    public List<WorkCase> listCases(String scene, Integer limit) {
        int size = (limit == null || limit <= 0 || limit > 100) ? 20 : limit;
        return workCaseMapper.selectList(
                Wrappers.<WorkCase>lambdaQuery()
                        .eq(WorkCase::getStatus, 1)
                        .eq(scene != null && !scene.isBlank(), WorkCase::getScene, scene)
                        .orderByAsc(WorkCase::getSort)
                        .orderByDesc(WorkCase::getCaseDate)
                        .last("limit " + size));
    }

    /** 作品详情 */
    public WorkCase getCase(Long id) {
        WorkCase item = workCaseMapper.selectById(id);
        if (item == null || item.getStatus() == null || item.getStatus() != 1) {
            throw new BizException(404, "该作品不存在或已下架");
        }
        return item;
    }

    /** 参考套餐 */
    public List<MenuPackage> listPackages() {
        return menuPackageMapper.selectList(
                Wrappers.<MenuPackage>lambdaQuery()
                        .eq(MenuPackage::getStatus, 1)
                        .orderByAsc(MenuPackage::getSort));
    }

    /**
     * 客户评价 —— 只返回已授权展示的（合规要求）
     * 未授权的评价仅在后台可见。
     */
    public List<Review> listReviews() {
        return reviewMapper.selectList(
                Wrappers.<Review>lambdaQuery()
                        .eq(Review::getStatus, 1)
                        .eq(Review::getAuthorized, true)
                        .orderByAsc(Review::getSort));
    }
}
