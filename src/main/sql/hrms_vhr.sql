-- =====================================================================
-- HRMS 数据库脚本 (hrms_vhr)
-- 说明：员工 OA 流程（打卡 / 请假批假 / 月工资计算）+ 基础管理
-- 按文档需求：4 个部门，其中“人事部”拥有最高权限；表去除外键，便于增删
-- =====================================================================
CREATE DATABASE IF NOT EXISTS hrms_vhr DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE hrms_vhr;

-- 为保证可重复执行，先按依赖顺序删除旧表
DROP TABLE IF EXISTS salary;
DROP TABLE IF EXISTS leave_request;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS position;
DROP TABLE IF EXISTS dept;
DROP TABLE IF EXISTS sys_user;

-- 1. 部门表
CREATE TABLE dept (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    manager   VARCHAR(20) COMMENT '部门负责人'
) COMMENT '部门表';

-- 2. 职位表（去除外键）
CREATE TABLE position (
    id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
    pos_name VARCHAR(50) NOT NULL COMMENT '职位名称',
    pos_desc VARCHAR(200) COMMENT '职位描述',
    dept_id  BIGINT COMMENT '所属部门ID'
) COMMENT '职位表';

-- 3. 员工表（去除外键，新增底薪 base_salary）
CREATE TABLE employee (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
    emp_id      VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    emp_name    VARCHAR(20) NOT NULL COMMENT '姓名',
    dept_id     BIGINT COMMENT '所属部门ID',
    pos_id      BIGINT COMMENT '所属职位ID',
    entry_date  DATE COMMENT '入职日期',
    base_salary DECIMAL(10, 2) DEFAULT 0 COMMENT '底薪'
) COMMENT '员工表';

-- 4. 系统用户表（新增 role 角色 / dept_id 所属部门 / employee_id 关联员工）
-- role: ADMIN=超级管理员  HR=人事部(最高权限,可管理)  EMPLOYEE=普通员工(仅OA)
CREATE TABLE sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt哈希，教学简化期可为明文）',
    real_name   VARCHAR(20) COMMENT '真实姓名',
    role        VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE' COMMENT '角色:ADMIN/HR/EMPLOYEE',
    dept_id     BIGINT COMMENT '所属部门ID',
    employee_id BIGINT COMMENT '关联员工ID(普通员工OA用)'
) COMMENT '系统用户表';

-- 5. 打卡表（从入职开始计算员工打卡，每人每天一条）
CREATE TABLE attendance (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    clock_date  DATE NOT NULL COMMENT '打卡日期',
    clock_in    DATETIME COMMENT '上班打卡时间',
    clock_out   DATETIME COMMENT '下班打卡时间',
    status      VARCHAR(10) DEFAULT '正常' COMMENT '状态:正常/迟到/缺勤',
    UNIQUE KEY uk_emp_date (employee_id, clock_date)
) COMMENT '打卡表';

-- 6. 请假表（与打卡统计结合，需审批）
CREATE TABLE leave_request (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id  BIGINT NOT NULL COMMENT '员工ID',
    start_date   DATE NOT NULL COMMENT '请假开始日期',
    end_date     DATE NOT NULL COMMENT '请假结束日期',
    days         INT COMMENT '请假天数',
    reason       VARCHAR(200) COMMENT '请假事由',
    status       VARCHAR(10) DEFAULT '待审批' COMMENT '状态:待审批/已批准/已驳回',
    apply_time   DATETIME COMMENT '申请时间',
    approver     VARCHAR(20) COMMENT '审批人',
    approve_time DATETIME COMMENT '审批时间'
) COMMENT '请假表';

-- 7. 工资表（每月工资计算结果）
CREATE TABLE salary (
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id           BIGINT NOT NULL COMMENT '员工ID',
    salary_month          VARCHAR(7) NOT NULL COMMENT '工资月份 yyyy-MM',
    base_salary           DECIMAL(10, 2) DEFAULT 0 COMMENT '底薪',
    required_days         INT DEFAULT 0 COMMENT '应出勤天数(工作日)',
    attend_days           INT DEFAULT 0 COMMENT '实际出勤天数',
    leave_days            INT DEFAULT 0 COMMENT '请假天数(已批准)',
    absent_days           INT DEFAULT 0 COMMENT '缺勤天数',
    full_attendance_bonus DECIMAL(10, 2) DEFAULT 0 COMMENT '满勤奖',
    performance           DECIMAL(10, 2) DEFAULT 0 COMMENT '绩效',
    deduction             DECIMAL(10, 2) DEFAULT 0 COMMENT '缺勤扣款',
    total_salary          DECIMAL(10, 2) DEFAULT 0 COMMENT '实发工资',
    create_time           DATETIME COMMENT '计算时间',
    UNIQUE KEY uk_emp_month (employee_id, salary_month)
) COMMENT '工资表';

-- =====================================================================
-- 测试数据
-- =====================================================================
-- 4 个部门，人事部为最高权限部门
INSERT INTO dept (id, dept_name, manager) VALUES
    (1, '研发部', '张三'),
    (2, '人事部', '李四'),
    (3, '财务部', '王五'),
    (4, '市场部', '赵六');

INSERT INTO position (id, pos_name, pos_desc, dept_id) VALUES
    (1, '后端开发', '负责后端接口开发', 1),
    (2, '前端开发', '负责前端页面开发', 1),
    (3, 'HR专员',  '负责员工招聘与人事', 2),
    (4, '会计',    '负责财务核算', 3),
    (5, '市场专员', '负责市场推广', 4);

INSERT INTO employee (id, emp_id, emp_name, dept_id, pos_id, entry_date, base_salary) VALUES
    (1, 'EMP001', '小明', 1, 1, '2025-01-15', 12000.00),
    (2, 'EMP002', '小红', 1, 2, '2025-02-20', 11000.00),
    (3, 'EMP003', '小刚', 2, 3, '2024-05-10', 9000.00),
    (4, 'EMP004', '小丽', 3, 4, '2024-08-01', 10000.00),
    (5, 'EMP005', '小强', 4, 5, '2025-03-01', 9500.00);

-- 用户：admin 超管；hr 人事部(最高权限)关联小刚；ming 普通员工关联小明
INSERT INTO sys_user (username, password, real_name, role, dept_id, employee_id) VALUES
    ('admin', '123456', '超级管理员', 'ADMIN', NULL, NULL),
    ('hr',    '123456', '小刚',       'HR',    2,    3),
    ('ming',  '123456', '小明',       'EMPLOYEE', 1, 1),
    ('hong',  '123456', '小红',       'EMPLOYEE', 1, 2);

-- 小明本月部分打卡记录（便于演示工资计算）
INSERT INTO attendance (employee_id, clock_date, clock_in, clock_out, status) VALUES
    (1, '2026-06-01', '2026-06-01 09:00:00', '2026-06-01 18:00:00', '正常'),
    (1, '2026-06-02', '2026-06-02 09:05:00', '2026-06-02 18:00:00', '正常'),
    (1, '2026-06-03', '2026-06-03 08:55:00', '2026-06-03 18:10:00', '正常');

-- 一条待审批请假，便于演示 HR 批假流程
INSERT INTO leave_request (employee_id, start_date, end_date, days, reason, status, apply_time) VALUES
    (2, '2026-06-10', '2026-06-11', 2, '家中有事', '待审批', '2026-06-04 10:00:00');
