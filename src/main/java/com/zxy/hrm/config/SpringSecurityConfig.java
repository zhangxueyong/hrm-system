package com.zxy.hrm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)// 开启方法级安全验证
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter  {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;



    /**
     * 认证
     * @param auth
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());

    }

    /**
     * 授权
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

        //开启跨域 以及csrf 攻击关闭
        //登录登出配置
        //拦截规则配置
        //异常处理配置
        //自定义过滤配置
        http.authorizeRequests()
                //白名单放行
                .antMatchers("/static/**","/css/**", "/js/**","/images/**","/lib/**","/api/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                //默认登录页面请求，必须是get请求
                .loginPage("/toLogin")
                .permitAll()
                //登录成功后跳转页面
                .successForwardUrl("/index")
                .and()
                .csrf().disable()
                .cors().disable();
        http.headers().frameOptions().disable();
        //开启记住我功能。默认保存两周
        http.rememberMe();
    }

    /**
     * 强散列数据加密
     * 保证相同的散列数据但不保证相同的加密结果
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        //构造方法可以传递整型参数，范围4~31之间，数字越发密码强度越高，性能则越低，取决于服务器的算力
        return new BCryptPasswordEncoder();
    }
}
