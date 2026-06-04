package org.example.hrms.controller;

import jakarta.annotation.Resource;
import org.example.hrms.common.Result;
import org.example.hrms.config.RequireManage;
import org.example.hrms.entity.Dept;
import org.example.hrms.service.DeptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dept")
public class DeptController {
    @Resource
    private DeptService deptService;

    @GetMapping("/list")
    public Result list() {
        return Result.ok(deptService.findAll());
    }

    @RequireManage
    @PostMapping("/add")
    public Result add(@RequestBody Dept dept) {
        deptService.addDept(dept);
        return Result.ok("新增成功", null);
    }

    @RequireManage
    @PutMapping("/update")
    public Result update(@RequestBody Dept dept) {
        deptService.updateDept(dept);
        return Result.ok("修改成功", null);
    }

    @RequireManage
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return Result.ok("删除成功", null);
    }
}
