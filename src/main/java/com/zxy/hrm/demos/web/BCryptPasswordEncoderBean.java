package com.zxy.hrm.demos.web;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderBean {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder_pro = new BCryptPasswordEncoder(15);
        System.out.println("加密2: " + encoder_pro.encode("000000"));
    }
}
