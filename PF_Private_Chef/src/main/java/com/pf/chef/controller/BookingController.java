package com.pf.chef.controller;

import com.pf.chef.common.R;
import com.pf.chef.config.AuthInterceptor;
import com.pf.chef.dto.BookingCreateReq;
import com.pf.chef.entity.Booking;
import com.pf.chef.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 预约（写操作 —— 这是整个小程序最重要的接口）
 *
 * 对应前端 utils/api.js：submitBooking / listMyBookings
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /** POST /api/bookings —— 提交预约 */
    @PostMapping
    public R<Booking> create(@RequestBody @Valid BookingCreateReq req, HttpServletRequest request) {
        String openid = (String) request.getAttribute(AuthInterceptor.ATTR_OPENID);
        return R.ok(bookingService.create(req, openid));
    }

    /**
     * GET /api/bookings/mine —— 我的预约
     *
     * TODO(上线前): 去掉 phone 参数，只认登录态。
     * 现在保留是为了前端还没接登录流程时也能联调。
     */
    @GetMapping("/mine")
    public R<List<Booking>> mine(HttpServletRequest request,
                                 @RequestParam(required = false) String phone) {
        String openid = (String) request.getAttribute(AuthInterceptor.ATTR_OPENID);
        return R.ok(bookingService.listMine(openid, phone));
    }
}
