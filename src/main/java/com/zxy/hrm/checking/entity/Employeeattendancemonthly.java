package com.zxy.hrm.checking.entity;

import lombok.Data;

/**
 * 阅读考勤状态表
 */
@Data
public class Employeeattendancemonthly {

  private String id;
  private String idCard;
  private String name;
  private String year;
  private String month;
  private String day;
  private long status;
  private String baseflag;
  private String desc;



}
