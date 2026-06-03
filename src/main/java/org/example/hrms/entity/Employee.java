package org.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String empId;
    private String empName;
    private Date entryDate;

    // 多对一：所属部门
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id")
    private Dept dept;

    // 多对一：所属职位
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pos_id")
    private Position position;
}