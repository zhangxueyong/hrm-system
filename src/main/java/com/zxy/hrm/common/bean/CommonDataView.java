package com.zxy.hrm.common.bean;

import lombok.Data;

import java.util.List;

@Data
public class  CommonDataView<T> {
    private Integer code;
    private String  msg;
    private Long count;
    private List<T> data;
}
