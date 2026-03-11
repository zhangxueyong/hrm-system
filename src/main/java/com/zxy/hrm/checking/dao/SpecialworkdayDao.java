package com.zxy.hrm.checking.dao;

import com.zxy.hrm.checking.entity.Specialworkday;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SpecialworkdayDao {

    int insert(Specialworkday record);

    int insertSelective(Specialworkday record);

    //void saveSpecialworkday(Specialholiday Specialworkdays);

    List<Specialworkday> querySpecialworkday();

    void deleteSpecialworkday(Integer id);
}