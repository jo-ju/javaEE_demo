package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.entity.SysUser;
import org.example.hrms.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper userMapper;

    public SysUser login(String username, String password) {
        return userMapper.login(username, password);
    }
}
