package org.example.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/** 请假申请：需 HR/管理员审批，与打卡统计结合 */
@Data
@Entity
@Table(name = "leave_request")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private Integer days;
    private String reason;
    /** 待审批 / 已批准 / 已驳回 */
    private String status;
    private Date applyTime;
    private String approver;
    private Date approveTime;

    // 非数据库字段，列表展示用
    @Transient
    private String empName;
    @Transient
    private String empNo;
}
