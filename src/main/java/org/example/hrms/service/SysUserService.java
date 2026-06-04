package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.common.PasswordEncoder;
import org.example.hrms.entity.SysUser;
import org.example.hrms.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserService.class);

    @Resource
    private SysUserMapper userMapper;

    /**
     * 登录验证：优先 BCrypt 比对，兼容历史明文密码。
     * 若为明文密码，首次登录成功后自动升级为 BCrypt 存储。
     */
    public SysUser login(String username, String password) {
        SysUser user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!PasswordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        // 渐进式升级：明文密码首次登录后自动加密
        if (!PasswordEncoder.isBCrypt(user.getPassword())) {
            String encoded = PasswordEncoder.encode(password);
            userMapper.updatePassword(user.getId(), encoded);
            user.setPassword(null); // 不回传密码
            log.info("用户 {} 密码已从明文升级为 BCrypt", username);
            return user;
        }
        user.setPassword(null);
        return user;
    }
}
