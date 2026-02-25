# 🚀 AIHOM 审批前端 - 快速启动指南

## 📋 启动前检查

### 1. 确认后端服务运行中
后端应该已经在 `http://localhost:8081/approval-boot/` 运行

### 2. 安装依赖（首次运行必须执行！）

```bash
cd E:\develop\JavaCode\JeecgBoot\approval-frontend
npm install
```

> ⚠️ **重要**：如果你还没运行 `npm install`，前端肯定启动不了！这是安装所有依赖包的关键步骤。

### 3. 启动开发服务器

```bash
npm run dev
```

### 4. 访问系统

打开浏览器访问：**http://localhost:5173**

---

## 🎯 功能菜单

启动成功后，你会看到左侧菜单栏包含：

### 核心功能（原有4个）
- 📊 **工作台** - 仪表盘概览
- 🚀 **发起审批** - 新建请假/报销/加班申请
- 📋 **我的申请** - 查看自己提交的申请
- ⏰ **待我审批** - 处理待审批的申请
- 📜 **审批历史** - 查看已完成的审批

### 扩展功能（新增5个）
- 📈 **统计报表** - 审批数据可视化
- 📝 **审批模板** - 管理审批模板
- 🔔 **通知中心** - 消息通知
- ⚙️ **系统设置** - 系统配置

---

## 🐛 常见问题

### 问题1：菜单不显示 / 页面点不动
**原因**：没有安装依赖
**解决**：运行 `npm install`

### 问题2：端口被占用
**错误信息**：`Port 5173 is already in use`
**解决**：
```bash
# 关闭占用5173端口的进程，或修改 vite.config.ts 中的端口号
```

### 问题3：连接后端失败
**错误信息**：`Network Error` 或 `404`
**解决**：
1. 确认后端服务在 `http://localhost:8081/approval-boot/` 运行
2. 检查 `src/utils/http.ts` 中的 `baseURL` 配置

### 问题4：白屏 / 报错
**解决**：
1. 打开浏览器控制台（F12）查看具体错误
2. 确保 Node.js 版本 >= 16
3. 删除 `node_modules` 文件夹，重新运行 `npm install`

---

## 📦 生产部署

### 1. 构建生产版本
```bash
npm run build
```

### 2. 构建结果
生成 `dist` 文件夹，包含所有静态资源

### 3. 部署方式

#### 方式1：Nginx
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/approval-frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /approval-boot/ {
        proxy_pass http://localhost:8081;
    }
}
```

#### 方式2：简单HTTP服务器
```bash
cd dist
python -m http.server 8080
```

---

## 🎨 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全
- **Vite** - 极速开发服务器
- **Ant Design Vue** - 企业级 UI 组件库
- **Vue Router** - 路由管理
- **Axios** - HTTP 客户端
- **Pinia** - 状态管理（已配置，未使用）
- **SCSS** - CSS 预处理器

---

## 📞 需要帮助？

如果遇到问题：
1. 检查浏览器控制台（F12）的错误信息
2. 查看终端的启动日志
3. 确认后端服务正常运行
4. 检查网络请求是否成功（F12 -> Network）





