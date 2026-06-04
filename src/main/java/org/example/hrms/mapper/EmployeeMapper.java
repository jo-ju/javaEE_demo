package org.example.hrms.mapper;

import org.apache.ibatis.annotations.*;
import org.example.hrms.entity.Employee;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeMapper {
    @Select("SELECT e.*, d.dept_name, p.pos_name FROM employee e " +
            "LEFT JOIN dept d ON e.dept_id = d.id " +
            "LEFT JOIN position p ON e.pos_id = p.id")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "base_salary", property = "baseSalary"),
            @Result(column = "dept_id", property = "dept.id"),
            @Result(column = "dept_name", property = "dept.deptName"),
            @Result(column = "pos_id", property = "position.id"),
            @Result(column = "pos_name", property = "position.posName")
    })
    List<Employee> findAll();

    @Select("SELECT e.*, d.dept_name, p.pos_name FROM employee e " +
            "LEFT JOIN dept d ON e.dept_id = d.id " +
            "LEFT JOIN position p ON e.pos_id = p.id WHERE e.id = #{id}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "base_salary", property = "baseSalary"),
            @Result(column = "dept_id", property = "dept.id"),
            @Result(column = "dept_name", property = "dept.deptName"),
            @Result(column = "pos_id", property = "position.id"),
            @Result(column = "pos_name", property = "position.posName")
    })
    Employee findById(Long id);

    @Insert("INSERT INTO employee (emp_id, emp_name, dept_id, pos_id, entry_date, base_salary) " +
            "VALUES (#{empId}, #{empName}, #{dept.id}, #{position.id}, #{entryDate}, #{baseSalary})")
    int addEmployee(Employee employee);

    @Update("UPDATE employee SET emp_id=#{empId}, emp_name=#{empName}, dept_id=#{dept.id}, " +
            "pos_id=#{position.id}, entry_date=#{entryDate}, base_salary=#{baseSalary} WHERE id=#{id}")
    int updateEmployee(Employee employee);

    @Delete("DELETE FROM employee WHERE id=#{id}")
    int deleteEmployee(Long id);

    // 按部门统计员工数量
    @Select("SELECT d.dept_name, COUNT(e.id) AS emp_count FROM dept d " +
            "LEFT JOIN employee e ON d.id = e.dept_id GROUP BY d.id, d.dept_name")
    List<Map<String, Object>> countEmpByDept();
}
