# AIHOM 智能审批系统 - 前端项目

<div align="center">
  <h1>🚀 AIHOM Approval System</h1>
  <p>现代化、高效的企业审批管理平台</p>
</div>

## ✨ 特性

- 🎨 **现代化 UI** - 基于 Ant Design Vue 4.x，美观易用
- 📱 **响应式设计** - 完美适配桌面端和移动端
- 🔥 **Vue 3 + TypeScript** - 使用最新的前端技术栈
- ⚡ **Vite 构建** - 极速的开发体验
- 📊 **数据可视化** - ECharts 图表展示审批数据
- 🎭 **动画效果** - 流畅的页面过渡和交互动画
- 🔐 **权限管理** - 完善的权限控制体系
- 🌈 **主题定制** - 支持自定义主题配置

## 📦 功能模块

### 核心功能
- ✅ **工作台** - 数据概览、待办事项、最近动态
- ✅ **发起审批** - 多种审批类型快速发起
- ✅ **我的申请** - 查看、修改、撤回申请
- ✅ **待我审批** - 审批处理、意见填写
- ✅ **审批历史** - 历史记录查询、流程追踪

### 扩展功能
- 📊 **统计报表** - 多维度数据分析和可视化
- 📝 **审批模板** - 自定义审批表单模板
- 🔔 **通知中心** - 实时消息推送和提醒
- ⚙️ **系统设置** - 灵活的系统配置选项

## 🛠️ 技术栈

- **框架**: Vue 3.4+
- **构建工具**: Vite 5.0+
- **UI 组件**: Ant Design Vue 4.1+
- **状态管理**: Pinia 2.1+
- **路由**: Vue Router 4.2+
- **HTTP 客户端**: Axios 1.6+
- **图表库**: ECharts 5.4+ / Vue-ECharts 6.6+
- **语言**: TypeScript 5.3+
- **样式**: SCSS

## 📁 项目结构

```
approval-frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   │   └── approval.ts    # 审批相关接口
│   ├── layout/            # 布局组件
│   │   └── index.vue      # 主布局
│   ├── router/            # 路由配置
│   │   └── index.ts       # 路由定义
│   ├── styles/            # 全局样式
│   │   └── index.scss     # 样式入口
│   ├── utils/             # 工具函数
│   │   └── http.ts        # HTTP 请求封装
│   ├── views/             # 页面组件
│   │   ├── dashboard/     # 工作台
│   │   ├── approval/      # 审批相关页面
│   │   ├── statistics/    # 统计报表
│   │   ├── templates/     # 审批模板
│   │   ├── notifications/ # 通知中心
│   │   └── settings/      # 系统设置
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── index.html             # HTML 模板
├── package.json           # 依赖配置
├── tsconfig.json          # TypeScript 配置
├── vite.config.ts         # Vite 配置
└── README.md              # 项目说明
```

## 🚀 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0 或 pnpm >= 8.0.0

### 安装依赖

```bash
# 使用 npm
npm install

# 或使用 pnpm（推荐）
pnpm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:3000

### 生产构建

```bash
npm run build
```

构建产物在 `dist` 目录

### 预览构建

```bash
npm run preview
```

## 🔧 配置说明

### API 代理配置

在 `vite.config.ts` 中配置后端 API 代理：

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8081/approval-boot',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
    },
  },
}
```

### 环境变量

创建 `.env.development` 和 `.env.production` 文件：

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8081/approval-boot

# .env.production
VITE_API_BASE_URL=https://your-domain.com/approval-boot
```

## 📱 页面展示

### 工作台
- 数据统计卡片
- 审批趋势图表
- 待办事项列表
- 最近动态时间线

### 发起审批
- 卡片式审批类型选择
- 表单填写和提交
- 实时验证

### 我的申请
- 申请列表查询
- 状态筛选
- 修改和撤回操作

### 待我审批
- 待审批列表
- 审批详情查看
- 通过/驳回操作

### 审批历史
- 历史记录查询
- 流程追踪
- 数据导出

### 统计报表
- 多维度数据统计
- 可视化图表展示
- 趋势分析

## 🎨 UI 设计

### 色彩方案
- 主色：`#1890ff` (蓝色)
- 成功：`#52c41a` (绿色)
- 警告：`#faad14` (橙色)
- 错误：`#ff4d4f` (红色)

### 渐变色
- 蓝紫渐变：`linear-gradient(135deg, #667eea 0%, #764ba2 100%)`
- 粉红渐变：`linear-gradient(135deg, #f093fb 0%, #f5576c 100%)`
- 青蓝渐变：`linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)`

### 动画效果
- 页面切换：淡入淡出
- 卡片悬停：上浮阴影
- 列表加载：渐入动画

## 🔌 API 接口

### 审批接口

```typescript
// 获取审批列表
GET /api/approval/appProject/list

// 新增审批
POST /api/approval/appProject/add

// 编辑审批
POST /api/approval/appProject/edit

// 删除审批
DELETE /api/approval/appProject/delete

// 查询审批记录
GET /api/approval/appProject/queryAppRecordByMainId
```

## 📝 开发规范

### 代码风格
- 使用 TypeScript 严格模式
- 遵循 Vue 3 Composition API 规范
- 使用 ESLint 进行代码检查

### 命名规范
- 组件名：PascalCase
- 文件名：kebab-case
- 变量名：camelCase
- 常量名：UPPER_SNAKE_CASE

### Git 提交规范
```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

## 🚢 部署

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/approval-frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8081/approval-boot;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Docker 部署

```dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

- 

---

<div align="center">
  <p>Made with ❤️ by AIHOM Team</p>
</div>





