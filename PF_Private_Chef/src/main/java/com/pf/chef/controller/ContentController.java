package com.pf.chef.controller;

import com.pf.chef.common.R;
import com.pf.chef.entity.ChefProfile;
import com.pf.chef.entity.MenuPackage;
import com.pf.chef.entity.Review;
import com.pf.chef.entity.WorkCase;
import com.pf.chef.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 展示内容（全部公开只读，无需登录）
 *
 * 对应前端 utils/api.js：getChef / listCases / getCase / listPackages / listReviews
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    /** GET /api/chef —— 厨师名片 */
    @GetMapping("/chef")
    public R<ChefProfile> chef() {
        return R.ok(contentService.getChef());
    }

    /** GET /api/cases?scene=家宴&limit=6 —— 作品案例 */
    @GetMapping("/cases")
    public R<List<WorkCase>> cases(@RequestParam(required = false) String scene,
                                   @RequestParam(required = false) Integer limit) {
        return R.ok(contentService.listCases(scene, limit));
    }

    /** GET /api/cases/{id} —— 作品详情 */
    @GetMapping("/cases/{id}")
    public R<WorkCase> caseDetail(@PathVariable Long id) {
        return R.ok(contentService.getCase(id));
    }

    /** GET /api/packages —— 参考套餐 */
    @GetMapping("/packages")
    public R<List<MenuPackage>> packages() {
        return R.ok(contentService.listPackages());
    }

    /** GET /api/reviews —— 客户评价（仅已授权展示的） */
    @GetMapping("/reviews")
    public R<List<Review>> reviews() {
        return R.ok(contentService.listReviews());
    }
}
