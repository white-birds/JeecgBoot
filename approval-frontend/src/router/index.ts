import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes: RouteRecordRaw[] = [
  // 注意：此处已彻底删除 /login 路由，因为登录已由 Keycloak 托管
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
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

/**
 * 路由守卫
 * 由于已经在 main.ts 中通过 initKeycloak 拦截了未登录用户，
 * 这里的逻辑主要是为了防止用户手动清理 localStorage 后的异常处理。
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 检查是否需要认证 (除了主 Layout 下的路由，其他默认都需要)
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  if (requiresAuth && !token) {
    // ⚠️ 重点：如果 Token 丢失，直接刷新页面。
    // 页面刷新会触发 main.ts 重新执行 initKeycloak，从而自动跳转到 Keycloak 登录页。
    window.location.href = '/'
  } else {
    next()
  }
})

export default router