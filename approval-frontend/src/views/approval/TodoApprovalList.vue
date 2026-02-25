<template>
  <div class="todo-container">
    <a-card :bordered="false">
      <template #title>
        <div class="card-title">
          <CheckSquareOutlined />
          <span>待我审批</span>
        </div>
      </template>
      
      <template #extra>
        <a-space>
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
              <a-button type="primary" size="small" @click="handleApprove(record, '通过')">
                通过
              </a-button>
              <a-button danger size="small" @click="handleApprove(record, '驳回')">
                驳回
              </a-button>
              <a-button type="link" size="small" @click="handleView(record)">
                查看详情
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 审批弹窗 -->
    <a-modal
      v-model:open="approvalVisible"
      :title="`${approvalAction}审批`"
      @ok="handleSubmitApproval"
      @cancel="approvalVisible = false"
    >
      <a-form :label-col="{ span: 6 }">
        <a-form-item label="审批意见">
          <a-textarea
            v-model:value="approvalComment"
            :rows="4"
            placeholder="请输入审批意见..."
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { CheckSquareOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { getApprovalList, approveAction, getApprovalRecords } from '@/api/approval'
import type { ApprovalItem } from '@/api/approval'

const loading = ref(false)
const searchText = ref('')
const dataSource = ref<ApprovalItem[]>([])
const approvalVisible = ref(false)
const approvalAction = ref('')
const approvalComment = ref('')
const currentRecord = ref<ApprovalItem | null>(null)

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
  { title: '申请人', dataIndex: 'applicant', key: 'applicant', width: 120 },
  { title: '提交时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '操作', key: 'action', fixed: 'right', width: 220 },
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
      status: '待审批',
      currentApprover: 'admin', // 当前登录用户
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
  loadData()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

const handleApprove = (record: ApprovalItem, action: string) => {
  currentRecord.value = record
  approvalAction.value = action
  approvalComment.value = ''
  approvalVisible.value = true
}

const handleSubmitApproval = async () => {
  if (!currentRecord.value) return
  
  try {
    loading.value = true
    
    // 构造审批记录
    const appRecordList = [{
      operator: 'admin', // 当前登录用户
      action: approvalAction.value,
      comment: approvalComment.value || `${approvalAction.value}`,
    }]
    
    // 更新审批状态
    await approveAction({
      ...currentRecord.value,
      status: approvalAction.value === '通过' ? '已通过' : '已驳回',
      appRecordList,
    })
    
    message.success(`${approvalAction.value}成功！`)
    approvalVisible.value = false
    loadData() // 重新加载数据
  } catch (error) {
    console.error('审批失败', error)
    message.error('审批失败')
  } finally {
    loading.value = false
  }
}

const handleView = (record: ApprovalItem) => {
  // 查看详情功能
  message.info('查看详情功能开发中...')
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.todo-container {
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
  }
}
</style>
