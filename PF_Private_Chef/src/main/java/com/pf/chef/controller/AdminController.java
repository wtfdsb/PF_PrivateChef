package com.pf.chef.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.common.BizException;
import com.pf.chef.common.R;
import com.pf.chef.entity.Booking;
import com.pf.chef.entity.Review;
import com.pf.chef.entity.ScheduleSlot;
import com.pf.chef.mapper.BookingMapper;
import com.pf.chef.mapper.ReviewMapper;
import com.pf.chef.service.AdminAuthService;
import com.pf.chef.service.BookingService;
import com.pf.chef.service.NotificationService;
import com.pf.chef.service.SlotService;
import com.pf.chef.service.WxKfService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家后台（厨师管理端）
 *
 * 鉴权：/api/admin/login 公开，其余接口由 AdminInterceptor 强制校验管理员 token。
 * 前端管理界面：static/admin/index.html（手机浏览器直接访问 http://主机IP:8080/admin/）。
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAuthService adminAuthService;
    private final BookingService bookingService;
    private final SlotService slotService;
    private final WxKfService wxKfService;
    private final NotificationService notificationService;
    private final BookingMapper bookingMapper;
    private final ReviewMapper reviewMapper;

    /** 管理员登录 */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginReq req) {
        return R.ok(adminAuthService.login(req.getUsername(), req.getPassword()));
    }

    /** 测试微信推送：给开发者微信发一条测试消息（验证推送通道配置） */
    @PostMapping("/notify/test")
    public R<Map<String, Object>> notifyTest() {
        if (!notificationService.enabled()) {
            return R.fail(400, "未配置推送通道。推荐 PushPlus（免费 200 条/天）：www.pushplus.plus 微信登录并实名 → 复制 token → 设置环境变量 PF_PUSHPLUS_TOKEN 后重启；或 Server酱：sct.ftqq.com → SendKey → 环境变量 PF_NOTIFY_KEY");
        }
        boolean ok = notificationService.push(
                "【新谷私厨】测试通知",
                "如果您在微信里收到这条消息，说明新订单微信推送已配置成功。今后每次有新预约，这里都会第一时间通知您。");
        if (ok) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("sent", true);
            return R.ok(m);
        }
        return R.fail(500, "推送失败，请检查推送通道配置是否正确");
    }

    /** 仪表盘统计：各状态单数 + 今日档期 */
    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("pending", bookingMapper.selectCount(
                Wrappers.<Booking>lambdaQuery().eq(Booking::getStatus, Booking.PENDING)));
        m.put("confirmed", bookingMapper.selectCount(
                Wrappers.<Booking>lambdaQuery().eq(Booking::getStatus, Booking.CONFIRMED)));
        m.put("making", bookingMapper.selectCount(
                Wrappers.<Booking>lambdaQuery().eq(Booking::getStatus, Booking.MAKING)));
        m.put("today", bookingMapper.selectCount(
                Wrappers.<Booking>lambdaQuery().eq(Booking::getSlotDate, LocalDate.now())
                        .ne(Booking::getStatus, Booking.CANCELED)));
        return R.ok(m);
    }

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

    /** 标记定金已收/未收（线下收款，商家手动标记） */
    @PostMapping("/bookings/{id}/deposit")
    public R<Booking> updateDeposit(@PathVariable Long id, @RequestBody DepositReq req) {
        return R.ok(bookingService.updateDeposit(id, Boolean.TRUE.equals(req.getPaid())));
    }

    /**
     * 给客户发客服消息（微信官方客服消息通道）
     * 前提：客户 48 小时内点过小程序「在线客服」并发过消息，且下单时已微信登录
     */
    @PostMapping("/bookings/{id}/kf-message")
    public R<Map<String, Object>> sendKfMessage(@PathVariable Long id, @RequestBody KfMsgReq req) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            return R.fail(404, "预约不存在");
        }
        if (req == null || req.getContent() == null || req.getContent().isBlank()) {
            return R.fail(400, "消息内容不能为空");
        }
        String openid = bookingService.getOpenid(booking);
        if (openid == null) {
            return R.fail(400, "发送失败：该客户未在微信登录，无法发客服消息（可电话联系）");
        }
        int err = wxKfService.sendText(openid, req.getContent());
        if (err == 0) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("errcode", 0);
            m.put("msg", "已发送到客户微信");
            return R.ok(m);
        }
        if (err == 45015 || err == 45047) {
            return R.fail(400, "发送失败：客户超过 48 小时未联系过在线客服，需客户先在「在线客服」里发一条消息");
        }
        if (err == -1) {
            return R.fail(400, "发送失败：微信未配置（WX_APPID/WX_SECRET）或客户无登录态");
        }
        return R.fail(500, "微信返回错误 errcode=" + err);
    }

    /** 改档期状态：open / full / closed */
    @PostMapping("/slots/{id}/status")
    public R<Void> updateSlotStatus(@PathVariable Long id, @RequestBody StatusReq req) {
        String status = req.getStatus();
        if (!ScheduleSlot.STATUS_OPEN.equals(status)
                && !ScheduleSlot.STATUS_FULL.equals(status)
                && !ScheduleSlot.STATUS_CLOSED.equals(status)) {
            throw new BizException(400, "非法档期状态：" + status);
        }
        slotService.updateStatus(id, status);
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
    public static class LoginReq {
        private String username;
        private String password;
    }

    @Data
    public static class StatusReq {
        /** 目标状态 */
        private String status;
        /** 商家备注（可空） */
        private String adminRemark;
    }

    @Data
    public static class DepositReq {
        /** true 已收 / false 未收 */
        private Boolean paid;
    }

    @Data
    public static class KfMsgReq {
        /** 发送给客户的文本内容 */
        private String content;
    }
}
