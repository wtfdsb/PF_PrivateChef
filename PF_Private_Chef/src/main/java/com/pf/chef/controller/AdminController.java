package com.pf.chef.controller;

import com.pf.chef.common.R;
import com.pf.chef.entity.Booking;
import com.pf.chef.entity.Review;
import com.pf.chef.entity.ScheduleSlot;
import com.pf.chef.mapper.ReviewMapper;
import com.pf.chef.service.BookingService;
import com.pf.chef.service.SlotService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家后台
 *
 * ⚠️ 安全提醒：这组接口目前【没有鉴权】，任何人都能调用。
 *    上线前必须加管理员校验（建议：独立后台账号 + Spring Security，
 *    或至少 IP 白名单 + 一个只有你哥知道的密钥）。
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookingService bookingService;
    private final SlotService slotService;
    private final ReviewMapper reviewMapper;

    /** 预约列表：可按状态/日期筛 */
    @GetMapping("/bookings")
    public R<List<Booking>> bookings(@RequestParam(required = false) String status,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return R.ok(bookingService.listAll(status, date));
    }

    /** 改预约状态：接单(confirmed) / 制作中(making) / 完成(done) / 取消(canceled) */
    @PostMapping("/bookings/{id}/status")
    public R<Booking> updateBookingStatus(@PathVariable Long id, @RequestBody StatusReq req) {
        return R.ok(bookingService.updateStatus(id, req.getStatus(), req.getAdminRemark()));
    }

    /** 改档期状态：open / full / closed */
    @PostMapping("/slots/{id}/status")
    public R<Void> updateSlotStatus(@PathVariable Long id, @RequestBody StatusReq req) {
        slotService.updateStatus(id, req.getStatus());
        return R.ok();
    }

    /** 评价列表（含未授权展示的，仅供后台查看） */
    @GetMapping("/reviews")
    public R<List<Review>> reviews() {
        return R.ok(reviewMapper.selectList(null));
    }

    /** 给评价授权/取消授权展示 */
    @PostMapping("/reviews/{id}/authorize")
    public R<Void> authorize(@PathVariable Long id, @RequestParam boolean authorized) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            return R.fail(404, "评价不存在");
        }
        review.setAuthorized(authorized);
        reviewMapper.updateById(review);
        return R.ok();
    }

    /** 快捷查档期（后台用） */
    @GetMapping("/slots")
    public R<List<ScheduleSlot>> slots(@RequestParam(required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                       @RequestParam(required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return R.ok(slotService.list(from, to));
    }

    @Data
    public static class StatusReq {
        /** 目标状态 */
        private String status;
        /** 商家备注（可空） */
        private String adminRemark;
    }
}
