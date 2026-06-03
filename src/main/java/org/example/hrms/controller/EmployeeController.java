package org.example.hrms.controller;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Employee;
import org.example.hrms.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    @GetMapping("/list")
    public List<Employee> list() {
        return employeeService.findAll();
    }

    @PostMapping("/add")
    public int add(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/update")
    public int update(@RequestBody Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/delete/{id}")
    public int delete(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }

    @GetMapping("/countByDept")
    public List<Map<String, Object>> countByDept() {
        return employeeService.countEmpByDept();
    }
}
