CREATE DATABASE IF NOT EXISTS hrms_vhr DEFAULT CHARACTER SET utf8mb4;
USE hrms_vhr;

-- 1. 系统用户表（新增，基础登录）
CREATE TABLE sys_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名',
                          password VARCHAR(32) NOT NULL COMMENT '密码（简化版明文，实际项目需加密）'
) COMMENT '系统用户表';

-- 2. 部门表
CREATE TABLE dept (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
                      dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
                      manager VARCHAR(20) COMMENT '部门负责人'
) COMMENT '部门表';

-- 3. 职位表（新增，统一管理职位）
CREATE TABLE position (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
                          pos_name VARCHAR(50) NOT NULL COMMENT '职位名称',
                          pos_desc VARCHAR(200) COMMENT '职位描述',
                          dept_id BIGINT COMMENT '所属部门ID',
                          FOREIGN KEY (dept_id) REFERENCES dept(id) ON DELETE SET NULL
) COMMENT '职位表';

-- 4. 员工表（修改，关联职位表）
CREATE TABLE employee (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
                          emp_id VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
                          emp_name VARCHAR(20) NOT NULL COMMENT '姓名',
                          dept_id BIGINT COMMENT '所属部门ID',
                          pos_id BIGINT COMMENT '所属职位ID',
                          entry_date DATE COMMENT '入职日期',
                          FOREIGN KEY (dept_id) REFERENCES dept(id) ON DELETE SET NULL,
                          FOREIGN KEY (pos_id) REFERENCES position(id) ON DELETE SET NULL
) COMMENT '员工表';

-- 测试数据
INSERT INTO sys_user (username, password) VALUES ('admin', '123456');

INSERT INTO dept (dept_name, manager) VALUES
                                          ('研发部', '张三'),
                                          ('人事部', '李四'),
                                          ('财务部', '王五');

INSERT INTO position (pos_name, pos_desc, dept_id) VALUES
                                                       ('后端开发', '负责后端接口开发', 1),
                                                       ('前端开发', '负责前端页面开发', 1),
                                                       ('HR专员', '负责员工招聘', 2),
                                                       ('会计', '负责财务核算', 3);

INSERT INTO employee (emp_id, emp_name, dept_id, pos_id, entry_date) VALUES
                                                                         ('EMP001', '小明', 1, 1, '2023-01-15'),
                                                                         ('EMP002', '小红', 1, 2, '2023-02-20'),
                                                                         ('EMP003', '小刚', 2, 3, '2022-05-10'),
                                                                         ('EMP004', '小丽', 3, 4, '2022-08-01');