import axios from 'axios'
import {ElMessage} from 'element-plus'
import router from '../router'

const request = axios.create({
    baseURL: '/api',
    withCredentials: true,
    timeout: 15000
})

// 响应拦截器：统一处理错误
request.interceptors.response.use(
    response => {
        const data = response.data
        if (data.code !== 200) {
            ElMessage.error(data.msg || '请求失败')
            return Promise.reject(data)
        }
        return data
    },
    error => {
        if (error.response?.status === 401) {
            ElMessage.error('登录已过期，请重新登录')
            router.push('/login')
        } else if (error.response?.status === 403) {
            ElMessage.error('无操作权限')
        } else {
            ElMessage.error(error.response?.data?.msg || error.message || '网络错误')
        }
        return Promise.reject(error)
    }
)

export default request
