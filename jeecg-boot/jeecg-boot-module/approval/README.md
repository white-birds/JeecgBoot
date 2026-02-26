# AIHOM Approval Module (审批模块)

## 📋 模块概述

AIHOM公司的审批管理模块，基于JeecgBoot低代码平台开发。

## 🏗️ 模块结构

```
org.aihom.modules.approval/
├── controller/          # 控制器层
│   └── AppProjectController.java  # 项目审批控制器
├── entity/             # 实体层
│   ├── AppProject.java     # 项目实体
│   └── AppRecord.java      # 审批记录实体
├── mapper/             # 数据访问层
│   ├── AppProjectMapper.java
│   ├── AppRecordMapper.java
│   └── xml/
│       ├── AppProjectMapper.xml
│       └── AppRecordMapper.xml
├── service/            # 业务逻辑层
│   ├── IAppProjectService.java
│   ├── IAppRecordService.java
│   └── impl/
│       ├── AppProjectServiceImpl.java
│       └── AppRecordServiceImpl.java
└── vo/                 # 视图对象层
    └── AppProjectPage.java
```

## 🚀 启动说明

1. **依赖配置**: 已添加到 `pom.xml`
2. **系统配置**: 已更新 `application-dev.yml`
3. **启动方式**: 使用现有的 `jeecg-system-start` 启动类

## 📊 功能特性

- ✅ 项目审批流程管理
- ✅ 审批记录跟踪
- ✅ 多级审批支持
- ✅ 审批状态流转

## 🔧 配置要求

- **JDK**: 17+
- **框架**: Spring Boot 3.x + MyBatis Plus
- **数据库**: MySQL 5.7+

## 📝 接口文档

启动项目后，可通过以下方式访问：
- Swagger UI: `http://localhost:8080/jeecg-boot/doc.html`
- 接口路径: `/jeecg-boot/approval/**`

## 👥 开发团队

AIHOM 技术团队