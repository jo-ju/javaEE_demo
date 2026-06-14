/** 日期格式化：取前10位 yyyy-MM-dd */
export function dateFmt(row, col, val) {
    if (!val) return ''
    return String(val).substring(0, 10)
}

/** 时间格式化：提取 HH:mm:ss */
export function timeFmt(row, col, val) {
    if (!val) return ''
    return String(val).replace('T', ' ').substring(11, 19)
}

/** 请假状态 → Element Plus Tag 类型 */
export function leaveTagType(status) {
    return {'待审批': 'info', '已批准': 'success', '已驳回': 'danger'}[status] || 'info'
}

/** 获取当前月份字符串 yyyy-MM */
export function currentMonth() {
    const now = new Date()
    return now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0')
}
