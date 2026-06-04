package org.example.hrms.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.example.hrms.common.Result;
import org.example.hrms.entity.SysUser;
import org.example.hrms.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Resource
    private AttendanceService attendanceService;

    /** 打卡列表：管理员/HR 看全部，普通员工只看自己 */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Long employeeId, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        Long filter = resolveEmployeeFilter(user, employeeId);
        return Result.ok(attendanceService.findAll(filter));
    }

    /** 上班打卡（当前登录员工） */
    @PostMapping("/clockIn")
    public Result clockIn(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user.getEmployeeId() == null) {
            return Result.fail("当前账号未关联员工，无法打卡");
        }
        return Result.ok(attendanceService.clockIn(user.getEmployeeId()), null);
    }

    /** 下班打卡（当前登录员工） */
    @PostMapping("/clockOut")
    public Result clockOut(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user.getEmployeeId() == null) {
            return Result.fail("当前账号未关联员工，无法打卡");
        }
        return Result.ok(attendanceService.clockOut(user.getEmployeeId()), null);
    }

    /** 普通员工只能查看自己的记录；管理员/HR 可按 employeeId 过滤，不传则全部 */
    private Long resolveEmployeeFilter(SysUser user, Long employeeId) {
        boolean manager = "ADMIN".equals(user.getRole()) || "HR".equals(user.getRole());
        if (manager) {
            return employeeId;
        }
        return user.getEmployeeId();
    }
}
