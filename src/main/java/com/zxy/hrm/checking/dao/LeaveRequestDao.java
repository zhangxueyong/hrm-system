package com.zxy.hrm.checking.dao;

import com.zxy.hrm.checking.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface LeaveRequestDao {

    void saveLeaverequests(List<LeaverequestExt> list);

    void insertLeaveRequest(LeaverequestExt leaveRequest);

    void deleteLeaveRequestById(Long id);

    void updateLeaveRequest(LeaverequestExt leaveRequest);

    LeaverequestExt getLeaveRequestById(Long id);

    List<LeaverequestExt> queryLeaveRequest(Leaverequests leaverequests);

    List<Dictitem> queryDictTypeByCode(String code);

    void saveAttedanceBaseDate(List<Employeeattendancemonthly> employeeattendancemonthlies);

    List<Employeeattendancemonthly> queryKaoQinRequest(Employeeattendancemonthly employeeattendancemonthly);


    void saveMonthlyAttendances(List<MonthlyAttendance> list);

    List<Employeeattendancemonthly> queryEmpErrDays(Map<String, String> param);

    List<LeaverequestExt> queryLeaveRequestByType(Map<String, String> param);

    List<Employeeattendancemonthly> queryEmpErrDaysByWqd(Map<String, String> param);

    List<Employeeattendancemonthly> queryEmpErrDaysByWqt(Map<String, String> param);

    List<MonthlyAttendance> queryHuiZongDetail(MonthlyAttendance employeeattendancemonthly);

    void saveWorkTime(WorkTimeEntity workTimeEntity);

    List<WorkTimeEntity> queryWorkTime();

    void updateWorkTime(WorkTimeEntity workTimeEntity);

    WorkTimeEntity queryWorkTimeByName(String deptName);

    void deleteWorkTime(Integer id);

    void saveHoliday(Holidays holidays);

    List<Holidays> queryHoliday();

    void deleteHoliday(Integer id);

    void saveFuhao(Dictitem holidays);

    List<Dictitem> queryFuhao(Dictitem dictitem);

    void deleteFuhao(Integer id);

    List<LeaverequestExt> queryLeaveRequestByErrDay(Map<String, String> param);

    void deleteLeaveRequestAll();

    void deleteEmployeeattendancemonthlyAll();

    void deleteMonthlyAttendanceAll();
}
