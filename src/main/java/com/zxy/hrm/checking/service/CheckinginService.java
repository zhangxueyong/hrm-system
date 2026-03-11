package com.zxy.hrm.checking.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxy.hrm.checking.dao.LeaveRequestDao;
import com.zxy.hrm.checking.dao.SpecialholidayDao;
import com.zxy.hrm.checking.dao.SpecialworkdayDao;
import com.zxy.hrm.checking.entity.*;
import com.zxy.hrm.common.bean.DiXiaoDays;
import com.zxy.hrm.common.bean.DiXiaoTotal;
import com.zxy.hrm.common.bean.LeaveRequestDiXiao;
import com.zxy.hrm.common.service.EffectiveLeaveSerice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CheckinginService {

    @Autowired
    private LeaveRequestDao leaveRequestDao;
    @Autowired
    private EffectiveLeaveSerice effectiveLeaveSerice;
    @Autowired
    private SpecialholidayDao specialholidayDao;
    @Autowired
    private SpecialworkdayDao specialworkdayDao;

    public void dealLeaveRequestBaseData(MultipartFile file) {
        try {
            List<Leaverequests> leaverequests = EasyExcel.read(file.getInputStream())
                    .head(Leaverequests.class)
                    .sheet()
                    .doReadSync();
            List<LeaverequestExt> leaverequestExts = new ArrayList<>();
            LocalDate localDate = LocalDate.now();
            int month = localDate.getMonth().getValue()-1;
            int year = localDate.getYear();
            if (month == 0) {
                month=12;
                year--;
            }
            final int y = year;
            final int m = month;
            leaverequests.forEach(req -> {
                LeaverequestExt leaverequestExt = new LeaverequestExt();
                BeanUtils.copyProperties(req, leaverequestExt);
                leaverequestExt.setMonth(m);
                leaverequestExt.setYears(y);
                leaverequestExts.add(leaverequestExt);
//                System.out.println(req.getName());
            });
            leaveRequestDao.saveLeaverequests(leaverequestExts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<LeaverequestExt> queryLeaveRequest(Leaverequests leaverequests) {

        return leaveRequestDao.queryLeaveRequest(leaverequests);
    }

    public PageInfo<LeaverequestExt> getLeaveRequestsByCondition(int pageNum, int pageSize, Leaverequests leaverequests) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 执行条件查询
        List<LeaverequestExt> leaveRequests = leaveRequestDao.queryLeaveRequest(leaverequests);

        // 处理查询结果...

        // 获取分页信息
        return new PageInfo<>(leaveRequests);
    }

    public void uploadAttedanceBaseDate(MultipartFile file) {
        try {
            List<AttendanceTemplate> attendanceTemplates = EasyExcel.read(file.getInputStream())
                    .head(AttendanceTemplate.class)
                    .sheet()
                    .doReadSync();
            final String code = "CHECKING_IN_BASE";
            Map<String, String> dictType = queryDictTypeByCode(code);
            List<Employeeattendancemonthly> employeeattendancemonthlies = new ArrayList<>();
            LocalDate localDate = LocalDate.now();
            int m = localDate.getMonth().getValue()-1;
            int y = localDate.getYear();
            if (m == 0) {
                m=12;
                y--;
            }
            String year = String.valueOf(y);

            String month = String.valueOf(m);

            for (AttendanceTemplate temp : attendanceTemplates) {
                // 获取对象的Class
                Class<?> clazz = temp.getClass();
                // 获取所有声明的字段
                Field[] fields = clazz.getDeclaredFields();
                // 遍历每个字段并获取值
                String name = temp.getName();
                String idCard = temp.getIdCard();
                for (Field field : fields) {
                    // 设置字段可访问，即使是私有字段也可以访问
                    field.setAccessible(true);
                    Employeeattendancemonthly emp = new Employeeattendancemonthly();
                    try {
                        // 获取字段值
                        Object value = field.get(temp);
                        // 打印字段名和值
                        String fieldName = field.getName();
                        if ("name".equals(field.getName())||"idCard".equals(fieldName)) {
                           continue;
                        } else {
                            emp.setId(UUID.randomUUID().toString());
                            emp.setYear(year);
                            emp.setMonth(month);
                            emp.setName(name);
                            emp.setIdCard(idCard);
                            String day = fieldName.replace("day", "");
                            emp.setDay(day);
                            if(value==null){
                                emp.setBaseflag("");
                            }else {
                                emp.setBaseflag(value.toString());
                            }

                            if (dictType.containsKey(value)){
                                String desc = dictType.get(value);
                                emp.setDesc(desc);
                                if ("正常，".trim().equals(desc.trim())) {
                                    emp.setStatus(0);
                                } else {
                                    emp.setStatus(1);
                                }
                            }else {
                                emp.setDesc("系统未知状态");
                                emp.setStatus(1);
                            }


                            employeeattendancemonthlies.add(emp);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }

            leaveRequestDao.saveAttedanceBaseDate(employeeattendancemonthlies);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> queryDictTypeByCode(String code) {
        List<Dictitem> dictitems = leaveRequestDao.queryDictTypeByCode(code);
        Map<String, String> res = dictitems.stream().collect(Collectors.toMap(Dictitem::getDicttype, Dictitem::getDictValue));
        return res;

    }

    public PageInfo<Employeeattendancemonthly> queryKaoQinRequest(Integer pageNum, Integer pageSize, Employeeattendancemonthly employeeattendancemonthly) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 执行条件查询
        List<Employeeattendancemonthly> leaveRequests = leaveRequestDao.queryKaoQinRequest(employeeattendancemonthly);

//        for (Employeeattendancemonthly leaveRequest : leaveRequests) {
//            String str =leaveRequest.getStatus()==0?"正常":"异常";
//            leaveRequest.set
//        }

        // 获取分页信息
        return new PageInfo<>(leaveRequests);
    }

    public void uploadHuiZongBaseData(MultipartFile file) {
        try {
            ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(file.getInputStream());
            ExcelReader build = EasyExcel.read(file.getInputStream()).build();
            List<ReadSheet> readSheets = build.excelExecutor().sheetList();

            for (int index = 0; ; index++) {
                //跳过表头两行数据
                int indxFlag=0;
                ReadSheet sheet = readSheets.get(index);
                String dep = sheet.getSheetName();
                ExcelReaderSheetBuilder readerSheetBuilder = excelReaderBuilder.sheet(index).sheetName(dep);
                List<MonthlyAttendance> monthlyAttendances = new ArrayList<>();
                if (readerSheetBuilder==null){
                    break;
                }else {
                   String deptName =  readerSheetBuilder.build().getSheetName();
                    List<HuiZongTemplateHead> huiZongTemplateHeads = readerSheetBuilder.head(HuiZongTemplateHead.class).doReadSync();

                    for (HuiZongTemplateHead huiZongTemplateHead : huiZongTemplateHeads) {
//                        indxFlag++;
//                        if (indxFlag<2){
//                            continue;
//                        }
                        //月
                        String yAm = huiZongTemplateHead.getYuefen();
                        //身份证
                        String idCard = huiZongTemplateHead.getIdCard();
                        //员工姓名
                        String name = huiZongTemplateHead.getUserLname();

                        MonthlyAttendance monthlyAttendance = new MonthlyAttendance();
                        monthlyAttendance.setId(UUID.randomUUID().toString());
                        monthlyAttendance.setName(name);
                        monthlyAttendance.setDepartment(deptName);
                        monthlyAttendance.setIdCard(idCard);
                        String [] dates = yAm.split("-");
                        monthlyAttendance.setYears(dates[0]);
                        monthlyAttendance.setMonths(dates[1]);
                        monthlyAttendance.setIdCard(idCard);
                        if (idCard.equals("371423199904055023")){
                            System.out.println("asdasd");
                        }
                        //应出天数
                        monthlyAttendance.setWorkingDays(huiZongTemplateHead.getYingchu1());
                        //迟到次数
                         Long cdc = Long.valueOf(huiZongTemplateHead.getCdci());
                        if (cdc!=0L){
                            //查询迟到日期
                            DiXiaoTotal diXiaoTotal = queryErrDay(yAm, idCard, name, "迟到", deptName);
                            monthlyAttendance.setLateCount(cdc-diXiaoTotal.getTotal()<0?0:cdc-diXiaoTotal.getTotal());
                            monthlyAttendance.setLateDates(diXiaoTotal.getDays());
                        }
                        //早退次数
                        long ztc = Long.valueOf(huiZongTemplateHead.getZtci());
                        //查询早退日期i
                        if (ztc!=0L){
                            DiXiaoTotal diXiaoTotal = queryErrDay(yAm, idCard, name, "早退", deptName);
                            monthlyAttendance.setEarlyLeaveCount(ztc-diXiaoTotal.getTotal()<0?0:ztc-diXiaoTotal.getTotal());
                            monthlyAttendance.setEarlyLeaveDates(diXiaoTotal.getDays());

                        }
                        if (idCard.equals("371423199904055023")){
                            System.out.println("371423199904055023");
                        }
                        //请假抵消未签到和未签离次数
//                        LeaveRequestDiXiao leaveRequestDiXiao = linkLeaveRequest(idCard, deptName,"");
//                        Integer totalWqd = leaveRequestDiXiao.getWqd();
//                        Integer totalWql = leaveRequestDiXiao.getWql();
//                        List<DiXiaoDays> diXiaoDays =leaveRequestDiXiao.getDiXiaoDays();

                        //未签到次数
                        long wqd = Long.valueOf(huiZongTemplateHead.getWqd());
                 //       long trueWqd = wqd-totalWqd<0?0:wqd-totalWqd;
                        if (wqd!=0L){
                            DiXiaoTotal diXiaoTotal = queryErrDay(yAm, idCard, name, "未签到", deptName);
                            monthlyAttendance.setUnsignedInCount(wqd-diXiaoTotal.getTotal()<0?0:wqd-diXiaoTotal.getTotal());
                            monthlyAttendance.setUnsignedInDates(diXiaoTotal.getDays());
                        }

                        //未签退次数
                        long wqt = Long.valueOf(huiZongTemplateHead.getWqt());
                      //  long trueWqt = wqt-totalWql<0?0:wqt-totalWql;
                        if (wqt!=0L){
                            DiXiaoTotal diXiaoTotal = queryErrDay(yAm, idCard, name, "未签退", deptName);
                            monthlyAttendance.setUnsignedOutCount(wqt-diXiaoTotal.getTotal()<0?0:wqt-diXiaoTotal.getTotal());

                            monthlyAttendance.setUnsignedOutDates(diXiaoTotal.getDays());
                        }

                        //事假天数
                        Map<String, String> sjMap = queryLeaveRequest(yAm, idCard, "事假",deptName);
                        monthlyAttendance.setPersonalLeaveDays(sjMap.get("total"));
                        monthlyAttendance.setPersonalLeaveDates(sjMap.get("days"));
                        //病假天数及日期
                        Map<String, String> sickMap = queryLeaveRequest(yAm, idCard, "病假",deptName);
                        monthlyAttendance.setSickLeaveDays(sickMap.get("total"));
                        monthlyAttendance.setSickLeaveDates(sickMap.get("days"));
                        //婚嫁天数及日期
                        Map<String, String> marryMap = queryLeaveRequest(yAm, idCard, "婚假",deptName);
                        monthlyAttendance.setMarriageLeaveDays(marryMap.get("total"));
                        monthlyAttendance.setMarriageLeaveDates(marryMap.get("days"));
                        //丧天数及日期
                        Map<String, String> dead = queryLeaveRequest(yAm, idCard, "丧假",deptName);
                        monthlyAttendance.setBereavementLeaveDays(dead.get("total"));
                        monthlyAttendance.setBereavementLeaveDates(dead.get("days"));
                        //陪护假天数及日期
                        Map<String, String> paternity = queryLeaveRequest(yAm, idCard, "陪护假",deptName);
                        monthlyAttendance.setPaternityLeaveDays(paternity.get("total"));
                        monthlyAttendance.setPaternityLeaveDates(paternity.get("days"));
                        //产假天数及日期
                        Map<String, String> maternit = queryLeaveRequest(yAm, idCard, "产假",deptName);
                        monthlyAttendance.setMaternityLeaveDays(maternit.get("total"));
                        monthlyAttendance.setMaternityLeaveDates(maternit.get("days"));

                        monthlyAttendances.add(monthlyAttendance);

                    }
                }
                leaveRequestDao.saveMonthlyAttendances(monthlyAttendances);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 匹配请假表
     */
    public LeaveRequestDiXiao linkLeaveRequest(String idCard, String deptName,String type){
        LeaveRequestDiXiao leaveRequestDiXiao = new LeaveRequestDiXiao();
        LeaverequestExt ext = new LeaverequestExt();
        ext.setIdCard(idCard);
        Integer totalWql = 0;
        Integer totalWqd = 0;
        List<LeaverequestExt> leaverequestExts=new ArrayList<>();
        leaverequestExts = leaveRequestDao.queryLeaveRequest(ext);
        if (idCard.equals("370305198302200429")){
            System.out.println("");
        }
        List<DiXiaoDays> diXiaoDays = new ArrayList<>();
        for (LeaverequestExt leaverequestExt : leaverequestExts) {
            String startTime = leaverequestExt.getStartTime();
            String endTime = leaverequestExt.getEndTime();
            DiXiaoDays diXiao = new DiXiaoDays();
            LocalDateTime startTimeLocal = effectiveLeaveSerice.localDateTimeContruct(startTime);
            LocalDateTime endTimeLocal = effectiveLeaveSerice.localDateTimeContruct(endTime);
//                        LocalDateTime leaveStartDateTime = LocalDateTime.of(2023, 11, 27, 8, 40);
//                        LocalDateTime leaveEndDateTime = LocalDateTime.of(2023, 11, 27, 17, 0);
            Map<String, Integer> stringIntegerMap = effectiveLeaveSerice.deleteLeaveReq(startTimeLocal, endTimeLocal, deptName,type);
            totalWqd =totalWqd+stringIntegerMap.get("wqd");
            totalWql =totalWql+stringIntegerMap.get("wql");
            Set<LocalDate> localDates = effectiveLeaveSerice.calculateWorkingDays(startTimeLocal.toLocalDate(), endTimeLocal.toLocalDate(),deptName);
            List<String> days = new ArrayList<>();
            for (LocalDate localDate : localDates) {

                String day = localDate.toString();
                days.add(day);
            }
            diXiao.setLeaveType(leaverequestExt.getType());
            diXiao.setDays(days);
            diXiaoDays.add(diXiao);

        }
        leaveRequestDiXiao.setWqd(totalWqd);
        leaveRequestDiXiao.setWql(totalWql);
        leaveRequestDiXiao.setDiXiaoDays(diXiaoDays);
        return leaveRequestDiXiao;

    }

    /**
     * 更具名字和身份证号查询异常具体是那些天
     * @param date 2023-10
     * @param idCard 身份证
     * @param empName 员工名字
     * @param flag  迟到，早退，未签到，为签退
     * @return
     */

    public DiXiaoTotal queryErrDay(String date,String idCard,String empName,String flag,String deptName){

        String [] dates = date.split("-");
        Map<String,String> param = new HashMap<>();
        param.put("idCard",idCard);
        param.put("empName",empName);
        param.put("flag",flag);
        param.put("year",dates[0]);
        param.put("month",dates[1]);
        List<Employeeattendancemonthly> empErrDays= new ArrayList<>();
        //请假抵消迟到次数
        Integer dxCd=0;
        //请假抵消早退次数
        Integer dxZt=0;
        //请假抵消未签到次数
        Integer dxWqd=0;
        //请假抵消未签离次数
        Integer dxWql=0;

        DiXiaoTotal diXiaoTotal = new DiXiaoTotal();

        if("未签到".equals(flag)){
            empErrDays= leaveRequestDao.queryEmpErrDaysByWqd(param);
            diXiaoTotal= getDixiaoNumber(idCard, flag, empErrDays, date, deptName);
        }else if ("未签退".equals(flag)){
            empErrDays= leaveRequestDao.queryEmpErrDaysByWqt(param);
            diXiaoTotal= getDixiaoNumber(idCard, flag, empErrDays, date, deptName);

        }else if ("迟到".equals(flag)){
            param.put("flag","上午迟到");
            empErrDays= leaveRequestDao.queryEmpErrDays(param);
            DiXiaoTotal dixiaoNumber = getDixiaoNumber(idCard, "上午迟到", empErrDays, date, deptName);
            param.put("flag","下午迟到");
            empErrDays= leaveRequestDao.queryEmpErrDays(param);
            DiXiaoTotal dixiaoNumber2 = getDixiaoNumber(idCard, "下午迟到", empErrDays, date, deptName);
            Integer total = dixiaoNumber.getTotal();
            Integer total1 = dixiaoNumber2.getTotal();
            dxCd = total+total1;
            diXiaoTotal.setTotal(dxCd);
            diXiaoTotal.setDays(dixiaoNumber.getDays()+dixiaoNumber2.getDays());
        }else if ("早退".equals(flag)){
            param.put("flag","上午早退");
            empErrDays= leaveRequestDao.queryEmpErrDays(param);
            DiXiaoTotal dixiaoNumber = getDixiaoNumber(idCard, "上午早退", empErrDays, date, deptName);
            param.put("flag","下午早退");
            empErrDays= leaveRequestDao.queryEmpErrDays(param);
            DiXiaoTotal dixiaoNumber2 = getDixiaoNumber(idCard, "下午早退", empErrDays, date, deptName);
            Integer total = dixiaoNumber.getTotal();
            Integer total1 = dixiaoNumber2.getTotal();
            dxCd = total+total1;
            diXiaoTotal.setTotal(dxCd);
            diXiaoTotal.setDays(dixiaoNumber.getDays()+dixiaoNumber2.getDays());

        }
        //查询这个人的请假数据，如果存在数据，则删除缺勤天数，根据异常考勤日期，去匹配请假开始时间，
        if (idCard.equals("371423198503020044")){
            System.out.println("371423198503020044");
        }
       // getDixiaoNumber(flag, diXiaoDays, empErrDays);
        return diXiaoTotal;

    }

    private DiXiaoTotal getDixiaoNumber(String idCard,String type,  List<Employeeattendancemonthly> empErrDays,String date,String deptName) {
        DiXiaoTotal diXiaoTotal = new DiXiaoTotal();
        Map<String,String> param = new HashMap<>();
        Map<String,String> result = new HashMap<>();
        StringBuffer res = new StringBuffer("");
        String [] dates = date.split("-");
        param.put("idCard",idCard);
        param.put("year",dates[0]);
        param.put("month",dates[1]);
        Set<String> total = new HashSet<>();
        int totalNum = 0;
        for (Employeeattendancemonthly empErrDay : empErrDays) {
            String year = empErrDay.getYear();
            StringBuffer errDay = new StringBuffer(year);
            String month = empErrDay.getMonth();
            String day = empErrDay.getDay();
            errDay.append("-").append(month).append("-").append(day);
            String dada =errDay.toString();
            String desc = empErrDay.getDesc();
            desc = desc.replace(";","");
            param.put("errDay",dada);
            if (total.contains(dada)){continue;}
            List<LeaverequestExt> requests = leaveRequestDao.queryLeaveRequestByErrDay(param);
            List<DiXiaoDays> diXiaoDays = new ArrayList<>();
            if (requests.size()>0 ){
                for (LeaverequestExt request : requests) {

                    String startTime = request.getStartTime();
                    String endTime = request.getEndTime();
                    DiXiaoDays diXiao = new DiXiaoDays();
                    LocalDateTime startTimeLocal = effectiveLeaveSerice.localDateTimeContruct(startTime);
                    LocalDateTime endTimeLocal = effectiveLeaveSerice.localDateTimeContruct(endTime);
                    Set<LocalDate> localDates = effectiveLeaveSerice.calculateWorkingDays(startTimeLocal.toLocalDate(), endTimeLocal.toLocalDate(),deptName);

                    Map<String, Integer> stringIntegerMap = effectiveLeaveSerice.deleteLeaveReq(startTimeLocal, endTimeLocal, deptName,type);
                    if ("上午迟到".equals(type)||"下午迟到".equals(type)){
                        totalNum=totalNum+stringIntegerMap.get("cd");
                    }
                    if ("上午早退".equals(type)||"下午早退".equals(type)){
                        totalNum = totalNum+stringIntegerMap.get("zt");
                    }
                    if ("未签到".equals(type)|| "为签退".equals(type)){
                        for (LocalDate localDate : localDates) {
                            String temp = localDate.toString();
                            if (total.contains(temp)){
                                continue;
                            }
                            total.add(temp);
                        }
                        if ("未签到".equals(type)){
                           totalNum = totalNum+ stringIntegerMap.get("wqd");
                        }
                        if ("为签退".equals(type)){
                            totalNum = totalNum+ stringIntegerMap.get("wql");
                        }

                    }
                    List<String> days = new ArrayList<>();
                    for (LocalDate localDate : localDates) {

                        String dayTemp = localDate.toString();
                        days.add(dayTemp);
                    }
                    diXiao.setLeaveType(request.getType());
                    diXiao.setDays(days);
                    diXiaoDays.add(diXiao);
                }
            }
            int aaa=0;
            if("未签到".equals(type)||"未签退".equals(type)){
                HashMap<String,List<String>> temp = new HashMap<>();
                for (DiXiaoDays diXiaoDay : diXiaoDays) {
                    String leaveType = diXiaoDay.getLeaveType();
                    if (temp.containsKey(leaveType)){
                        List<String> list = temp.get(leaveType);
                        list.addAll(diXiaoDay.getDays());
                        temp.put(leaveType,list);
                    }else {
                        temp.put(leaveType,diXiaoDay.getDays());
                    }
                    List<String> days = diXiaoDay.getDays();

                }
                for (String s : temp.keySet()) {
                    List<String> list = temp.get(s);
                    if (list.contains(dada)){
                        res.append(day).append("号").append(desc).append("-请假：").append(s).append(" | ");
                        aaa=1;
                    }
                }
                if (aaa==0){
                    res.append(day).append("号").append(desc).append(" | ");
                }
            }else{
                res.append(day).append("号").append(desc).append(" | ");
            }



        }
        diXiaoTotal.setDays(res.toString());
        diXiaoTotal.setTotal(totalNum);
        return diXiaoTotal;
    }

    /**
     * 查询单月请假时长
     * @param date 月份
     * @param idCard 身份证
     * @param type 请假类型
     * @return
     */
    public Map<String,String> queryLeaveRequest(String date, String idCard, String type,String deptName){
        Map<String,String> param = new HashMap<>();
        Map<String,String> result = new HashMap<>();
        StringBuffer res = new StringBuffer("");
        String [] dates = date.split("-");
        param.put("idCard",idCard);
        param.put("type",type);
        param.put("year",dates[0]);
        param.put("month",dates[1]);
        List<LeaverequestExt> requests = leaveRequestDao.queryLeaveRequestByType(param);
        BigDecimal total = BigDecimal.ZERO;
        for (LeaverequestExt request : requests) {
            //2023-10-31 13:59:00
            String startTime = request.getStartTime();
            String endTime = request.getEndTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date startDate = dateFormat.parse(startTime);
                Date endDate = dateFormat.parse(endTime);
                // 获取年、月、日、时、分、秒
                int year = startDate.getYear() + 1900; // 请注意：getYear()返回的是相对于1900年的年份
                int month = startDate.getMonth() + 1; // 月份从0开始，所以要加1
                int day = startDate.getDate();
                int hour = startDate.getHours();
                int minute = startDate.getMinutes();
                int second = startDate.getSeconds();

                int yearE = endDate.getYear() + 1900; // 请注意：getYear()返回的是相对于1900年的年份
                int monthE = endDate.getMonth() + 1; // 月份从0开始，所以要加1
                int dayE = endDate.getDate();
                int hourE = endDate.getHours();
                int minuteE = endDate.getMinutes();
                int secondE = endDate.getSeconds();

                LocalDateTime leaveStartTime = LocalDateTime.of(year,month,day,hour,minute,second);
                LocalDateTime leaveEndTime = LocalDateTime.of(yearE,monthE,dayE,hourE,minuteE,secondE);
                BigDecimal item = effectiveLeaveSerice.calculateEffectiveLeaveHours(leaveStartTime, leaveEndTime,deptName);
                total = total.add(item);
                LocalDate leaveStartDay = LocalDate.of(year,month,day);
                LocalDate leaveEndDay = LocalDate.of(yearE,monthE,dayE);
                Set<LocalDate> localDates = effectiveLeaveSerice.calculateWorkingDays(leaveStartDay, leaveEndDay,deptName);
                for (LocalDate localDate : localDates) {
                    res.append(localDate.toString()).append(";");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        result.put("total",total.toString());
        result.put("days",res.toString());
        return result;
    }


    public PageInfo<MonthlyAttendance> queryHuiZongDetail(Integer page, Integer limit, MonthlyAttendance employeeattendancemonthly) {
        // 设置分页信息
        PageHelper.startPage(page, limit);

        // 执行条件查询
        List<MonthlyAttendance> leaveRequests = leaveRequestDao.queryHuiZongDetail(employeeattendancemonthly);

//        for (Employeeattendancemonthly leaveRequest : leaveRequests) {
//            String str =leaveRequest.getStatus()==0?"正常":"异常";
//            leaveRequest.set
//        }

        // 获取分页信息
        return new PageInfo<>(leaveRequests);
    }

    /**
     * 保存或者更新
     * @param workTimeEntity
     */
    public void saveWorkTime(WorkTimeEntity workTimeEntity) {
        WorkTimeEntity temp = leaveRequestDao.queryWorkTimeByName(workTimeEntity.getDeptName());
        if (temp!=null){
            leaveRequestDao.updateWorkTime(workTimeEntity);
        }else {
            leaveRequestDao.saveWorkTime(workTimeEntity);
        }
    }

    public List<WorkTimeEntity> queryWorkTime() {
        List<WorkTimeEntity> workTimeEntities = leaveRequestDao.queryWorkTime();
        return workTimeEntities;
    }

    public void deleteWorkTime(Integer id) {
        leaveRequestDao.deleteWorkTime(id);
    }

    public void deleteHoliday(Integer id) {
        leaveRequestDao.deleteHoliday(id);
    }

    public PageInfo<Holidays> queryHoliday(Integer page, Integer limit, String s) {
        // 设置分页信息
        PageHelper.startPage(page, limit);

        // 执行条件查询
        List<Holidays> leaveRequests = leaveRequestDao.queryHoliday();

//        for (Employeeattendancemonthly leaveRequest : leaveRequests) {
//            String str =leaveRequest.getStatus()==0?"正常":"异常";
//            leaveRequest.set
//        }

        // 获取分页信息
        return new PageInfo<>(leaveRequests);
    }

    public void saveHoliday(Holidays holidays) {
        leaveRequestDao.saveHoliday(holidays);
    }

    public void deleteFuhao(Integer id) {
        leaveRequestDao.deleteFuhao(id);
    }

    public PageInfo<Dictitem> queryFuhao(Integer page, Integer limit, Dictitem dictitem) {
        // 设置分页信息
        PageHelper.startPage(page, limit);

        // 执行条件查询
        List<Dictitem> leaveRequests = leaveRequestDao.queryFuhao(dictitem);

//        for (Employeeattendancemonthly leaveRequest : leaveRequests) {
//            String str =leaveRequest.getStatus()==0?"正常":"异常";
//            leaveRequest.set
//        }

        // 获取分页信息
        return new PageInfo<>(leaveRequests);
    }

    public void saveFuhao(Dictitem holidays) {
        leaveRequestDao.saveFuhao(holidays);
    }

    public List<Specialholiday> querySpecialHoliday(Specialholiday specialholiday) {
        if (specialholiday==null){
            specialholiday = new Specialholiday();
        }
        return specialholidayDao.querySpecialHoliday(specialholiday.getSpecialname());
    }

    public void saveSpecialHoliday(Specialholiday specialholiday) {
        specialholidayDao.insert(specialholiday);
    }
    public void deleteSpecialHoliday(Integer id){
        specialholidayDao.deleteByPrimaryKey(id);
    }

    public void saveSpecialWorkDay(Specialworkday specialworkday) {
        specialworkdayDao.insert(specialworkday);
    }

    public PageInfo<Specialworkday> querySpecialWorkDay(Integer page, Integer limit, String s) {
        // 设置分页信息
        PageHelper.startPage(page, limit);

        // 执行条件查询
        List<Specialworkday> leaveRequests = specialworkdayDao.querySpecialworkday();

//        for (Employeeattendancemonthly leaveRequest : leaveRequests) {
//            String str =leaveRequest.getStatus()==0?"正常":"异常";
//            leaveRequest.set
//        }

        // 获取分页信息
        return new PageInfo<>(leaveRequests);

    }

    public void deleteSpecialWorkDay(Integer id) {
        specialworkdayDao.deleteSpecialworkday(id);
    }

    public void delQjBase() {
        leaveRequestDao.deleteLeaveRequestAll();
    }
    public void deleteEmployeeattendancemonthlyAll() {
        leaveRequestDao.deleteEmployeeattendancemonthlyAll();
    }
    public void deleteMonthlyAttendanceAll() {
        leaveRequestDao.deleteMonthlyAttendanceAll();
    }
}

