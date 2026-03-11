package com.zxy.hrm.checking.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * specialholiday
 * @author 
 */
@Data
public class Specialholiday implements Serializable {
    private Integer id;

    /**
     * 开始时间
     */
    private String starttime;

    /**
     * 结束时间
     */
    private String endtime;

    /**
     * 特殊假期名称
     */
    private String specialname;
    /**
     * 机构名称
     */
    private String deptname;

    private static final long serialVersionUID = 1L;
}