# Yihen-Drama（AI 短剧生成平台）

面向“剧情文本 → 信息提取 → 角色/场景图生成 → 分镜生成 → 视频生成/编辑”的一体化创作系统。  
本仓库包含完整前后端代码与一键部署编排（MySQL/Redis/RabbitMQ/MinIO/Elasticsearch/Kibana）。

## 功能概览

- 项目管理：创建、编辑、删除、搜索、分页
- 章节管理：创建、更新、删除、流程步骤控制
- 提取能力：角色/场景提取与回写
- 素材能力：角色与场景的增删改查、搜索、上传、替换
- 分镜能力：分镜生成、编辑、删除、关联角色/场景、提示词维护
- 视频能力：分镜首帧、分镜视频提示词、视频任务提交与轮询回传（WebSocket）
- 模型管理：厂商、模型实例、默认实例、按类型管理（文本/图像/视频/语音）
- 搜索能力：项目、角色、场景的 ES 搜索与 suggest 补全

## 技术栈

- 前端：Vue 3 + Vite + Vue Router + Pinia + Axios + Sass
- 后端：Spring Boot 3 + MyBatis-Plus + Redis + RabbitMQ + Elasticsearch + MinIO
- 基础设施：Docker Compose（MySQL 8 / Redis 7 / RabbitMQ 4 / MinIO / ES 8.11.3 / Kibana）

## 仓库结构

```text
.
├─ docker-compose.yml
├─ deploy.ps1 / deploy.bat
├─ yihen-drama/                               # 后端
│  ├─ src/main/java/com/yihen/controller/...
│  ├─ src/main/resources/application.yml
│  ├─ sql/init_schema.sql                     # MySQL 初始化脚本
│  ├─ docker/elasticsearch/Dockerfile         # ES + IK/Pinyin 插件镜像
│  └─ Dockerfile
└─ yihen-ai-short-drama-front-end/frontend/   # 前端
   ├─ src/
   ├─ Dockerfile
   └─ nginx.conf
```

## 运行方式

### 1) 一键 Docker 启动（推荐）

在仓库根目录执行：

```bash
docker compose up -d --build
```

Windows 可直接执行：

```powershell
./deploy.ps1
```

或

```bat
deploy.bat
```

启动后默认访问：

- 前端：`http://localhost:3000`
- 后端：`http://localhost:8080`
- Swagger/Knife4j：`http://localhost:8080/doc.html`
- MinIO Console：`http://localhost:9001`
- RabbitMQ Console：`http://localhost:15672`
- Kibana：`http://localhost:5601`
- Elasticsearch：`http://localhost:9200`

> 首次启动会自动初始化 MySQL：`yihen-drama/sql/init_schema.sql`。  
> 仅在 `mysql_data` 卷为空时执行；若需重新初始化，请先 `docker compose down -v`。

### 2) 本地开发运行（分开）

#### 后端

```bash
cd yihen-drama
mvn clean package
mvn spring-boot:run
```

#### 前端

```bash
cd yihen-ai-short-drama-front-end/frontend
npm install
npm run dev
```

## Docker 编排说明

`docker-compose.yml` 已包含以下服务并按健康检查顺序依赖启动：

- `mysql`（自动执行 init_schema.sql）
- `redis`
- `mq`（RabbitMQ）
- `minio`
- `es`（自定义镜像，自动安装 IK + Pinyin）
- `kibana`
- `backend`
- `frontend`

### Elasticsearch 中文插件（IK + Pinyin）

已内置在 `yihen-drama/docker/elasticsearch/Dockerfile`。  
部署时自动安装，无需手工进入容器执行。

可验证：

```bash
docker compose exec es elasticsearch-plugin list
docker compose exec es curl -s http://localhost:9200/_cat/plugins?v
```

## 配置说明

后端关键配置在 `yihen-drama/src/main/resources/application.yml`，支持通过环境变量覆盖，Compose 已预置：

- 数据库：`SPRING_DATASOURCE_URL/USERNAME/PASSWORD`
- Redis：`SPRING_DATA_REDIS_HOST/SPRING_DATA_REDIS_PORT`
- RabbitMQ：`SPRING_RABBITMQ_HOST/PORT/USERNAME/PASSWORD`
- Elasticsearch：`SPRING_ELASTICSEARCH_URIS`
- MinIO：`MINIO_END_POINT/MINIO_ACCESS_KEY/MINIO_SECRET_KEY`

前端可通过 `VITE_API_BASE_URL` 指定 API 基地址（Compose 中使用同域代理方式，默认留空）。

## 常见问题

### 1. 后端报 Redis 连接失败

- 确认 `docker compose ps` 中 `yihen-redis` 为 `healthy`
- 确认后端使用的是 `SPRING_DATA_REDIS_HOST=redis`（非 localhost）

### 2. MySQL 未重新初始化

- `init_schema.sql` 只在空数据卷执行
- 执行 `docker compose down -v` 后再 `up -d --build`

### 3. DockerHub 拉取超时

- 本项目已将后端/ES 镜像切到镜像源（`docker.1ms.run`）
- 如前端镜像拉取失败，可将前端 Dockerfile 基础镜像也切到可用镜像源

## 模块文档

- 后端文档：`yihen-drama/README.md`
- 前端文档：`yihen-ai-short-drama-front-end/frontend/README.md`

