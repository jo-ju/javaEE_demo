<template>
  <div>
    <!-- 打卡面板 -->
    <div class="card" v-if="userStore.loginUser.employeeId">
      <h3>每日打卡</h3>
      <div class="clock-panel">
        <span class="clock-time">{{ nowTime }}</span>
        <el-button type="primary" @click="doClockIn" :loading="clockLoading">上班打卡</el-button>
        <el-button type="success" @click="doClockOut" :loading="clockLoading">下班打卡</el-button>
        <span style="color:#909399;">迟到判定：上班打卡晚于 09:00</span>
      </div>
    </div>

    <!-- 打卡记录 -->
    <div class="card">
      <h3>
        打卡记录
        <span style="font-size:12px;color:#909399;">
          （{{ userStore.canManage ? '全体员工' : '我的记录' }}）
        </span>
      </h3>
      <el-table :data="attendanceList" border stripe>
        <el-table-column prop="empNo" label="工号" width="100"/>
        <el-table-column prop="empName" label="姓名" width="100"/>
        <el-table-column prop="clockDate" label="日期" :formatter="dateFmt"/>
        <el-table-column prop="clockIn" label="上班打卡" :formatter="timeFmt"/>
        <el-table-column prop="clockOut" label="下班打卡" :formatter="timeFmt"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'warning'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {useUserStore} from '../../store/user'
import {clockIn, clockOut, getAttendanceList} from '../../api'
import {dateFmt, timeFmt} from '../../utils/format'

const userStore = useUserStore()
const attendanceList = ref([])
const nowTime = ref('')
const clockLoading = ref(false)
let clockTimer = null

const updateClock = () => {
  nowTime.value = new Date().toLocaleString('zh-CN', {hour12: false})
}

const loadList = async () => {
  const res = await getAttendanceList()
  attendanceList.value = res.data || []
}

const doClockIn = async () => {
  if (clockLoading.value) return
  clockLoading.value = true
  try {
    const res = await clockIn()
    ElMessage.success(res.data || res.msg)
    loadList()
  } catch (err) {
    // 拦截器已处理
  } finally {
    clockLoading.value = false
  }
}

const doClockOut = async () => {
  if (clockLoading.value) return
  clockLoading.value = true
  try {
    const res = await clockOut()
    ElMessage.success(res.data || res.msg)
    loadList()
  } catch (err) {
    // 拦截器已处理
  } finally {
    clockLoading.value = false
  }
}

onMounted(() => {
  updateClock()
  clockTimer = setInterval(updateClock, 1000)
  loadList()
})

onUnmounted(() => clockTimer && clearInterval(clockTimer))
</script>

<style scoped>
.card {
  background: #fff;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.card h3 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
}

.clock-panel {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.clock-time {
  font-size: 26px;
  font-weight: bold;
  color: #409eff;
  font-variant-numeric: tabular-nums;
}
</style>
