<template>
  <div class="aihom-wrapper">
    <!-- 左侧介绍区 -->
    <div class="aihom-left">
      <div class="aihom-left-inner">
        <div class="aihom-brand">
          <div class="aihom-logo-large">
            <img src="/logo.png" alt="AIHOM Logo" style="width: 120px; height: auto;" />
          </div>
          <h1 class="aihom-brand-name">AIHOM</h1>
          <p class="aihom-brand-sub">智能数据分析平台</p>
        </div>
        <div class="aihom-features">
          <div class="aihom-feature-item">
            <span class="feature-dot"></span>
            <span>安全可靠的身份认证</span>
          </div>
          <div class="aihom-feature-item">
            <span class="feature-dot"></span>
            <span>统一单点登录管理</span>
          </div>
          <div class="aihom-feature-item">
            <span class="feature-dot"></span>
            <span>企业级权限控制</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="aihom-right">
      <div class="aihom-form-container">
        <!-- Logo区域 -->
        <div class="aihom-form-logo">
          <img src="/logo.png" alt="AIHOM Logo" style="width: 40px; height: auto;" />
          <span class="aihom-form-title">AIHOM 系统登录</span>
        </div>

        <h2 class="aihom-login-heading">欢迎回来</h2>
        <p class="aihom-login-sub">请登录您的账户以继续</p>

        <!-- 账号输入框 -->
        <div class="aihom-field">
          <label>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            用户名
          </label>
          <input type="text" v-model="loginForm.username" placeholder="请输入用户名" @keyup.enter="handleLogin"/>
        </div>

        <!-- 密码输入框 -->
        <div class="aihom-field">
          <label>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            密码
          </label>
          <div class="aihom-password-wrap">
            <input :type="showPassword ? 'text' : 'password'" v-model="loginForm.password" placeholder="请输入密码"
                   @keyup.enter="handleLogin"/>
            <button type="button" class="aihom-eye-btn" @click="showPassword = !showPassword">
              <!-- 闭眼图标 (隐藏密码时显示) -->
              <svg v-if="!showPassword" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                   stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              <!-- 睁眼图标 (显示密码时显示) -->
              <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                <line x1="1" y1="1" x2="23" y2="23"/>
              </svg>
            </button>
          </div>
        </div>

        <div class="aihom-options">
          <label class="aihom-checkbox">
            <input type="checkbox"> 记住我
          </label>
          <div class="aihom-links">
            <a href="#" class="aihom-forgot">忘记密码？</a>
            <a href="#" class="aihom-register" @click.prevent="showRegisterDrawer = true">注册账号</a>
          </div>
        </div>

        <!-- 登录按钮 -->
        <button class="aihom-submit-btn" @click="handleLogin" :disabled="loading">
          <span>{{ loading ? '登录中...' : '登 录' }}</span>
          <svg v-if="!loading" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
               stroke-width="2">
            <path d="M5 12h14"/>
            <path d="m12 5 7 7-7 7"/>
          </svg>
        </button>

        <p class="aihom-footer">© 2025 AIHOM 智能审批系统 · 保留所有权利</p>
      </div>
    </div>

    <!-- 注册抽屉 -->
    <a-drawer
        v-model:open="showRegisterDrawer"
        title="注册新账号"
        placement="right"
        :width="420"
        :closable="true"
        :maskClosable="true"
    >
      <div class="register-form">
        <a-form
            :model="registerForm"
            :rules="registerRules"
            ref="registerFormRef"
            layout="vertical"
        >
          <a-form-item label="用户名" name="username">
            <a-input
                v-model:value="registerForm.username"
                placeholder="请输入用户名（必填）"
                size="large"
            >
              <template #prefix>
                <UserOutlined style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="真实姓名" name="realname">
            <a-input
                v-model:value="registerForm.realname"
                placeholder="请输入真实姓名（可选）"
                size="large"
            >
              <template #prefix>
                <IdcardOutlined style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="邮箱" name="email">
            <a-input
                v-model:value="registerForm.email"
                placeholder="请输入邮箱地址（可选）"
                size="large"
            >
              <template #prefix>
                <MailOutlined style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="手机号" name="phone">
            <a-input
                v-model:value="registerForm.phone"
                placeholder="请输入手机号码（可选）"
                size="large"
            >
              <template #prefix>
                <PhoneOutlined style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="密码" name="password">
            <a-input-password
                v-model:value="registerForm.password"
                placeholder="请输入密码（6-20位）"
                size="large"
            >
              <template #prefix>
                <LockOutlined style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item label="确认密码" name="confirmPassword">
            <a-input-password
                v-model:value="registerForm.confirmPassword"
                placeholder="请再次输入密码"
                size="large"
            >
              <template #prefix>
                <LockOutlined style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-space direction="vertical" style="width: 100%" :size="12">
              <a-button
                  type="primary"
                  size="large"
                  block
                  :loading="registerLoading"
                  @click="handleRegister"
              >
                立即注册
              </a-button>
              <a-button
                  size="large"
                  block
                  @click="showRegisterDrawer = false"
              >
                取消
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive} from 'vue'
import {useRouter} from 'vue-router'
import {message} from 'ant-design-vue'
import {http} from '@/utils/http'
import {registerUser, type RegisterForm} from '@/api/approval'
import {
  UserOutlined,
  LockOutlined,
  MailOutlined,
  PhoneOutlined,
  IdcardOutlined
} from '@ant-design/icons-vue'
import type {FormInstance, Rule} from 'ant-design-vue'

