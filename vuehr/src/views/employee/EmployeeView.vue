<template>
  <div>
    <div class="card" v-if="userStore.canManage">
      <el-form :model="empForm" inline>
        <el-form-item label="工号">
          <el-input v-model="empForm.empId" placeholder="工号" style="width:120px;"/>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="empForm.empName" placeholder="姓名" style="width:120px;"/>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="empForm.dept.id" placeholder="部门" style="width:130px;"
                     @change="onDeptChange">
            <el-option v-for="d in deptList" :key="d.id" :label="d.deptName" :value="d.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="empForm.position.id" placeholder="职位" style="width:130px;">
            <el-option v-for="p in filteredPositions" :key="p.id" :label="p.posName"
                       :value="p.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="底薪">
          <el-input v-model="empForm.baseSalary" placeholder="底薪" style="width:110px;"/>
        </el-form-item>
        <el-form-item label="入职日期">
          <el-date-picker v-model="empForm.entryDate" type="date" value-format="YYYY-MM-DD"
                          placeholder="入职日期" style="width:150px;"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEmp">
            {{ empForm.id ? '修改' : '新增' }}
          </el-button>
          <el-button @click="resetEmpForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card">
      <el-table :data="empList" border stripe>
        <el-table-column prop="empId" label="工号" width="100"/>
        <el-table-column prop="empName" label="姓名" width="100"/>
        <el-table-column prop="dept.deptName" label="部门"/>
        <el-table-column prop="position.posName" label="职位"/>
        <el-table-column prop="baseSalary" label="底薪"/>
        <el-table-column prop="entryDate" label="入职日期" :formatter="dateFmt"/>
        <el-table-column label="操作" width="160" v-if="userStore.canManage">
          <template #default="{ row }">
            <el-button size="small" @click="editEmp(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteEmp(row.id)">删除</el-button>
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
import {
  addEmployee,
  deleteEmployee as delEmp,
  getDeptList,
  getEmployeeList,
  getPositionList,
  updateEmployee
} from '../../api'
import {dateFmt} from '../../utils/format'

const userStore = useUserStore()
const empList = ref([])
const deptList = ref([])
const positionList = ref([])
const filteredPositions = ref([])

const empForm = reactive({
  id: '', empId: '', empName: '', baseSalary: '',
  dept: {id: ''}, position: {id: ''}, entryDate: ''
})

const loadList = async () => {
  const res = await getEmployeeList()
  empList.value = res.data || []
}

const loadMeta = async () => {
  const [deptRes, posRes] = await Promise.all([getDeptList(), getPositionList()])
  deptList.value = deptRes.data || []
  positionList.value = posRes.data || []
  filteredPositions.value = positionList.value
}

const onDeptChange = () => {
  empForm.position.id = ''
  filteredPositions.value = empForm.dept.id
      ? positionList.value.filter(p => p.dept && p.dept.id === empForm.dept.id)
      : positionList.value
}

const saveEmp = async () => {
  if (!empForm.empId || !empForm.empName) {
    ElMessage.warning('请输入工号和姓名');
    return
  }
  empForm.id ? await updateEmployee(empForm) : await addEmployee(empForm)
  ElMessage.success('操作成功')
  resetEmpForm()
  loadList()
}

const editEmp = (row) => {
  empForm.id = row.id
  empForm.empId = row.empId
  empForm.empName = row.empName
  empForm.baseSalary = row.baseSalary
  empForm.entryDate = row.entryDate ? String(row.entryDate).substring(0, 10) : ''
  empForm.dept = {id: row.dept?.id || ''}
  empForm.position = {id: row.position?.id || ''}
  // 先过滤职位列表，再设置选中值
  filteredPositions.value = empForm.dept.id
      ? positionList.value.filter(p => p.dept && p.dept.id === empForm.dept.id)
      : positionList.value
}

const deleteEmp = async (id) => {
  await ElMessageBox.confirm('确定删除该员工吗？', '提示', {type: 'warning'})
  await delEmp(id)
  ElMessage.success('删除成功')
  loadList()
}

const resetEmpForm = () => {
  Object.assign(empForm, {
    id: '', empId: '', empName: '', baseSalary: '',
    dept: {id: ''}, position: {id: ''}, entryDate: ''
  })
  filteredPositions.value = positionList.value
}

onMounted(() => {
  loadList();
  if (userStore.canManage) loadMeta()
})
</script>

<style scoped>
.card {
  background: #fff;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 16px;
}
</style>
