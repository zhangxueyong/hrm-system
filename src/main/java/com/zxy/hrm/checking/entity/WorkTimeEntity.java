package com.zxy.hrm.checking.entity;

import lombok.Data;

@Data
public class WorkTimeEntity {
    private Integer id;
    private String deptName;
    private String morningStart;
    private String morningEnd;
    private String afternoonStart;
    private String afternoonEnd;
}
