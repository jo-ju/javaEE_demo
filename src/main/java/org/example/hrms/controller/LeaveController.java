package org.example.hrms.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.example.hrms.common.Result;
import org.example.hrms.config.RequireManage;
import org.example.hrms.entity.LeaveRequest;
import org.example.hrms.entity.SysUser;
import org.example.hrms.service.LeaveRequestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {
    @Resource
    private LeaveRequestService leaveRequestService;

    /** 请假列表：管理员/HR 看全部（用于批假），普通员工只看自己 */
    @GetMapping("/list")
    public Result list(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        boolean manager = "ADMIN".equals(user.getRole()) || "HR".equals(user.getRole());
        Long filter = manager ? null : user.getEmployeeId();
        return Result.ok(leaveRequestService.findAll(filter));
    }

    /** 提交请假申请（当前登录员工） */
    @PostMapping("/apply")
    public Result apply(@RequestBody LeaveRequest leave, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user.getEmployeeId() == null) {
            return Result.fail("当前账号未关联员工，无法申请请假");
        }
        leave.setEmployeeId(user.getEmployeeId());
        leaveRequestService.applyLeave(leave);
        return Result.ok("请假申请已提交，等待审批", null);
    }

    /** 批假：仅管理员/HR */
    @RequireManage
    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id,
                          @RequestParam boolean approved,
                          HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        int rows = leaveRequestService.approve(id, approved, user.getRealName());
        if (rows == 0) {
            return Result.fail("审批失败，请假记录不存在");
        }
        return Result.ok(approved ? "已批准" : "已驳回", null);
    }

    /** 撤销/删除请假：仅管理员/HR */
    @RequireManage
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        leaveRequestService.deleteLeave(id);
        return Result.ok("删除成功", null);
    }
}
