# 审批模块独立部署指南

## 📦 打包说明

审批模块已经配置为可独立运行的Spring Boot应用。

### 1. 打包命令

在 `approval` 模块目录下执行:

```bash
# 方式1: 在approval目录下打包
cd jeecg-boot/jeecg-boot-module/approval
mvn clean package -DskipTests

# 方式2: 在项目根目录打包指定模块
mvn clean package -pl jeecg-boot/jeecg-boot-module/approval -am -DskipTests
```

### 2. 打包产物

打包成功后,会在 `target` 目录下生成:
- `approval-boot.jar` - 可执行的jar包(包含所有依赖)

### 3. 运行jar包

```bash
# 使用默认配置运行(dev环境)
java -jar target/approval-boot.jar

# 指定配置文件运行
java -jar target/approval-boot.jar --spring.profiles.active=prod

# 指定端口运行
java -jar target/approval-boot.jar --server.port=8082

# 后台运行
nohup java -jar target/approval-boot.jar > approval.log 2>&1 &
```

### 4. 配置说明

#### 数据库配置
修改 `src/main/resources/application-dev.yml` 中的数据库连接信息:

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://你的数据库地址:3306/jeecg-boot?...
          username: 你的用户名
          password: 你的密码
```

#### Redis配置
```yaml
spring:
  redis:
    host: 你的Redis地址
    port: 6379
    password: 你的密码
```

#### 端口配置
```yaml
server:
  port: 8081  # 修改为你需要的端口
```

### 5. 访问地址

启动成功后:
- 应用地址: http://localhost:8081/approval-boot/
- Swagger文档: http://localhost:8081/approval-boot/doc.html
- 审批接口: http://localhost:8081/approval-boot/approval/appProject/list

### 6. 生产环境部署

#### 创建生产环境配置
复制 `application-dev.yml` 为 `application-prod.yml`,修改为生产环境配置。

#### 打包并运行
```bash
# 打包
mvn clean package -DskipTests

# 上传jar包到服务器
scp target/approval-boot.jar user@server:/opt/approval/

# 在服务器上运行
cd /opt/approval
nohup java -jar approval-boot.jar --spring.profiles.active=prod > approval.log 2>&1 &

# 查看日志
tail -f approval.log
```

### 7. 系统服务配置(可选)

创建 systemd 服务文件 `/etc/systemd/system/approval.service`:

```ini
[Unit]
Description=Approval Boot Application
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

启动服务:
```bash
systemctl daemon-reload
systemctl start approval
systemctl enable approval
systemctl status approval
```

### 8. Docker部署(可选)

创建 `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/approval-boot.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
```

构建并运行:
```bash
docker build -t approval-boot:latest .
docker run -d -p 8081:8081 --name approval approval-boot:latest
```

## 🔧 常见问题

### 1. 依赖问题
如果打包时提示找不到父模块依赖,需要先在项目根目录执行:
```bash
cd jeecg-boot
mvn clean install -DskipTests
```

### 2. 端口冲突
修改 `application-dev.yml` 中的 `server.port` 配置

### 3. 数据库连接失败
检查数据库地址、用户名、密码是否正确,数据库是否启动

## 📝 注意事项

1. **数据库**: 确保数据库中已创建相关表结构
2. **Redis**: 确保Redis服务已启动
3. **JDK版本**: 需要JDK 17或更高版本
4. **防火墙**: 确保服务器防火墙开放了对应端口

## 👥 技术支持

如有问题,请联系 AIHOM 技术团队





