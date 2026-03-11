package com.zxy.hrm.checking.dao;

import com.zxy.hrm.checking.entity.Specialholiday;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SpecialholidayDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Specialholiday record);

    int insertSelective(Specialholiday record);

    Specialholiday selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Specialholiday record);

    int updateByPrimaryKey(Specialholiday record);

    List<Specialholiday> querySpecialHoliday(String specialname);
}