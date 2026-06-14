<template>
  <div>
    <div class="card" v-if="userStore.canManage">
      <el-form :model="positionForm" inline>
        <el-form-item label="职位名称">
          <el-input v-model="positionForm.posName" placeholder="职位名称"/>
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="positionForm.dept.id" placeholder="选择部门" style="width:150px;">
            <el-option v-for="d in deptList" :key="d.id" :label="d.deptName" :value="d.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="职位描述">
          <el-input v-model="positionForm.posDesc" placeholder="职位描述"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="savePosition">
            {{ positionForm.id ? '修改' : '新增' }}
          </el-button>
          <el-button @click="resetPositionForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card">
      <el-table :data="positionList" border stripe>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="posName" label="职位名称"/>
        <el-table-column prop="dept.deptName" label="所属部门"/>
        <el-table-column prop="posDesc" label="职位描述"/>
        <el-table-column label="操作" width="160" v-if="userStore.canManage">
          <template #default="{ row }">
            <el-button size="small" @click="editPosition(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePosition(row.id)">删除</el-button>
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
import {addPosition, deletePosition as delPos, getDeptList, getPositionList, updatePosition} from '../../api'

const userStore = useUserStore()
const positionList = ref([])
const deptList = ref([])
const positionForm = reactive({id: '', posName: '', posDesc: '', dept: {id: ''}})

const loadList = async () => {
  const [posRes, deptRes] = await Promise.all([getPositionList(), getDeptList()])
  positionList.value = posRes.data || []
  deptList.value = deptRes.data || []
}

const savePosition = async () => {
  if (!positionForm.posName) {
    ElMessage.warning('请输入职位名称');
    return
  }
  positionForm.id ? await updatePosition(positionForm) : await addPosition(positionForm)
  ElMessage.success('操作成功')
  resetPositionForm()
  loadList()
}

const editPosition = (row) => {
  positionForm.id = row.id
  positionForm.posName = row.posName
  positionForm.posDesc = row.posDesc
  positionForm.dept = {id: row.dept?.id || ''}
}

const deletePosition = async (id) => {
  await ElMessageBox.confirm('确定删除该职位吗？', '提示', {type: 'warning'})
  await delPos(id)
  ElMessage.success('删除成功')
  loadList()
}

const resetPositionForm = () =>
    Object.assign(positionForm, {id: '', posName: '', posDesc: '', dept: {id: ''}})

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
