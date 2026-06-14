import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import {checkLogin, login as loginApi, logout as logoutApi} from '../api'

export const useUserStore = defineStore('user', () => {
    const loginUser = ref({})

    const isLogin = computed(() => !!loginUser.value.id)
    const role = computed(() => loginUser.value.role || '')
    const canManage = computed(() => ['ADMIN', 'HR'].includes(role.value))
    const roleLabel = computed(() =>
        ({ADMIN: '超级管理员', HR: '人事部', EMPLOYEE: '普通员工'}[role.value] || '用户')
    )
    const roleTagType = computed(() =>
        ({ADMIN: 'danger', HR: 'warning', EMPLOYEE: 'info'}[role.value] || 'info')
    )

    async function login(username, password) {
        const res = await loginApi({username, password})
        loginUser.value = res.data
        return res
    }

    async function checkSession() {
        try {
            const res = await checkLogin()
            if (res.code === 200) {
                loginUser.value = res.data
                return true
            }
        } catch (e) {
            // 未登录
        }
        return false
    }

    async function logout() {
        try {
            await logoutApi()
        } finally {
            loginUser.value = {}
        }
    }

    return {
        loginUser, isLogin, role, canManage, roleLabel, roleTagType,
        login, checkSession, logout
    }
})
