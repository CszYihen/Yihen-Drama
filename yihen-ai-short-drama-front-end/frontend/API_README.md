# 前端API对接说明

## 目录结构

```
src/
├── api/
│   └── index.js              # 所有API接口定义
├── composables/
│   ├── useModelConfig.js     # 模型配置组合式函数（含API调用）
│   └── useToast.js           # Toast提示组合式函数
├── stores/
│   ├── global.js             # 全局状态管理
│   └── user.js               # 用户认证状态管理
└── views/
    ├── LoginView.vue         # 登录页（对接Auth API）
    ├── RegisterView.vue      # 注册页
    ├── DashboardView.vue     # 工作台（对接Project API）
    ├── SettingsView.vue      # 设置页（对接Model API）
    └── WorkspaceView.vue     # 创作空间（对接Episode/Storyboard/Video API）
```

## 使用方式

### 1. 用户认证

```javascript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 登录
const result = await userStore.login(username, password)
if (result.success) {
  router.push('/')
}

// 注册
await userStore.register(username, password, email)

// 检查登录状态
if (userStore.isLoggedIn) {
  // 已登录
}
```

### 2. 直接调用API

```javascript
import { ModelInstanceApi, ProjectApi, EpisodeApi } from '@/api'

// 获取模型实例列表
const response = await ModelInstanceApi.list({ type: 'text' })

// 获取项目列表
const projects = await ProjectApi.list({ status: 'processing' })

// 创建章节
await EpisodeApi.create(projectId, { title: '新章节' })
```

### 3. Toast提示

```javascript
import { useToast } from '@/composables/useToast'

const { success, error, warning } = useToast()

success('操作成功')
error('操作失败')
warning('请注意')
```

## API接口列表

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | `/auth/login` | POST | 用户登录 |
| 认证 | `/auth/register` | POST | 用户注册 |
| 认证 | `/auth/logout` | POST | 退出登录 |
| 用户 | `/users/me` | GET | 获取当前用户 |
| 项目 | `/projects` | GET/POST | 项目列表/创建 |
| 项目 | `/projects/{id}` | GET/PUT/DELETE | 项目详情/更新/删除 |
| 章节 | `/projects/{id}/episodes` | GET/POST | 章节列表/创建 |
| 章节 | `/episodes/{id}` | GET/PUT/DELETE | 章节详情/更新/删除 |
| 分镜 | `/episodes/{id}/storyboard` | GET/PUT/POST | 分镜脚本 |
| 分镜 | `/episodes/{id}/storyboard/generate` | POST | 生成分镜 |
| 视频 | `/video-tasks` | POST | 提交任务 |
| 视频 | `/video-tasks/{id}/status` | GET | 任务状态 |
| 视频 | `/video-tasks/{id}/progress` | GET | 生成进度 |
| 视频 | `/video-tasks/{id}/cancel` | POST | 取消任务 |
| 视频 | `/video-tasks/{id}/retry` | POST | 重试任务 |
| 模型 | `/model-instances` | GET/POST | 模型实例列表/创建 |
| 模型 | `/model-instances/{id}` | PUT/DELETE | 更新/删除实例 |
| 模型 | `/model-instances/{id}/test` | POST | 测试连接 |
| 模型 | `/model-instances/{id}/default` | PUT | 设为默认 |
| 文件 | `/files/upload` | POST | 上传文件 |

## 环境配置

1. 复制 `.env.example` 为 `.env`
2. 修改 `VITE_API_BASE_URL` 为实际后端地址
3. 开发环境: `npm run dev`
4. 生产环境: `npm run build`

## 路由守卫

系统会自动检查用户登录状态：

- 访问 `/login` 和 `/register` 时，如果已登录会自动跳转到首页
- 访问其他页面时，如果未登录会跳转到登录页

## 响应格式

系统期望后端返回统一格式：

```json
{
  "code": 200,
  "data": { ... },
  "message": "success"
}
```

## 常见问题

### 1. 401未授权
系统会自动清除token并跳转到登录页

### 2. 请求失败
检查后端服务是否启动，以及CORS配置

### 3. 使用模拟数据
在视图组件中已有mock数据回退机制，API调用失败时会自动使用模拟数据
