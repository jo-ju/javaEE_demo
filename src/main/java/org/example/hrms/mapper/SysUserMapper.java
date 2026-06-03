package org.example.hrms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.hrms.entity.SysUser;

@Mapper
public interface SysUserMapper {
    @Select("SELECT * FROM sys_user WHERE username=#{username} AND password=#{password}")
    SysUser login(String username, String password);
}
