package com.pf.chef.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.common.BizException;
import com.pf.chef.dto.BookingCreateReq;
import com.pf.chef.entity.Booking;
import com.pf.chef.entity.ScheduleSlot;
import com.pf.chef.entity.WxUser;
import com.pf.chef.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 预约 —— 整条业务链的核心
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    private final BookingMapper bookingMapper;
    private final SlotService slotService;
    private final WxAuthService wxAuthService;

    /** 同一手机号每天最多提交几单（防刷） */
    @Value("${pf.booking.max-per-phone-per-day:3}")
    private int maxPerPhonePerDay;

    /**
     * 提交预约
     *
     * 关键校验：
     *   1. 档期必须存在且可约（防"约到已满档期"）
     *   2. 同一手机号当日提交数上限（防刷）
     *   3. 单号唯一（DB 唯一索引兜底）
     *
     * 注意：这里没有"扣减库存"的概念 —— 私厨的稀缺性是「一天只能接一场」，
     * 由档期状态表达。定档动作在线下完成，所以档期不会因为一次提交就变 full。
     */
    @Transactional(rollbackFor = Exception.class)
    public Booking create(BookingCreateReq req, String openid) {
        // 1. 档期校验
        ScheduleSlot slot = slotService.find(req.getDate(), req.getMeal());
        if (slot == null) {
            throw new BizException("该日期暂未开放预约，请换个日期或直接电话联系");
        }
        if (!ScheduleSlot.STATUS_OPEN.equals(slot.getStatus())) {
            throw new BizException("该档期已被约满，请换个日期或直接电话联系");
        }

        // 2. 防刷：同一手机号当日提交数
        Long todayCount = bookingMapper.selectCount(
                Wrappers.<Booking>lambdaQuery()
                        .eq(Booking::getPhone, req.getPhone())
                        .eq(Booking::getSlotDate, req.getDate())
                        .ne(Booking::getStatus, Booking.CANCELED));
        if (todayCount != null && todayCount >= maxPerPhonePerDay) {
            throw new BizException("您当天提交的预约已达上限，我会尽快联系您，请勿重复提交");
        }

        // 3. 落库
        Booking booking = new Booking();
        booking.setBookingNo(genBookingNo());
        booking.setName(req.getName());
        booking.setPhone(req.getPhone());
        booking.setSlotDate(req.getDate());
        booking.setMeal(req.getMeal());
        booking.setPeople(req.getPeople());
        booking.setBudget(req.getBudget());
        booking.setAddress(req.getAddress());
        booking.setRemark(req.getRemark());
        booking.setSource(req.getSource());
        booking.setChannel(req.getChannel() == null ? "direct" : req.getChannel());
        booking.setStatus(Booking.PENDING);

        WxUser user = wxAuthService.findByOpenid(openid);
        if (user != null) {
            booking.setUserId(user.getId());
        }

        bookingMapper.insert(booking);
        log.info("新预约 {} {} {} {}人 电话{}", booking.getBookingNo(),
                booking.getSlotDate(), booking.getMeal(), booking.getPeople(), booking.getPhone());
        return booking;
    }

    /**
     * 我的预约
     *
     * TODO(上线前): 必须只按登录用户查。当前为了前端还没接登录，
     * 允许用手机号兜底查询 —— 这是临时方案。
     */
    public List<Booking> listMine(String openid, String phone) {
        if (openid != null) {
            WxUser user = wxAuthService.findByOpenid(openid);
            if (user != null) {
                return bookingMapper.selectList(
                        Wrappers.<Booking>lambdaQuery()
                                .eq(Booking::getUserId, user.getId())
                                .orderByDesc(Booking::getId));
            }
        }
        if (phone != null && !phone.isBlank()) {
            return bookingMapper.selectList(
                    Wrappers.<Booking>lambdaQuery()
                            .eq(Booking::getPhone, phone)
                            .orderByDesc(Booking::getId));
        }
        return List.of();
    }

    // ==================== 后台 ====================

    /** 后台：按状态/日期查预约 */
    public List<Booking> listAll(String status, LocalDate date) {
        return bookingMapper.selectList(
                Wrappers.<Booking>lambdaQuery()
                        .eq(status != null && !status.isBlank(), Booking::getStatus, status)
                        .eq(date != null, Booking::getSlotDate, date)
                        .orderByDesc(Booking::getId));
    }

    /** 后台：改预约状态（接单 / 完成 / 取消） */
    public Booking updateStatus(Long id, String status, String adminRemark) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BizException(404, "预约不存在");
        }
        booking.setStatus(status);
        if (adminRemark != null) {
            booking.setAdminRemark(adminRemark);
        }
        bookingMapper.updateById(booking);
        return booking;
    }

    private String genBookingNo() {
        // 必须用 LocalDateTime：模式里含 HHmmss，LocalDate 会抛 UnsupportedTemporalTypeException
        return "PF" + LocalDateTime.now().format(NO_FMT) + ThreadLocalRandom.current().nextInt(100, 1000);
    }
}
