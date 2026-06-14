<template>
  <div class="login-wrapper">
    <div class="login-box">
      <h2>人力资源管理系统</h2>
      <p class="subtitle">HRMS · 员工 OA 平台</p>
      <el-form :model="loginForm" @submit.prevent>
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="用户名" size="large"
                    prefix-icon="User"/>
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" type="password" placeholder="密码"
                    size="large" prefix-icon="Lock" show-password
                    @keyup.enter="handleLogin"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleLogin"
                     style="width:100%;" :loading="loading">登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-tip">
        管理员：admin / 123456（全部权限）<br/>
        人事部：hr / 123456（最高管理权限）<br/>
        普通员工：ming / 123456、hong / 123456（OA）
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {useUserStore} from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const loginForm = reactive({username: '', password: ''})
const loading = ref(false)

onMounted(async () => {
  // 如已有 session，直接跳转
  const ok = await userStore.checkSession()
  if (ok) {
    const path = userStore.canManage ? '/employee' : '/attendance'
    router.push(path)
  }
})

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(loginForm.username, loginForm.password)
    ElMessage.success('登录成功')
    const path = userStore.canManage ? '/employee' : '/attendance'
    router.push(path)
  } catch (err) {
    // 错误消息已由 request 拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f3a5f 0%, #2c5282 100%);
}

.login-box {
  width: 380px;
  padding: 36px 30px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 6px;
  color: #1f3a5f;
}

.subtitle {
  text-align: center;
  color: #999;
  font-size: 13px;
  margin-bottom: 24px;
}

.login-tip {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
  line-height: 1.8;
}
</style>
