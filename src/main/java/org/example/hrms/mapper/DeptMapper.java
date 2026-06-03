package org.example.hrms.mapper;

import org.apache.ibatis.annotations.*;
import org.example.hrms.entity.Dept;

import java.util.List;

@Mapper
public interface DeptMapper {
    @Select("SELECT * FROM dept")
    List<Dept> findAll();

    @Insert("INSERT INTO dept (dept_name, manager) VALUES (#{deptName}, #{manager})")
    int addDept(Dept dept);

    @Update("UPDATE dept SET dept_name=#{deptName}, manager=#{manager} WHERE id=#{id}")
    int updateDept(Dept dept);

    @Delete("DELETE FROM dept WHERE id=#{id}")
    int deleteDept(Long id);
}
