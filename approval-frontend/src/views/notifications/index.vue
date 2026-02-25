<template>
  <div class="notifications-container">
    <a-card :bordered="false" class="fade-in">
      <template #title>
        <div class="card-title">
          <BellOutlined />
          <span>通知中心</span>
          <a-badge :count="unreadCount" :overflow-count="99" />
        </div>
      </template>
      
      <template #extra>
        <a-space>
          <a-button @click="handleMarkAllRead">
            <CheckOutlined />
            全部已读
          </a-button>
          <a-button type="primary" @click="handleClearAll" :loading="loading">
            <ClockCircleOutlined />
            刷新
          </a-button>
        </a-space>
      </template>

      <!-- 通知筛选 -->
      <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
        <a-tab-pane key="all">
          <template #tab>
            全部 <a-badge :count="notifications.length" :overflow-count="99" />
          </template>
        </a-tab-pane>
        <a-tab-pane key="unread">
          <template #tab>
            未读 <a-badge :count="unreadCount" :overflow-count="99" />
          </template>
        </a-tab-pane>
      </a-tabs>
      
      <a-empty 
        v-if="filteredNotifications.length === 0" 
        description="暂无通知"
        style="margin: 60px 0"
      >
        <template #image>
          <BellOutlined style="font-size: 64px; color: #d9d9d9" />
        </template>
      </a-empty>

      <!-- 通知列表 -->
      <a-list
        v-if="filteredNotifications.length > 0"
        :data-source="filteredNotifications"
        :loading="loading"
        class="notification-list"
      >
        <template #renderItem="{ item }">
          <a-list-item
            class="notification-item"
            :class="{ unread: !item.read }"
            @click="handleRead(item)"
          >
            <a-list-item-meta>
              <template #avatar>
                <a-avatar :style="{ backgroundColor: item.color }">
                  <template #icon>
                    <component :is="item.icon" />
                  </template>
                </a-avatar>
              </template>
              
              <template #title>
                <div class="notification-title">
                  <span>{{ item.title }}</span>
                  <a-tag v-if="!item.read" color="red" size="small">未读</a-tag>
                </div>
              </template>
              
              <template #description>
                <div class="notification-content">
                  <p>{{ item.content }}</p>
                  <div class="notification-footer">
                    <span class="notification-time">
                      <ClockCircleOutlined />
                      {{ item.time }}
                    </span>
                    <a-tag :color="item.typeColor" size="small">{{ item.type }}</a-tag>
                  </div>
                </div>
              </template>
            </a-list-item-meta>
            
            <template #actions>
              <a-button type="link" size="small" @click.stop="handleDelete(item)">
                <DeleteOutlined />
              </a-button>
            </template>
          </a-list-item>
        </template>
      </a-list>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  BellOutlined,
  CheckOutlined,
  DeleteOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons-vue'
import { getApprovalList } from '@/api/approval'
import type { ApprovalItem } from '@/api/approval'

const loading = ref(false)
const activeTab = ref('all')
const notifications = ref<any[]>([])

// 计算时间差
const getTimeAgo = (dateStr: string) => {
  const now = new Date()
  const date = new Date(dateStr)
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  
  const days = Math.floor(hours / 24)
  return `${days}天前`
}

// 加载最近1小时内的审批通知
const loadNotifications = async () => {
  loading.value = true
  try {
    const res = await getApprovalList({
      pageNo: 1,
      pageSize: 9999,
    })
    
    const records = res.records || []
    
    // 获取1小时前的时间戳
    const oneHourAgo = new Date().getTime() - (60 * 60 * 1000)
    
    // 筛选最近1小时内通过或驳回的审批
    const recentApprovals = records.filter((r: ApprovalItem) => {
      if (!r.updateTime) return false
      const updateTime = new Date(r.updateTime).getTime()
      return updateTime > oneHourAgo && (r.status === '已通过' || r.status === '已驳回')
    })
    
    // 转换为通知格式
    notifications.value = recentApprovals.map((r: ApprovalItem) => ({
      id: r.id,
      title: r.status === '已通过' ? '审批通过通知' : '审批驳回通知',
      content: `您的${r.type}申请「${r.title}」已${r.status}`,
      time: getTimeAgo(r.updateTime!),
      type: '审批通知',
      typeColor: r.status === '已通过' ? 'green' : 'red',
      icon: r.status === '已通过' ? CheckCircleOutlined : CloseCircleOutlined,
      color: r.status === '已通过' ? '#52c41a' : '#ff4d4f',
      read: false,
      approvalData: r,
    }))
    
    console.log('📬 通知数量:', notifications.value.length)
  } catch (error) {
    console.error('加载通知失败', error)
    message.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const approvalCount = computed(() => {
  return notifications.value.length
})

const filteredNotifications = computed(() => {
  let list = notifications.value
  
  if (activeTab.value === 'unread') {
    list = list.filter(n => !n.read)
  }
  
  return list
})

const handleTabChange = (key: string) => {
  activeTab.value = key
}

const handleRead = (item: any) => {
  item.read = true
}

const handleMarkAllRead = () => {
  notifications.value.forEach(n => n.read = true)
  message.success('已全部标记为已读')
}

const handleClearAll = () => {
  loadNotifications()
  message.success('已刷新通知')
}

const handleDelete = (item: any) => {
  const index = notifications.value.findIndex(n => n.id === item.id)
  if (index > -1) {
    notifications.value.splice(index, 1)
    message.success('已删除')
  }
}

onMounted(() => {
  loadNotifications()
  
  // 每分钟自动刷新一次
  setInterval(() => {
    loadNotifications()
  }, 60000)
})
</script>

<style lang="scss" scoped>
.notifications-container {
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 500;
  }
  
  .notification-list {
    .notification-item {
      cursor: pointer;
      transition: all 0.3s;
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 8px;
      
      &.unread {
        background: #f0f7ff;
      }
      
      &:hover {
        background: #fafafa;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }
      
      .notification-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 15px;
        font-weight: 500;
      }
      
      .notification-content {
        p {
          margin-bottom: 8px;
          color: #666;
        }
        
        .notification-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .notification-time {
            font-size: 12px;
            color: #999;
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
    }
  }
}
</style>

