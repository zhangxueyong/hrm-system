package com.zxy.hrm.checking.entity;

import lombok.Data;

@Data
public class Dictitem {

  private long id;
  private String dicttype;
  private String dictValue;
  private long parentId;
  private long seq;



}
