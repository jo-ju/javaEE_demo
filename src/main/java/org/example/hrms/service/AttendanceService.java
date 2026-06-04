package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Attendance;
import org.example.hrms.mapper.AttendanceMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AttendanceService {
    @Resource
    private AttendanceMapper attendanceMapper;

    private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");

    /** 并发控制：防止同一员工快速双击导致冗余打卡 */
    private final ConcurrentHashMap<Long, Object> lockMap = new ConcurrentHashMap<>();

    public List<Attendance> findAll(Long employeeId) {
        return attendanceMapper.findAll(employeeId);
    }

    public List<Attendance> findByEmpAndMonth(Long employeeId, String month) {
        return attendanceMapper.findByEmpAndMonth(employeeId, month);
    }

    /**
     * 上班打卡：每天只能打一次卡。9:00 之后视为迟到。
     * 事务保护 + 并发锁防止重复提交。
     */
    @Transactional
    public String clockIn(Long employeeId) {
        Object lock = lockMap.computeIfAbsent(employeeId, k -> new Object());
        synchronized (lock) {
            try {
                String today = dateFmt.format(new Date());
                Attendance exist = attendanceMapper.findByEmpAndDate(employeeId, today);
                if (exist != null && exist.getClockIn() != null) {
                    return "今日已打过上班卡";
                }
                Date now = new Date();
                Attendance a = new Attendance();
                a.setEmployeeId(employeeId);
                a.setClockDate(now);
                a.setClockIn(now);
                a.setStatus(isLate(now) ? "迟到" : "正常");
                try {
                    attendanceMapper.insertClockIn(a);
                } catch (DuplicateKeyException e) {
                    return "今日已打过上班卡";
                }
                return "上班打卡成功（" + a.getStatus() + "）";
            } finally {
                lockMap.remove(employeeId);
            }
        }
    }

    /**
     * 下班打卡：需先有上班打卡记录。
     */
    @Transactional
    public String clockOut(Long employeeId) {
        String today = dateFmt.format(new Date());
        Attendance exist = attendanceMapper.findByEmpAndDate(employeeId, today);
        if (exist == null || exist.getClockIn() == null) {
            return "请先进行上班打卡";
        }
        if (exist.getClockOut() != null) {
            return "今日已打过下班卡";
        }
        exist.setClockOut(new Date());
        attendanceMapper.updateClockOut(exist);
        return "下班打卡成功";
    }

    /** 9:00 之后视为迟到 */
    private boolean isLate(Date clockIn) {
        Calendar c = Calendar.getInstance();
        c.setTime(clockIn);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return hour > 9 || (hour == 9 && minute > 0);
    }
}
