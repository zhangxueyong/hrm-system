package com.zxy.hrm.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class MyPasswordEncoder implements PasswordEncoder {
    /**
     * 密码加密方法，
     * @param rawPassword 密码的明文
     * @return 密码加密后的密文
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    /**
     * 校验密码的明文和密文是是否是相同的方法
     * @param rawPassword 明文密码是前端传过来的
     * @param encodedPassword 密文密码，在数据库中存储的
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        //用相同的机密策略，与机密的密文进行比较
        return encode(rawPassword).equals(encodedPassword);
    }

    /**
     * 是否需要升级密码解析策略，强化密码解析策略
     * @param encodedPassword
     * @return
     */
    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
