package com.zxy.hrm.checking.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * specialworkday
 * @author 
 */
@Data
public class Specialworkday implements Serializable {
    private Integer id;

    private String specialworkname;

    private String specialworktime;

    /**
     * 全部；科室，初中，高中，职教
     */
    private String deptrange;

    private static final long serialVersionUID = 1L;
}