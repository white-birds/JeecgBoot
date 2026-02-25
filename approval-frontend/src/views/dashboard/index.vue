<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <a-card class="welcome-card" :bordered="false">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>你好，Admin 👋</h1>
          <p>欢迎回到 AIHOM 审批系统 · {{ new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' }) }}</p>
        </div>
        <div class="welcome-actions">
          <a-button type="primary" size="large" :loading="loading" @click="loadStatistics">
            <template #icon><ReloadOutlined /></template>
            刷新数据
          </a-button>
        </div>
      </div>
    </a-card>

    <!-- 统计卡片 -->
    <a-row :gutter="[16, 16]" class="stats-row">
      <a-col :xs="24" :sm="12" :lg="6" v-for="(stat, index) in stats" :key="index">
        <div class="stat-card" :style="{ '--card-color': stat.color }">
          <div class="stat-icon-wrapper">
            <component :is="stat.icon" class="stat-icon" />
          </div>
          <div class="stat-content">
            <div class="stat-label">{{ stat.title }}</div>
            <div class="stat-value-wrapper">
              <span class="stat-value">{{ stat.value }}</span>
              <span class="stat-change" :class="stat.trend">
                <CaretUpOutlined v-if="stat.trend === 'up'" />
                <CaretDownOutlined v-else />
                {{ stat.change }}
              </span>
            </div>
            <div class="stat-desc">较上周</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 快捷操作 -->
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :lg="12">
        <a-card title="快捷操作" :bordered="false">
          <a-space direction="vertical" style="width: 100%">
            <a-button type="primary" block size="large" @click="router.push('/initiate')">
              <template #icon><PlusCircleOutlined /></template>
              发起审批
            </a-button>
            <a-button block size="large" @click="router.push('/todo')">
              <template #icon><CheckSquareOutlined /></template>
              待我审批 ({{ todoCount }})
            </a-button>
            <a-button block size="large" @click="router.push('/my-applications')">
              <template #icon><FileTextOutlined /></template>
              我的申请
            </a-button>
            <a-button block size="large" @click="router.push('/history')">
              <template #icon><HistoryOutlined /></template>
              审批历史
            </a-button>
          </a-space>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="12">
        <a-card title="最近动态" :bordered="false">
          <a-timeline>
            <a-timeline-item v-for="(activity, index) in activities" :key="index" :color="activity.color">
              <p>{{ activity.content }}</p>
              <p class="activity-time">{{ activity.time }}</p>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  FileTextOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  PlusCircleOutlined,
  CheckSquareOutlined,
  HistoryOutlined,
  ReloadOutlined,
  CaretUpOutlined,
  CaretDownOutlined,
} from '@ant-design/icons-vue'
import { getApprovalList } from '@/api/approval'

const router = useRouter()
const loading = ref(false)
const todoCount = ref(0)

const stats = ref([
  {
    title: '总申请数',
    value: 0,
    change: '+12%',
    trend: 'up',
    icon: FileTextOutlined,
    color: '#1890ff',
  },
  {
    title: '已通过',
    value: 0,
    change: '+8%',
    trend: 'up',
    icon: CheckCircleOutlined,
    color: '#52c41a',
  },
  {
    title: '待审批',
    value: 0,
    change: '-5%',
    trend: 'down',
    icon: ClockCircleOutlined,
    color: '#faad14',
  },
  {
    title: '已驳回',
    value: 0,
    change: '+3%',
    trend: 'up',
    icon: CloseCircleOutlined,
    color: '#ff4d4f',
  },
])

const activities = ref([
  { content: '张三 提交了请假申请', time: '2分钟前', color: 'blue' },
  { content: '李四 的报销申请已通过', time: '10分钟前', color: 'green' },
  { content: '王五 撤回了加班申请', time: '1小时前', color: 'orange' },
  { content: '赵六 的请假申请被驳回', time: '2小时前', color: 'red' },
])

// 加载统计数据 - 用 list 接口查询，前端自己统计
const loadStatistics = async () => {
  loading.value = true
  try {
    // 查询所有数据（不分页）
    const res = await getApprovalList({
      pageNo: 1,
      pageSize: 9999, // 查询全部
    })
    
    const records = res.records || []
    console.log('📊 查询到的数据:', records.length, '条')
    
    // 前端统计
    const totalCount = records.length
    const approvedCount = records.filter(r => r.status === '已通过').length
    const pendingCount = records.filter(r => r.status === '待审批').length
    const rejectedCount = records.filter(r => r.status === '已驳回').length
    
    // 更新统计卡片
    stats.value[0].value = totalCount
    stats.value[1].value = approvedCount
    stats.value[2].value = pendingCount
    stats.value[3].value = rejectedCount
    
    // 更新待审批数量
    todoCount.value = pendingCount
    
    message.success('统计数据已更新')
  } catch (error) {
    console.error('加载统计数据失败', error)
    message.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .welcome-card {
    margin-bottom: 16px;
    background: #1890ff;
    color: white;
    
    :deep(.ant-card-body) {
      padding: 32px;
    }
    
    .welcome-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .welcome-text {
        h1 {
          font-size: 28px;
          font-weight: 600;
          margin-bottom: 8px;
          color: white;
        }
        
        p {
          font-size: 14px;
          opacity: 0.9;
          color: white;
          margin: 0;
        }
      }
    }
  }
  
  .stats-row {
    margin-bottom: 16px;
  }
  
  .stat-card {
    position: relative;
    padding: 24px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    overflow: hidden;
    transition: all 0.3s ease;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 4px;
      height: 100%;
      background: var(--card-color);
    }
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
    
    .stat-icon-wrapper {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 12px;
      background: var(--card-color);
      opacity: 0.1;
      margin-bottom: 16px;
      
      .stat-icon {
        font-size: 24px;
        color: var(--card-color);
        opacity: 1;
      }
    }
    
    .stat-content {
      .stat-label {
        font-size: 14px;
        color: #666;
        margin-bottom: 12px;
        font-weight: 500;
      }
      
      .stat-value-wrapper {
        display: flex;
        align-items: baseline;
        gap: 12px;
        margin-bottom: 8px;
        
        .stat-value {
          font-size: 32px;
          font-weight: 700;
          color: #1a1a1a;
          line-height: 1;
        }
        
        .stat-change {
          display: inline-flex;
          align-items: center;
          gap: 4px;
          font-size: 14px;
          font-weight: 600;
          padding: 2px 8px;
          border-radius: 4px;
          
          &.up {
            color: #52c41a;
            background: #f6ffed;
          }
          
          &.down {
            color: #ff4d4f;
            background: #fff1f0;
          }
        }
      }
      
      .stat-desc {
        font-size: 12px;
        color: #999;
      }
    }
  }
  
  .activity-time {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
  }
}
</style>
