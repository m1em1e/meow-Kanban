# MeowKanban

MeowKanban 是一个轻量级项目看板应用，目前仓库包含 Spring Boot 后端基础结构、数据库初始化脚本、Thymeleaf 登录/注册页面，以及一个可直接预览的静态看板原型。

## 项目功能

- 看板分区：默认包含待规划、待处理、进行中、验收、完成等任务列。
- 任务卡片：展示任务编号、标题、描述、提到用户、截止日期、优先级和标签。
- 搜索与筛选：支持按任务内容搜索，并按紧急优先级、阻塞、我负责等条件筛选。
- 任务详情抽屉：点击任务卡片后查看详情，并支持向前或向后流转状态。
- 拖拽流转：支持将任务卡片拖拽到不同分区。
- 分区管理：支持新增、重命名、删除看板分区，分区配置会保存到浏览器 `localStorage`。
- 响应式布局：适配桌面端和移动端基础使用场景。

## 技术栈

- Java 17
- Spring Boot 3.5.13
- Maven
- Thymeleaf：用于服务端页面模板渲染
- Spring Security：当前已引入并配置为开发阶段放行所有请求
- MyBatis-Plus：用于数据访问层能力
- SQLite：当前默认轻量级数据库
- Hutool：通用工具库
- HTML / CSS / JavaScript 静态原型：当前阶段的界面预览

## 目录结构

```text
.
├── data
│   ├── .gitkeep
│   └── meowkanban.db              # 本地运行生成的 SQLite 数据库，已被 .gitignore 忽略
├── docs
│   ├── 数据库设计.md
│   └── 接口文档.md
├── LICENSE
├── NOTICE
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java/com/godotvillage/meowkanban
│   │   │   ├── MeowKanbanApplication.java
│   │   │   ├── common
│   │   │   │   ├── config
│   │   │   │   │   ├── JacksonConfig.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── constant
│   │   │   │   │   └── ExceptionStatusCodeConstant.java
│   │   │   │   ├── exception
│   │   │   │   │   ├── handler
│   │   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   │   ├── BaseException.java
│   │   │   │   │   ├── DataSaveFaiedException.java
│   │   │   │   │   ├── ImageErrorException.java
│   │   │   │   │   └── LoginFailedException.java
│   │   │   │   ├── handler
│   │   │   │   │   └── AutoMetaObjectHandler.java
│   │   │   │   └── result
│   │   │   │       └── Result.java
│   │   │   ├── controller
│   │   │   │   ├── restController        # REST 控制器预留目录
│   │   │   │   └── PageController.java   # 页面访问入口，返回 login/register 视图
│   │   │   ├── domain
│   │   │   │   ├── entity
│   │   │   │   │   ├── Board.java
│   │   │   │   │   ├── BoardMember.java
│   │   │   │   │   ├── BoardSection.java
│   │   │   │   │   ├── Tag.java
│   │   │   │   │   ├── Task.java
│   │   │   │   │   ├── TaskActivity.java
│   │   │   │   │   ├── TaskAttachment.java
│   │   │   │   │   ├── TaskComment.java
│   │   │   │   │   ├── TaskTag.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── UserPreference.java
│   │   │   │   ├── param
│   │   │   │   │   └── LoginParam.java
│   │   │   │   └── vo
│   │   │   │       ├── LoginVO.java
│   │   │   │       └── UserProfileVO.java
│   │   │   ├── mapper                   # Mapper 预留目录
│   │   │   └── service
│   │   │       └── impl                 # 服务实现预留目录
│   │   └── resources
│   │       ├── application.yaml
│   │       ├── data.sql
│   │       ├── schema.sql
│   │       ├── static/prototype
│   │       │   ├── app.js
│   │       │   ├── auth.html
│   │       │   ├── auth.js
│   │       │   ├── index.html
│   │       │   ├── register.html
│   │       │   ├── register.js
│   │       │   └── styles.css
│   │       └── templates
│   │           ├── login.html
│   │           └── register.html
│   └── test
│       └── java/com/godotvillage/meowkanban
│           └── MeowKanbanApplicationTests.java
└── tools
```

说明：

- `src/main/resources/templates` 是 Thymeleaf 页面目录，`PageController` 中的 `/login` 和 `/register` 会分别返回这里的 `login.html` 和 `register.html`。
- `src/main/resources/static/prototype` 是静态原型目录，其中 `index.html` 可直接用浏览器打开预览；Thymeleaf 页面应通过 Spring Boot 访问。
- `data/meowkanban.db`、`target/`、`.idea/` 等属于本地运行或 IDE 生成内容，不作为项目源码维护。

## 快速开始

### 环境要求

- JDK 17+
- Maven

### 运行测试

Windows:

```powershell
mvn test
```

macOS / Linux:

```bash
mvn test
```

### 启动 Spring Boot 应用

Windows:

```powershell
mvn spring-boot:run
```

macOS / Linux:

```bash
mvn spring-boot:run
```

启动后可访问：

```text
http://localhost:8080/login
http://localhost:8080/register
```

### 预览静态原型

当前看板界面位于：

```text
src/main/resources/static/prototype/index.html
```

可以直接用浏览器打开该文件进行预览。

通过 Spring Boot 启动后，也可以访问静态资源路径：

```text
http://localhost:8080/prototype/index.html
```

## 当前状态

项目处于早期开发阶段：

- 后端已具备 Spring Boot 启动类、统一返回结果、基础异常和全局异常处理等公共结构。
- 已引入 Spring Web、Thymeleaf、Validation、Spring Security、MyBatis-Plus、SQLite JDBC 等基础依赖。
- 已有 SQLite 初始化脚本 `schema.sql` 和 `data.sql`，默认数据库路径配置在 `application.yaml` 中；应用启动时会执行幂等建表脚本，默认数据只在首次初始化时写入。
- 已有 `/login`、`/register` 页面入口和对应 Thymeleaf 模板。
- 前端已有静态看板原型，但任务数据仍写在前端 JavaScript 中。
- 用户认证、任务 API、权限控制、真实业务服务层等能力仍处于开发中。

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

项目当前优先使用 SQLite 作为默认数据库，适合单机部署、个人项目管理和轻量团队场景。

后续会开放数据库配置能力，预留对主流数据库的兼容空间：

- MySQL：适合多人协作、长期在线服务和更完整的生产部署。
- NoSQL：可用于活动日志、通知、扩展配置、非结构化任务元数据等场景。
- 数据访问层会尽量保持清晰边界，避免业务逻辑和具体数据库实现强耦合。

## 后续规划

- 补充 REST Controller、Service、Mapper，实现任务、看板、分区、用户等核心业务 API。
- 将静态原型中的任务、看板分区和用户配置逐步接入 SQLite 持久化。
- 增加可配置的数据源方案，为 MySQL、NoSQL 等数据库适配预留扩展点。
- 继续完善 Thymeleaf 页面模板，并逐步将静态原型改造为 Vue 驱动的真实交互页面。
- 增加登录、权限、团队协作、评论、附件和操作日志。
- 补充接口测试、业务单元测试和前端交互测试。