const router = useRouter()
const showPassword = ref(false)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

// 注册相关
const showRegisterDrawer = ref(false)
const registerLoading = ref(false)
const registerFormRef = ref<FormInstance>()

const registerForm = reactive<RegisterForm>({
  username: '',
  password: '',
  confirmPassword: '',
  realname: '',
  email: '',
  phone: ''
})

// 自定义验证规则
const validatePassword = async (_rule: Rule, value: string) => {
  if (value === '') {
    return Promise.reject('请输入密码')
  }
  if (value.length < 6 || value.length > 20) {
    return Promise.reject('密码长度为6-20位')
  }
  return Promise.resolve()
}

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value === '') {
    return Promise.reject('请再次输入密码')
  }
  if (value !== registerForm.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

// 注册表单验证规则
const registerRules: Record<string, Rule[]> = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 2, max: 20, message: '用户名长度为2-20位', trigger: 'blur'}
  ],
  realname: [
    {min: 2, max: 20, message: '姓名长度为2-20位', trigger: 'blur'}
  ],
  email: [
    {type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur'}
  ],
  phone: [
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur'}
  ],
  password: [
    {required: true, validator: validatePassword, trigger: 'blur'}
  ],
  confirmPassword: [
    {required: true, validator: validateConfirmPassword, trigger: 'blur'}
  ]
}

// 点击登录
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    message.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res: any = await http.post('/sys/login', loginForm)

    console.log("后端返回完整数据:", res)

    // 检查返回结果
    if (res?.success === false) {
      // 后端返回业务错误（如密码错误）
      message.error(res?.message || '登录失败')
      return
    }

    const token = res?.token || res?.result?.token;
    const userInfo = res?.userInfo || res?.result?.userInfo;

    if (token) {
      localStorage.setItem('systemToken', token)

      if (userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
      }

      console.log("✅ 登录成功，Token 已保存")
      message.success('登录成功')

      router.push('/dashboard')
    } else {
      console.error("❌ 未找到 Token, res结构:", res)
      message.error(res?.message || '登录失败，请稍后重试')
    }
  } catch (error: any) {
    console.error('网络请求错误:', error)
    // 只有真正的网络错误才显示这个提示
    if (error?.code === 'ERR_NETWORK' || error?.message?.includes('Network Error')) {
      message.error('网络连接失败，请检查后端是否启动')
    } else if (error?.response?.status === 404) {
      message.error('接口不存在，请检查后端配置')
    } else {
      message.error('请求失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()

    registerLoading.value = true

    // 移除 confirmPassword 字段，不传给后端
    const {confirmPassword, ...submitData} = registerForm

    const res: any = await registerUser(submitData)

    // 检查返回结果
    if (res?.success === false) {
      // 后端返回业务错误（如用户名已存在）
      message.error(res?.message || '注册失败')
      return
    }

    if (res?.success || res?.code === 200 || res?.code === 0) {
      message.success('注册成功！请登录')

      // 自动填充用户名到登录表单
      loginForm.username = registerForm.username
      loginForm.password = ''

      // 关闭抽屉并重置表单
      showRegisterDrawer.value = false
      registerFormRef.value.resetFields()

      // 重置表单数据
      Object.assign(registerForm, {
        username: '',
        password: '',
        confirmPassword: '',
        realname: '',
        email: '',
        phone: ''
      })
    } else {
      message.error(res?.message || '注册失败，请稍后重试')
    }
  } catch (error: any) {
    console.error('注册失败:', error)
    if (error?.errorFields) {
      // 表单验证失败，不显示错误提示
      return
    }
    // 只有真正的网络错误才显示提示
    if (error?.code === 'ERR_NETWORK' || error?.message?.includes('Network Error')) {
      message.error('网络连接失败，请检查后端是否启动')
    } else if (error?.response?.status === 404) {
      message.error('接口不存在，请检查后端配置')
    } else {
      message.error('请求失败，请稍后重试')
    }
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
/* 核心布局与样式 */
.aihom-wrapper {
  display: flex;
  min-height: 100vh;
  width: 100vw;
  background: #f5f7fa;
  color: #1a202c;
  font-family: 'Noto Sans SC', sans-serif;
}

.aihom-left {
  flex: 1;
  background: linear-gradient(145deg, #1a56db 0%, #1e40af 50%, #1e3a8a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.aihom-left::before {
  content: '';
  position: absolute;
  top: -100px;
  right: -100px;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.aihom-left::after {
  content: '';
  position: absolute;
  bottom: -80px;
  left: -80px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.aihom-left-inner {
  position: relative;
  z-index: 1;
  color: white;
  max-width: 420px;
}

.aihom-brand {
  margin-bottom: 60px;
}

.aihom-logo-large {
  margin-bottom: 20px;
  animation: fadeInDown 0.8s ease;
}

.aihom-brand-name {
  font-size: 42px;
  font-weight: 700;
  letter-spacing: 4px;
  margin-bottom: 8px;
  animation: fadeInDown 0.9s ease;
}

.aihom-brand-sub {
  font-size: 16px;
  font-weight: 300;
  opacity: 0.8;
  letter-spacing: 2px;
  animation: fadeInDown 1s ease;
}

.aihom-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.aihom-feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 15px;
  font-weight: 300;
  opacity: 0.9;
  animation: fadeInLeft 1.1s ease;
}

.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.7);
  border: 2px solid rgba(255, 255, 255, 0.4);
  flex-shrink: 0;
}

.aihom-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  padding: 60px 48px;
}

.aihom-form-container {
  width: 100%;
  max-width: 360px;
  animation: fadeInRight 0.8s ease;
}

.aihom-form-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 36px;
}

.aihom-form-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a202c;
  letter-spacing: 0.5px;
}

