package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Employee;
import org.example.hrms.entity.Salary;
import org.example.hrms.mapper.AttendanceMapper;
import org.example.hrms.mapper.EmployeeMapper;
import org.example.hrms.mapper.LeaveRequestMapper;
import org.example.hrms.mapper.SalaryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class SalaryService {

    /** 满勤奖固定 200（文档：每月满勤+200） */
    private static final BigDecimal FULL_ATTENDANCE_BONUS = new BigDecimal("200");

    @Resource
    private SalaryMapper salaryMapper;
    @Resource
    private EmployeeMapper employeeMapper;
    @Resource
    private AttendanceMapper attendanceMapper;
    @Resource
    private LeaveRequestMapper leaveRequestMapper;

    public List<Salary> findAll(String month, Long employeeId) {
        return salaryMapper.findAll(month, employeeId);
    }

    /**
     * 计算单个员工某月工资并落库（已存在则更新）。
     * 规则：
     *   应出勤天数 = 该月工作日(周一~周五)，入职当月从入职日起算
     *   实际出勤   = 该月“正常/迟到”的打卡天数
     *   请假天数   = 该月已批准的请假天数（带薪，不扣款但影响满勤）
     *   缺勤天数   = 应出勤 - 实际出勤 - 请假（不小于0）
     *   缺勤扣款   = (底薪 / 应出勤) * 缺勤天数
     *   满勤奖     = 无缺勤且无请假时 +200
     *   实发工资   = 底薪 + 满勤奖 + 绩效 - 缺勤扣款
     *
     * @param performance 绩效（由 HR 设定，可为 null=0）
     */
    @Transactional
    public Salary calculate(Long employeeId, String month, BigDecimal performance) {
        Employee emp = employeeMapper.findById(employeeId);
        if (emp == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }
        if (performance == null) {
            performance = BigDecimal.ZERO;
        }
        BigDecimal baseSalary = emp.getBaseSalary() == null ? BigDecimal.ZERO : emp.getBaseSalary();

        int requiredDays = calcRequiredWorkdays(month, emp.getEntryDate());
        int attendDays = attendanceMapper.countAttendDays(employeeId, month);
        int leaveDays = leaveRequestMapper.countApprovedLeaveDays(employeeId, month);
        int absentDays = requiredDays - attendDays - leaveDays;
        if (absentDays < 0) {
            absentDays = 0;
        }

        // 缺勤扣款
        BigDecimal deduction = BigDecimal.ZERO;
        if (requiredDays > 0 && absentDays > 0) {
            BigDecimal dailyWage = baseSalary.divide(new BigDecimal(requiredDays), 2, RoundingMode.HALF_UP);
            deduction = dailyWage.multiply(new BigDecimal(absentDays)).setScale(2, RoundingMode.HALF_UP);
        }

        // 满勤奖：无缺勤且无请假
        BigDecimal bonus = (absentDays == 0 && leaveDays == 0) ? FULL_ATTENDANCE_BONUS : BigDecimal.ZERO;

        BigDecimal total = baseSalary.add(bonus).add(performance).subtract(deduction).setScale(2, RoundingMode.HALF_UP);

        Salary salary = salaryMapper.findByEmpAndMonth(employeeId, month);
        boolean isNew = (salary == null);
        if (isNew) {
            salary = new Salary();
            salary.setEmployeeId(employeeId);
            salary.setSalaryMonth(month);
        }
        salary.setBaseSalary(baseSalary);
        salary.setRequiredDays(requiredDays);
        salary.setAttendDays(attendDays);
        salary.setLeaveDays(leaveDays);
        salary.setAbsentDays(absentDays);
        salary.setFullAttendanceBonus(bonus);
        salary.setPerformance(performance);
        salary.setDeduction(deduction);
        salary.setTotalSalary(total);
        salary.setCreateTime(new Date());

        if (isNew) {
            salaryMapper.insertSalary(salary);
        } else {
            salaryMapper.updateSalary(salary);
        }
        return salary;
    }

    /** 一键计算全部员工某月工资 */
    public int calculateAll(String month, BigDecimal performance) {
        List<Employee> employees = employeeMapper.findAll();
        int count = 0;
        for (Employee emp : employees) {
            calculate(emp.getId(), month, performance);
            count++;
        }
        return count;
    }

    /**
     * 计算某月应出勤工作日（周一~周五）。
     * 若为员工入职当月，则从入职日起算（从入职开始计算打卡）。
     */
    private int calcRequiredWorkdays(String month, Date entryDate) {
        YearMonth ym = YearMonth.parse(month); // yyyy-MM
        LocalDate first = ym.atDay(1);
        LocalDate last = ym.atEndOfMonth();

        LocalDate start = first;
        if (entryDate != null) {
            LocalDate entry = entryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (entry.isAfter(last)) {
                return 0; // 该月尚未入职
            }
            if (entry.isAfter(first)) {
                start = entry; // 入职当月从入职日起算
            }
        }

        int workdays = 0;
        for (LocalDate d = start; !d.isAfter(last); d = d.plusDays(1)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                workdays++;
            }
        }
        return workdays;
    }
}
