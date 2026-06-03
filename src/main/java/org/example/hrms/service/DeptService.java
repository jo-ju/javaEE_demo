package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Dept;
import org.example.hrms.mapper.DeptMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptService {
    @Resource
    private DeptMapper deptMapper;

    public List<Dept> findAll() {
        return deptMapper.findAll();
    }

    public int addDept(Dept dept) {
        return deptMapper.addDept(dept);
    }

    public int updateDept(Dept dept) {
        return deptMapper.updateDept(dept);
    }

    public int deleteDept(Long id) {
        return deptMapper.deleteDept(id);
    }
}
