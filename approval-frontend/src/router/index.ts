import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'DashboardOutlined' },
      },
      {
        path: 'initiate',
        name: 'InitiateApproval',
        component: () => import('@/views/approval/InitiateApproval.vue'),
        meta: { title: '发起审批', icon: 'PlusCircleOutlined' },
      },
      {
        path: 'my-applications',
        name: 'MyApplications',
        component: () => import('@/views/approval/MyApplicationList.vue'),
        meta: { title: '我的申请', icon: 'FileTextOutlined' },
      },
      {
        path: 'todo',
        name: 'TodoApproval',
        component: () => import('@/views/approval/TodoApprovalList.vue'),
        meta: { title: '待我审批', icon: 'CheckSquareOutlined' },
      },
      {
        path: 'history',
        name: 'HistoryApproval',
        component: () => import('@/views/approval/HistoryApprovalList.vue'),
        meta: { title: '审批历史', icon: 'HistoryOutlined' },
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '统计报表', icon: 'BarChartOutlined' },
      },
      {
        path: 'templates',
        name: 'Templates',
        component: () => import('@/views/templates/index.vue'),
        meta: { title: '审批模板', icon: 'FormOutlined' },
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/notifications/index.vue'),
        meta: { title: '通知中心', icon: 'BellOutlined' },
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/index.vue'),
        meta: { title: '系统设置', icon: 'SettingOutlined' },
      },
      {
        path: 'bi-superset',
        name: 'BISuperset',
        component: () => import('@/views/bi/superset.vue'),
        meta: { title: '数据分析', icon: 'LineChartOutlined' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 如果访问登录页
  if (to.path === '/login') {
    // 已登录则跳转到首页
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  
  // 如果需要认证
  if (to.matched.some(record => record.meta.requiresAuth !== false)) {
    if (!token) {
      // 未登录则跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router

