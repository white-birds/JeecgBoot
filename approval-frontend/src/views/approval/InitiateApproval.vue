<template>
  <div class="initiate-container">
    <a-card :bordered="false" class="header-card fade-in">
      <div class="header-content">
        <div>
          <h2>发起审批</h2>
          <p>选择您需要申请的审批类型，快速发起流程</p>
        </div>
        <RocketOutlined class="header-icon" />
      </div>
    </a-card>

    <a-row :gutter="[24, 24]" class="types-row">
      <a-col :xs="24" :sm="12" :lg="8" v-for="(item, index) in approvalTypes" :key="index">
        <a-card 
          class="type-card card-hover fade-in" 
          :bordered="false"
          @click="handleCreate(item)"
          :style="{ animationDelay: `${index * 0.1}s` }"
        >
          <div class="type-content">
            <div class="type-icon" :style="{ background: item.color }">
              <component :is="item.icon" />
            </div>
            <div class="type-info">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
              <div class="type-stats">
                <span><FileTextOutlined /> 本月 {{ item.count }} 次</span>
              </div>
            </div>
          </div>
          <a-button type="primary" block size="large" class="type-button">
            立即发起
            <ArrowRightOutlined />
          </a-button>
        </a-card>
      </a-col>
    </a-row>

    <!-- 审批表单弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="700px"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirmLoading="confirmLoading"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 19 }"
      >
        <a-form-item label="申请标题" name="title">
          <a-input v-model:value="formData.title" placeholder="请输入申请标题" />
        </a-form-item>
        
        <a-form-item label="申请类型" name="type">
          <a-select v-model:value="formData.type" disabled>
            <a-select-option value="请假">请假</a-select-option>
            <a-select-option value="报销">报销</a-select-option>
            <a-select-option value="加班">加班</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="申请内容" name="content">
          <a-textarea
            v-model:value="formData.content"
            :rows="6"
            placeholder="请详细描述您的申请内容..."
          />
        </a-form-item>
        
        <a-form-item label="审批人" name="currentApprover">
          <a-input
            v-model:value="formData.currentApprover"
            placeholder="请输入审批人账号（如：admin）"
          />
        </a-form-item>
        
        <a-form-item label="申请人" name="applicant">
          <a-input v-model:value="formData.applicant" disabled />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  DollarOutlined,
  ClockCircleOutlined,
  FileTextOutlined,
  ArrowRightOutlined,
  RocketOutlined,
} from '@ant-design/icons-vue'
import { addApproval } from '@/api/approval'
import type { ApprovalItem } from '@/api/approval'

const router = useRouter()
const formRef = ref()
const modalVisible = ref(false)
const confirmLoading = ref(false)
const modalTitle = ref('')

const approvalTypes = ref([
  {
    title: '请假申请',
    value: '请假',
    icon: CalendarOutlined,
    color: '#1890ff',
    description: '包含年假、病假、事假等各类假期申请',
    count: 12,
  },
  {
    title: '报销申请',
    value: '报销',
    icon: DollarOutlined,
    color: '#722ed1',
    description: '差旅费、加班费、办公用品等费用报销',
    count: 8,
  },
  {
    title: '加班申请',
    value: '加班',
    icon: ClockCircleOutlined,
    color: '#13c2c2',
    description: '工作日加班、节假日加班申请及补休登记',
    count: 15,
  },
])

const formData = reactive<ApprovalItem>({
  title: '',
  type: '',
  content: '',
  applicant: 'admin',
  currentApprover: 'admin',
  status: '待审批',
})

const rules = {
  title: [{ required: true, message: '请输入申请标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择申请类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入申请内容', trigger: 'blur' }],
  currentApprover: [{ required: true, message: '请输入审批人账号', trigger: 'blur' }],
}

const handleCreate = (item: any) => {
  modalTitle.value = `发起${item.title}`
  formData.type = item.value
  formData.title = ''
  formData.content = ''
  formData.currentApprover = 'admin'
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    confirmLoading.value = true
    
    await addApproval(formData)
    
    message.success('申请提交成功！')
    modalVisible.value = false
    
    // 跳转到我的申请页面
    setTimeout(() => {
      router.push('/my-applications')
    }, 500)
  } catch (error) {
    console.error('提交失败', error)
  } finally {
    confirmLoading.value = false
  }
}

const handleCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}
</script>

<style lang="scss" scoped>
.initiate-container {
  .header-card {
    margin-bottom: 24px;
    background: #1890ff;
    
    :deep(.ant-card-body) {
      padding: 32px;
    }
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: white;
      
      h2 {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 8px;
        color: white;
      }
      
      p {
        font-size: 16px;
        opacity: 0.9;
        color: white;
      }
      
      .header-icon {
        font-size: 64px;
        opacity: 0.3;
      }
    }
  }
  
  .types-row {
    .type-card {
      height: 100%;
      transition: all 0.3s ease;
      cursor: pointer;
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
      }
      
      :deep(.ant-card-body) {
        padding: 24px;
        display: flex;
        flex-direction: column;
        height: 100%;
      }
      
      .type-content {
        flex: 1;
        margin-bottom: 20px;
        
        .type-icon {
          width: 64px;
          height: 64px;
          border-radius: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 32px;
          color: white;
          margin-bottom: 16px;
        }
        
        .type-info {
          h3 {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 8px;
            color: #333;
          }
          
          p {
            font-size: 14px;
            color: #666;
            line-height: 1.6;
            margin-bottom: 12px;
            min-height: 44px;
          }
          
          .type-stats {
            font-size: 13px;
            color: #999;
            
            span {
              display: inline-flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
      
      .type-button {
        border-radius: 8px;
        height: 44px;
        font-size: 16px;
        font-weight: 500;
      }
    }
  }
}
</style>

