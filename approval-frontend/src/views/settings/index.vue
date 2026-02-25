<template>
  <div class="settings-container">
    <a-card :bordered="false" class="fade-in">
      <template #title>
        <div class="card-title">
          <SettingOutlined />
          <span>系统设置</span>
        </div>
      </template>

      <a-tabs v-model:activeKey="activeTab">
        <!-- 基本设置 -->
        <a-tab-pane key="basic" tab="基本设置">
          <a-form
            :model="basicForm"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 12 }"
          >
            <a-form-item label="系统名称">
              <a-input v-model:value="basicForm.systemName" />
            </a-form-item>
            
            <a-form-item label="系统Logo">
              <a-upload
                list-type="picture-card"
                :show-upload-list="false"
              >
                <div v-if="basicForm.logo">
                  <img :src="basicForm.logo" alt="logo" style="width: 100%" />
                </div>
                <div v-else>
                  <PlusOutlined />
                  <div style="margin-top: 8px">上传</div>
                </div>
              </a-upload>
            </a-form-item>
            
            <a-form-item label="系统描述">
              <a-textarea v-model:value="basicForm.description" :rows="4" />
            </a-form-item>
            
            <a-form-item label="联系邮箱">
              <a-input v-model:value="basicForm.email" />
            </a-form-item>
            
            <a-form-item label="联系电话">
              <a-input v-model:value="basicForm.phone" />
            </a-form-item>
            
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-space>
                <a-button type="primary" @click="handleSaveBasic">
                  保存设置
                </a-button>
                <a-button @click="handleResetBasic">
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- 审批设置 -->
        <a-tab-pane key="approval" tab="审批设置">
          <a-form
            :model="approvalForm"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 12 }"
          >
            <a-form-item label="自动通过">
              <a-switch v-model:checked="approvalForm.autoApprove" />
              <span style="margin-left: 8px; color: #999">
                启用后，符合条件的申请将自动通过
              </span>
            </a-form-item>
            
            <a-form-item label="超时提醒">
              <a-switch v-model:checked="approvalForm.timeoutRemind" />
              <span style="margin-left: 8px; color: #999">
                审批超时后发送提醒通知
              </span>
            </a-form-item>
            
            <a-form-item label="超时时间">
              <a-input-number
                v-model:value="approvalForm.timeoutHours"
                :min="1"
                :max="168"
                addon-after="小时"
              />
            </a-form-item>
            
            <a-form-item label="允许撤回">
              <a-switch v-model:checked="approvalForm.allowWithdraw" />
              <span style="margin-left: 8px; color: #999">
                允许申请人撤回待审批的申请
              </span>
            </a-form-item>
            
            <a-form-item label="允许修改">
              <a-switch v-model:checked="approvalForm.allowEdit" />
              <span style="margin-left: 8px; color: #999">
                允许申请人修改待审批的申请
              </span>
            </a-form-item>
            
            <a-form-item label="抄送功能">
              <a-switch v-model:checked="approvalForm.enableCC" />
              <span style="margin-left: 8px; color: #999">
                启用审批抄送功能
              </span>
            </a-form-item>
            
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-space>
                <a-button type="primary" @click="handleSaveApproval">
                  保存设置
                </a-button>
                <a-button @click="handleResetApproval">
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- 通知设置 -->
        <a-tab-pane key="notification" tab="通知设置">
          <a-form
            :model="notificationForm"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 12 }"
          >
            <a-form-item label="邮件通知">
              <a-switch v-model:checked="notificationForm.emailNotify" />
            </a-form-item>
            
            <a-form-item label="短信通知">
              <a-switch v-model:checked="notificationForm.smsNotify" />
            </a-form-item>
            
            <a-form-item label="站内通知">
              <a-switch v-model:checked="notificationForm.systemNotify" />
            </a-form-item>
            
            <a-form-item label="通知场景">
              <a-checkbox-group v-model:value="notificationForm.scenarios">
                <a-checkbox value="submit">提交申请时</a-checkbox>
                <a-checkbox value="approve">审批通过时</a-checkbox>
                <a-checkbox value="reject">审批驳回时</a-checkbox>
                <a-checkbox value="withdraw">撤回申请时</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-space>
                <a-button type="primary" @click="handleSaveNotification">
                  保存设置
                </a-button>
                <a-button @click="handleResetNotification">
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- 安全设置 -->
        <a-tab-pane key="security" tab="安全设置">
          <a-form
            :model="securityForm"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 12 }"
          >
            <a-form-item label="密码强度">
              <a-select v-model:value="securityForm.passwordStrength">
                <a-select-option value="low">低（6位以上）</a-select-option>
                <a-select-option value="medium">中（8位，包含字母数字）</a-select-option>
                <a-select-option value="high">高（10位，包含字母数字特殊字符）</a-select-option>
              </a-select>
            </a-form-item>
            
            <a-form-item label="密码有效期">
              <a-input-number
                v-model:value="securityForm.passwordExpireDays"
                :min="0"
                :max="365"
                addon-after="天"
              />
              <span style="margin-left: 8px; color: #999">
                0表示永不过期
              </span>
            </a-form-item>
            
            <a-form-item label="登录失败锁定">
              <a-switch v-model:checked="securityForm.loginLock" />
              <span style="margin-left: 8px; color: #999">
                连续登录失败后锁定账号
              </span>
            </a-form-item>
            
            <a-form-item label="失败次数">
              <a-input-number
                v-model:value="securityForm.maxLoginAttempts"
                :min="3"
                :max="10"
                addon-after="次"
              />
            </a-form-item>
            
            <a-form-item label="会话超时">
              <a-input-number
                v-model:value="securityForm.sessionTimeout"
                :min="10"
                :max="1440"
                addon-after="分钟"
              />
            </a-form-item>
            
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-space>
                <a-button type="primary" @click="handleSaveSecurity">
                  保存设置
                </a-button>
                <a-button @click="handleResetSecurity">
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { SettingOutlined, PlusOutlined } from '@ant-design/icons-vue'

const activeTab = ref('basic')

const basicForm = reactive({
  systemName: 'AIHOM 智能审批系统',
  logo: '',
  description: '高效、智能的企业审批管理平台',
  email: 'support@aihom.com',
  phone: '400-123-4567',
})

const approvalForm = reactive({
  autoApprove: false,
  timeoutRemind: true,
  timeoutHours: 24,
  allowWithdraw: true,
  allowEdit: true,
  enableCC: true,
})

const notificationForm = reactive({
  emailNotify: true,
  smsNotify: false,
  systemNotify: true,
  scenarios: ['submit', 'approve', 'reject'],
})

const securityForm = reactive({
  passwordStrength: 'medium',
  passwordExpireDays: 90,
  loginLock: true,
  maxLoginAttempts: 5,
  sessionTimeout: 120,
})

const handleSaveBasic = () => {
  message.success('基本设置已保存')
}

const handleResetBasic = () => {
  message.info('已重置为默认设置')
}

const handleSaveApproval = () => {
  message.success('审批设置已保存')
}

const handleResetApproval = () => {
  message.info('已重置为默认设置')
}

const handleSaveNotification = () => {
  message.success('通知设置已保存')
}

const handleResetNotification = () => {
  message.info('已重置为默认设置')
}

const handleSaveSecurity = () => {
  message.success('安全设置已保存')
}

const handleResetSecurity = () => {
  message.info('已重置为默认设置')
}
</script>

<style lang="scss" scoped>
.settings-container {
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 500;
  }
}
</style>