.aihom-login-heading {
  font-size: 26px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 8px;
}

.aihom-login-sub {
  font-size: 14px;
  color: #718096;
  margin-bottom: 32px;
}

.aihom-field {
  margin-bottom: 20px;
}

.aihom-field label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #4a5568;
  margin-bottom: 8px;
}

.aihom-field label svg {
  color: #a0aec0;
}

.aihom-field input {
  width: 100%;
  height: 46px;
  padding: 0 16px;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  color: #1a202c;
  background: #fafafa;
  transition: all 0.2s ease;
  outline: none;
}

.aihom-field input:focus {
  border-color: #1a56db;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(26, 86, 219, 0.1);
}

.aihom-field input::placeholder {
  color: #cbd5e0;
}

.aihom-password-wrap {
  position: relative;
}

.aihom-password-wrap input {
  padding-right: 46px;
}

.aihom-eye-btn {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #a0aec0;
  padding: 4px;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}

.aihom-eye-btn:hover {
  color: #4a5568;
}

.aihom-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.aihom-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #4a5568;
  cursor: pointer;
  user-select: none;
}

.aihom-checkbox input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #1a56db;
  cursor: pointer;
}

.aihom-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.aihom-forgot,
.aihom-register {
  font-size: 13px;
  color: #1a56db;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.aihom-forgot:hover,
.aihom-register:hover {
  color: #1e40af;
  text-decoration: underline;
}

.aihom-register {
  position: relative;
  padding-left: 12px;
}

.aihom-register::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 12px;
  background: #cbd5e0;
}

.aihom-submit-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #1a56db 0%, #1e40af 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  font-family: inherit;
  letter-spacing: 2px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.2s ease;
  box-shadow: 0 4px 15px rgba(26, 86, 219, 0.35);
}

.aihom-submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(26, 86, 219, 0.45);
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
}

.aihom-submit-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(26, 86, 219, 0.3);
}

.aihom-submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.aihom-footer {
  text-align: center;
  font-size: 12px;
  color: #cbd5e0;
  margin-top: 40px;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 768px) {
  .aihom-left {
    display: none;
  }

  .aihom-right {
    width: 100%;
    padding: 40px 24px;
  }
}

/* 注册抽屉样式 */
.register-form {
  padding-top: 8px;
}

.register-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.register-form :deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #1a202c;
}

.register-form :deep(.ant-input-affix-wrapper),
.register-form :deep(.ant-input) {
  border-radius: 8px;
  border-color: #e2e8f0;
}

.register-form :deep(.ant-input-affix-wrapper:focus),
.register-form :deep(.ant-input-affix-wrapper-focused),
.register-form :deep(.ant-input:focus) {
  border-color: #1a56db;
  box-shadow: 0 0 0 2px rgba(26, 86, 219, 0.1);
}

.register-form :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #1a56db 0%, #1e40af 100%);
  border: none;
  border-radius: 8px;
  height: 44px;
  font-weight: 600;
  letter-spacing: 1px;
  box-shadow: 0 4px 12px rgba(26, 86, 219, 0.3);
}

.register-form :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  box-shadow: 0 6px 16px rgba(26, 86, 219, 0.4);
}

.register-form :deep(.ant-btn-default) {
  border-radius: 8px;
  height: 44px;
  border-color: #e2e8f0;
  color: #4a5568;
}

.register-form :deep(.ant-btn-default:hover) {
  border-color: #cbd5e0;
  color: #1a202c;
}
</style>