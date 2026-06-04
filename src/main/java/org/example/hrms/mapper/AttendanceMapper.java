package org.example.hrms.mapper;

import org.apache.ibatis.annotations.*;
import org.example.hrms.entity.Attendance;

import java.util.List;

@Mapper
public interface AttendanceMapper {

    /** 查询某员工某天的打卡记录 */
    @Select("SELECT * FROM attendance WHERE employee_id=#{employeeId} AND clock_date=#{clockDate}")
    Attendance findByEmpAndDate(@Param("employeeId") Long employeeId, @Param("clockDate") String clockDate);

    /** 全部打卡记录（含员工姓名/工号），可按员工筛选 */
    @Select("<script>" +
            "SELECT a.*, e.emp_name AS empName, e.emp_id AS empNo FROM attendance a " +
            "LEFT JOIN employee e ON a.employee_id = e.id " +
            "<where>" +
            "  <if test='employeeId != null'> a.employee_id = #{employeeId} </if>" +
            "</where>" +
            " ORDER BY a.clock_date DESC, a.employee_id" +
            "</script>")
    List<Attendance> findAll(@Param("employeeId") Long employeeId);

    @Insert("INSERT INTO attendance (employee_id, clock_date, clock_in, status) " +
            "VALUES (#{employeeId}, #{clockDate}, #{clockIn}, #{status})")
    int insertClockIn(Attendance attendance);

    @Update("UPDATE attendance SET clock_out=#{clockOut} WHERE id=#{id}")
    int updateClockOut(Attendance attendance);

    /** 统计某员工某月“正常/迟到”（出勤）天数 */
    @Select("SELECT COUNT(*) FROM attendance " +
            "WHERE employee_id=#{employeeId} AND DATE_FORMAT(clock_date, '%Y-%m')=#{month} " +
            "AND status IN ('正常','迟到')")
    int countAttendDays(@Param("employeeId") Long employeeId, @Param("month") String month);

    /** 某员工某月打卡明细 */
    @Select("SELECT a.*, e.emp_name AS empName, e.emp_id AS empNo FROM attendance a " +
            "LEFT JOIN employee e ON a.employee_id = e.id " +
            "WHERE a.employee_id=#{employeeId} AND DATE_FORMAT(a.clock_date,'%Y-%m')=#{month} " +
            "ORDER BY a.clock_date")
    List<Attendance> findByEmpAndMonth(@Param("employeeId") Long employeeId, @Param("month") String month);
}
