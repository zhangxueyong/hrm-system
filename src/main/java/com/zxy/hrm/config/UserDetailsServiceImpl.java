package com.zxy.hrm.config;

import com.zxy.hrm.audit.dao.UserDao;
import com.zxy.hrm.audit.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 登录服务实现方法
 * 次方发用户查询对象，
 * 仅需根据用户名查询用户并饭回
 * 自定义实现对象，并必须被spring 管理且唯一
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userMapper;

    /**
     * 获取用户对象
     * 更具用户名查询用户对象，和用户权限列表
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.findUserByBame(username);

        if (user == null) {
            throw  new UsernameNotFoundException("用户名或密码错误");
        }
        // 匹配用户密码
        // org.springframework.security.core.userdetails.User
        User res = new User(username, user.getUserPwd(), AuthorityUtils.createAuthorityList());
        return res;
    }


}
