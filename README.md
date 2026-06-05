# MeowKanban

MeowKanban 是一个轻量级项目看板应用，目前仓库包含 Spring Boot 项目骨架、通用后端基础类，以及一个可直接预览的静态看板原型。

## 项目功能

- 看板分区：默认包含待规划、待处理、进行中、验收、完成等任务列。
- 任务卡片：展示任务编号、标题、描述、负责人、截止日期、优先级和标签。
- 搜索与筛选：支持按任务内容搜索，并按高优先级、阻塞、我负责等条件筛选。
- 任务详情抽屉：点击任务卡片后查看详情，并支持向前或向后流转状态。
- 拖拽流转：支持将任务卡片拖拽到不同分区。
- 分区管理：支持新增、重命名、删除看板分区，分区配置会保存到浏览器 `localStorage`。
- 响应式布局：适配桌面端和移动端基础使用场景。

## 技术栈

- Java 17
- Spring Boot 3.5.14
- Maven Wrapper
- Thymeleaf：计划用于服务端模板渲染、页面入口和基础布局
- Vue：计划用于看板交互、组件化任务卡片和局部状态管理
- SQLite：计划作为默认轻量级数据库
- HTML / CSS / JavaScript 静态原型：当前阶段的界面预览

## 目录结构

```text
.
├── pom.xml
├── mvnw / mvnw.cmd
├── src
│   ├── main
│   │   ├── java/com/godotvillage/meowkanban
│   │   │   ├── MeowKanbanApplication.java
│   │   │   └── common
│   │   │       ├── constant
│   │   │       ├── exception
│   │   │       ├── handler
│   │   │       └── result
│   │   └── resources
│   │       ├── application.yaml
│   │       └── static/prototype
│   │           ├── index.html
│   │           ├── styles.css
│   │           └── app.js
│   └── test
│       └── java/com/godotvillage/meowkanban
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 可选，项目已内置 Maven Wrapper

### 运行测试

Windows:

```powershell
.\mvnw.cmd test
```

macOS / Linux:

```bash
./mvnw test
```

### 启动 Spring Boot 应用

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

### 预览静态原型

当前看板界面位于：

```text
src/main/resources/static/prototype/index.html
```

可以直接用浏览器打开该文件进行预览。

> 注意：当前 `pom.xml` 暂未引入 `spring-boot-starter-web`，因此静态原型主要按本地 HTML 文件方式预览。后续如果需要通过 Spring Boot 提供 Web 访问，可加入 Web Starter 并访问对应静态资源路径。

## 当前状态

项目处于早期开发阶段：

- 后端已具备 Spring Boot 启动类、统一返回结果、基础异常和全局异常处理等公共结构。
- 前端已有静态看板原型，但任务数据仍写在前端 JavaScript 中。
- 暂未接入数据库、用户认证、任务 API、权限控制等完整业务能力。

## 开源协议

本项目采用 Apache License 2.0 开源协议。

该协议允许商业使用、修改、分发和演绎作品，同时要求保留版权声明、许可证文本和署名信息。项目根目录中的 `LICENSE` 保存完整协议文本，`NOTICE` 保存项目署名信息。

## 技术规划

### 前端方案

项目计划采用 Thymeleaf + Vue 的组合：

- Thymeleaf 负责服务端页面模板、公共布局、静态资源组织和首屏页面装配。
- Vue 负责看板核心交互，包括任务卡片、分区、拖拽流转、筛选搜索、详情抽屉等动态能力。
- 早期可以在 Thymeleaf 页面中逐步挂载 Vue 组件，后续根据复杂度再决定是否拆分为更完整的前端工程。

### 数据库方案

项目计划优先使用 SQLite 作为默认数据库，适合单机部署、个人项目管理和轻量团队场景。

后续会开放数据库配置能力，预留对主流数据库的兼容空间：

- MySQL：适合多人协作、长期在线服务和更完整的生产部署。
- NoSQL：可用于活动日志、通知、扩展配置、非结构化任务元数据等场景。
- 数据访问层会尽量保持清晰边界，避免业务逻辑和具体数据库实现强耦合。

## 后续规划

- 引入 `spring-boot-starter-web`，提供 REST API。
- 设计任务、看板、分区、用户、成员关系等领域模型。
- 接入 SQLite，并持久化任务、看板分区和用户配置。
- 增加可配置的数据源方案，为 MySQL、NoSQL 等数据库适配预留扩展点。
- 引入 Thymeleaf 页面模板，并逐步将静态原型改造为 Vue 驱动的真实交互页面。
- 增加登录、权限、团队协作、评论、附件和操作日志。
- 补充接口测试、业务单元测试和前端交互测试。
