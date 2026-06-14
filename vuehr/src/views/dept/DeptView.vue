<template>
  <div>
    <!-- 新增/编辑表单 -->
    <div class="card" v-if="userStore.canManage">
      <el-form :model="deptForm" inline>
        <el-form-item label="部门名称">
          <el-input v-model="deptForm.deptName" placeholder="部门名称"/>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="deptForm.manager" placeholder="负责人"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveDept">
            {{ deptForm.id ? '修改' : '新增' }}
          </el-button>
          <el-button @click="resetDeptForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 部门列表 -->
    <div class="card">
      <el-table :data="deptList" border stripe>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="deptName" label="部门名称"/>
        <el-table-column prop="manager" label="负责人"/>
        <el-table-column label="操作" width="160" v-if="userStore.canManage">
          <template #default="{ row }">
            <el-button size="small" @click="editDept(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteDept(row.id)">删除</el-button>
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
import {addDept, deleteDept as delDept, getDeptList, updateDept} from '../../api'

const userStore = useUserStore()
const deptList = ref([])
const deptForm = reactive({id: '', deptName: '', manager: ''})

const loadList = async () => {
  const res = await getDeptList()
  deptList.value = res.data || []
}

const saveDept = async () => {
  if (!deptForm.deptName) {
    ElMessage.warning('请输入部门名称');
    return
  }
  deptForm.id ? await updateDept(deptForm) : await addDept(deptForm)
  ElMessage.success('操作成功')
  resetDeptForm()
  loadList()
}

const editDept = (row) => Object.assign(deptForm, row)

const deleteDept = async (id) => {
  await ElMessageBox.confirm('确定删除该部门吗？', '提示', {type: 'warning'})
  await delDept(id)
  ElMessage.success('删除成功')
  loadList()
}

const resetDeptForm = () => Object.assign(deptForm, {id: '', deptName: '', manager: ''})

onMounted(loadList)
</script>

<style scoped>
.card {
  background: #fff;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 16px;
}
</style>
