<template>
  <div ref="pageRef" class="superset-page" :class="{ 'is-fullscreen': isFullscreen }">
    <a-spin :spinning="loading" tip="正在加载数据中，请耐心等待...">
      <div class="superset-header">
        <a-space>
          <LineChartOutlined style="font-size: 24px; color: #1890ff" />
          <span class="title">数据分析平台</span>
        </a-space>

        <a-space>
          <a-segmented v-model:value="viewMode" :options="viewModeOptions" @change="handleViewModeChange" />
          
          <a-select
            v-if="viewMode === 'dashboard'"
            v-model:value="selectedDashboard"
            style="width: 320px"
            placeholder="选择仪表板"
            @change="handleDashboardChange"
          >
            <a-select-option v-for="d in dashboards" :key="d.embeddedUuid" :value="d.embeddedUuid">
              {{ d.name }}
            </a-select-option>
          </a-select>

          <a-button type="primary" ghost @click="toggleFullScreen">
            <template #icon>
              <CompressOutlined v-if="isFullscreen" />
              <ExpandOutlined v-else />
            </template>
            {{ isFullscreen ? '退出全屏' : '全屏显示' }}
          </a-button>
          
          <a-button @click="reloadSuperset">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
        </a-space>
      </div>

      <div class="superset-content">
        <!-- 完整系统模式：直接用 iframe -->
        <iframe
          v-if="viewMode === 'full'"
          ref="fullIframe"
          :src="supersetFullUrl"
          class="superset-iframe"
          frameborder="0"
          @load="handleIframeLoad"
        />
        
        <!-- Dashboard 模式：使用 embedded SDK -->
        <div v-else>
          <div ref="mountPoint" class="superset-mount" />
          <a-empty v-if="!loading && !mountedOk" description="无法连接到数据分析平台" />
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  LineChartOutlined, 
  ReloadOutlined, 
  ExpandOutlined, 
  CompressOutlined 
} from '@ant-design/icons-vue'
import { embedDashboard } from '@superset-ui/embedded-sdk'
import { http } from '@/utils/http'

// --- 状态定义 ---
const loading = ref(true)
const mountedOk = ref(false)
const isFullscreen = ref(false)
const supersetBaseUrl = ref('http://localhost:8088')
const dashboards = ref<Array<{ name: string; embeddedUuid: string }>>([])
const selectedDashboard = ref<string>('')
const viewMode = ref<'full' | 'dashboard'>('dashboard') // 默认仪表板模式

const pageRef = ref<HTMLElement | null>(null)
const mountPoint = ref<HTMLElement | null>(null)
const fullIframe = ref<HTMLIFrameElement | null>(null)

const route = useRoute()
const router = useRouter()

// 视图模式选项
const viewModeOptions = [
  { label: '完整系统', value: 'full' },
  { label: '仪表板', value: 'dashboard' },
]

// 完整系统的 URL
// const supersetFullUrl = ref('http://localhost:8088/superset/welcome/')
const supersetFullUrl = ref('http://localhost:8088/login/keycloak?next=/superset/welcome/')
// --- 核心功能：原生全屏切换 ---
const toggleFullScreen = () => {
  const el = pageRef.value as any

  if (!isFullscreen.value) {
    if (el.requestFullscreen) {
      el.requestFullscreen()
    } else if (el.webkitRequestFullscreen) {
      el.webkitRequestFullscreen()
    } else if (el.msRequestFullscreen) {
      el.msRequestFullscreen()
    }
  } else {
    const doc = document as any
    if (doc.exitFullscreen) {
      doc.exitFullscreen()
    } else if (doc.webkitExitFullscreen) {
      doc.webkitExitFullscreen()
    } else if (doc.msExitFullscreen) {
      doc.msExitFullscreen()
    }
  }
}

// --- 关键监听：处理用户按 ESC 键的情况 ---
const checkFullscreenState = () => {
  const doc = document as any
  const isFull = !!(doc.fullscreenElement || doc.webkitFullscreenElement || doc.msFullscreenElement)
  
  isFullscreen.value = isFull

  if (isFull) {
    router.replace({ query: { ...route.query, fullscreen: '1' } })
  } else {
    const query = { ...route.query }
    delete query.fullscreen
    router.replace({ query })
  }
}

// --- 基础 Superset 逻辑 ---
const clearMountPoint = () => {
  if (mountPoint.value) mountPoint.value.innerHTML = ''
}

const normalizeSupersetDomain = (url: string) => {
  const trimmed = (url || '').trim()
  return trimmed.endsWith('/') ? trimmed.slice(0, -1) : trimmed
}

