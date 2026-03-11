package com.zxy.hrm.audit.entity;

import lombok.Data;

@Data
public class SysUser {

  private long id;
  private String userNumber;
  private String userName;
  private String userPwd;
  private String roleId;
  private long isStop;
  private long createTime;
  private java.sql.Timestamp modifiedTime;
  private long isDeleted;



}
