package com.zxy.hrm;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestPasswordEncoder {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123"));
        System.out.println(passwordEncoder.encode("123"));
        System.out.println(passwordEncoder.encode("123"));
        System.out.println(passwordEncoder.encode("123"));

        System.out.println(passwordEncoder.matches("123","$2a$10$s28NxvksGk4OT2hpMozf3.ZEdQC/9cDmkw0GnCFwE.y/ic2CjhOpK"));
        System.out.println(passwordEncoder.matches("123","$2a$10$O1m.k7XcH5hogARgSZur6OZFo2fx5itMedbmbny3ChyQwya6xpASe"));
    }
}
