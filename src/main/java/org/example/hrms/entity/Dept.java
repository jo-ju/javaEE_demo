package org.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "dept")
public class Dept {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deptName;
    private String manager;

    // 一对多：部门下的职位
    @OneToMany(mappedBy = "dept", fetch = FetchType.LAZY)
    private List<Position> positions;
}