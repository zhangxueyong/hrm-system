package com.zxy.hrm.common.bean;

import lombok.Data;

@Data
public class TableSearchParam<T> {
    private Integer page;
    private Integer limit;
    private T searchParams;
}
