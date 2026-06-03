package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Employee;
import org.example.hrms.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    @Resource
    private EmployeeMapper employeeMapper;

    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    public int addEmployee(Employee employee) {
        return employeeMapper.addEmployee(employee);
    }

    public int updateEmployee(Employee employee) {
        return employeeMapper.updateEmployee(employee);
    }

    public int deleteEmployee(Long id) {
        return employeeMapper.deleteEmployee(id);
    }

    public List<Map<String, Object>> countEmpByDept() {
        return employeeMapper.countEmpByDept();
    }
}