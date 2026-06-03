package org.example.hrms.controller;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Dept;
import org.example.hrms.service.DeptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Resource
    private DeptService deptService;

    @GetMapping("/list")
    public List<Dept> list() {
        return deptService.findAll();
    }

    @PostMapping("/add")
    public int add(@RequestBody Dept dept) {
        return deptService.addDept(dept);
    }

    @PutMapping("/update")
    public int update(@RequestBody Dept dept) {
        return deptService.updateDept(dept);
    }

    @DeleteMapping("/delete/{id}")
    public int delete(@PathVariable Long id) {
        return deptService.deleteDept(id);
    }
}
