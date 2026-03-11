package com.zxy.hrm.checking.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Leaverequests {


  @ExcelProperty(index = 1)
  private String name;
  @ExcelProperty(index = 0)
  private String idCard;
  @ExcelProperty(index = 2)
  private String type;
  @ExcelProperty(index = 3)
  private String startTime;
  @ExcelProperty(index = 4)
  private String endTime;
  @ExcelProperty(index = 5)
  private String reason;




}