const loadSuperset = async () => {
  if (!selectedDashboard.value) return 
  loading.value = true
  mountedOk.value = false

  try {
    const config = await http.get('/superset/config')
    if (config && (config as any).supersetUrl) {
      supersetBaseUrl.value = (config as any).supersetUrl
    }
    const embeddedUuid = selectedDashboard.value
    await nextTick()
    clearMountPoint()
    if (!mountPoint.value) throw new Error('mountPoint 不存在')

    await embedDashboard({
      id: embeddedUuid,
      supersetDomain: normalizeSupersetDomain(supersetBaseUrl.value),
      mountPoint: mountPoint.value,
      fetchGuestToken: async () => {
        const token = await http.get('/superset/system-token', { params: { embeddedUuid } })
        return token as any
      },
      dashboardUiConfig: {
        hideTitle: false,
        hideTab: false,
        hideChartControls: false,
        filters: { visible: true, expanded: false },
      },
      iframeSandboxExtras: ['allow-forms', 'allow-popups', 'allow-same-origin', 'allow-scripts'],
      // 添加语言配置
      locale: 'zh',
    })
    mountedOk.value = true
  } catch (error: any) {
    console.error(error)
    message.error('加载失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const handleDashboardChange = () => loadSuperset()

const handleViewModeChange = (value: string | number) => {
  if (value === 'full') {
    loading.value = false
    mountedOk.value = false
  } else if (value === 'dashboard') {
    if (selectedDashboard.value) {
      loadSuperset()
    }
  }
}

const handleIframeLoad = () => {
  loading.value = false
  console.log('Superset 完整系统加载完成')
}

const reloadSuperset = () => {
  if (viewMode.value === 'full') {
    if (fullIframe.value) {
      loading.value = true
      fullIframe.value.src = fullIframe.value.src
    }
  } else {
    loadSuperset()
  }
}

// --- 生命周期 ---
onMounted(async () => {
  document.addEventListener('fullscreenchange', checkFullscreenState)
  document.addEventListener('webkitfullscreenchange', checkFullscreenState)
  document.addEventListener('mozfullscreenchange', checkFullscreenState)
  document.addEventListener('MSFullscreenChange', checkFullscreenState)

  try {
    const config = await http.get('/superset/config')
    if (config && (config as any).supersetUrl) {
      supersetBaseUrl.value = (config as any).supersetUrl
      supersetFullUrl.value = `${supersetBaseUrl.value}/superset/welcome/`
    }

    const res = await http.get('/superset/dashboards')
    let list = []
    if (Array.isArray(res)) list = res
    else if (res && res.result && Array.isArray(res.result)) list = res.result

    if (list.length > 0) {
      dashboards.value = list
      const uuidFromQuery = route.query.uuid as string | undefined
      const exist = dashboards.value.some(d => d.embeddedUuid === uuidFromQuery)
      selectedDashboard.value = (uuidFromQuery && exist) ? uuidFromQuery : dashboards.value[0].embeddedUuid
      
      // 默认加载第一个 Dashboard
      loadSuperset()
    } else {
      loading.value = false
    }
  } catch (err) {
    loading.value = false
    message.error('初始化失败')
  }
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', checkFullscreenState)
  document.removeEventListener('webkitfullscreenchange', checkFullscreenState)
  document.removeEventListener('mozfullscreenchange', checkFullscreenState)
  document.removeEventListener('MSFullscreenChange', checkFullscreenState)
})
</script>

<style scoped>
.superset-page {
  height: calc(100vh - 120px); 
  background: #fff;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease-in-out;
}

.superset-page.is-fullscreen {
  background: #fff;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

:deep(.ant-spin-nested-loading), :deep(.ant-spin-container) {
  width: 100%; height: 100%;
}
:deep(.ant-spin-container) {
  display: flex; flex-direction: column;
}
.superset-header {
  flex-shrink: 0; display: flex; justify-content: space-between; align-items: center;
  padding: 16px 24px; border-bottom: 1px solid #f0f0f0;
}
.title { font-size: 18px; font-weight: 500; color: #333; }
.superset-content { flex: 1; position: relative; width: 100%; overflow: hidden; }
.superset-mount { position: absolute; top: 0; left: 0; right: 0; bottom: 0; width: 100%; height: 100%; }
:deep(.superset-mount > iframe) { display: block; width: 100% !important; height: 100% !important; border: 0; }
.superset-iframe { position: absolute; top: 0; left: 0; right: 0; bottom: 0; width: 100%; height: 100%; border: 0; }
</style>
