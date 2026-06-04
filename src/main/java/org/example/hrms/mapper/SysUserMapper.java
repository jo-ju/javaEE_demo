package org.example.hrms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.hrms.entity.SysUser;

@Mapper
public interface SysUserMapper {
    @Select("SELECT * FROM sys_user WHERE username=#{username} AND password=#{password}")
    SysUser login(String username, String password);

    /**
     * 仅按用户名查询（用于 BCrypt 验证 + 渐进式密码升级）
     */
    @Select("SELECT * FROM sys_user WHERE username=#{username}")
    SysUser findByUsername(String username);

    /**
     * 更新密码为 BCrypt 哈希
     */
    @Update("UPDATE sys_user SET password=#{password} WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
