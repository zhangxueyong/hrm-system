package com.zxy.hrm.checking.entity;

import lombok.Data;

@Data
public class MonthlyAttendance {

  private String id;
  private String name;
  private String department;
  private String workingDays;
  private long lateCount;
  private String lateDates;
  private long unsignedInCount;
  private String unsignedInDates;
  private long unsignedOutCount;
  private String unsignedOutDates;
  private long earlyLeaveCount;
  private String earlyLeaveDates;
  private String personalLeaveDays;
  private String personalLeaveDates;
  private String sickLeaveDays;
  private String sickLeaveDates;
  private String marriageLeaveDays;
  private String marriageLeaveDates;
  private String bereavementLeaveDays;
  private String bereavementLeaveDates;
  private String paternityLeaveDays;
  private String paternityLeaveDates;
  private String maternityLeaveDays;
  private String maternityLeaveDates;
  private String idCard;
  private String years;
  private String months;



}
