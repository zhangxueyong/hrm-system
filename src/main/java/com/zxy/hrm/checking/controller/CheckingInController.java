package com.zxy.hrm.checking.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zxy.hrm.checking.entity.*;
import com.zxy.hrm.checking.service.CheckinginService;
import com.zxy.hrm.common.bean.CommonDataView;
import com.zxy.hrm.common.bean.TableSearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author zxy
 *  :考勤相关业务
 * @date 20231111
 */
@Controller
@RequestMapping("/checking")
public class CheckingInController
{
    @Autowired
    private CheckinginService checkinginService;
    /***
     * 上传请假基础数据
     */
    @RequestMapping("/baseData")
    @ResponseBody
    public String uploadCheckingBaseDate(@RequestParam(value = "file") MultipartFile file) throws IOException {
        if (file != null) {
            checkinginService.dealLeaveRequestBaseData(file);
            return "success";
        } else {
            return "file 为 null";
        }
    }
    /***
     * 清空请假数据
     */
    @RequestMapping("/delQjBase")
    @ResponseBody
    public CommonDataView delQjBase()  {
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.delQjBase();
            commonDataView.setCode(200);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }
    /***
     * 清空汇总数据
     */
    @RequestMapping("/deleteMonthlyAttendanceAll")
    @ResponseBody
    public CommonDataView deleteMonthlyAttendanceAll()  {
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.deleteMonthlyAttendanceAll();
            commonDataView.setCode(200);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }
    /***
     * 清空符号表数据
     */
    @RequestMapping("/deleteEmployeeattendancemonthlyAll")
    @ResponseBody
    public CommonDataView deleteEmployeeattendancemonthlyAll()  {
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.deleteEmployeeattendancemonthlyAll();
            commonDataView.setCode(200);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }
    /***
     * 上传考勤符号表基础数据
     */
    @RequestMapping("/attedanceBaseData")
    @ResponseBody
    public String uploadAttedanceBaseDate(@RequestParam(value = "file") MultipartFile file) throws IOException {
        if (file != null) {
            checkinginService.uploadAttedanceBaseDate(file);
            return "success";
        } else {
            return "file 为 null";
        }
    }

    /***
     * 上传汇总表基础数据
     */
    @RequestMapping("/uploadHuiZongBaseData")
    @ResponseBody
    public String uploadHuiZongBaseData(@RequestParam(value = "file") MultipartFile file) throws IOException {
        if (file != null) {
            checkinginService.uploadHuiZongBaseData(file);
            return "success";
        } else {
            return "file 为 null";
        }
    }
    /***
     * 分页查询请假数据
     */
    @RequestMapping("/queryLeaveRequest")
    @ResponseBody
    public CommonDataView<LeaverequestExt> queryLeaveRequest(TableSearchParam<String> leaverequests)  {
        CommonDataView<LeaverequestExt> commonDataView = new CommonDataView<>();
    //    List<LeaverequestExt> leaverequestExts = new ArrayList<>();
        String param  = leaverequests.getSearchParams();
        Leaverequests leaverequests1 = JSONObject.parseObject(param,Leaverequests.class);
        PageInfo<LeaverequestExt> pageInfo = checkinginService.getLeaveRequestsByCondition(leaverequests.getPage(),leaverequests.getLimit(),leaverequests1);
        commonDataView.setCode(0);
        commonDataView.setCount(pageInfo.getTotal());
        commonDataView.setData(pageInfo.getList());
        return commonDataView;
    }

    /***
     * 分页查询考勤数据
     */
    @RequestMapping("/queryKaoQinRequest")
    @ResponseBody
    public CommonDataView<Employeeattendancemonthly> queryKaoQinRequest(TableSearchParam<String> leaverequests)  {
        CommonDataView<Employeeattendancemonthly> commonDataView = new CommonDataView<>();
        //    List<LeaverequestExt> leaverequestExts = new ArrayList<>();
        String param  = leaverequests.getSearchParams();
        Employeeattendancemonthly employeeattendancemonthly = JSONObject.parseObject(param, Employeeattendancemonthly.class);
        PageInfo<Employeeattendancemonthly> pageInfo = checkinginService.queryKaoQinRequest(leaverequests.getPage(),leaverequests.getLimit(),employeeattendancemonthly);
        commonDataView.setCode(0);
        commonDataView.setCount(pageInfo.getTotal());
        commonDataView.setData(pageInfo.getList());
        return commonDataView;
    }

    /***
     * 分页查询考勤数据
     */
    @RequestMapping("/queryHuiZongDetail")
    @ResponseBody
    public CommonDataView<MonthlyAttendance> queryHuiZongDetail(TableSearchParam<String> leaverequests)  {
        CommonDataView<MonthlyAttendance> commonDataView = new CommonDataView<>();
        //    List<LeaverequestExt> leaverequestExts = new ArrayList<>();
        String param  = leaverequests.getSearchParams();
        MonthlyAttendance employeeattendancemonthly = JSONObject.parseObject(param, MonthlyAttendance.class);
        PageInfo<MonthlyAttendance> pageInfo = checkinginService.queryHuiZongDetail(leaverequests.getPage(),leaverequests.getLimit(),employeeattendancemonthly);
        commonDataView.setCode(0);
        commonDataView.setCount(pageInfo.getTotal());
        commonDataView.setData(pageInfo.getList());
        return commonDataView;
    }

