<template>
  <div class="card">
    <h3>各部门员工数量统计</h3>
    <el-table :data="statList" border stripe>
      <el-table-column prop="dept_name" label="部门名称"/>
      <el-table-column prop="emp_count" label="员工数量"/>
    </el-table>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {countByDept} from '../../api'

const statList = ref([])

onMounted(async () => {
  const res = await countByDept()
  statList.value = res.data || []
})
</script>

<style scoped>
.card {
  background: #fff;
  padding: 20px;
  border-radius: 6px;
}

.card h3 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
}
</style>
