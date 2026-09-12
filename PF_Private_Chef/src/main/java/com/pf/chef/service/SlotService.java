package com.pf.chef.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.entity.ScheduleSlot;
import com.pf.chef.mapper.ScheduleSlotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 档期
 */
@Service
@RequiredArgsConstructor
public class SlotService {

    private final ScheduleSlotMapper slotMapper;

    /**
     * 查询档期
     *
     * @param from 起始日期（含），不传则从今天开始
     * @param to   结束日期（含），不传则默认往后 30 天
     */
    public List<ScheduleSlot> list(LocalDate from, LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = to != null ? to : start.plusDays(30);
        return slotMapper.selectList(
                Wrappers.<ScheduleSlot>lambdaQuery()
                        .ge(ScheduleSlot::getSlotDate, start)
                        .le(ScheduleSlot::getSlotDate, end)
                        .orderByAsc(ScheduleSlot::getSlotDate)
                        .orderByAsc(ScheduleSlot::getId));
    }

    /** 查单个档期 */
    public ScheduleSlot find(LocalDate date, String meal) {
        return slotMapper.selectOne(
                Wrappers.<ScheduleSlot>lambdaQuery()
                        .eq(ScheduleSlot::getSlotDate, date)
                        .eq(ScheduleSlot::getMeal, meal)
                        .last("limit 1"));
    }

    /** 改档期状态（后台用） */
    public void updateStatus(Long id, String status) {
        ScheduleSlot slot = slotMapper.selectById(id);
        if (slot == null) {
            throw new com.pf.chef.common.BizException(404, "档期不存在");
        }
        slot.setStatus(status);
        slotMapper.updateById(slot);
    }
}
