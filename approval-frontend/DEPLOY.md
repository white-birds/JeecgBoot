# AIHOM 智能审批系统 - 部署指南

## 📋 目录

- [环境准备](#环境准备)
- [前端部署](#前端部署)
- [后端部署](#后端部署)
- [Nginx 配置](#nginx-配置)
- [Docker 部署](#docker-部署)
- [常见问题](#常见问题)

## 🔧 环境准备

### 前端环境
- Node.js >= 16.0.0
- npm >= 8.0.0 或 pnpm >= 8.0.0

### 后端环境
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 🎨 前端部署

### 1. 安装依赖

```bash
cd approval-frontend
npm install
```

### 2. 配置环境变量

创建 `.env.production` 文件：

```bash
# API 地址
VITE_API_BASE_URL=http://your-domain.com:8081/approval-boot

# 其他配置
VITE_APP_TITLE=AIHOM 智能审批系统
```

### 3. 构建生产版本

```bash
npm run build
```

构建完成后，产物在 `dist` 目录。

### 4. 部署到服务器

#### 方式 1：直接部署

```bash
# 上传到服务器
scp -r dist/* user@server:/var/www/approval-frontend/

# 或使用 rsync
rsync -avz dist/ user@server:/var/www/approval-frontend/
```

#### 方式 2：使用 Nginx

```bash
# 复制文件到 Nginx 目录
cp -r dist/* /usr/share/nginx/html/approval/
```

## 🚀 后端部署

### 1. 配置数据库

```sql
-- 创建数据库
CREATE DATABASE `jeecg-boot` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 导入表结构（如果有 SQL 文件）
source /path/to/schema.sql;
```

### 2. 修改配置文件

编辑 `application-prod.yml`：

```yaml
server:
  port: 8081

spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/jeecg-boot?characterEncoding=UTF-8&useUnicode=true&useSSL=false
          username: your_username
          password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

### 3. 打包应用

```bash
cd jeecg-boot/jeecg-boot-module/approval
mvn clean package -DskipTests
```

### 4. 运行应用

```bash
# 前台运行
java -jar target/approval-boot.jar --spring.profiles.active=prod

# 后台运行
nohup java -jar target/approval-boot.jar --spring.profiles.active=prod > approval.log 2>&1 &

# 查看日志
tail -f approval.log
```

### 5. 配置为系统服务

创建 `/etc/systemd/system/approval.service`：

```ini
[Unit]
Description=AIHOM Approval Service
After=syslog.target network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/approval
ExecStart=/usr/bin/java -jar /opt/approval/approval-boot.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
systemctl daemon-reload
systemctl start approval
systemctl enable approval
systemctl status approval
```

## 🌐 Nginx 配置

### 完整配置示例

创建 `/etc/nginx/conf.d/approval.conf`：

```nginx
# 前端服务
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    root /var/www/approval-frontend;
    index index.html;
    
    # 开启 gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
    gzip_min_length 1000;
    
    # 前端路由
    location / {
        try_files $uri $uri/ /index.html;
        
        # 缓存策略
        add_header Cache-Control "no-cache";
    }
    
    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # API 代理
    location /api/ {
        proxy_pass http://localhost:8081/approval-boot/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}

# HTTPS 配置（可选）
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    # SSL 配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    # 其他配置同上...
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

重启 Nginx：

```bash
nginx -t
systemctl restart nginx
```

## 🐳 Docker 部署

### 1. 前端 Dockerfile

创建 `approval-frontend/Dockerfile`：

```dockerfile
# 构建阶段
FROM node:18-alpine as builder

WORKDIR /app

# 复制依赖文件
COPY package*.json ./
RUN npm install

# 复制源代码
COPY . .

# 构建
RUN npm run build

# 生产阶段
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

创建 `approval-frontend/nginx.conf`：

```nginx
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://approval-backend:8081/approval-boot/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 2. 后端 Dockerfile

创建 `approval/Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制 jar 包
COPY target/approval-boot.jar app.jar

# 暴露端口
EXPOSE 8081

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

### 3. Docker Compose

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: approval-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: jeecg-boot
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - approval-network

  # Redis
  redis:
    image: redis:7-alpine
    container_name: approval-redis
    ports:
      - "6379:6379"
    networks:
      - approval-network

  # 后端服务
  backend:
    build: ./approval
    container_name: approval-backend
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/jeecg-boot?characterEncoding=UTF-8
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123
      SPRING_REDIS_HOST: redis
    depends_on:
      - mysql
      - redis
    networks:
      - approval-network

  # 前端服务
  frontend:
    build: ./approval-frontend
    container_name: approval-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - approval-network

volumes:
  mysql-data:

networks:
  approval-network:
    driver: bridge
```

### 4. 启动服务

```bash
# 构建并启动
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重启服务
docker-compose restart
```

## ❓ 常见问题

### 1. 前端访问 404

**问题**：刷新页面出现 404

**解决**：配置 Nginx 的 `try_files`，将所有请求重定向到 `index.html`

### 2. API 跨域问题

**问题**：前端调用后端 API 出现跨域错误

**解决**：
- 使用 Nginx 代理
- 或在后端添加 CORS 配置

### 3. 后端启动失败

**问题**：数据库连接失败

**解决**：
- 检查数据库是否启动
- 检查连接配置是否正确
- 检查防火墙设置

### 4. 打包后文件过大

**问题**：dist 目录文件过大

**解决**：
- 开启 gzip 压缩
- 使用 CDN 加载第三方库
- 代码分割和懒加载

### 5. 内存不足

**问题**：Java 应用内存溢出

**解决**：调整 JVM 参数

```bash
java -Xms512m -Xmx1024m -jar approval-boot.jar
```

## 📞 技术支持

如有问题，请联系：
- 邮箱：support@aihom.com
- 电话：400-123-4567
- 官网：https://aihom.com

---

祝您部署顺利！🎉





