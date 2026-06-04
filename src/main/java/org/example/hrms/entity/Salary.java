package org.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/** 月工资计算结果 */
@Data
@Entity
@Table(name = "salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    /** 工资月份 yyyy-MM */
    private String salaryMonth;
    private BigDecimal baseSalary;
    /** 应出勤天数(工作日) */
    private Integer requiredDays;
    /** 实际出勤天数 */
    private Integer attendDays;
    /** 已批准请假天数 */
    private Integer leaveDays;
    /** 缺勤天数 */
    private Integer absentDays;
    /** 满勤奖 */
    private BigDecimal fullAttendanceBonus;
    /** 绩效 */
    private BigDecimal performance;
    /** 缺勤扣款 */
    private BigDecimal deduction;
    /** 实发工资 */
    private BigDecimal totalSalary;
    private Date createTime;

    // 非数据库字段，列表展示用
    @Transient
    private String empName;
    @Transient
    private String empNo;
}
