# 前端文档（`frontend`）

## 1. 模块定位

前端是 AI 短剧平台的工作台与创作主界面，覆盖：

- 工作台项目管理（搜索、筛选、分页）
- 创作空间多步骤流程（输入 → 提取 → 生成图 → 角色固定 → 分镜 → 视频生成/编辑）
- 资产管理（角色/场景）
- 提示词管理
- 模型管理（厂商、实例、默认实例）
- 任务联动（HTTP + WebSocket）

## 2. 技术栈

- Vue 3
- Vite 5
- Vue Router 4
- Pinia
- Axios
- Sass

## 3. 目录结构

```text
frontend/
├─ src/
│  ├─ api/                     # 接口封装
│  ├─ components/              # 通用组件
│  ├─ composables/             # 组合式函数
│  ├─ router/                  # 路由配置
│  ├─ stores/                  # 全局状态
│  ├─ views/                   # 页面视图
│  │  ├─ DashboardView.vue
│  │  ├─ WorkspaceView.vue
│  │  ├─ AssetManageView.vue
│  │  ├─ PromptView.vue
│  │  ├─ TaskManagerView.vue
│  │  └─ SettingsView.vue
│  └─ main.js
├─ public/
├─ Dockerfile
├─ nginx.conf
└─ package.json
```

## 4. 本地开发

### 4.1 安装依赖

```bash
cd yihen-ai-short-drama-front-end/frontend
npm install
```

### 4.2 启动开发环境

```bash
npm run dev
```

默认地址：`http://localhost:3000`

### 4.3 构建生产包

```bash
npm run build
```

### 4.4 本地预览

```bash
npm run preview
```

## 5. 环境变量

示例文件：`.env.example`

关键变量：

- `VITE_API_BASE_URL`
  - 本地开发可配置为后端地址，例如 `http://localhost:8080`
  - Docker Compose 中默认留空，通过同域转发方式访问后端

## 6. 路由页面

路由定义见 `src/router/index.js`：

- `/`：工作台
- `/workspace/:id`：创作空间
- `/assets`：资产管理
- `/prompts`：提示词管理
- `/tasks`：任务管理
- `/settings`：模型管理

## 7. API 层约定

入口文件：`src/api/index.js`

- 统一基于 Axios 实例
- 统一处理后端 `Result` 包装（`code/message/data`）
- 业务模块包括：
  - `projectApi`
  - `episodeApi`
  - `characterApi`
  - `sceneApi`
  - `storyboardApi`
  - `videoTaskApi`
  - `modelInstanceApi`
  - `modelDefinitionApi`

## 8. 关键交互说明

### 8.1 视频任务状态回传

- 提交视频任务后，前端通过 WebSocket 接收后端状态推送
- 任务完成后根据返回字段（如 `videoUrl`）更新对应卡片与预览

### 8.2 搜索

- 工作台：项目搜索 + suggest
- 资产管理：角色/场景搜索 + suggest
- 分镜步骤：角色/场景关联搜索

## 9. Docker 运行

前端镜像已在根目录 Compose 集成，推荐从仓库根目录启动：

```bash
docker compose up -d --build
```

单独构建前端镜像：

```bash
docker build -t yihen-drama-frontend -f Dockerfile .
```

## 10. 常见问题

### 10.1 页面出现旧资源

- 清理浏览器缓存
- 或重新构建前端镜像：`docker compose build --no-cache frontend`

### 10.2 动态路由组件加载失败

- 通常是 Vue 文件语法错误（标签未闭合）或编码异常
- 先执行 `npm run build` 定位具体文件行号

### 10.3 API 请求 404/跨域

- 检查 `VITE_API_BASE_URL`
- 检查后端容器和端口是否就绪（`docker compose ps`）