    /**
     * 保存作息时间
     * @param workTimeEntity
     * @return
     */
    @RequestMapping("/saveWorkTime")
    @ResponseBody
    public CommonDataView saveWorkTime(WorkTimeEntity workTimeEntity){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.saveWorkTime(workTimeEntity);
            commonDataView.setCode(200);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }
    @RequestMapping("/queryWorkTime")
    @ResponseBody
    public CommonDataView<WorkTimeEntity> queryWorkTime(TableSearchParam param){
        CommonDataView commonDataView = new CommonDataView();
        try {
            List<WorkTimeEntity> workTimeEntities = checkinginService.queryWorkTime();
            commonDataView.setData(workTimeEntities);
            commonDataView.setCode(0);
            commonDataView.setCount(Long.valueOf(workTimeEntities.size()));
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }

    /**
     * 删除工作时间
     * @return
     */
    @RequestMapping(value = "/deleteWorkTime/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonDataView deleteWorkTime(@PathVariable("id") Integer id){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.deleteWorkTime(id);
            commonDataView.setCode(0);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }
    /*************************************************************************************************/

    /**
     * 保存作息时间
     * @param      * @return
     */
    @RequestMapping("/saveHoliday")
    @ResponseBody
    public CommonDataView saveHoliday(Holidays holidays){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.saveHoliday(holidays);
            commonDataView.setCode(200);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }
    @RequestMapping("/queryHoliday")
    @ResponseBody
    public CommonDataView<Holidays> queryHoliday(TableSearchParam param){
        CommonDataView commonDataView = new CommonDataView();
        try {
            PageInfo<Holidays> holidaysPageInfo = checkinginService.queryHoliday(param.getPage(), param.getLimit(), "");
            commonDataView.setData(holidaysPageInfo.getList());
            commonDataView.setCode(0);
            commonDataView.setCount(holidaysPageInfo.getTotal());
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }

    /**
     * 删除工作时间
     * @return
     */
    @RequestMapping(value = "/deleteHoliday/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonDataView deleteHoliday(@PathVariable("id") Integer id){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.deleteHoliday(id);
            commonDataView.setCode(0);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }
    /********************************************************/
    /**
     * 保存作息时间
     * @param      * @return
     */
    @RequestMapping("/saveFuhao")
    @ResponseBody
    public CommonDataView saveFuhao(Dictitem holidays){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.saveFuhao(holidays);
            commonDataView.setCode(200);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }
    @RequestMapping("/queryFuhao")
    @ResponseBody
    public CommonDataView<Dictitem> queryFuhao(TableSearchParam<String> param){
        CommonDataView commonDataView = new CommonDataView();
        try {
            String pp =  param.getSearchParams();
            Dictitem dictitem = JSON.parseObject(pp, Dictitem.class);
            PageInfo<Dictitem> holidaysPageInfo = checkinginService.queryFuhao(param.getPage(), param.getLimit(), dictitem);
            commonDataView.setData(holidaysPageInfo.getList());
            commonDataView.setCode(0);
            commonDataView.setCount(holidaysPageInfo.getTotal());
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }

    /**
     * 删除工作时间
     * @return
     */
    @RequestMapping(value = "/deleteFuhao/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonDataView deleteFuhao(@PathVariable("id") Integer id){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.deleteFuhao(id);
            commonDataView.setCode(0);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }
    @RequestMapping("/querySpecialHoliday")
    @ResponseBody
    public CommonDataView<Specialholiday> querySpecialHoliday(TableSearchParam<String> param){
        CommonDataView<Specialholiday> commonDataView = new CommonDataView<>();
        String searchParams = param.getSearchParams();
        Specialholiday specialholiday = JSON.parseObject(searchParams, Specialholiday.class);
        try {
            List<Specialholiday> specialholidays  = checkinginService.querySpecialHoliday(specialholiday);
            commonDataView.setData(specialholidays);
            commonDataView.setCode(0);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }

    @RequestMapping("/saveSpecialHoliday")
    @ResponseBody
    public CommonDataView saveSpecialHoliday(Specialholiday specialholiday){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.saveSpecialHoliday(specialholiday);
            commonDataView.setCode(0);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return  commonDataView;
    }
    @RequestMapping(value = "/deleteSpecialHoliday/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonDataView deleteSpecialHoliday(@PathVariable("id") Integer id){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.deleteSpecialHoliday(id);
            commonDataView.setCode(0);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }

    /**
     * 保存作息时间
     * @param      * @return
     */
    @RequestMapping("/saveSpecialWorkDay")
    @ResponseBody
    public CommonDataView saveSpecialWorkDay(Specialworkday specialworkday){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.saveSpecialWorkDay(specialworkday);
            commonDataView.setCode(200);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }
    @RequestMapping("/querySpecialWorkDay")
    @ResponseBody
    public CommonDataView<Specialworkday> querySpecialWorkDay(TableSearchParam param){
        CommonDataView commonDataView = new CommonDataView();
        try {
            PageInfo<Specialworkday> holidaysPageInfo = checkinginService.querySpecialWorkDay(param.getPage(), param.getLimit(), "");
            commonDataView.setData(holidaysPageInfo.getList());
            commonDataView.setCode(0);
            commonDataView.setCount(holidaysPageInfo.getTotal());
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;

    }

    /**
     * 删除工作时间
     * @return
     */
    @RequestMapping(value = "/deleteSpecialWorkDay/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonDataView deleteSpecialWorkDay(@PathVariable("id") Integer id){
        CommonDataView commonDataView = new CommonDataView();
        try {
            checkinginService.deleteSpecialWorkDay(id);
            commonDataView.setCode(0);
        }catch (Exception e){
            e.printStackTrace();
            commonDataView.setCode(500);
        }
        return commonDataView;
    }
}
