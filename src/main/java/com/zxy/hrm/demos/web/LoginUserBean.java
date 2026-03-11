package com.zxy.hrm.demos.web;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserBean implements Serializable {
    private String username;
    private String password;
    private String captcha;
}
