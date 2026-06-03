package org.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "position")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String posName;
    private String posDesc;

    // 多对一：所属部门
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id")
    private Dept dept;

    // 一对多：职位下的员工
    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY)
    private List<Employee> employees;
}
