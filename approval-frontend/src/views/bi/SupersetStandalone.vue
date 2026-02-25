<template>
  <div class="superset-standalone-page">
    <a-spin :spinning="loading" tip="正在加载 Superset 数据分析平台...">
      <div ref="mountPoint" class="superset-mount" />
      <a-empty v-if="!loading && !mountedOk" description="无法连接到数据分析平台" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { embedDashboard } from '@superset-ui/embedded-sdk'
import { http } from '@/utils/http'

const loading = ref(true)
const mountedOk = ref(false)
const supersetBaseUrl = ref('http://localhost:8088')
const mountPoint = ref<HTMLElement | null>(null)
const route = useRoute()

const normalizeSupersetDomain = (url: string) => {
  const trimmed = (url || '').trim()
  return trimmed.endsWith('/') ? trimmed.slice(0, -1) : trimmed
}

const loadSuperset = async () => {
  loading.value = true
  mountedOk.value = false

  const embeddedUuid = route.query.uuid as string | undefined
  if (!embeddedUuid) {
    message.error('缺少仪表板ID')
    loading.value = false
    return
  }

  try {
    const config = await http.get('/superset/config')
    if (config && (config as any).supersetUrl) {
      supersetBaseUrl.value = (config as any).supersetUrl
    }

    await nextTick()

    if (!mountPoint.value) {
      throw new Error('mountPoint 不存在')
    }

    await embedDashboard({
      id: embeddedUuid,
      supersetDomain: normalizeSupersetDomain(supersetBaseUrl.value),
      mountPoint: mountPoint.value,
      fetchGuestToken: async () => {
        const token = await http.get('/superset/system-token', {
          params: { embeddedUuid },
        })
        return token as any
      },
      dashboardUiConfig: {
        hideTitle: true,
        hideTab: true,
        hideChartControls: true,
        filters: {
          visible: false,
          expanded: false,
        },
      },
      iframeSandboxExtras: ['allow-forms', 'allow-popups', 'allow-same-origin', 'allow-scripts'],
      referrerPolicy: 'strict-origin-when-cross-origin',
    })

    mountedOk.value = true
  } catch (error: any) {
    console.error('加载 Superset 失败:', error)
    message.error('加载失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSuperset()
})
</script>

<style scoped>
.superset-standalone-page {
  width: 100vw;
  height: 100vh;
  background: #fff;
}

.superset-mount {
  width: 100%;
  height: 100%;
}

:deep(.ant-spin-nested-loading),
:deep(.ant-spin-container) {
  height: 100%;
}
</style>



