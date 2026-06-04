package org.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/** 打卡记录：每位员工每天一条 */
@Data
@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date clockDate;
    private Date clockIn;
    private Date clockOut;
    /** 正常 / 迟到 / 缺勤 */
    private String status;

    // 非数据库字段，列表展示用
    @Transient
    private String empName;
    @Transient
    private String empNo;
}
