package org.example.hrms.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.example.hrms.entity.SysUser;
import org.example.hrms.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Resource
    private SysUserService userService;

    @PostMapping
    public Map<String, Object> login(@RequestBody SysUser user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        SysUser loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            session.setAttribute("loginUser", loginUser);
            result.put("code", 200);
            result.put("msg", "登录成功");
        } else {
            result.put("code", 500);
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }

    @GetMapping("/check")
    public Map<String, Object> checkLogin(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser != null) {
            result.put("code", 200);
            result.put("user", loginUser);
        } else {
            result.put("code", 401);
            result.put("msg", "未登录");
        }
        return result;
    }
}
