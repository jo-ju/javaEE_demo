package org.example.hrms.mapper;

import org.apache.ibatis.annotations.*;
import org.example.hrms.entity.Position;

import java.util.List;

@Mapper
public interface PositionMapper {
    @Select("SELECT p.*, d.dept_name, d.manager FROM position p LEFT JOIN dept d ON p.dept_id = d.id")
    @Results({
            @Result(column = "dept_id", property = "dept.id"),
            @Result(column = "dept_name", property = "dept.deptName"),
            @Result(column = "manager", property = "dept.manager")
    })
    List<Position> findAll();

    @Insert("INSERT INTO position (pos_name, pos_desc, dept_id) VALUES (#{posName}, #{posDesc}, #{dept.id})")
    int addPosition(Position position);

    @Update("UPDATE position SET pos_name=#{posName}, pos_desc=#{posDesc}, dept_id=#{dept.id} WHERE id=#{id}")
    int updatePosition(Position position);

    @Delete("DELETE FROM position WHERE id=#{id}")
    int deletePosition(Long id);
}