package org.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sys_user")
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String realName;
    /**
     * 角色：ADMIN 超管 / HR 人事部(最高权限) / EMPLOYEE 普通员工
     */
    private String role;
    /**
     * 所属部门ID
     */
    private Long deptId;
    /**
     * 关联员工ID（普通员工OA用）
     */
    private Long employeeId;
}
