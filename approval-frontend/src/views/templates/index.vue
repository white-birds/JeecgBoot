<template>
  <div class="templates-container">
    <a-card :bordered="false" class="fade-in">
      <template #title>
        <div class="card-title">
          <FormOutlined />
          <span>审批模板管理</span>
        </div>
      </template>
      
      <template #extra>
        <a-button type="primary" @click="handleAdd">
          <PlusOutlined />
          新建模板
        </a-button>
      </template>

      <!-- 模板列表 -->
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :lg="8" v-for="(template, index) in templates" :key="index">
          <a-card class="template-card card-hover" :bordered="false" @click="handleEdit(template)">
            <div class="template-header">
              <div class="template-icon" :style="{ background: template.gradient }">
                <component :is="template.icon" />
              </div>
              <a-dropdown>
                <MoreOutlined class="more-icon" />
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="handleEdit(template)">
                      <EditOutlined />
                      编辑
                    </a-menu-item>
                    <a-menu-item @click="handleCopy(template)">
                      <CopyOutlined />
                      复制
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item danger @click="handleDelete(template)">
                      <DeleteOutlined />
                      删除
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
            
            <h3>{{ template.name }}</h3>
            <p class="template-desc">{{ template.description }}</p>
            
            <a-divider style="margin: 12px 0" />
            
            <div class="template-stats">
              <span><FileTextOutlined /> 使用 {{ template.useCount }} 次</span>
              <a-tag :color="template.status === '启用' ? 'green' : 'default'">
                {{ template.status }}
              </a-tag>
            </div>
            

          </a-card>
        </a-col>
      </a-row>
    </a-card>

    <!-- 编辑模板弹窗 -->
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
        <a-form-item label="模板名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入模板名称" />
        </a-form-item>
        
        <a-form-item label="模板类型" name="type">
          <a-select v-model:value="formData.type" placeholder="请选择类型">
            <a-select-option value="请假">请假</a-select-option>
            <a-select-option value="报销">报销</a-select-option>
            <a-select-option value="加班">加班</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="模板描述" name="description">
          <a-textarea v-model:value="formData.description" :rows="3" placeholder="请输入模板描述" />
        </a-form-item>
        
        <a-form-item label="表单字段" name="fields">
          <a-space direction="vertical" style="width: 100%">
            <div v-for="(field, index) in formData.fields" :key="index" class="field-item">
              <a-input-group compact>
                <a-input v-model:value="field.label" placeholder="字段名称" style="width: 30%" />
                <a-select v-model:value="field.type" placeholder="字段类型" style="width: 30%">
                  <a-select-option value="input">单行文本</a-select-option>
                  <a-select-option value="textarea">多行文本</a-select-option>
                  <a-select-option value="number">数字</a-select-option>
                  <a-select-option value="date">日期</a-select-option>
                  <a-select-option value="select">下拉选择</a-select-option>
                </a-select>
                <a-input v-model:value="field.placeholder" placeholder="提示文字" style="width: 30%" />
                <a-button danger @click="removeField(index)" style="width: 10%">
                  <DeleteOutlined />
                </a-button>
              </a-input-group>
            </div>
            <a-button type="dashed" block @click="addField">
              <PlusOutlined />
              添加字段
            </a-button>
          </a-space>
        </a-form-item>
        
        <a-form-item label="默认审批人" name="defaultApprover">
          <a-input v-model:value="formData.defaultApprover" placeholder="请输入默认审批人账号" />
        </a-form-item>
        
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio value="启用">启用</a-radio>
            <a-radio value="禁用">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import {
  FormOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  CopyOutlined,
  MoreOutlined,
  FileTextOutlined,
  CalendarOutlined,
  DollarOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons-vue'

const formRef = ref()
const modalVisible = ref(false)
const confirmLoading = ref(false)
const modalTitle = ref('')

const templates = ref([
  {
    id: 1,
    name: '年假申请',
    type: '请假',
    description: '员工年假申请模板，包含请假时间、天数、事由等信息',
    icon: CalendarOutlined,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    useCount: 156,
    status: '启用',
  },
  {
    id: 2,
    name: '差旅报销',
    type: '报销',
    description: '差旅费用报销模板，包含出差地点、时间、费用明细等',
    icon: DollarOutlined,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    useCount: 89,
    status: '启用',
  },
  {
    id: 3,
    name: '加班申请',
    type: '加班',
    description: '员工加班申请模板，包含加班时间、时长、原因等',
    icon: ClockCircleOutlined,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    useCount: 124,
    status: '启用',
  },
])

const formData = reactive({
  name: '',
  type: '',
  description: '',
  fields: [] as any[],
  defaultApprover: 'admin',
  status: '启用',
})

const rules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择模板类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入模板描述', trigger: 'blur' }],
}

const handleAdd = () => {
  modalTitle.value = '新建模板'
  formData.name = ''
  formData.type = ''
  formData.description = ''
  formData.fields = []
  formData.defaultApprover = 'admin'
  formData.status = '启用'
  modalVisible.value = true
}

const handleEdit = (template: any) => {
  modalTitle.value = '编辑模板'
  Object.assign(formData, template)
  if (!formData.fields) {
    formData.fields = []
  }
  modalVisible.value = true
}

const handleCopy = (template: any) => {
  message.success('模板已复制')
}

const handleDelete = (template: any) => {
  message.success('模板已删除')
}


const addField = () => {
  formData.fields.push({
    label: '',
    type: 'input',
    placeholder: '',
  })
}

const removeField = (index: number) => {
  formData.fields.splice(index, 1)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    confirmLoading.value = true
    
    // 模拟保存
    setTimeout(() => {
      message.success('保存成功')
      modalVisible.value = false
      confirmLoading.value = false
    }, 1000)
  } catch (error) {
    console.error('提交失败', error)
  }
}

const handleCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}
</script>

<style lang="scss" scoped>
.templates-container {
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 500;
  }
  
  .template-card {
    height: 100%;
    
    .template-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;
      
      .template-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: white;
      }
      
      .more-icon {
        font-size: 18px;
        cursor: pointer;
        color: #999;
        
        &:hover {
          color: #333;
        }
      }
    }
    
    h3 {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 8px;
    }
    
    .template-desc {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      min-height: 44px;
    }
    
    .template-stats {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      color: #999;
      margin-bottom: 12px;
    }
    
    .use-btn {
      margin-top: 12px;
    }
  }
  
  .field-item {
    margin-bottom: 8px;
  }
}
</style>



