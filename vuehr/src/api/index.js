import request from './request'

// ========== 登录 ==========
export const login = (data) => request.post('/login', data)
export const checkLogin = () => request.get('/login/check')
export const logout = () => request.post('/logout')

// ========== 部门 ==========
export const getDeptList = () => request.get('/dept/list')
export const addDept = (data) => request.post('/dept/add', data)
export const updateDept = (data) => request.put('/dept/update', data)
export const deleteDept = (id) => request.delete('/dept/delete/' + id)

// ========== 职位 ==========
export const getPositionList = () => request.get('/position/list')
export const addPosition = (data) => request.post('/position/add', data)
export const updatePosition = (data) => request.put('/position/update', data)
export const deletePosition = (id) => request.delete('/position/delete/' + id)

// ========== 员工 ==========
export const getEmployeeList = () => request.get('/employee/list')
export const addEmployee = (data) => request.post('/employee/add', data)
export const updateEmployee = (data) => request.put('/employee/update', data)
export const deleteEmployee = (id) => request.delete('/employee/delete/' + id)
export const countByDept = () => request.get('/employee/countByDept')

// ========== 打卡 ==========
export const getAttendanceList = () => request.get('/attendance/list')
export const clockIn = () => request.post('/attendance/clockIn')
export const clockOut = () => request.post('/attendance/clockOut')

// ========== 请假 ==========
export const getLeaveList = () => request.get('/leave/list')
export const applyLeave = (data) => request.post('/leave/apply', data)
export const approveLeave = (id, approved) =>
    request.post(`/leave/approve/${id}?approved=${approved}`)
export const deleteLeave = (id) => request.delete('/leave/delete/' + id)

// ========== 薪资 ==========
export const getSalaryList = (params) => request.get('/salary/list', {params})
export const calculateSalary = (params) => request.post('/salary/calculate', null, {params})
export const calculateAllSalary = (params) => request.post('/salary/calculateAll', null, {params})
