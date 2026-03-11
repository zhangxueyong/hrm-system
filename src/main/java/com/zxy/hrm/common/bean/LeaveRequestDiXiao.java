package com.zxy.hrm.common.bean;

import lombok.Data;

import java.util.List;

@Data
public class LeaveRequestDiXiao {
    private Integer wqd;
    private Integer wql;
    private List<DiXiaoDays> diXiaoDays;

}
