import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { title: '工作台' }
  },
  {
    path: '/workspace/:id',
    name: 'Workspace',
    component: () => import('@/views/WorkspaceView.vue'),
    meta: { title: '创作空间' }
  },
  {
    path: '/prompts',
    name: 'Prompts',
    component: () => import('@/views/PromptView.vue'),
    meta: { title: '提示词管理' }
  },
  {
    path: '/tasks',
    name: 'TaskManager',
    component: () => import('@/views/TaskManagerView.vue'),
    meta: { title: '任务管理' }
  },
  {
    path: '/assets',
    name: 'Assets',
    component: () => import('@/views/AssetManageView.vue'),
    meta: { title: '资产管理' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsView.vue'),
    meta: { title: '模型管理' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'AI短剧生成'} - AI短剧生成系统`
  next()
})

export default router
