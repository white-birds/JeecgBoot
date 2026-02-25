<template>
  <div class="my-application-container">
    <a-card :bordered="false">
      <template #title>
        <div class="card-title">
          <FileTextOutlined />
          <span>我的申请</span>
        </div>
      </template>
      
      <template #extra>
        <a-space>
          <a-select
            v-model:value="statusFilter"
            style="width: 120px"
            @change="handleSearch"
          >
            <a-select-option value="">全部状态</a-select-option>
            <a-select-option value="待审批">待审批</a-select-option>
            <a-select-option value="已通过">已通过</a-select-option>
            <a-select-option value="已驳回">已驳回</a-select-option>
          </a-select>
          
          <a-input-search
            v-model:value="searchText"
            placeholder="搜索标题"
            style="width: 200px"
            @search="handleSearch"
          />
          
          <a-button @click="handleRefresh">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
          
          <a-button type="primary" @click="$router.push('/initiate')">
            <template #icon><PlusOutlined /></template>
            新建申请
          </a-button>
        </a-space>
      </template>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        :row-key="record => record.id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <a-tag :color="getTypeColor(record.type)">
              {{ record.type }}
            </a-tag>
          </template>
          
          <template v-if="column.key === 'status'">
            <a-badge :status="getStatusBadge(record.status)" :text="record.status" />
          </template>
          
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleView(record)">
                查看详情
              </a-button>
              <a-button
                v-if="record.status === '待审批'"
                type="link"
                danger
                size="small"
                @click="handleDelete(record)"
              >
                撤回
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { FileTextOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { getApprovalList, deleteApproval } from '@/api/approval'
import type { ApprovalItem } from '@/api/approval'

const router = useRouter()
const loading = ref(false)
const searchText = ref('')
const statusFilter = ref('')
const dataSource = ref<ApprovalItem[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

const columns = [
  { title: '申请标题', dataIndex: 'title', key: 'title', width: 200, ellipsis: true },
  { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: '当前审批人', dataIndex: 'currentApprover', key: 'currentApprover', width: 120 },
  { title: '提交时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '操作', key: 'action', fixed: 'right', width: 180 },
]

const getTypeColor = (type: string) => {
  const colors: Record<string, string> = {
    '请假': 'blue',
    '报销': 'purple',
    '加班': 'cyan',
  }
  return colors[type] || 'default'
}

const getStatusBadge = (status: string) => {
  const badges: Record<string, string> = {
    '待审批': 'processing',
    '已通过': 'success',
    '已驳回': 'error',
  }
  return badges[status] || 'default'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getApprovalList({
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      applicant: 'admin', // 当前登录用户
      status: statusFilter.value,
      title: searchText.value,
    })
    dataSource.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载数据失败', error)
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleRefresh = () => {
  pagination.current = 1
  searchText.value = ''
  statusFilter.value = ''
  loadData()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

const handleView = (record: ApprovalItem) => {
  message.info('查看详情功能开发中...')
}

const handleDelete = (record: ApprovalItem) => {
  Modal.confirm({
    title: '确认撤回',
    content: '确定要撤回这个申请吗？',
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await deleteApproval(record.id!)
        message.success('撤回成功')
        loadData()
      } catch (error) {
        console.error('撤回失败', error)
        message.error('撤回失败')
      }
    },
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.my-application-container {
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
  }
}
</style>
