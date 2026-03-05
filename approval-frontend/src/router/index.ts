import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

// 假设你刚才写的登录页放在 views/Login.vue
// 如果路径不对，请修改这里
import Login from '@/views/Login.vue'

const routes: RouteRecordRaw[] = [
  // ✅ 新增：恢复登录页路由
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', requiresAuth: false }
  },
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
      // ... 你的其他业务路由保持不变 ...
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
      // ✅ Superset 路由
      {
        path: 'bi-superset',
        name: 'BISuperset',
        component: () => import('@/views/bi/superset.vue'), // 确保这里指向你刚才改的 index.vue
        meta: { title: '数据分析', icon: 'LineChartOutlined' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// ✅ 重写：基于 systemToken 的原生路由守卫
router.beforeEach((to, from, next) => {
  // 1. 获取我们自己存的 token (注意 key 要和 Login.vue 里一致)
  const token = localStorage.getItem('systemToken')

  // 2. 如果去的是登录页
  if (to.path === '/login') {
    if (token) {
      // 已经登录了，就踢回首页
      return next('/')
    }
    // 没登录，允许访问登录页
    return next()
  }

  // 3. 如果去的是其他页面（需要认证）
  if (!token) {
    // 没 Token，强制跳转到我们自己的登录页
    return next('/login')
  }

  // 4. 有 Token，放行
  next()
})

export default router