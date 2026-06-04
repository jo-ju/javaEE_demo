package org.example.hrms.mapper;

import org.apache.ibatis.annotations.*;
import org.example.hrms.entity.Salary;

import java.util.List;

@Mapper
public interface SalaryMapper {

    /** 工资列表（含员工信息），可按月份、员工筛选 */
    @Select("<script>" +
            "SELECT s.*, e.emp_name AS empName, e.emp_id AS empNo FROM salary s " +
            "LEFT JOIN employee e ON s.employee_id = e.id " +
            "<where>" +
            "  <if test='month != null and month != \"\"'> s.salary_month = #{month} </if>" +
            "  <if test='employeeId != null'> AND s.employee_id = #{employeeId} </if>" +
            "</where>" +
            " ORDER BY s.salary_month DESC, s.employee_id" +
            "</script>")
    List<Salary> findAll(@Param("month") String month, @Param("employeeId") Long employeeId);

    @Select("SELECT * FROM salary WHERE employee_id=#{employeeId} AND salary_month=#{month}")
    Salary findByEmpAndMonth(@Param("employeeId") Long employeeId, @Param("month") String month);

    @Insert("INSERT INTO salary (employee_id, salary_month, base_salary, required_days, attend_days, " +
            "leave_days, absent_days, full_attendance_bonus, performance, deduction, total_salary, create_time) " +
            "VALUES (#{employeeId}, #{salaryMonth}, #{baseSalary}, #{requiredDays}, #{attendDays}, " +
            "#{leaveDays}, #{absentDays}, #{fullAttendanceBonus}, #{performance}, #{deduction}, #{totalSalary}, #{createTime})")
    int insertSalary(Salary salary);

    @Update("UPDATE salary SET base_salary=#{baseSalary}, required_days=#{requiredDays}, attend_days=#{attendDays}, " +
            "leave_days=#{leaveDays}, absent_days=#{absentDays}, full_attendance_bonus=#{fullAttendanceBonus}, " +
            "performance=#{performance}, deduction=#{deduction}, total_salary=#{totalSalary}, create_time=#{createTime} " +
            "WHERE id=#{id}")
    int updateSalary(Salary salary);
}
