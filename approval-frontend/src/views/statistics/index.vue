<template>
  <div class="statistics-container">
    <a-card :bordered="false" class="header-card fade-in">
      <div class="header-content">
        <div>
          <h2>统计报表</h2>
          <p>审批数据分析 · 实时更新</p>
        </div>
        <div class="header-actions">
          <a-button type="primary" size="large" :loading="loading" @click="loadStatistics">
            刷新数据
          </a-button>
        </div>
      </div>
    </a-card>

    <!-- 统计卡片 -->
    <a-row :gutter="[16, 16]" class="stats-cards">
      <a-col :xs="24" :sm="12" :lg="6" v-for="(stat, index) in statsCards" :key="index">
        <div class="stat-card fade-in" :style="{ animationDelay: `${index * 0.1}s`, '--card-color': stat.color }">
          <div class="stat-icon-wrapper">
            <component :is="stat.icon" class="stat-icon" />
          </div>
          <div class="stat-content">
            <div class="stat-label">{{ stat.title }}</div>
            <div class="stat-value-wrapper">
              <span class="stat-value">{{ loading ? '-' : stat.value }}</span>
              <span class="stat-suffix">{{ stat.suffix }}</span>
            </div>
            <div class="stat-desc">
              <span class="trend-badge">🔄 实时数据</span>
            </div>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="[16, 16]">
      <!-- 审批趋势图 -->
      <a-col :xs="24" :lg="16">
        <a-card :bordered="false" :loading="loading" class="fade-in">
          <template #title>
            <a-space>
              <span>审批趋势分析（按日期统计）</span>
              <a-tag color="green">实时数据</a-tag>
            </a-space>
          </template>
          <v-chart class="chart" :option="trendChartOption" autoresize />
        </a-card>
      </a-col>

      <!-- 类型分布饼图 -->
      <a-col :xs="24" :lg="8">
        <a-card :bordered="false" :loading="loading" class="fade-in">
          <template #title>
            <a-space>
              <span>审批类型分布</span>
              <a-tag color="green">实时数据</a-tag>
            </a-space>
          </template>
          <v-chart class="chart" :option="pieChartOption" autoresize />
        </a-card>
      </a-col>

      <!-- 审批人排行 -->
      <a-col :xs="24">
        <a-card :bordered="false" :loading="loading" class="fade-in">
          <template #title>
            <a-space>
              <span>审批人排行榜（处理数量 TOP 5）</span>
              <a-tag color="green">实时数据</a-tag>
            </a-space>
          </template>
          <v-chart class="chart" :option="barChartOption" autoresize />
        </a-card>
      </a-col>

      <!-- 月度统计表格 -->
      <a-col :span="24">
        <a-card :bordered="false" class="fade-in">
          <template #title>
            <a-space>
              <span>月度统计明细（最近6个月）</span>
              <a-tag color="green">实时数据</a-tag>
            </a-space>
          </template>
          <a-table
            :columns="tableColumns"
            :data-source="tableData"
            :pagination="false"
            :loading="loading"
            size="middle"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'passRate'">
                <a-progress
                  :percent="record.passRate"
                  :stroke-color="record.passRate >= 80 ? '#52c41a' : '#faad14'"
                  size="small"
                />
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components'
import {
  FileTextOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons-vue'
import { getApprovalList } from '@/api/approval'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
])

const loading = ref(false)

const statsCards = ref([
  {
    title: '总申请数',
    value: 0,
    suffix: '件',
    icon: FileTextOutlined,
    color: '#1890ff',
    trend: 12.5,
  },
  {
    title: '已通过',
    value: 0,
    suffix: '件',
    icon: CheckCircleOutlined,
    color: '#52c41a',
    trend: 8.3,
  },
  {
    title: '待审批',
    value: 0,
    suffix: '件',
    icon: ClockCircleOutlined,
    color: '#faad14',
    trend: -5.2,
  },
  {
    title: '已驳回',
    value: 0,
    suffix: '件',
    icon: CloseCircleOutlined,
    color: '#ff4d4f',
    trend: 3.1,
  },
])

const trendChartOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
    },
  },
  legend: {
    data: ['提交', '通过', '驳回'],
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [],
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      name: '提交',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#1890ff' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
            { offset: 1, color: 'rgba(24, 144, 255, 0.05)' },
          ],
        },
      },
    },
    {
      name: '通过',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#52c41a' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
            { offset: 1, color: 'rgba(82, 196, 26, 0.05)' },
          ],
        },
      },
    },
    {
      name: '驳回',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#ff4d4f' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(255, 77, 79, 0.3)' },
            { offset: 1, color: 'rgba(255, 77, 79, 0.05)' },
          ],
        },
      },
    },
  ],
})

const pieChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)',
  },
  legend: {
    orient: 'vertical',
    left: 'left',
  },
  series: [
    {
      name: '审批类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2,
      },
      label: {
        show: false,
        position: 'center',
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold',
        },
      },
      labelLine: {
        show: false,
      },
      data: [
        { value: 0, name: '请假', itemStyle: { color: '#1890ff' } },
        { value: 0, name: '报销', itemStyle: { color: '#722ed1' } },
        { value: 0, name: '加班', itemStyle: { color: '#13c2c2' } },
      ],
    },
  ],
})

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
    console.log('📊 统计报表数据:', records.length, '条')
    
    // 1. 统计卡片
    const totalCount = records.length
    const approvedCount = records.filter(r => r.status === '已通过').length
    const pendingCount = records.filter(r => r.status === '待审批').length
    const rejectedCount = records.filter(r => r.status === '已驳回').length
    
    statsCards.value[0].value = totalCount
    statsCards.value[1].value = approvedCount
    statsCards.value[2].value = pendingCount
    statsCards.value[3].value = rejectedCount
    
    // 2. 类型分布饼图
    const typeColors = {
      '请假': '#1890ff',
      '报销': '#722ed1',
      '加班': '#13c2c2',
    }
    
    const typeStats: Record<string, number> = {}
    records.forEach(r => {
      typeStats[r.type] = (typeStats[r.type] || 0) + 1
    })
    
    pieChartOption.value.series[0].data = Object.entries(typeStats).map(([type, count]) => ({
      value: count,
      name: type,
      itemStyle: { color: typeColors[type as keyof typeof typeColors] || '#1890ff' },
    }))
    
    // 3. 审批趋势分析（按日期）- 取最近7天
    const dateStats: Record<string, { submit: number; pass: number; reject: number }> = {}
    
    records.forEach(r => {
      if (r.createTime) {
        const date = r.createTime.split(' ')[0] // 取日期部分
        if (!dateStats[date]) {
          dateStats[date] = { submit: 0, pass: 0, reject: 0 }
        }
        dateStats[date].submit++
        if (r.status === '已通过') dateStats[date].pass++
        if (r.status === '已驳回') dateStats[date].reject++
      }
    })
    
    // 排序并取最近7条
    const sortedDates = Object.keys(dateStats).sort().slice(-7)
    Object.assign(trendChartOption.value.xAxis, { data: sortedDates.map(d => d.substring(5)) }) // 只显示月-日
    Object.assign(trendChartOption.value.series[0], { data: sortedDates.map(d => dateStats[d].submit) })
    Object.assign(trendChartOption.value.series[1], { data: sortedDates.map(d => dateStats[d].pass) })
    Object.assign(trendChartOption.value.series[2], { data: sortedDates.map(d => dateStats[d].reject) })
    
    // 4. 审批人排行榜
    const approverStats: Record<string, number> = {}
    records.forEach(r => {
      if (r.currentApprover) {
        approverStats[r.currentApprover] = (approverStats[r.currentApprover] || 0) + 1
      }
    })
    
    // 排序并取前5名
    const topApprovers = Object.entries(approverStats)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
    
    Object.assign(barChartOption.value.yAxis, { data: topApprovers.map(([name]) => name) })
    Object.assign(barChartOption.value.series[0], { data: topApprovers.map(([, count]) => count) })
    
    // 5. 月度统计明细
    const monthStats: Record<string, { submit: number; pass: number; reject: number }> = {}
    
    records.forEach(r => {
      if (r.createTime) {
        const month = r.createTime.substring(0, 7) // 取年-月
        if (!monthStats[month]) {
          monthStats[month] = { submit: 0, pass: 0, reject: 0 }
        }
        monthStats[month].submit++
        if (r.status === '已通过') monthStats[month].pass++
        if (r.status === '已驳回') monthStats[month].reject++
      }
    })
    
    // 转换为表格数据并排序
    tableData.value = Object.entries(monthStats)
      .sort((a, b) => b[0].localeCompare(a[0])) // 降序
      .slice(0, 6) // 最近6个月
      .map(([month, stats]) => ({
        month,
        submit: stats.submit,
        pass: stats.pass,
        reject: stats.reject,
        passRate: stats.submit > 0 ? Math.round((stats.pass / stats.submit) * 100) : 0,
      }))
    
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

const barChartOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow',
    },
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true,
  },
  xAxis: {
    type: 'value',
  },
  yAxis: {
    type: 'category',
    data: [],
  },
  series: [
    {
      name: '审批数量',
      type: 'bar',
      data: [],
      itemStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 1,
          y2: 0,
          colorStops: [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' },
          ],
        },
        borderRadius: [0, 8, 8, 0],
      },
    },
  ],
})

const tableColumns = [
  { title: '月份', dataIndex: 'month', key: 'month' },
  { title: '提交数', dataIndex: 'submit', key: 'submit' },
  { title: '通过数', dataIndex: 'pass', key: 'pass' },
  { title: '驳回数', dataIndex: 'reject', key: 'reject' },
  { title: '通过率', dataIndex: 'passRate', key: 'passRate' },
]

const tableData = ref<any[]>([])
</script>

<style lang="scss" scoped>
.statistics-container {
  .header-card {
    margin-bottom: 16px;
    background: #1890ff;
    
    :deep(.ant-card-body) {
      padding: 24px 32px;
    }
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: white;
      
      h2 {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 4px;
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
  
  .stats-cards {
    margin-bottom: 16px;
    
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
          gap: 4px;
          margin-bottom: 8px;
          
          .stat-value {
            font-size: 32px;
            font-weight: 700;
            color: #1a1a1a;
            line-height: 1;
          }
          
          .stat-suffix {
            font-size: 16px;
            color: #999;
            font-weight: 500;
          }
        }
        
        .stat-desc {
          .trend-badge {
            display: inline-flex;
            align-items: center;
            font-size: 12px;
            color: #52c41a;
            background: #f6ffed;
            padding: 2px 8px;
            border-radius: 4px;
          }
        }
      }
    }
  }
  
  .chart {
    height: 350px;
  }
}
</style>

