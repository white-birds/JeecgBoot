<template>
  <div class="history-container">
    <a-card :bordered="false">
      <template #title>
        <div class="card-title">
          <HistoryOutlined />
          <span>审批历史</span>
        </div>
      </template>
      
      <template #extra>
        <a-space>
          <a-range-picker
            v-model:value="dateRange"
            @change="handleSearch"
            format="YYYY-MM-DD"
          />
          
          <a-select
            v-model:value="statusFilter"
            style="width: 120px"
            @change="handleSearch"
          >
            <a-select-option value="">全部状态</a-select-option>
            <a-select-option value="已通过">已通过</a-select-option>
            <a-select-option value="已驳回">已驳回</a-select-option>
          </a-select>
          
          <a-input-search
            v-model:value="searchText"
            placeholder="搜索标题或申请人"
            style="width: 200px"
            @search="handleSearch"
          />
          
          <a-button @click="handleRefresh">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
          <a-button
            type="primary"
            danger
            @click="handleBatchDelete"
            :disabled="!hasSelected"
            :loading="loading"
          >
            批量删除
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
        :row-selection="rowSelection"
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
              <a-button type="link" size="small" @click="handleViewRecords(record)">
                审批记录
              </a-button>
              <a-popconfirm
                title="确定要删除这条审批历史吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" size="small" danger>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 审批记录弹窗 -->
    <a-modal
      v-model:open="recordsVisible"
      title="审批记录"
      :footer="null"
      width="600px"
    >
      <a-timeline>
        <a-timeline-item
          v-for="(record, index) in approvalRecords"
          :key="index"
          :color="record.action === '通过' ? 'green' : record.action === '驳回' ? 'red' : 'blue'"
        >
          <p><strong>{{ record.operator }}</strong> - {{ record.action }}</p>
          <p v-if="record.comment">意见：{{ record.comment }}</p>
          <p class="record-time">{{ record.updateTime }}</p>
        </a-timeline-item>
      </a-timeline>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { HistoryOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { getApprovalList, getApprovalRecords, deleteApproval, deleteBatchApproval } from '@/api/approval'
import type { ApprovalItem, ApprovalRecord } from '@/api/approval'
import type { Dayjs } from 'dayjs'

const loading = ref(false)
const searchText = ref('')
const statusFilter = ref('')
const dateRange = ref<[Dayjs, Dayjs]>()
const dataSource = ref<ApprovalItem[]>([])
const recordsVisible = ref(false)
const approvalRecords = ref<ApprovalRecord[]>([])
const selectedRowKeys = ref<string[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

const columns = [
  { title: '申请标题', dataIndex: 'title', key: 'title', width: 180, ellipsis: true },
  { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: '申请人', dataIndex: 'applicant', key: 'applicant', width: 100 },
  { title: '审批人', dataIndex: 'currentApprover', key: 'currentApprover', width: 100 },
  { title: '提交时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
  { title: '完成时间', dataIndex: 'updateTime', key: 'updateTime', width: 160 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'action', fixed: 'right', width: 180 },
]

const hasSelected = computed(() => selectedRowKeys.value.length > 0)

const rowSelection = computed(() => {
  return {
    selectedRowKeys: selectedRowKeys.value,
    onChange: (keys: string[]) => {
      selectedRowKeys.value = keys
    },
  }
})

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
    '已通过': 'success',
    '已驳回': 'error',
  }
  return badges[status] || 'default'
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      currentApprover: 'admin',
      title: searchText.value,
    }
    
    if (statusFilter.value) {
      params.status = statusFilter.value
    } else {
      params.status_in = '已通过,已驳回'
    }
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.createTime_begin = dateRange.value[0].format('YYYY-MM-DD')
      params.createTime_end = dateRange.value[1].format('YYYY-MM-DD')
    }
    
    const res = await getApprovalList(params)
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
  dateRange.value = undefined
  loadData()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

const handleDelete = async (record: ApprovalItem) => {
  try {
    await deleteApproval(record.id!)
    message.success('删除成功')
    loadData()
  } catch (error) {
    message.error('删除失败')
  }
}

const handleBatchDelete = () => {
  if (!hasSelected.value) return
  Modal.confirm({
    title: '确定要批量删除选中的记录吗？',
    content: '此操作不可恢复。',
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        loading.value = true
        await deleteBatchApproval(selectedRowKeys.value.join(','))
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadData()
      } catch (error) {
        message.error('批量删除失败')
      } finally {
        loading.value = false
      }
    },
  })
}

const handleViewRecords = async (record: ApprovalItem) => {
  try {
    const records = await getApprovalRecords(record.id!)
    approvalRecords.value = records || []
    recordsVisible.value = true
  } catch (error) {
    console.error('加载审批记录失败', error)
    message.error('加载审批记录失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.history-container {
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
  }
  
  .record-time {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
  }
}
</style>
