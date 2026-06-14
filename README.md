1. MVC 分层架构与数据流
1.1 分层职责
系统采用经典的 Controller → Service → Mapper (DAO) → Entity 四层架构，每一层职责明确，向上层提供服务，向下层发送请求。

层级	包路径	职责	关键特性
Controller	org.example.hrms.controller	接收 HTTP 请求，解析参数，调用 Service，返回 Result	RESTful 接口，@RequestMapping，@RequireManage 权限标记
Service	org.example.hrms.service	业务逻辑编排，事务管理，数据校验与计算	@Service，@Transactional，无接口抽象（直接实现类）
Mapper	org.example.hrms.mapper	数据访问层，SQL 语句定义与执行	@Mapper，MyBatis 行内 SQL 注解，@Param 参数绑定
Entity	org.example.hrms.entity	数据库表映射的 Java POJO，ORM 元信息	JPA @Entity/@Table 注解，MyBatis 仅利用映射名
1.2 统一返回与异常处理
common/Result.java — 所有 Controller 返回 Result 对象，包含 code（200 成功/500 失败）、msg、data 三字段，序列化为 JSON 响给前端。
common/GlobalExceptionHandler.java — @RestControllerAdvice 全局异常捕获，将未处理的异常转化为 Result.fail(...) 返回，避免堆栈泄露。
1.3 请求处理数据流
┌──────────┐     HTTP Request      ┌──────────────┐
│  Browser  │ ──────────────────→   │  Tomcat      │
│ (Vue SPA) │ ←── JSON Response ── │  (Port 8080) │
└──────────┘                       └──────┬───────┘
                                          │
                                    ┌─────▼──────┐
                                    │  CorsFilter │  ← CORS 跨域配置
                                    └─────┬──────┘
                                          │
                                    ┌─────▼────────────┐
                                    │  AuthInterceptor  │  ← 拦截 /api/**
                                    │  - 检查 Session    │
                                    │  - 检查 @RequireMg │
                                    └─────┬────────────┘
                                          │
                                    ┌─────▼──────┐
                                    │  Controller  │  ← 参数解析，权限路由
                                    └─────┬──────┘
                                          │
                                    ┌─────▼──────┐
                                    │  Service     │  ← @Transactional 事务
                                    │  业务逻辑计算  │
                                    └─────┬──────┘
                                          │
                                    ┌─────▼──────┐
                                    │  Mapper     │  ← MyBatis 注解 SQL
                                    └─────┬──────┘
                                          │
                                    ┌─────▼──────┐
                                    │  MySQL 8.0  │
                                    │  hrms_vhr   │
                                    └────────────┘
数据流详细说明：

浏览器发起 HTTP 请求到 localhost:8080。
CorsFilter 放行跨域（开发阶段允许所有来源）。
AuthInterceptor 拦截 /api/** 路径：
检查 HttpSession 中是否存在 loginUser — 不存在返回 401。
若请求的方法或 Controller 标记了 @RequireManage，检查当前用户角色是否为 ADMIN 或 HR — 否则返回 403。
放行 /api/login、/api/logout、/api/login/check 路径（无需登录）。
Controller 根据映射调用相应的 Service 方法，并返回 Result。
Service 层执行业务逻辑，如有需要调用一个或多个 Mapper 接口。
Mapper 通过 MyBatis 执行 SQL 操作 MySQL 8.0 数据库。
数据沿原路径返回，由 Jackson 3 序列化为 JSON 响应。
2. 系统功能结构
2.1 角色体系
系统按「部门区分人员」原则，定义三种核心角色：

角色	级别	权限说明
ADMIN	超级管理员	系统全部访问和业务操作权限
HR	人事部专员	最高业务权限（部门管理、审批、薪资计算等）
EMPLOYEE	普通员工	仅限 OA 模块（打卡、请假、查看个人薪资）
2.2 功能菜单树
人力资源管理系统 HRMS
├── 基础管理 (ADMIN / HR)
│   ├── 部门管理      → 新增 / 编辑 / 删除部门
│   ├── 职位管理      → 新增 / 编辑 / 删除职位（关联部门）
│   └── 员工管理      → 新增 / 编辑 / 删除员工（关联部门 & 职位）
│
├── 员工 OA (ADMIN / HR / EMPLOYEE)
│   ├── 我的打卡      → 上班/下班打卡 + 历史记录（迟到判定：09:00）
│   ├── 请假/批假     → 员工提交申请，HR/ADMIN 审批/驳回
│   └── 工资管理      → 月度薪资查看；ADMIN/HR 可触发计算
│
└── 统计报表 (ADMIN / HR)
    └── 信息统计      → 按部门统计员工数量
2.3 核心业务流程
员工日常 OA 流程：

  打卡签到                       请假申请
  ┌─────────┐                  ┌──────────┐
  │ 上班打卡 │                  │ 填写申请  │
  │ 09:00前=正常│              │ 日期+事由 │
  │ 09:00后=迟到│              └────┬─────┘
  └────┬────┘                       │
       │                            ▼
       │                    ┌──────────────┐
       │                    │  待审批       │
       │                    │ HR/ADMIN审批  │
  ┌────▼────┐               ├──────┬───────┤
  │ 下班打卡 │               │ 批准  │ 驳回  │
  └─────────┘               └──────┴───────┘
                                │
                                ▼
                    ┌──────────────────────┐
                    │   月度薪资计算         │
                    │ 底薪 + 满勤奖(+200)    │
                    │ + 绩效 − 缺勤扣款      │
                    │ = 实发工资             │
                    │ (HR/ADMIN 手动触发)    │
                    └──────────────────────┘
3. 项目文件结构
hrms/
├── pom.xml                                              # Maven 项目配置（依赖管理）
│
├── src/main/java/org/example/hrms/
│   ├── HrmsApplication.java                             # 启动类 @SpringBootApplication
│   │
│   ├── common/
│   │   ├── Result.java                                  # 统一 API 返回包装
│   │   ├── PasswordEncoder.java                         # BCrypt 密码加密工具
│   │   └── GlobalExceptionHandler.java                  # @RestControllerAdvice 全局异常处理
│   │
│   ├── config/
│   │   ├── AuthInterceptor.java                         # 登录与权限拦截器
│   │   ├── WebConfig.java                               # 注册拦截器（排除登录路径）
│   │   └── RequireManage.java                           # 自定义权限注解 @RequireManage
│   │
│   ├── entity/
│   │   ├── Dept.java                                    # 部门实体 (dept 表)
│   │   ├── Position.java                                # 职位实体 (position 表)
│   │   ├── Employee.java                                # 员工实体 (employee 表)
│   │   ├── SysUser.java                                 # 系统用户实体 (sys_user 表)
│   │   ├── Attendance.java                              # 打卡记录实体 (attendance 表)
│   │   ├── LeaveRequest.java                            # 请假申请实体 (leave_request 表)
│   │   └── Salary.java                                  # 薪资实体 (salary 表)
│   │
│   ├── mapper/
│   │   ├── DeptMapper.java                              # 部门 CRUD SQL
│   │   ├── PositionMapper.java                          # 职位 CRUD SQL（含 LEFT JOIN dept）
│   │   ├── EmployeeMapper.java                          # 员工 CRUD SQL（含 LEFT JOIN dept/position）
│   │   ├── SysUserMapper.java                           # 用户登录查询 + BCrypt 更新
│   │   ├── AttendanceMapper.java                        # 打卡记录查询/写入
│   │   ├── LeaveRequestMapper.java                      # 请假申请 CRUD + 跨月天数统计
│   │   └── SalaryMapper.java                            # 薪资记录查询/写入
│   │
│   ├── service/
│   │   ├── DeptService.java                             # 部门业务（CRUD 透传）
│   │   ├── PositionService.java                         # 职位业务（CRUD 透传）
│   │   ├── EmployeeService.java                         # 员工业务（CRUD + 统计）
│   │   ├── SysUserService.java                          # 登录验证（BCrypt + 明文渐进升级）
│   │   ├── AttendanceService.java                       # 打卡业务（迟到判定 + 并发保护）
│   │   ├── LeaveRequestService.java                     # 请假业务（天数计算 + 审批）
│   │   └── SalaryService.java                           # 薪资计算（核心算法 + 全员工批量）
│   │
│   └── controller/
│       ├── CorsConfig.java                              # CORS 跨域配置（全局允许）
│       ├── LoginController.java                         # 登录/登出/登录检查
│       ├── DeptController.java                          # /api/dept/** REST 接口
│       ├── PositionController.java                      # /api/position/** REST 接口
│       ├── EmployeeController.java                      # /api/employee/** REST 接口
│       ├── AttendanceController.java                    # /api/attendance/** REST 接口
│       ├── LeaveController.java                         # /api/leave/** REST 接口
│       └── SalaryController.java                        # /api/salary/** REST 接口
│
├── src/main/resources/
│   ├── application.yml                                  # Spring Boot 配置（数据源/Jackson/MyBatis）
│   └── static/
│       └── index.html                                   # 前端 SPA（Vue 3 + Element Plus，已迁移至 vuehr/）
│
├── src/main/sql/
│   └── hrms_vhr.sql                                     # 数据库初始化脚本（7 张表 + 种子数据）
│
├── vuehr/                                               # 前端 Vite + Vue 3 工程
│   ├── index.html                                       # Vite 入口 HTML
│   ├── package.json                                     # 依赖管理（Vue/Router/Pinia/ElementPlus/Axios）
│   ├── vite.config.js                                   # Vite 配置 + /api → localhost:8080 代理
│   └── src/
│       ├── main.js                                      # 应用入口（createApp + Pinia + Router）
│       ├── App.vue                                      # 根组件 <router-view>
│       ├── api/
│       │   ├── request.js                               # Axios 封装（baseURL/拦截器/错误处理）
│       │   └── index.js                                 # 27 个 API 函数按模块导出
│       ├── store/
│       │   └── user.js                                  # Pinia 用户状态（loginUser/role/canManage）
│       ├── router/
│       │   └── index.js                                 # 路由表 + beforeEach 权限守卫
│       ├── utils/
│       │   └── format.js                                # 日期/时间/状态格式化工具
│       ├── layouts/
│       │   └── MainLayout.vue                           # 主框架（侧边栏 + 顶栏 + router-view）
│       └── views/
│           ├── login/LoginView.vue                      # 登录页
│           ├── dept/DeptView.vue                        # 部门管理 CRUD
│           ├── position/PositionView.vue                # 职位管理 CRUD
│           ├── employee/EmployeeView.vue                # 员工管理 CRUD
│           ├── attendance/AttendanceView.vue            # 打卡面板 + 记录表
│           ├── leave/LeaveView.vue                      # 请假申请 + 审批列表
│           ├── salary/SalaryView.vue                    # 薪资计算 + 明细表
│           └── statistics/StatisticsView.vue             # 按部门统计
│
└── target/                                              # Maven 构建输出（已忽略）
4. 表结构与关系模式
4.1 总览
数据库 hrms_vhr 共 7 张表，无物理外键约束（便于灵活增删），业务关联通过程序逻辑维护。

┌─────────────────────────────────────────────────────────────────┐
│                         hrms_vhr 数据库                         │
│                                                                │
│  ┌─────────┐     ┌──────────────┐      ┌───────────────┐      │
│  │  dept   │←────│  position    │      │   employee    │      │
│  │ 部门    │ dept│  职位        │      │   员工        │      │
│  └─────────┘  ──→│  (dept_id)   │      │  (dept_id)    │      │
│                   └──────────────┘      │  (pos_id)     │      │
│                                         └───────┬───────┘      │
│                                                  │              │
│              ┌──────────────┐       ┌────────────┴──────────┐  │
│              │  sys_user    │       │  attendance            │  │
│              │  用户        │       │  打卡记录              │  │
│              │ (employee_id)│       │  (employee_id)         │  │
│              └──────┬───────┘      └────────────────────────┘  │
│                     │                                          │
│              ┌──────┴──────────┐    ┌────────────────────────┐  │
│              │  leave_request  │    │  salary                │  │
│              │  请假申请       │    │  薪资                  │  │
│              │  (employee_id)  │    │  (employee_id)         │  │
│              └─────────────────┘    └────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘

注解：──→ 表示「业务关联字段」，无物理外键约束
4.2 各表结构
4.2.1 dept（部门表）
字段	类型	说明
id	BIGINT PK	部门 ID
dept_name	VARCHAR(50)	部门名称
manager	VARCHAR(20)	部门负责人
种子数据：研发部、人事部（最高权限部门）、财务部、市场部

4.2.2 position（职位表）
字段	类型	说明
id	BIGINT PK	职位 ID
pos_name	VARCHAR(50)	职位名称
pos_desc	VARCHAR(200)	职位描述
dept_id	BIGINT	所属部门 ID（业务关联，无外键）
4.2.3 employee（员工表）
字段	类型	说明
id	BIGINT PK	员工 ID
emp_id	VARCHAR(20) UNIQUE	工号
emp_name	VARCHAR(20)	姓名
dept_id	BIGINT	所属部门 ID（业务关联）
pos_id	BIGINT	所属职位 ID（业务关联）
entry_date	DATE	入职日期（薪资计算起算点）
base_salary	DECIMAL(10,2)	底薪（月度薪资计算基础）
4.2.4 sys_user（系统用户表）
字段	类型	说明
id	BIGINT PK	用户 ID
username	VARCHAR(20) UNIQUE	用户名
password	VARCHAR(100)	密码（BCrypt 哈希，首次登录前为明文）
real_name	VARCHAR(20)	真实姓名
role	VARCHAR(20)	角色：ADMIN / HR / EMPLOYEE
dept_id	BIGINT	所属部门 ID
employee_id	BIGINT	关联员工 ID（员工 OA 使用时指向 employee 表）
4.2.5 attendance（打卡表）
字段	类型	说明
id	BIGINT PK	记录 ID
employee_id	BIGINT	员工 ID
clock_date	DATE	打卡日期（每人每天一条，UNIQUE KEY）
clock_in	DATETIME	上班打卡时间
clock_out	DATETIME	下班打卡时间
status	VARCHAR(10)	状态：正常 / 迟到 / 缺勤
4.2.6 leave_request（请假表）
字段	类型	说明
id	BIGINT PK	申请 ID
employee_id	BIGINT	员工 ID
start_date	DATE	请假开始日期
end_date	DATE	请假结束日期
days	INT	请假天数
reason	VARCHAR(200)	请假事由
status	VARCHAR(10)	状态：待审批 / 已批准 / 已驳回
apply_time	DATETIME	申请时间
approver	VARCHAR(20)	审批人
approve_time	DATETIME	审批时间
4.2.7 salary（工资表）
字段	类型	说明
id	BIGINT PK	记录 ID
employee_id	BIGINT	员工 ID
salary_month	VARCHAR(7)	工资月份 yyyy-MM（每人每月一条，UNIQUE KEY）
base_salary	DECIMAL(10,2)	底薪
required_days	INT	应出勤天数（工作日，周一~周五）
attend_days	INT	实际出勤天数
leave_days	INT	请假天数（已批准）
absent_days	INT	缺勤天数
full_attendance_bonus	DECIMAL(10,2)	满勤奖（缺勤=0 且 请假=0 时 +200）
performance	DECIMAL(10,2)	绩效
deduction	DECIMAL(10,2)	缺勤扣款
total_salary	DECIMAL(10,2)	实发工资
create_time	DATETIME	计算时间
5. 后端框架依赖说明
5.1 Spring Boot 4.0.6
核心入口：spring-boot-starter-web

提供嵌入式 Tomcat 11、Spring MVC 框架、REST 支持、Jackson 3 序列化。项目以此为父工程，自动管理所有 Spring 组件的版本。

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.6</version>
</parent>
关键自动配置：

特性	说明
spring.jackson.time-zone: Asia/Shanghai	统一时区，避免 UTC 时差
spring.jackson.date-format: yyyy-MM-dd HH:mm:ss	全局日期格式化
spring.jpa.hibernate.ddl-auto: none	关闭 Hibernate DDL，表由 SQL 脚本手工管理
spring.jpa.open-in-view: false	关闭 Open Session in View 性能优化
5.2 Spring MVC 组件
由 spring-boot-starter-web 传递引入，不需要单独声明版本：

Spring Web MVC — @RestController、@RequestMapping、拦截器体系
Spring Web — HttpSession 会话管理、CORS 过滤器
Tomcat 11 — 内嵌 Servlet 容器
Jackson 3（包名 tools.jackson.*）— JSON 序列化/反序列化
自定义 MVC 配置：

config/AuthInterceptor.java — HandlerInterceptor 实现，在 preHandle 中完成登录检查和权限校验
config/WebConfig.java — 注册拦截器，指定拦截路径 /api/**，排除 /api/login 等
config/RequireManage.java — 自定义注解，配合拦截器实现声明式权限控制
5.3 Spring Data JPA
依赖：spring-boot-starter-data-jpa

在项目中的角色：仅用于实体注解（@Entity、@Table、@Id、@ManyToOne、@OneToMany、@Transient 等），所有实际数据库查询走 MyBatis。不使用 JPA Repository。

5.4 MyBatis
依赖：mybatis-spring-boot-starter 4.0.1

完整的 MyBatis 与 Spring Boot 集成。配置：

mybatis:
  type-aliases-package: org.example.hrms.entity
  configuration.map-underscore-to-camel-case: true
SQL 风格：100% 行内注解 SQL（@Select、@Insert、@Update、@Delete），无 XML 映射文件
动态 SQL：复杂查询使用 <script> 标签包裹 XML 样式的 <where>、<if> 条件
结果映射：@Results + @Result 用于多表 LEFT JOIN 的字段到嵌套对象的映射
5.5 BCrypt 密码加密
依赖：spring-security-crypto（独立于 Spring Security 自动配置）

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
common/PasswordEncoder.java — 封装 BCryptPasswordEncoder，提供 encode()、matches()、isBCrypt() 方法
渐进式升级：数据库种子数据为明文密码，首次登录成功后自动升级为 BCrypt 哈希存储
5.6 其他依赖
依赖	版本	作用域	用途
mysql-connector-j	9.7.0（由 parent 管理）	runtime	MySQL JDBC 驱动
lombok	由 parent 管理	optional	@Data、@Getter 等消除样板代码
spring-boot-starter-test	由 parent 管理	test	JUnit 5 + Mockito 测试框架
mybatis-spring-boot-starter-test	4.0.1	test	MyBatis 测试支持
6. 前端工程化分析
6.1 架构演进说明
项目前端经历了从单文件 SPA 到 Vite 工程化的改造。旧版 static/index.html 保留作为备参，当前活动前端为 vuehr/ 目录下的独立工程。

维度	旧版 (index.html)	新版 (vuehr/)
构建工具	无（CDN 加载）	Vite 6 工程构建
模块化	单文件 917 行	19 个模块化文件
路由	v-show 条件渲染	Vue Router 4 路由导航
状态管理	无	Pinia 2 集中存储
组件封装	无（全局 setup）	8 个 .vue SFC
开发服务器	Spring Boot 静态资源	Vite Dev Server (:5173)
API 通信	axios.defaults.baseURL	Axios 实例 + 响应拦截器
6.2 已实现的前端工程
6.2.1 依赖清单
{
  "dependencies": {
    "vue": "^3.5",
    "vue-router": "^4.5",
    "pinia": "^2.3",
    "element-plus": "^2.9",
    "axios": "^1.7"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.2",
    "vite": "^6.0"
  }
}
6.2.2 组件结构
vuehr/
├── index.html                               # Vite 入口 HTML
├── vite.config.js                           # Vite 配置 + 代理
├── package.json
│
└── src/
    ├── main.js                              # createApp + Pinia + Router + ElementPlus
    ├── App.vue                              # <router-view> 根组件
    │
    ├── api/
    │   ├── request.js                       # Axios 实例化、响应拦截（401跳转登录）
    │   └── index.js                         # 27 个 API 函数按模块导出
    │
    ├── store/
    │   └── user.js                          # Pinia: loginUser, isLogin, canManage, roleLabel
    │
    ├── router/
    │   └── index.js                         # 7 条子路由 + beforeEach 守卫
    │
    ├── utils/
    │   └── format.js                        # dateFmt, timeFmt, leaveTagType, currentMonth
    │
    ├── layouts/
    │   └── MainLayout.vue                   # 侧边栏（动态菜单）+ 顶栏 + <router-view>
    │
    └── views/
        ├── login/LoginView.vue              # 登录页（表单 + session 自动恢复）
        ├── dept/DeptView.vue                # 部门管理 CRUD
        ├── position/PositionView.vue        # 职位管理 CRUD（部门级联下拉）
        ├── employee/EmployeeView.vue        # 员工管理 CRUD（部门→职位联动）
        ├── attendance/AttendanceView.vue    # 打卡面板 + 实时时钟 + 记录表
        ├── leave/LeaveView.vue              # 请假申请 + 审批列表
        ├── salary/SalaryView.vue            # 工资计算 + 明细表
        └── statistics/StatisticsView.vue    # 按部门统计
6.2.3 Axios 封装
// src/api/request.js
const request = axios.create({
  baseURL: '/api',
  withCredentials: true,
  timeout: 15000
})

// 响应拦截器：统一处理 401/403/500
request.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code !== 200) {
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(data)
    }
    return data
  },
  error => {
    if (error.response?.status === 401) {
      router.push('/login')           // 未登录跳转
    } else if (error.response?.status === 403) {
      ElMessage.error('无操作权限')
    } else {
      ElMessage.error(error.response?.data?.msg || '网络错误')
    }
    return Promise.reject(error)
  }
)
6.2.4 Vite 代理配置
// vite.config.js
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
6.2.5 路由与权限守卫
// src/router/index.js
const routes = [
  { path: '/login', component: LoginView },
  {
    path: '/',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      { path: 'dept',        component: DeptView,        meta: { roles: ['ADMIN', 'HR'] } },
      { path: 'position',    component: PositionView,    meta: { roles: ['ADMIN', 'HR'] } },
      { path: 'employee',    component: EmployeeView,    meta: { roles: ['ADMIN', 'HR'] } },
      { path: 'attendance',  component: AttendanceView                              },
      { path: 'leave',       component: LeaveView                                   },
      { path: 'salary',      component: SalaryView                                  },
      { path: 'statistics',  component: StatisticsView,  meta: { roles: ['ADMIN', 'HR'] } },
    ]
  }
]

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLogin) return next('/login')
  if (to.meta.roles && !to.meta.roles.includes(userStore.role)) return next('/')
  next()
})
6.2.6 Pinia 状态管理
// src/store/user.js
export const useUserStore = defineStore('user', () => {
  const loginUser = ref({})
  const isLogin  = computed(() => !!loginUser.value.id)
  const role      = computed(() => loginUser.value.role || '')
  const canManage = computed(() => ['ADMIN', 'HR'].includes(role.value))

  async function login(username, password) {
    const res = await loginApi({ username, password })
    loginUser.value = res.data
  }

  async function checkSession() {
    const res = await checkLogin()
    if (res.code === 200) { loginUser.value = res.data; return true }
    return false
  }

  async function logout() { await logoutApi(); loginUser.value = {} }
  return { loginUser, isLogin, role, canManage, login, checkSession, logout }
})
7. 模块功能代码对照
前端文件路径均指向 vuehr/ 工程下的 .vue 组件。旧版单文件 static/index.html 保留作为备参，但其功能已全部迁移至组件中。

7.1 登录 / 登出模块
层级	文件	关键内容
Controller	controller/LoginController.java	POST /api/login、GET /api/login/check、POST /api/logout
Service	service/SysUserService.java	登录验证（BCrypt + 明文渐进升级）
Mapper	mapper/SysUserMapper.java	findByUsername() 按用户名查询、updatePassword() 升级密码
前端	vuehr/src/views/login/LoginView.vue	登录表单 + 角色提示 + 登录按钮
7.2 部门管理模块
层级	文件	关键内容
Controller	controller/DeptController.java	GET /api/dept/list、POST /add、PUT /update、DELETE /delete/{id}
Service	service/DeptService.java	CRUD 透传
Mapper	mapper/DeptMapper.java	findAll()、addDept()、updateDept()、deleteDept()
前端	vuehr/src/views/dept/DeptView.vue	部门表单 + 表格
7.3 职位管理模块
层级	文件	关键内容
Controller	controller/PositionController.java	GET /api/position/list、POST /add、PUT /update、DELETE /delete/{id}
Service	service/PositionService.java	CRUD 透传
Mapper	mapper/PositionMapper.java	findAll() LEFT JOIN dept、CRUD
前端	vuehr/src/views/position/PositionView.vue	职位表单（部门下拉）+ 表格
7.4 员工管理模块
层级	文件	关键内容
Controller	controller/EmployeeController.java	GET /api/employee/list、POST /add、PUT /update、DELETE /delete/{id}、GET /countByDept
Service	service/EmployeeService.java	CRUD + countEmpByDept()
Mapper	mapper/EmployeeMapper.java	findAll() LEFT JOIN dept/position、CRUD、countEmpByDept()
前端	vuehr/src/views/employee/EmployeeView.vue	员工表单（部门→职位级联）+ 表格
7.5 打卡考勤模块
层级	文件	关键内容
Controller	controller/AttendanceController.java	GET /list、POST /clockIn、POST /clockOut
Service	service/AttendanceService.java	clockIn() 迟到判定 + 并发锁、clockOut()
Mapper	mapper/AttendanceMapper.java	findByEmpAndDate()、findAll()、insertClockIn()、updateClockOut()、countAttendDays()
前端	vuehr/src/views/attendance/AttendanceView.vue	实时时钟显示、上下班按钮、打卡记录表
7.6 请假审批模块
层级	文件	关键内容
Controller	controller/LeaveController.java	GET /list、POST /apply、POST /approve/{id}、DELETE /delete/{id}
Service	service/LeaveRequestService.java	applyLeave() 天数计算、approve() 审批
Mapper	mapper/LeaveRequestMapper.java	findAll()、addLeave()、updateStatus()、countApprovedLeaveDays()（跨月统计）
前端	vuehr/src/views/leave/LeaveView.vue	日期范围选择、请假申请表单、审批/驳回按钮
7.7 薪资管理模块
层级	文件	关键内容
Controller	controller/SalaryController.java	GET /list（按角色过滤）、POST /calculate、POST /calculateAll
Service	service/SalaryService.java	calculate() 核心算法、calculateAll() 批量计算
Mapper	mapper/SalaryMapper.java	findAll()（支持行/员工过滤）、insertSalary()、updateSalary()
前端	vuehr/src/views/salary/SalaryView.vue	月份选择、绩效输入、一键计算按钮、工资明细表
7.8 统计报表模块
层级	文件	关键内容
Controller	controller/EmployeeController.java	GET /api/employee/countByDept
Service	service/EmployeeService.java	countEmpByDept()
Mapper	mapper/EmployeeMapper.java	countEmpByDept() — LEFT JOIN + GROUP BY
前端	vuehr/src/views/statistics/StatisticsView.vue	部门名称 + 员工数量的简单表格
7.9 全局基础设施
组件	文件	功能
统一返回	common/Result.java	code/msg/data 三字段标准响应
全局异常	common/GlobalExceptionHandler.java	6 类异常的统一捕获和友好提示
密码加密	common/PasswordEncoder.java	BCrypt 加密 + 明文兼容
权限注解	config/RequireManage.java	@RequireManage 声明式权限标记
登录拦截	config/AuthInterceptor.java	Session 检查 + @RequireManage 角色校验
拦截注册	config/WebConfig.java	注册拦截器到 /api/**，排除登录路径
跨域	controller/CorsConfig.java	CorsFilter 全局允许跨域
应用配置	application.yml	数据库、Jackson、MyBatis 配置
数据库脚本	sql/hrms_vhr.sql	7 张表 DDL + 种子数据
8. 测试用例与权限分组
8.1 权限矩阵
API 端点	ADMIN	HR	EMPLOYEE	拦截方式
POST /api/login	✅	✅	✅	不拦截（显式排除）
GET /api/login/check	✅	✅	✅	不拦截
POST /api/logout	✅	✅	✅	不拦截
GET /api/dept/list	✅	✅	✅	仅检查登录
POST/PUT/DELETE /api/dept/*	✅	✅	❌	@RequireManage
GET /api/position/list	✅	✅	✅	仅检查登录
POST/PUT/DELETE /api/position/*	✅	✅	❌	@RequireManage
GET /api/employee/list	✅	✅	✅	仅检查登录
POST/PUT/DELETE /api/employee/*	✅	✅	❌	@RequireManage
GET /api/employee/countByDept	✅	✅	❌	@RequireManage
GET /api/attendance/list	✅（全部）	✅（全部）	✅（仅自己）	角色逻辑
POST /api/attendance/clockIn	❌（需employeeId）	❌	✅	仅检查登录
POST /api/attendance/clockOut	❌	❌	✅	仅检查登录
GET /api/leave/list	✅（全部）	✅（全部）	✅（仅自己）	角色逻辑
POST /api/leave/apply	❌	❌	✅	仅检查登录
POST /api/leave/approve/{id}	✅	✅	❌	@RequireManage
DELETE /api/leave/delete/{id}	✅	✅	❌	@RequireManage
GET /api/salary/list	✅（全部）	✅（全部）	✅（仅自己）	角色逻辑
POST /api/salary/calculate	✅	✅	❌	@RequireManage
POST /api/salary/calculateAll	✅	✅	❌	@RequireManage
8.2 权限控制实现原理
AuthInterceptor.preHandle()
│
├─ 1. 放行非 HandlerMethod（静态资源等）
│
├─ 2. 从 HttpSession 获取 loginUser
│   └─ 空 → 返回 401 JSON (未登录)
│
├─ 3. 检查方法或类是否有 @RequireManage 注解
│   └─ 有 → 检查 loginUser.role 是否为 ADMIN 或 HR
│       ├─ 否 → 返回 403 JSON (无权限)
│       └─ 是 → 放行
│
└─ 4. 放行请求到 Controller
8.3 登录测试用例
用例#	用户名	密码	预期结果	验证点
TC01	admin	123456	登录成功，role=ADMIN	密码自动升级为 BCrypt
TC02	hr	123456	登录成功，role=HR	密码自动升级为 BCrypt
TC03	ming	123456	登录成功，role=EMPLOYEE，employeeId=1	密码自动升级为 BCrypt
TC04	hong	123456	登录成功，role=EMPLOYEE，employeeId=2	密码自动升级为 BCrypt
TC05	admin	wrong	登录失败，"用户名或密码错误"	
TC06	nobody	123456	登录失败，"用户名或密码错误"	
TC07	—	—	GET /api/login/check 返回 401（未登录）	Session 未建立
8.4 端到端 OA 流程测试
TC-OA-01: 员工 ming 完整打卡流程
  步骤:
  1. POST /api/login (ming/123456) → 200
  2. POST /api/attendance/clockIn → "上班打卡成功（正常/迟到）"
  3. POST /api/attendance/clockOut → "下班打卡成功"
  4. POST /api/attendance/clockIn → "今日已打过上班卡"（幂等）
  预期: 数据库 attendance 表新增一条完整记录

TC-OA-02: 员工 ming 请假 + 管理员审批
  步骤:
  1. POST /api/login (ming/123456) → 200
  2. POST /api/leave/apply {"startDate":"2026-06-15","endDate":"2026-06-16"} → "请假申请已提交"
  3. POST /api/login (admin/123456) → 200
  4. GET /api/leave/list → 包含 ming 的待审批记录
  5. POST /api/leave/approve/2?approved=true → "已批准"
  预期: leave_request 状态变更为"已批准"，approver 为"超级管理员"

TC-OA-03: 管理员计算 ming 某月薪资
  步骤:
  1. POST /api/login (admin/123456) → 200
  2. POST /api/salary/calculate?employeeId=1&month=2026-06&performance=500 → "计算完成"
  3. GET /api/salary/list?month=2026-06 → 包含 ming 的记录
  预期: salary 表新增记录，total_salary = 底薪 + 满勤奖 + 绩效 − 缺勤扣款

TC-OA-04: 权限隔离
  步骤:
  1. POST /api/login (ming/123456) → 200
  2. POST /api/employee/add → 403（Employee 无管理权限）
  3. POST /api/dept/add → 403
  4. POST /api/leave/approve/1 → 403
  5. POST /api/salary/calculateAll → 403
  预期: 所有管理接口返回 403

TC-OA-05: 员工薪资隔离
  步骤:
  1. POST /api/login (ming/123456) → 200
  2. GET /api/salary/list → 仅返回 ming 自己的薪资记录
  预期: 数据不包含其他员工的薪资
8.5 并发安全性测试
TC-CON-01: 快速双击打卡
  步骤:
  1. 连续两次 POST /api/attendance/clockIn
  预期: 第一次成功，第二次返回"今日已打过上班卡"
  验证: attendance 表仅有一条当日记录

TC-CON-02: 重复计算薪资
  步骤:
  1. 连续两次 POST /api/salary/calculate?employeeId=1&month=2026-06
  预期: 第一次成功插入，第二次更新同一记录
  验证: salary 表仅有一条 employeeId=1 + salaryMonth=2026-06 的记录
9. 附录
9.1 环境要求
组件	版本	路径
JDK	17+	D:\Program Files\zulu17\bin\java.exe
Maven	3.9+	D:\Program Files\apache-maven-3.9.16\bin\mvn
MySQL	8.0+	localhost:3306
数据库	hrms_vhr	root / 123456
9.2 运行命令速查
# ===== 后端（Spring Boot） =====

# 1. 初始化数据库
mysql -uroot -p123456 --default-character-set=utf8mb4 < src/main/sql/hrms_vhr.sql

# 2. 构建（跳过测试）
mvn -DskipTests clean package

# 3. 运行
java -jar target/hrms-0.0.1-SNAPSHOT.jar
# （或）mvn spring-boot:run

# ===== 前端（Vue 3 + Vite） =====

# 4. 进入前端工程目录并安装依赖（仅首次）
cd vuehr && npm install

# 5. 启动前端开发服务器
cd vuehr && npm run dev

# ===== 访问 =====

# 后端 API：http://localhost:8080/api/...
# 前端页面：http://localhost:5173        ← 开发时使用此地址
# 旧版 SPA：http://localhost:8080/       ← 不启动前端时使用
