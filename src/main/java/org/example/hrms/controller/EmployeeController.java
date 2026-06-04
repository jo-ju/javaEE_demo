package org.example.hrms.controller;

import jakarta.annotation.Resource;
import org.example.hrms.common.Result;
import org.example.hrms.config.RequireManage;
import org.example.hrms.entity.Employee;
import org.example.hrms.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    @GetMapping("/list")
    public Result list() {
        return Result.ok(employeeService.findAll());
    }

    @RequireManage
    @PostMapping("/add")
    public Result add(@RequestBody Employee employee) {
        employeeService.addEmployee(employee);
        return Result.ok("新增成功", null);
    }

    @RequireManage
    @PutMapping("/update")
    public Result update(@RequestBody Employee employee) {
        employeeService.updateEmployee(employee);
        return Result.ok("修改成功", null);
    }

    @RequireManage
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return Result.ok("删除成功", null);
    }

    @GetMapping("/countByDept")
    public Result countByDept() {
        return Result.ok(employeeService.countEmpByDept());
    }
}
