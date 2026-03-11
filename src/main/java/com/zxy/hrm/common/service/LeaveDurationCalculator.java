package com.zxy.hrm.common.service;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

public class LeaveDurationCalculator {

    // 存储特殊时间段，需要在计算有效请假时长时扣除
    private static Set<TimeRange> specialTimeRanges = new HashSet<>();

    // 初始化特殊时间段
    static {
        specialTimeRanges.add(new TimeRange(
                LocalDateTime.of(2023, 11, 28, 14, 0),
                LocalDateTime.of(2023, 11, 28, 18, 0)
        ));
    }

    /**
     * 计算有效请假时长
     *
     * @param leaveStartDateTime 请假开始时间
     * @param leaveEndDateTime   请假结束时间
     * @return 有效请假时长（小时）
     */
    public static long calculateEffectiveLeaveHours(LocalDateTime leaveStartDateTime, LocalDateTime leaveEndDateTime) {
        LocalDateTime leaveStartDateTimeTemp = leaveStartDateTime;
        // 上午上班开始时间
        LocalTime morningStart = LocalTime.of(7, 50);
        // 上午上班结束时间
        LocalTime morningEnd = LocalTime.of(11, 50);

        // 下午上班开始时间
        LocalTime afternoonStart = LocalTime.of(14, 0);
        // 下午上班结束时间
        LocalTime afternoonEnd = LocalTime.of(18, 0);

        // 如果请假开始时间晚于结束时间，抛出异常
        if (leaveStartDateTime.isAfter(leaveEndDateTime)) {
            throw new IllegalArgumentException("请假开始时间应早于请假结束时间");
        }

        // 初始化有效请假时长为0
        Duration effectiveLeaveDuration = Duration.ZERO;

        // 遍历每一天的请假情况
        while (leaveStartDateTime.isBefore(leaveEndDateTime)) {
            // 排除周六和周日
            if (leaveStartDateTime.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    leaveStartDateTime.getDayOfWeek() != DayOfWeek.SUNDAY) {

                // 当天的最后时间
                LocalDateTime dayEnd = LocalDateTime.of(leaveStartDateTime.toLocalDate(), LocalTime.MAX);
                // 实际请假结束时间为请假结束时间和当天最后时间的较小值
                LocalDateTime dayLeaveEnd = (leaveEndDateTime.isBefore(dayEnd)) ? leaveEndDateTime : dayEnd;

                // 计算上午有效请假时长
                if (leaveStartDateTime.toLocalTime().isBefore(morningEnd) &&
                        dayLeaveEnd.toLocalTime().isAfter(morningStart)) {
                    LocalDateTime leaveStart = (leaveStartDateTime.toLocalTime().isBefore(morningStart)) ?
                            LocalDateTime.of(leaveStartDateTime.toLocalDate(), morningStart) : leaveStartDateTime;
                    LocalDateTime leaveEnd = (dayLeaveEnd.toLocalTime().isAfter(morningEnd)) ?
                            LocalDateTime.of(dayLeaveEnd.toLocalDate(), morningEnd) : dayLeaveEnd;
                    effectiveLeaveDuration = effectiveLeaveDuration.plus(Duration.between(leaveStart, leaveEnd));
                }

                // 计算下午有效请假时长
                if (leaveStartDateTime.toLocalTime().isBefore(afternoonEnd) &&
                        dayLeaveEnd.toLocalTime().isAfter(afternoonStart)) {
                    LocalDateTime leaveStart = (leaveStartDateTime.toLocalTime().isBefore(afternoonStart)) ?
                            LocalDateTime.of(leaveStartDateTime.toLocalDate(), afternoonStart) : leaveStartDateTime;
                    LocalDateTime leaveEnd = (dayLeaveEnd.toLocalTime().isAfter(afternoonEnd)) ?
                            LocalDateTime.of(dayLeaveEnd.toLocalDate(), afternoonEnd) : dayLeaveEnd;
                    effectiveLeaveDuration = effectiveLeaveDuration.plus(Duration.between(leaveStart, leaveEnd));
                }
            }

            // 进入下一天
            leaveStartDateTime = LocalDateTime.of(leaveStartDateTime.toLocalDate().plusDays(1), LocalTime.MIN);
        }

        // 扣除特殊时间段
        for (TimeRange specialTimeRange : specialTimeRanges) {
            effectiveLeaveDuration = deductSpecialTimeRange(effectiveLeaveDuration, specialTimeRange, leaveStartDateTimeTemp, leaveEndDateTime);
        }

        // 确保有效请假时长不为负数
        return Math.max(0, effectiveLeaveDuration.toHours());
    }

    /**
     * 扣除特殊时间段的请假时长
     *
     * @param originalDuration      原始请假时长
     * @param specialTimeRange      特殊时间段
     * @param leaveStartDateTime    请假开始时间
     * @param leaveEndDateTime      请假结束时间
     * @return 扣除特殊时间段后的请假时长
     */
    private static Duration deductSpecialTimeRange(Duration originalDuration, TimeRange specialTimeRange, LocalDateTime leaveStartDateTime, LocalDateTime leaveEndDateTime) {
        // 计算特殊时间段与请假时间的重叠部分
        LocalDateTime overlapStart = (leaveStartDateTime.isBefore(specialTimeRange.getEnd()) && leaveEndDateTime.isAfter(specialTimeRange.getStart())) ?
                (leaveStartDateTime.isAfter(specialTimeRange.getStart()) ? leaveStartDateTime : specialTimeRange.getStart()) :
                LocalDateTime.MIN;
        LocalDateTime overlapEnd = (leaveEndDateTime.isAfter(specialTimeRange.getStart()) && leaveStartDateTime.isBefore(specialTimeRange.getEnd())) ?
                (leaveEndDateTime.isBefore(specialTimeRange.getEnd()) ? leaveEndDateTime : specialTimeRange.getEnd()) :
                LocalDateTime.MIN;

        // 如果有重叠部分，扣除时长
        if (overlapStart.isBefore(overlapEnd)) {
            return originalDuration.minus(Duration.between(overlapStart, overlapEnd));
        }
        return originalDuration;
    }

    /**
     * 表示一个时间范围的内部类
     */
    static class TimeRange {
        private final LocalDateTime start;
        private final LocalDateTime end;

        public TimeRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }
    }

    // 主函数用于测试
    public static void main(String[] args) {
        LocalDateTime leaveStartDateTime = LocalDateTime.of(2023, 11, 27, 7, 50);
        LocalDateTime leaveEndDateTime = LocalDateTime.of(2023, 11, 28, 18, 0);

        // 计算有效请假时长
        long effectiveLeaveHours = calculateEffectiveLeaveHours(leaveStartDateTime, leaveEndDateTime);

        // 输出结果
        System.out.println("有效请假时长：" + effectiveLeaveHours + "小时");
    }
}