<template>
  <div>
    <!-- 工资计算面板（仅管理员） -->
    <div class="card" v-if="userStore.canManage">
      <h3>月工资计算</h3>
      <el-form inline>
        <el-form-item label="工资月份">
          <el-date-picker v-model="salaryMonth" type="month" value-format="YYYY-MM"
                          placeholder="选择月份"/>
        </el-form-item>
        <el-form-item label="绩效(元)">
          <el-input v-model="salaryPerformance" placeholder="统一绩效，可空" style="width:130px;"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doCalculateAll">一键计算全员工资</el-button>
        </el-form-item>
      </el-form>
      <div style="color:#909399;font-size:13px;">
        规则：实发 = 底薪 + 满勤奖(无缺勤无请假 +200) + 绩效 - 缺勤扣款；缺勤扣款 = 底薪/应出勤工作日 × 缺勤天数。
      </div>
    </div>

    <!-- 工资明细 -->
    <div class="card">
      <h3>工资明细</h3>
      <el-table :data="salaryList" border stripe size="small">
        <el-table-column prop="empNo" label="工号" width="90"/>
        <el-table-column prop="empName" label="姓名" width="80"/>
        <el-table-column prop="salaryMonth" label="月份" width="90"/>
        <el-table-column prop="baseSalary" label="底薪"/>
        <el-table-column prop="requiredDays" label="应出勤" width="80"/>
        <el-table-column prop="attendDays" label="实出勤" width="80"/>
        <el-table-column prop="leaveDays" label="请假" width="70"/>
        <el-table-column prop="absentDays" label="缺勤" width="70"/>
        <el-table-column prop="fullAttendanceBonus" label="满勤奖" width="80"/>
        <el-table-column prop="performance" label="绩效" width="80"/>
        <el-table-column prop="deduction" label="扣款" width="80"/>
        <el-table-column prop="totalSalary" label="实发工资" width="100">
          <template #default="{ row }">
            <strong style="color:#e6534d;">{{ row.totalSalary }}</strong>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {useUserStore} from '../../store/user'
import {calculateAllSalary, getSalaryList} from '../../api'
import {currentMonth} from '../../utils/format'

const userStore = useUserStore()
const salaryList = ref([])
const salaryMonth = ref(currentMonth())
const salaryPerformance = ref('')

const loadList = async () => {
  const res = await getSalaryList()
  salaryList.value = res.data || []
}

const doCalculateAll = async () => {
  if (!salaryMonth.value) {
    ElMessage.warning('请选择工资月份');
    return
  }
  const params = {month: salaryMonth.value}
  if (salaryPerformance.value !== '') params.performance = salaryPerformance.value
  const res = await calculateAllSalary(params)
  ElMessage.success(res.msg)
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
