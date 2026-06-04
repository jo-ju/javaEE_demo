package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.entity.LeaveRequest;
import org.example.hrms.mapper.LeaveRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LeaveRequestService {
    @Resource
    private LeaveRequestMapper leaveRequestMapper;

    public List<LeaveRequest> findAll(Long employeeId) {
        return leaveRequestMapper.findAll(employeeId);
    }

    /** 提交请假申请 */
    @Transactional
    public int applyLeave(LeaveRequest leave) {
        // 计算请假天数（含首尾）
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long diff = leave.getEndDate().getTime() - leave.getStartDate().getTime();
            int days = (int) (TimeUnit.MILLISECONDS.toDays(diff) + 1);
            leave.setDays(Math.max(days, 1));
        }
        leave.setStatus("待审批");
        leave.setApplyTime(new Date());
        return leaveRequestMapper.addLeave(leave);
    }

    /** 审批：approved=true 批准，false 驳回 */
    @Transactional
    public int approve(Long id, boolean approved, String approver) {
        LeaveRequest leave = leaveRequestMapper.findById(id);
        if (leave == null) {
            return 0;
        }
        leave.setStatus(approved ? "已批准" : "已驳回");
        leave.setApprover(approver);
        leave.setApproveTime(new Date());
        return leaveRequestMapper.updateStatus(leave);
    }

    @Transactional
    public int deleteLeave(Long id) {
        return leaveRequestMapper.deleteLeave(id);
    }
}
