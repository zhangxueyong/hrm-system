package com.zxy.hrm.common.service;

import com.zxy.hrm.checking.dao.LeaveRequestDao;
import com.zxy.hrm.checking.dao.SpecialholidayDao;
import com.zxy.hrm.checking.dao.SpecialworkdayDao;
import com.zxy.hrm.checking.entity.Holidays;
import com.zxy.hrm.checking.entity.Specialholiday;
import com.zxy.hrm.checking.entity.Specialworkday;
import com.zxy.hrm.checking.entity.WorkTimeEntity;
import com.zxy.hrm.common.bean.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EffectiveLeaveSerice {

    @Autowired
    LeaveRequestDao leaveRequestDao;
    @Autowired
    SpecialworkdayDao specialworkdayDao;
    @Autowired
    SpecialholidayDao specialholidayDao;


    // 模拟法定节假日的日期列表
    private static Set<LocalDate> statutoryHolidays = new HashSet<>();
    // 存储特殊时间段，需要在计算有效请假时长时扣除
    private static HashMap<String, Set<TimeRange>> specialTimeRanges = new HashMap<>();
    //存储调休工作日
    private static HashMap<String, Set<LocalDate>> specialWorkTimeRanges = new HashMap<>();


    @PostConstruct
    private void init() {
//        List<Holidays> list = leaveRequestDao.queryHoliday();
//        for (Holidays holidays : list) {
//            String holidaytime = holidays.getHolidaytime();
//            String [] hs = holidaytime.split("-");
//            statutoryHolidays.add(LocalDateTime.of(Integer.parseInt(hs[0]), Integer.parseInt(hs[1]), Integer.parseInt(hs[2]), 0, 0));
//        }
//        List<Specialworkday> specialworkdays = specialworkdayDao.querySpecialworkday();
//        Map<String, List<Specialworkday>> listMap = specialworkdays.stream().collect(Collectors.groupingBy(Specialworkday::getDeptrange));
//        for (String key : listMap.keySet()) {
//            Set<LocalDateTime> localDateTimes = new HashSet<>();
//            List<Specialworkday> specialworkdays1 = listMap.get(key);
//            for (Specialworkday specialworkday : specialworkdays) {
//                String holidaytime = specialworkday.getSpecialworktime();
//                String [] hs = holidaytime.split("-");
//                localDateTimes.add(LocalDateTime.of(Integer.parseInt(hs[0]), Integer.parseInt(hs[1]), Integer.parseInt(hs[2]), 0, 0));
//            }
//            specialWorkTimeRanges.put(key,localDateTimes);
//        }
    }


    public BigDecimal calculateEffectiveLeaveHours(LocalDateTime leaveStartDateTime, LocalDateTime leaveEndDateTime, String deptName) {
        LocalDateTime leaveStartDateTimeTemp = leaveStartDateTime;

        if (statutoryHolidays.size() == 0) {
            List<Holidays> list = leaveRequestDao.queryHoliday();

            for (Holidays holidays : list) {
                String holidaytime = holidays.getHolidaytime();
                String[] hs = holidaytime.split("-");
                statutoryHolidays.add(LocalDate.of(Integer.parseInt(hs[0]), Integer.parseInt(hs[1]), Integer.parseInt(hs[2])));
            }
        }
        //初始化各个校区及科室特殊时间段放假计划
        if (specialTimeRanges.size() == 0) {
//            specialTimeRanges.add(new TimeRange(
//                    LocalDateTime.of(2023, 11, 28, 14, 0),
//                    LocalDateTime.of(2023, 11, 28, 18, 0)
//            ));
            List<Specialholiday> specialholidays = specialholidayDao.querySpecialHoliday("");
            Map<String, List<Specialholiday>> listMap = specialholidays.stream().collect(Collectors.groupingBy(Specialholiday::getDeptname));
            for (String key : listMap.keySet()) {
                Set<TimeRange> item = new HashSet<>();
                for (Specialholiday specialholiday : listMap.get(key)) {
                    String startTime = specialholiday.getStarttime();
                    LocalDateTime start = localDateTimeContruct(startTime);
                    String endTime = specialholiday.getEndtime();
                    LocalDateTime end = localDateTimeContruct(endTime);
                    TimeRange timeRange = new TimeRange(start, end);
                    item.add(timeRange);
                }
                specialTimeRanges.put(key, item);

            }

        }
        //法定节假日周六日调休的情况
        if (specialWorkTimeRanges.isEmpty()) {
            List<Specialworkday> specialworkdays = specialworkdayDao.querySpecialworkday();
            Map<String, List<Specialworkday>> listMap = specialworkdays.stream().collect(Collectors.groupingBy(Specialworkday::getDeptrange));
            for (String key : listMap.keySet()) {
                Set<LocalDate> localDateTimes = new HashSet<>();
                List<Specialworkday> specialworkdays1 = listMap.get(key);
                for (Specialworkday specialworkday : specialworkdays1) {
                    String holidaytime = specialworkday.getSpecialworktime();
                    String[] hs = holidaytime.split("-");
                    localDateTimes.add(LocalDate.of(Integer.parseInt(hs[0]), Integer.parseInt(hs[1]), Integer.parseInt(hs[2])));
                }
                specialWorkTimeRanges.put(key, localDateTimes);
            }
        }

        WorkTimeEntity workTimeEntity = leaveRequestDao.queryWorkTimeByName(deptName);
        String ns = workTimeEntity.getMorningStart();
        String ne = workTimeEntity.getMorningEnd();
        String as = workTimeEntity.getAfternoonStart();
        String ae = workTimeEntity.getAfternoonEnd();
        String[] nss = ns.split(":");
        String[] nes = ne.split(":");
        String[] ass = as.split(":");
        String[] aes = ae.split(":");
        // 上午工作时间段
        LocalTime morningStart = LocalTime.of(Integer.parseInt(nss[0]), Integer.parseInt(nss[1]));
        LocalTime morningEnd = LocalTime.of(Integer.parseInt(nes[0]), Integer.parseInt(nes[1]));

        // 下午工作时间段
        LocalTime afternoonStart = LocalTime.of(Integer.parseInt(ass[0]), Integer.parseInt(ass[1]));
        LocalTime afternoonEnd = LocalTime.of(Integer.parseInt(aes[0]), Integer.parseInt(aes[1]));

        // 确保开始时间在结束时间之前
        if (leaveStartDateTime.isAfter(leaveEndDateTime)) {
//            throw new IllegalArgumentException("请假开始时间应早于请假结束时间");
            System.out.println("请假开始时间应早于请假结束时间");
            return BigDecimal.ZERO;
        }

        // 计算有效的请假时长
        Duration effectiveLeaveDuration = Duration.ZERO;

        // 遍历每一天
        while (leaveStartDateTime.toLocalDate().isBefore(leaveEndDateTime.toLocalDate()) ||
                (leaveStartDateTime.toLocalDate().isEqual(leaveEndDateTime.toLocalDate()) &&
                        leaveStartDateTime.toLocalTime().isBefore(leaveEndDateTime.toLocalTime()))) {

            // 判断是否是周六或周日，并且周六日不调休,是则跳过
            // 判断是否是法定节假日，是则跳过
            if (isWorkingDay(leaveStartDateTime.toLocalDate(), deptName)) {


                // 判断是否是周六或周日，是则跳过
//            if (leaveStartDateTime.getDayOfWeek() != DayOfWeek.SATURDAY &&
//                    leaveStartDateTime.getDayOfWeek() != DayOfWeek.SUNDAY) {
//
//                // 判断是否是法定节假日，是则跳过
//                if (!statutoryHolidays.contains(leaveStartDateTime)) {

                // 计算每一天的有效请假时长
                LocalDateTime dayEnd = LocalDateTime.of(leaveStartDateTime.toLocalDate(), LocalTime.MAX);
                LocalDateTime dayLeaveEnd = (leaveEndDateTime.isBefore(dayEnd)) ? leaveEndDateTime : dayEnd;

                // 上午工作时间段内的有效请假时长
                if (leaveStartDateTime.toLocalTime().isBefore(morningEnd) &&
                        dayLeaveEnd.toLocalTime().isAfter(morningStart)) {
                    LocalDateTime leaveStart = (leaveStartDateTime.toLocalTime().isBefore(morningStart)) ?
                            LocalDateTime.of(leaveStartDateTime.toLocalDate(), morningStart) : leaveStartDateTime;
                    LocalDateTime leaveEnd = (dayLeaveEnd.toLocalTime().isAfter(morningEnd)) ?
                            LocalDateTime.of(dayLeaveEnd.toLocalDate(), morningEnd) : dayLeaveEnd;
                    effectiveLeaveDuration = effectiveLeaveDuration.plus(Duration.between(leaveStart, leaveEnd));
                }

                // 下午工作时间段内的有效请假时长
                if (leaveStartDateTime.toLocalTime().isBefore(afternoonEnd) &&
                        dayLeaveEnd.toLocalTime().isAfter(afternoonStart)) {
                    LocalDateTime leaveStart = (leaveStartDateTime.toLocalTime().isBefore(afternoonStart)) ?
                            LocalDateTime.of(leaveStartDateTime.toLocalDate(), afternoonStart) : leaveStartDateTime;
                    LocalDateTime leaveEnd = (dayLeaveEnd.toLocalTime().isAfter(afternoonEnd)) ?
                            LocalDateTime.of(dayLeaveEnd.toLocalDate(), afternoonEnd) : dayLeaveEnd;
                    effectiveLeaveDuration = effectiveLeaveDuration.plus(Duration.between(leaveStart, leaveEnd));
                }
//                }
//            }
            }
            // 将开始时间移到下一天的开始
            leaveStartDateTime = LocalDateTime.of(leaveStartDateTime.toLocalDate().plusDays(1), LocalTime.MIN);
            // 扣除特殊时间段
            Set<TimeRange> ranges = specialTimeRanges.get(deptName);
            if (ranges != null) {
                for (TimeRange specialTimeRange : ranges) {
                    effectiveLeaveDuration = deductSpecialTimeRange(effectiveLeaveDuration, specialTimeRange, leaveStartDateTimeTemp, leaveEndDateTime);
                }
            }
        }

        Long hours = Math.max(0, effectiveLeaveDuration.toHours());
        BigDecimal bigDecimal = BigDecimal.valueOf(hours);
        BigDecimal day = bigDecimal.divide(BigDecimal.valueOf(8L));
        // 将时长转换为小时
        return day;
    }

    public LocalDateTime localDateTimeContruct(String startTime) {
        String[] hs = startTime.split(" ");
        String[] hsd = hs[0].split("-");
        String[] hsh = hs[1].split(":");
        LocalDateTime localdate = LocalDateTime.of(Integer.parseInt(hsd[0]), Integer.parseInt(hsd[1]), Integer.parseInt(hsd[2]), Integer.parseInt(hsh[0]), Integer.parseInt(hsh[1]));
        return localdate;
    }

    public Set<LocalDate> calculateWorkingDays(LocalDate startDate, LocalDate endDate, String deptName) {

        Set<LocalDate> workingDays = new HashSet<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (isWorkingDay(currentDate, deptName)) {
                workingDays.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
    }

    private boolean isWorkingDay(LocalDate date, String deptName) {
        // 排除周六和周日
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            Set<LocalDate> all = new HashSet<>();
            Set<LocalDate> part = new HashSet<>();
            all = specialWorkTimeRanges.get("全部");
            part = specialWorkTimeRanges.get(deptName);
            if (all != null) {
                if (all.contains(date)) {
                    return true;
                }
            }
            if (part != null) {
                if (part.contains(date)) {
                    return true;
                }
            }

            return false;
        }
        // 排除法定节假日
        return !statutoryHolidays.contains(date);
    }

    /**
     * 扣除特殊时间段的请假时长
     *
     * @param originalDuration   原始请假时长
     * @param specialTimeRange   特殊时间段
     * @param leaveStartDateTime 请假开始时间
     * @param leaveEndDateTime   请假结束时间
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
     * 抵消迟到、早退、未签到、未签离次数
     * @param leaveStartDateTime
     * @param leaveEndDateTime
     * @param deptName
     * @param type
     * @return
     */
    public  Map<String, Integer> deleteLeaveReq(LocalDateTime leaveStartDateTime, LocalDateTime leaveEndDateTime, String deptName,String type) {
        Map<String, Integer> res = new HashMap<>();
        WorkTimeEntity workTimeEntity = leaveRequestDao.queryWorkTimeByName(deptName);
        String ns = workTimeEntity.getMorningStart();
        String ne = workTimeEntity.getMorningEnd();
        String as = workTimeEntity.getAfternoonStart();
        String ae = workTimeEntity.getAfternoonEnd();
        String[] nss = ns.split(":");
        String[] nes = ne.split(":");
        String[] ass = as.split(":");
        String[] aes = ae.split(":");
        // 上午工作时间段
        LocalTime morningStart = LocalTime.of(Integer.parseInt(nss[0]), Integer.parseInt(nss[1]));
        LocalTime morningEnd = LocalTime.of(Integer.parseInt(nes[0]), Integer.parseInt(nes[1]));

        // 下午工作时间段
        LocalTime afternoonStart = LocalTime.of(Integer.parseInt(ass[0]), Integer.parseInt(ass[1]));
        LocalTime afternoonEnd = LocalTime.of(Integer.parseInt(aes[0]), Integer.parseInt(aes[1]));
//        LocalTime morningStart = LocalTime.of(7, 30);
//        LocalTime morningEnd = LocalTime.of(11, 30);
//
//        // 下午工作时间段
//        LocalTime afternoonStart = LocalTime.of(13, 30);
//        LocalTime afternoonEnd = LocalTime.of(17, 30);

        //未签到次数
        int wqdCount = 0;
        //未签离次数
        int wqlCount = 0;
        //迟到次数
        int cdCount = 0;
        //早退次数
        int ztCount = 0;
        // 遍历每一天
        while (leaveStartDateTime.toLocalDate().isBefore(leaveEndDateTime.toLocalDate()) ||
                (leaveStartDateTime.toLocalDate().isEqual(leaveEndDateTime.toLocalDate()) &&
                        leaveStartDateTime.toLocalTime().isBefore(leaveEndDateTime.toLocalTime()))) {
            // 计算每一天的有效请假时长
            LocalDateTime dayEnd = LocalDateTime.of(leaveStartDateTime.toLocalDate(), LocalTime.MAX);
            LocalDateTime dayLeaveEnd = (leaveEndDateTime.isBefore(dayEnd)) ? leaveEndDateTime : dayEnd;
            LocalTime start = leaveStartDateTime.toLocalTime();
            LocalTime end  = dayLeaveEnd.toLocalTime();
            if("未签到".equals(type)||"未签退".equals(type)){
                if (start.isBefore(morningStart)||start.equals(morningStart)){
                    if (end.isBefore(morningEnd)){
                        wqdCount++;
                    }else if ((end.isAfter(morningEnd)||end.equals(morningEnd))&&end.isBefore(afternoonStart)){
                        if ("初中".equals(deptName)) {
                            wqlCount++;
                        }
                        wqdCount++;
                    }else if ((end.equals(afternoonStart)||end.isAfter(afternoonStart))&& end.isBefore(afternoonEnd)){
                        if ("初中".equals(deptName)) {
                            wqlCount++;
                        }
                        wqdCount = wqdCount+2;
                    }else if (end.isAfter(afternoonEnd)||end.equals(afternoonEnd)){
                        if ("初中".equals(deptName)) {
                            wqlCount=wqlCount+2;
                        }else {
                            wqlCount++;
                        }
                        wqdCount = wqdCount+2;
                    }
                }else if (start.isAfter(morningStart)&&(start.isBefore(morningEnd)||start.equals(morningEnd))){
                    if ((end.isAfter(morningEnd)||end.equals(morningEnd))&&end.isBefore(afternoonStart)){
                        if ("初中".equals(deptName)) {
                            wqlCount++;
                        }
                        wqdCount++;
                    }else if ((end.equals(afternoonStart)||end.isAfter(afternoonStart))&& end.isBefore(afternoonEnd)){
                        if ("初中".equals(deptName)) {
                            wqlCount++;
                        }
                        wqdCount = wqdCount+2;
                    }else if (end.isAfter(afternoonEnd)||end.equals(afternoonEnd)){
                        if ("初中".equals(deptName)) {
                            wqlCount=wqlCount+2;
                        }else {
                            wqlCount++;
                        }
                        wqdCount = wqdCount+2;
                    }
                }else if (start.isAfter(morningEnd)&&(start.isBefore(afternoonStart)||start.equals(afternoonStart))){
                    if ((end.equals(afternoonStart)||end.isAfter(afternoonStart))&& end.isBefore(afternoonEnd)){
                        if ("初中".equals(deptName)) {
                            wqlCount++;
                        }
                        wqdCount = wqdCount+2;
                    }else if (end.isAfter(afternoonEnd)||end.equals(afternoonEnd)){
                        if ("初中".equals(deptName)) {
                            wqlCount=wqlCount+2;
                        }else {
                            wqlCount++;
                        }
                        wqdCount = wqdCount+2;
                    }
                }else if (start.isAfter(afternoonStart)&&(start.isBefore(afternoonEnd)||start.equals(afternoonEnd))){
                    if (end.isAfter(afternoonEnd)||end.equals(afternoonEnd)){
                        if ("初中".equals(deptName)) {
                            wqlCount=wqlCount+2;
                        }else {
                            wqlCount++;
                        }
                        wqdCount = wqdCount+2;
                    }
                }
            }else if ("上午迟到".equals(type)){
                if (start.equals(morningStart)||start.isBefore(morningStart)){
                    cdCount++;
                    break;
                }

            }else if ("下午迟到".equals(type)){
                if(start.isBefore(afternoonStart)|| start.equals(afternoonStart)){
                    cdCount++;
                    break;
                }
            }else if ("初中".equals(deptName)&&"上午早退".equals(type)){
                if ((start.isBefore(morningEnd)||start.equals(morningEnd))&&(end.isAfter(morningEnd)||end.equals(morningEnd))){
                    ztCount++;
                    break;
                }
            }else if ("下午早退".equals(type)){
                if ((start.isBefore(afternoonEnd)||start.equals(afternoonEnd))&& (end.equals(afternoonEnd)||end.isAfter(afternoonEnd))){
                    ztCount++;
                    break;
                }
            }

            // 将开始时间移到下一天的开始
            leaveStartDateTime = LocalDateTime.of(leaveStartDateTime.toLocalDate().plusDays(1), LocalTime.MIN);
        }
        res.put("wql", wqlCount);
        res.put("wqd", wqdCount);
        res.put("cd", cdCount);
        res.put("zt", ztCount);
        return res;

    }

    public static void main(String[] args) {
        // 请假开始时间和结束时间
        LocalDateTime leaveStartDateTime = LocalDateTime.of(2023, 11, 20, 13, 0);
        LocalDateTime leaveEndDateTime = LocalDateTime.of(2023, 11, 21, 18, 0);
   //     Map<String, Integer> res = deleteLeaveReq(leaveStartDateTime, leaveEndDateTime, "科室");
      //  System.out.println("未签到："+res.get("wqd"));
   //     System.out.println("未签离："+res.get("wql"));
        // 计算有效请假时长
        //  long effectiveLeaveHours = calculateEffectiveLeaveHours(leaveStartDateTime, leaveEndDateTime);

        // 输出结果
        //  System.out.println("有效请假时长：" + effectiveLeaveHours + "小时");
    }
}