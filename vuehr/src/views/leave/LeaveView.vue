<template>
  <div>
    <!-- 提交请假申请 -->
    <div class="card" v-if="userStore.loginUser.employeeId">
      <h3>提交请假申请</h3>
      <el-form :model="leaveForm" inline>
        <el-form-item label="请假日期">
          <el-date-picker v-model="leaveForm.range" type="daterange"
                          range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
                          value-format="YYYY-MM-DD"/>
        </el-form-item>
        <el-form-item label="事由">
          <el-input v-model="leaveForm.reason" placeholder="请假事由" style="width:220px;"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doApplyLeave">提交申请</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 请假记录 -->
    <div class="card">
      <h3>
        请假记录
        <span style="font-size:12px;color:#909399;">
          （{{ userStore.canManage ? '全体 · 可批假' : '我的申请' }}）
        </span>
      </h3>
      <el-table :data="leaveList" border stripe>
        <el-table-column prop="empNo" label="工号" width="100"/>
        <el-table-column prop="empName" label="姓名" width="90"/>
        <el-table-column prop="startDate" label="开始" :formatter="dateFmt"/>
        <el-table-column prop="endDate" label="结束" :formatter="dateFmt"/>
        <el-table-column prop="days" label="天数" width="70"/>
        <el-table-column prop="reason" label="事由"/>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="leaveTagType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approver" label="审批人" width="90"/>
        <el-table-column label="操作" width="200" v-if="userStore.canManage">
          <template #default="{ row }">
            <template v-if="row.status === '待审批'">
              <el-button size="small" type="success" @click="doApprove(row.id, true)">批准</el-button>
              <el-button size="small" type="warning" @click="doApprove(row.id, false)">驳回</el-button>
            </template>
            <el-button size="small" type="danger" @click="doDeleteLeave(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useUserStore} from '../../store/user'
import {applyLeave, approveLeave, deleteLeave, getLeaveList} from '../../api'
import {dateFmt, leaveTagType} from '../../utils/format'

const userStore = useUserStore()
const leaveList = ref([])
const leaveForm = reactive({range: [], reason: ''})

const loadList = async () => {
  const res = await getLeaveList()
  leaveList.value = res.data || []
}

const doApplyLeave = async () => {
  if (!leaveForm.range || leaveForm.range.length !== 2) {
    ElMessage.warning('请选择请假日期');
    return
  }
  await applyLeave({
    startDate: leaveForm.range[0],
    endDate: leaveForm.range[1],
    reason: leaveForm.reason
  })
  ElMessage.success('请假申请已提交，等待审批')
  leaveForm.range = []
  leaveForm.reason = ''
  loadList()
}

const doApprove = async (id, approved) => {
  await approveLeave(id, approved)
  ElMessage.success(approved ? '已批准' : '已驳回')
  loadList()
}

const doDeleteLeave = async (id) => {
  await ElMessageBox.confirm('确定删除该请假记录吗？', '提示', {type: 'warning'})
  await deleteLeave(id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(loadList)
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
</style>
