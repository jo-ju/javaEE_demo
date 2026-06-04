package org.example.hrms.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.example.hrms.common.Result;
import org.example.hrms.config.RequireManage;
import org.example.hrms.entity.SysUser;
import org.example.hrms.service.SalaryService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {
    @Resource
    private SalaryService salaryService;

    /** 工资列表（可按月份筛选）。管理员/HR 看全部，普通员工只看自己。 */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String month,
                       HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        boolean manager = "ADMIN".equals(user.getRole()) || "HR".equals(user.getRole());
        Long filterEmpId = manager ? null : user.getEmployeeId();
        return Result.ok(salaryService.findAll(month, filterEmpId));
    }

    /** 计算单个员工某月工资：仅管理员/HR */
    @RequireManage
    @PostMapping("/calculate")
    public Result calculate(@RequestParam Long employeeId,
                            @RequestParam String month,
                            @RequestParam(required = false) BigDecimal performance) {
        return Result.ok("计算完成", salaryService.calculate(employeeId, month, performance));
    }

    /** 一键计算全部员工某月工资：仅管理员/HR */
    @RequireManage
    @PostMapping("/calculateAll")
    public Result calculateAll(@RequestParam String month,
                               @RequestParam(required = false) BigDecimal performance) {
        int count = salaryService.calculateAll(month, performance);
        return Result.ok("已计算 " + count + " 名员工工资", null);
    }
}
