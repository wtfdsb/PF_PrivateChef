package com.pf.chef.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pf.chef.entity.ScheduleSlot;
import com.pf.chef.mapper.ScheduleSlotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 档期
 *
 * 除了查询，还负责「自动补档」：查询某个日期范围时，缺的午/晚宴档期会自动生成，
 * 这样日历永远不会断档（种子数据只灌了未来 21 天，之后的日子靠这里补齐）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlotService {

    private static final List<String> MEALS = List.of("午宴", "晚宴");

    private final ScheduleSlotMapper slotMapper;

    /**
     * 查询档期（先自动补齐缺失的档期，再返回）
     *
     * @param from 起始日期（含），不传则从今天开始
     * @param to   结束日期（含），不传则默认往后 30 天
     */
    public List<ScheduleSlot> list(LocalDate from, LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = to != null ? to : start.plusDays(30);
        ensureSlots(start, end);
        return slotMapper.selectList(
                Wrappers.<ScheduleSlot>lambdaQuery()
                        .ge(ScheduleSlot::getSlotDate, start)
                        .le(ScheduleSlot::getSlotDate, end)
                        .orderByAsc(ScheduleSlot::getSlotDate)
                        .orderByAsc(ScheduleSlot::getId));
    }

    /**
     * 自动补档：确保 [start, end] 每天都有午宴/晚宴两条记录。
     * 新生成的档期规则与种子数据一致：周末晚宴默认已满，其余可约。
     * synchronized 防并发重复插入；唯一索引 uk_date_meal 兜底。
     */
    public synchronized void ensureSlots(LocalDate start, LocalDate end) {
        List<ScheduleSlot> exist = slotMapper.selectList(
                Wrappers.<ScheduleSlot>lambdaQuery()
                        .ge(ScheduleSlot::getSlotDate, start)
                        .le(ScheduleSlot::getSlotDate, end));
        Set<String> have = new HashSet<>();
        for (ScheduleSlot s : exist) {
            have.add(s.getSlotDate() + "|" + s.getMeal());
        }

        List<ScheduleSlot> todo = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            DayOfWeek dw = d.getDayOfWeek();
            boolean weekend = dw == DayOfWeek.SATURDAY || dw == DayOfWeek.SUNDAY;
            for (String meal : MEALS) {
                if (!have.contains(d + "|" + meal)) {
                    ScheduleSlot s = new ScheduleSlot();
                    s.setSlotDate(d);
                    s.setMeal(meal);
                    s.setStatus(weekend && "晚宴".equals(meal)
                            ? ScheduleSlot.STATUS_FULL : ScheduleSlot.STATUS_OPEN);
                    todo.add(s);
                }
            }
        }

        int added = 0;
        for (ScheduleSlot s : todo) {
            try {
                slotMapper.insert(s);
                added++;
            } catch (DuplicateKeyException e) {
                // 并发兜底：已有就不插
            }
        }
        if (added > 0) {
            log.info("自动补档 {} 条（{} ~ {}）", added, start, end);
        }
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
