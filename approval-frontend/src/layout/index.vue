<template>
  <a-layout class="layout-container">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      class="layout-sider"
      :width="240"
    >
      <div class="logo">
        <!-- 收起状态：只显示图标 -->
        <div class="logo-icon" v-if="collapsed">
          <CheckOutlined />
        </div>
        <!-- 展开状态：显示图片或文字 -->
        <template v-else>
          <img 
            v-if="!logoError"
            src="/logo.png" 
            alt="AIHOM 审批系统" 
            class="logo-image"
            @error="handleLogoError"
          />
          <div v-else class="logo-text-fallback">
            <CheckCircleOutlined class="logo-icon-inline" />
            <span>AIHOM 审批</span>
          </div>
        </template>
      </div>
      
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
      >
        <a-menu-item v-for="route in menuRoutes" :key="'/' + route.path">
          <component :is="route.iconComponent" v-if="route.iconComponent" />
          <span>{{ route.meta?.title }}</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <!-- 主内容区 -->
    <a-layout>
      <!-- 顶部栏 -->
      <a-layout-header class="layout-header">
        <div class="header-left">
          <MenuUnfoldOutlined
            v-if="collapsed"
            class="trigger"
            @click="collapsed = !collapsed"
          />
          <MenuFoldOutlined
            v-else
            class="trigger"
            @click="collapsed = !collapsed"
          />
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item>
              <a @click="router.push('/dashboard')">首页</a>
            </a-breadcrumb-item>
            <a-breadcrumb-item v-if="route.path !== '/dashboard'">
              {{ currentTitle }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        
        <div class="header-right">
          <a-badge :count="5" class="header-icon">
            <BellOutlined style="font-size: 18px" />
          </a-badge>
          
          <a-dropdown>
            <div class="user-info">
              <a-avatar :size="32" style="background-color: #1890ff">
                <template #icon><UserOutlined /></template>
              </a-avatar>
              <span class="username">{{ userInfo?.realname || 'Admin' }}</span>
            </div>
            <template #overlay>
              <a-menu @click="handleUserMenuClick">
                <a-menu-item key="profile">
                  <UserOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-item key="settings">
                  <SettingOutlined />
                  系统设置
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 内容区 -->
      <a-layout-content class="layout-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Modal, message } from 'ant-design-vue'
import { http } from '@/utils/http'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  BellOutlined,
  SettingOutlined,
  LogoutOutlined,
  CheckCircleOutlined,
  CheckOutlined,
  DashboardOutlined,
  PlusCircleOutlined,
  FileTextOutlined,
  CheckSquareOutlined,
  HistoryOutlined,
  BarChartOutlined,
  FormOutlined,

  LineChartOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const collapsed = ref(false)
const selectedKeys = ref([route.path])
const logoError = ref(false)
const userInfo = ref<any>(null)

const handleLogoError = () => {
  logoError.value = true
}

// 加载用户信息
onMounted(() => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
})

// 处理用户菜单点击
const handleUserMenuClick = ({ key }: { key: string }) => {
  if (key === 'logout') {
    Modal.confirm({
      title: '确认退出',
      content: '确定要退出登录吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          // 1. 调用后端接口清理后端 session
          await http.post('/sys/logout')
        } catch (error) {
          console.error('退出登录失败:', error)
        } finally {
          // 2.【修改点】清理我们自己的 Token 和 用户信息
          localStorage.removeItem('systemToken')
          localStorage.removeItem('userInfo')

          // 3.【修改点】直接跳转回登录页，不要再调 keycloak 了
          router.push('/login')
        }
      }
    })
  } else if (key === 'profile') {
    message.info('个人中心功能开发中')
  } else if (key === 'settings') {
    message.info('系统设置功能开发中')
  }
}

// 图标映射
const iconMap: Record<string, any> = {
  DashboardOutlined,
  PlusCircleOutlined,
  FileTextOutlined,
  CheckSquareOutlined,
  HistoryOutlined,
  BarChartOutlined,
  FormOutlined,
  BellOutlined,
  LineChartOutlined,
  SettingOutlined,
}

// 从路由配置中获取菜单
const menuRoutes = computed(() => {
  const routes = router.options.routes
  const layoutRoute = routes.find(r => r.path === '/')
  if (!layoutRoute || !layoutRoute.children) return []
  
  return layoutRoute.children.map(route => ({
    ...route,
    iconComponent: iconMap[route.meta?.icon as string]
  }))
})

const currentTitle = computed(() => {
  const currentRoute = menuRoutes.value.find(r => '/' + r.path === route.path)
  return currentRoute?.meta?.title || '工作台'
})

const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key)
  selectedKeys.value = [key]
}

watch(() => route.path, (newPath) => {
  selectedKeys.value = [newPath]
})
</script>

<style lang="scss" scoped>
.layout-container {
  min-height: 100vh;
}

.layout-sider {
  background: #001529;
  
  .logo {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    color: white;
    font-size: 20px;
    font-weight: bold;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding: 0 16px;
    
    .logo-icon {
      font-size: 28px;
      color: #1890ff;
    }
    
    .logo-image {
      max-height: 48px;
      max-width: 100%;
      object-fit: contain;
    }
    
    .logo-text-fallback {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .logo-icon-inline {
        font-size: 24px;
        color: #1890ff;
      }
      
      span {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
  }
}

.layout-header {
  background: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .trigger {
      font-size: 18px;
      cursor: pointer;
      transition: color 0.3s;
      
      &:hover {
        color: #1890ff;
      }
    }
    
    .breadcrumb {
      font-size: 14px;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .header-icon {
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        color: #1890ff;
      }
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      
      .username {
        font-size: 14px;
        color: #333;
      }
    }
  }
}

.layout-content {
  margin: 24px;
  background: transparent;
  min-height: calc(100vh - 112px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>

