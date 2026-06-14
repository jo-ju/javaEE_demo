<template>
  <div class="main-container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="logo">HRMS</div>
      <template v-for="m in visibleMenus" :key="m.key || m.group">
        <div class="group-title" v-if="m.group">{{ m.group }}</div>
        <div
            v-else
            class="menu-item"
            :class="{ active: currentKey === m.key }"
            @click="navigate(m.key)"
        >
          {{ m.label }}
        </div>
      </template>
    </div>

    <!-- 内容区域 -->
    <div class="content-wrapper">
      <div class="header">
        <span class="title">{{ menuTitle }}</span>
        <div class="user-info">
          <span>{{ userStore.loginUser.realName || userStore.loginUser.username }}</span>
          <el-tag class="role-tag" :type="userStore.roleTagType" size="small">
            {{ userStore.roleLabel }}
          </el-tag>
          <el-button size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </div>

      <div class="main-content">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useUserStore} from '../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const allMenus = [
  {group: '基础管理'},
  {key: 'dept', label: '部门管理', manageOnly: true},
  {key: 'position', label: '职位管理', manageOnly: true},
  {key: 'employee', label: '员工管理', manageOnly: true},
  {group: '员工 OA'},
  {key: 'attendance', label: '我的打卡'},
  {key: 'leave', label: '请假/批假'},
  {key: 'salary', label: '工资管理'},
  {group: '统计报表'},
  {key: 'statistics', label: '信息统计', manageOnly: true}
]

const menuTitles = {
  dept: '部门管理', position: '职位管理', employee: '员工管理',
  attendance: '我的打卡', leave: '请假/批假', salary: '工资管理', statistics: '信息统计'
}

const visibleMenus = computed(() =>
    allMenus.filter(m => m.group || !m.manageOnly || userStore.canManage)
)

const currentKey = computed(() => route.name?.toLowerCase() || 'attendance')
const menuTitle = computed(() => menuTitles[currentKey.value] || 'HRMS')

function navigate(key) {
  // 映射 key → route name
  const nameMap = {emp: 'Employee', stat: 'Statistics'}
  const name = nameMap[key] || key.charAt(0).toUpperCase() + key.slice(1)
  router.push({name})
}

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: "Helvetica Neue", Arial, sans-serif;
}

.main-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 210px;
  background: #304156;
  color: #bfcbd9;
  overflow-y: auto;
  flex-shrink: 0;
}

.sidebar .logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #2b3648;
  letter-spacing: 2px;
}

.sidebar .menu-item {
  padding: 14px 24px;
  cursor: pointer;
  transition: all .2s;
  font-size: 14px;
}

.sidebar .menu-item:hover {
  background: #263445;
  color: #fff;
}

.sidebar .menu-item.active {
  background: #409eff;
  color: #fff;
}

.sidebar .group-title {
  padding: 10px 24px 4px;
  font-size: 12px;
  color: #8492a6;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  overflow: hidden;
}

.header {
  height: 56px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.header .title {
  font-size: 18px;
  color: #303133;
}

.header .user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #606266;
}

.role-tag {
  font-size: 12px;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
