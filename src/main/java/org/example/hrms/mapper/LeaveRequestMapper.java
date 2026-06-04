package org.example.hrms.mapper;

import org.apache.ibatis.annotations.*;
import org.example.hrms.entity.LeaveRequest;

import java.util.List;

@Mapper
public interface LeaveRequestMapper {

    /** 全部请假申请（含员工信息），可按员工筛选 */
    @Select("<script>" +
            "SELECT l.*, e.emp_name AS empName, e.emp_id AS empNo FROM leave_request l " +
            "LEFT JOIN employee e ON l.employee_id = e.id " +
            "<where>" +
            "  <if test='employeeId != null'> l.employee_id = #{employeeId} </if>" +
            "</where>" +
            " ORDER BY l.apply_time DESC" +
            "</script>")
    List<LeaveRequest> findAll(@Param("employeeId") Long employeeId);

    @Select("SELECT * FROM leave_request WHERE id=#{id}")
    LeaveRequest findById(Long id);

    @Insert("INSERT INTO leave_request (employee_id, start_date, end_date, days, reason, status, apply_time) " +
            "VALUES (#{employeeId}, #{startDate}, #{endDate}, #{days}, #{reason}, #{status}, #{applyTime})")
    int addLeave(LeaveRequest leave);

    @Update("UPDATE leave_request SET status=#{status}, approver=#{approver}, approve_time=#{approveTime} WHERE id=#{id}")
    int updateStatus(LeaveRequest leave);

    @Delete("DELETE FROM leave_request WHERE id=#{id}")
    int deleteLeave(Long id);

    /**
     * 统计某员工某月已批准的请假天数。
     * 支持跨月请假：按实际落在该月内的天数计算。
     */
    @Select("SELECT COALESCE(SUM(" +
            "  CASE " +
            "    WHEN start_date < CONCAT(#{month}, '-01') THEN " +
            "      DATEDIFF(LEAST(end_date, LAST_DAY(CONCAT(#{month}, '-01'))), CONCAT(#{month}, '-01')) + 1 " +
            "    ELSE " +
            "      DATEDIFF(LEAST(end_date, LAST_DAY(CONCAT(#{month}, '-01'))), start_date) + 1 " +
            "  END" +
            "), 0) FROM leave_request " +
            "WHERE employee_id=#{employeeId} AND status='已批准' " +
            "AND start_date <= LAST_DAY(CONCAT(#{month}, '-01')) " +
            "AND end_date >= CONCAT(#{month}, '-01')")
    int countApprovedLeaveDays(@Param("employeeId") Long employeeId, @Param("month") String month);
}
