DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '考勤记录ID',
  `eid` int(11) NOT NULL COMMENT '员工ID',
  `attendance_date` date NOT NULL COMMENT '考勤日期',
  `check_in_time` time COMMENT '打卡上班时间',
  `check_out_time` time COMMENT '打卡下班时间',
  `status` enum('正常','迟到','早退','缺勤','病假','事假','年假') DEFAULT '正常' COMMENT '考勤状态',
  `remark` varchar(255) COMMENT '备注',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `eid` (`eid`),
  KEY `attendance_date` (`attendance_date`),
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '薪资ID',
  `eid` int(11) NOT NULL COMMENT '员工ID',
  `salary_month` varchar(7) NOT NULL COMMENT '薪资年月(YYYY-MM)',
  `base_salary` decimal(10,2) DEFAULT 0 COMMENT '基本工资',
  `performance_bonus` decimal(10,2) DEFAULT 0 COMMENT '绩效奖金',
  `attendance_allowance` decimal(10,2) DEFAULT 0 COMMENT '考勤补贴',
  `gross_salary` decimal(10,2) DEFAULT 0 COMMENT '应发工资',
  `social_security` decimal(10,2) DEFAULT 0 COMMENT '社保扣款',
  `provident_fund` decimal(10,2) DEFAULT 0 COMMENT '公积金扣款',
  `personal_income_tax` decimal(10,2) DEFAULT 0 COMMENT '个人所得税',
  `other_deductions` decimal(10,2) DEFAULT 0 COMMENT '其他扣款',
  `net_salary` decimal(10,2) DEFAULT 0 COMMENT '实发工资',
  `status` enum('草稿','已核算','已发放','已取消') DEFAULT '草稿' COMMENT '工资单状态',
  `issued_date` date COMMENT '发放日期',
  `remark` varchar(255) COMMENT '备注',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `eid` (`eid`),
  KEY `salary_month` (`salary_month`),
  CONSTRAINT `salary_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator` varchar(50) COMMENT '操作人',
  `module` varchar(50) NOT NULL COMMENT '操作模块',
  `operation` varchar(100) NOT NULL COMMENT '操作内容',
  `method` varchar(255) COMMENT '请求方法',
  `params` longtext COMMENT '请求参数',
  `result` longtext COMMENT '返回结果',
  `status` enum('成功','失败') DEFAULT '成功' COMMENT '操作状态',
  `error_msg` longtext COMMENT '错误信息',
  `ip_address` varchar(50) COMMENT 'IP地址',
  `user_agent` varchar(500) COMMENT '用户代理',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `operator` (`operator`),
  KEY `module` (`module`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
