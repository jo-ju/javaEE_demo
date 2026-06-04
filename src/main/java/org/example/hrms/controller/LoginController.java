package org.example.hrms.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.example.hrms.common.Result;
import org.example.hrms.entity.SysUser;
import org.example.hrms.service.SysUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {
    @Resource
    private SysUserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody SysUser user, HttpSession session) {
        SysUser loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            loginUser.setPassword(null); // 不回传密码
            session.setAttribute("loginUser", loginUser);
            return Result.ok("登录成功", loginUser);
        }
        return Result.fail("用户名或密码错误");
    }

    @GetMapping("/login/check")
    public Result checkLogin(HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser != null) {
            return Result.ok(loginUser);
        }
        return Result.fail(401, "未登录");
    }

    @PostMapping("/logout")
    public Result logout(HttpSession session) {
        session.invalidate();
        return Result.ok("已退出登录", null);
    }
}
