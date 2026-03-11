package com.zxy.hrm.audit.dao;

import com.zxy.hrm.audit.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {
    SysUser findUserByBame(String username);
}
