import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from '../store/user'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/LoginView.vue')
    },
    {
        path: '/',
        component: () => import('../layouts/MainLayout.vue'),
        meta: {requiresAuth: true},
        redirect: '/attendance',
        children: [
            {
                path: 'dept',
                name: 'Dept',
                component: () => import('../views/dept/DeptView.vue'),
                meta: {roles: ['ADMIN', 'HR']}
            },
            {
                path: 'position',
                name: 'Position',
                component: () => import('../views/position/PositionView.vue'),
                meta: {roles: ['ADMIN', 'HR']}
            },
            {
                path: 'employee',
                name: 'Employee',
                component: () => import('../views/employee/EmployeeView.vue'),
                meta: {roles: ['ADMIN', 'HR']}
            },
            {
                path: 'attendance',
                name: 'Attendance',
                component: () => import('../views/attendance/AttendanceView.vue')
            },
            {
                path: 'leave',
                name: 'Leave',
                component: () => import('../views/leave/LeaveView.vue')
            },
            {
                path: 'salary',
                name: 'Salary',
                component: () => import('../views/salary/SalaryView.vue')
            },
            {
                path: 'statistics',
                name: 'Statistics',
                component: () => import('../views/statistics/StatisticsView.vue'),
                meta: {roles: ['ADMIN', 'HR']}
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    if (to.meta.requiresAuth && !userStore.isLogin) {
        return next('/login')
    }
    if (to.meta.roles && !to.meta.roles.includes(userStore.role)) {
        return next('/')
    }
    next()
})

export default router
