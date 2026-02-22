# 后端服务文档（`yihen-drama`）

## 1. 模块定位

`yihen-drama` 是 AI 短剧生成平台的后端服务，负责：

- 项目/章节/角色/场景/分镜/视频任务等核心业务
- 模型实例管理与默认模型策略
- 搜索（Elasticsearch）
- 对象存储（MinIO）
- 缓存与并发控制（Redis）
- 消息队列任务编排（RabbitMQ）
- WebSocket 任务状态推送

## 2. 技术栈

- Java 17
- Spring Boot 3.0.5
- MyBatis-Plus 3.5.3.1
- MySQL 8
- Redis（Spring Data Redis + Lettuce）
- RabbitMQ
- Elasticsearch 8.11.3
- MinIO
- Knife4j OpenAPI

## 3. 目录说明

```text
yihen-drama/
├─ src/main/java/com/yihen/
│  ├─ controller/              # 接口层
│  ├─ service/                 # 业务层
│  ├─ mapper/                  # 数据访问
│  ├─ websocket/               # WebSocket
│  ├─ core/model/...           # 模型调用与策略
│  └─ search/...               # ES 索引/查询
├─ src/main/resources/
│  ├─ application.yml
│  └─ es/*.json                # 索引 mapping/settings
├─ sql/init_schema.sql         # 数据库初始化脚本
├─ docker/elasticsearch/
│  └─ Dockerfile               # ES 插件镜像（IK + Pinyin）
└─ Dockerfile                  # 后端应用镜像
```

## 4. 本地开发启动

### 4.1 前置依赖

需先准备并启动：

- MySQL 8
- Redis
- RabbitMQ
- MinIO
- Elasticsearch

可直接使用根目录 `docker-compose.yml` 先起依赖。

### 4.2 启动命令

```bash
cd yihen-drama
mvn clean package
mvn spring-boot:run
```

默认端口：`8080`

## 5. 配置项

主配置文件：`src/main/resources/application.yml`

### 5.1 关键连接项

- `spring.datasource.*`
- `spring.data.redis.host/port`
- `spring.rabbitmq.*`
- `spring.elasticsearch.uris`
- `minio.*`

### 5.2 Docker 场景建议

容器内部请使用服务名（如 `redis`、`mysql`、`mq`、`es`、`minio`），不要写 `localhost`。

## 6. API 文档

启动后访问：

- Knife4j：`http://localhost:8080/doc.html`

主要控制器位于 `com.yihen.controller`，包含：

- `ProjectController`
- `EpisodeController`
- `CharacterController`
- `SceneController`
- `StoryboardController`
- `VideoTaskController`
- `ModelManageController`
- `PromptController`
- `AudioController`

## 7. WebSocket

后端会在视频任务状态变化时推送消息；前端按项目维度建立连接并更新任务状态与资源回写。

## 8. Docker 镜像构建

### 8.1 构建后端镜像

在仓库根目录（推荐）使用 Compose 自动构建：

```bash
docker compose build backend
```

或单独在本目录：

```bash
docker build -t yihen-drama-backend:latest .
```

### 8.2 运行

推荐统一通过根目录 Compose 运行，避免依赖遗漏。

## 9. Elasticsearch 中文插件

插件镜像定义在 `docker/elasticsearch/Dockerfile`，部署自动安装：

- `analysis-ik`
- `analysis-pinyin`

验证命令：

```bash
docker compose exec es elasticsearch-plugin list
```

## 10. 常见故障排查

### 10.1 RedisConnectionFailureException

- 原因：后端 Redis 地址仍为 `localhost`
- 处理：改用 `SPRING_DATA_REDIS_HOST=redis`（Compose 已配置）

### 10.2 数据库脚本未执行

- 原因：MySQL 数据卷已存在
- 处理：`docker compose down -v` 后重启

### 10.3 ES 索引异常

- 查看后端启动日志中 `search.init` 模块输出
- 在 Kibana 或 `curl` 下检查索引状态

