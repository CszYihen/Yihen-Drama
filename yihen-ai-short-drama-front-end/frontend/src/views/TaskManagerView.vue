<template>
  <div class="task-manager">
    <header class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="title-h1">任务管理</h1>
          <p class="header-subtitle">查看所有项目的视频生成任务</p>
        </div>
      </div>
    </header>
    
    <div class="page-content">
      <!-- 项目选择 -->
      <div class="project-selector">
        <label class="form-label">选择项目</label>
        <select class="input" v-model="selectedProjectId" @change="loadProjectTasks">
          <option value="">全部项目</option>
          <option v-for="project in projects" :key="project.id" :value="project.id">
            {{ project.name }}
          </option>
        </select>
      </div>
      
      <!-- 任务列表 -->
      <div class="tasks-section">
        <div class="section-header">
          <h2 class="title-h3">
            {{ selectedProjectId ? '项目任务' : '所有任务' }}
            <span class="task-count">({{ tasks.length }})</span>
          </h2>
        </div>
        
        <div class="tasks-grid" v-if="tasks.length > 0">
          <div 
            v-for="task in tasks" 
            :key="task.id"
            class="task-card card"
          >
            <div class="task-header">
              <span class="task-id">{{ task.taskId }}</span>
              <span class="task-status" :class="getStatusClass(task.status)">
                {{ getStatusText(task.status) }}
              </span>
            </div>
            
            <div class="task-info">
              <div class="info-row">
                <span class="info-label">所属项目:</span>
                <span class="info-value">{{ getProjectName(task.projectId) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">使用模型:</span>
                <span class="info-value">{{ getInstanceName(task.instanceId) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">任务类型:</span>
                <span class="info-value">{{ getTaskTypeText(task.taskType) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">创建时间:</span>
                <span class="info-value">{{ formatDate(task.createTime) }}</span>
              </div>
            </div>
            
            <!-- 已完成的视频 -->
            <div class="task-video" v-if="task.videoUrl && isSuccessStatus(task.status)">
              <video :src="task.videoUrl" controls class="video-player"></video>
            </div>
            
            <!-- 失败信息 -->
            <div class="task-error" v-else-if="isFailedStatus(task.status)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="15" y1="9" x2="9" y2="15"/>
                <line x1="9" y1="9" x2="15" y2="15"/>
              </svg>
              <span>{{ task.errorMessage || '视频生成失败' }}</span>
            </div>
            
            <!-- 处理中状态 -->
            <div class="task-processing" v-else>
              <svg class="spinner" viewBox="0 0 24 24" width="20" height="20">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                </circle>
              </svg>
              <span>{{ isPendingStatus(task.status) ? '等待中...' : '处理中...' }}</span>
            </div>
            
            <!-- 操作按钮 -->
            <div class="task-actions" v-if="task.videoUrl && isSuccessStatus(task.status)">
              <a :href="task.videoUrl" download class="btn btn-secondary btn-sm">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="7 10 12 15 17 10"/>
                  <line x1="12" y1="15" x2="12" y2="3"/>
                </svg>
                下载视频
              </a>
            </div>
          </div>
        </div>
        
        <div class="empty-state" v-else>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="width: 48px; height: 48px; margin-bottom: 16px;">
            <polygon points="5 3 19 12 5 21 5 3"/>
          </svg>
          <p>暂无任务数据</p>
          <p style="font-size: 12px; color: var(--text-secondary);">
            {{ selectedProjectId ? '该项目暂无视频任务' : '请先选择或创建项目' }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { videoTaskApi, projectApi, modelInstanceApi } from '@/api'

const projects = ref([])
const tasks = ref([])
const selectedProjectId = ref('')
const projectMap = ref({}) // 项目ID -> 项目名称映射
const instanceMap = ref({}) // 实例ID -> 实例名称映射

// 任务类型映射 (对应 TaskType 枚举)
const taskTypeMap = {
  'CHARACTER_VIDEO_GENERATION': '角色视频生成',
  'character_video_generation': '角色视频生成',
  1: '角色视频生成',
  'SHOT_VIDEO_GENERATION': '分镜视频生成',
  'shot_video_generation': '分镜视频生成',
  2: '分镜视频生成',
  'COMPOSITING': '音视频合成',
  'compositing': '音视频合成',
  3: '音视频合成'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const getTaskTypeText = (taskType) => {
  return taskTypeMap[taskType] || taskType || '未知类型'
}

const getStatusText = (status) => {
  if (status === 'succeeded' || status === 'success' || status === 'succeed' || status === 1) return '已完成'
  if (status === 'failed' || status === 3) return '失败'
  if (status === 0 || status === '0' || status === 'pending') return '等待中'
  if (status === 'processing' || status === 'running') return '处理中'
  return '未知'
}

const getStatusClass = (status) => {
  if (status === 'succeeded' || status === 'success' || status === 'succeed' || status === 1) return 'success'
  if (status === 'failed' || status === 3) return 'failed'
  return 'processing'
}

const isSuccessStatus = (status) => {
  return status === 'succeeded' || status === 'success' || status === 'succeed' || status === 1
}

const isFailedStatus = (status) => {
  return status === 'failed' || status === 3
}

const isPendingStatus = (status) => {
  return status === 0 || status === '0' || status === 'pending'
}

const getProjectName = (projectId) => {
  return projectMap.value[projectId] || `项目-${projectId}`
}

const getInstanceName = (instanceId) => {
  return instanceMap.value[instanceId] || `实例-${instanceId}`
}

const loadProjects = async () => {
  try {
    const result = await projectApi.list()
    if (result.code === 200 && result.data) {
      projects.value = result.data
      // 构建项目ID到名称的映射
      result.data.forEach(p => {
        projectMap.value[p.id] = p.name
      })
    }
  } catch (err) {
    console.error('加载项目列表失败:', err)
  }
}

const loadInstanceNames = async (instanceIds) => {
  const uniqueIds = [...new Set(instanceIds.filter(id => id && !instanceMap.value[id]))]
  for (const id of uniqueIds) {
    try {
      const result = await modelInstanceApi.get(id)
      if (result.code === 200 && result.data) {
        instanceMap.value[id] = result.data.instanceName || result.data.modelCode || `实例-${id}`
      }
    } catch (err) {
      console.error(`加载实例 ${id} 信息失败:`, err)
    }
  }
}

const loadProjectTasks = async () => {
  tasks.value = []
  try {
    if (selectedProjectId.value) {
      const result = await videoTaskApi.getByProject(Number(selectedProjectId.value))
      if (result.code === 200 && result.data) {
        tasks.value = result.data
        // 加载实例名称
        const instanceIds = result.data.map(t => t.instanceId).filter(Boolean)
        await loadInstanceNames(instanceIds)
      }
    } else {
      // 加载所有项目最近的任务
      for (const project of projects.value) {
        try {
          const result = await videoTaskApi.getByProject(project.id)
          if (result.code === 200 && result.data) {
            tasks.value.push(...result.data)
          }
        } catch (err) {
          console.error(`加载项目 ${project.id} 任务失败:`, err)
        }
      }
      // 按创建时间倒序
      tasks.value.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      // 加载所有实例名称
      const instanceIds = tasks.value.map(t => t.instanceId).filter(Boolean)
      await loadInstanceNames(instanceIds)
    }
  } catch (err) {
    console.error('加载任务列表失败:', err)
  }
}

onMounted(async () => {
  await loadProjects()
  await loadProjectTasks()
})
</script>

<style lang="scss" scoped>
.task-manager {
  padding: 32px 40px;
}

.page-header {
  margin-bottom: 32px;
}

.header-subtitle {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-top: 8px;
}

.project-selector {
  margin-bottom: 32px;
  max-width: 300px;
  
  select {
    width: 100%;
    cursor: pointer;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.task-count {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-tertiary);
}

.tasks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.task-card {
  padding: 20px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.task-id {
  font-family: monospace;
  font-size: 12px;
  color: var(--text-secondary);
}

.task-status {
  padding: 4px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 500;
  
  &.success {
    background: rgba(16, 185, 129, 0.15);
    color: var(--success);
  }
  
  &.failed {
    background: rgba(239, 68, 68, 0.15);
    color: var(--error);
  }
  
  &.processing {
    background: rgba(245, 158, 11, 0.15);
    color: var(--warning);
  }
}

.task-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  gap: 8px;
  font-size: 13px;
}

.info-label {
  color: var(--text-tertiary);
  min-width: 60px;
}

.info-value {
  color: var(--text-secondary);
}

.task-video {
  margin-bottom: 16px;
}

.video-player {
  width: 100%;
  max-height: 200px;
  object-fit: contain;
  border-radius: var(--radius-sm);
  background: rgba(0, 0, 0, 0.5);
}

.task-error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: rgba(239, 68, 68, 0.1);
  border-radius: var(--radius-sm);
  color: var(--error);
  font-size: 13px;
  margin-bottom: 16px;
  
  svg {
    width: 18px;
    height: 18px;
    flex-shrink: 0;
  }
}

.task-processing {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: rgba(245, 158, 11, 0.1);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 16px;
  
  .spinner {
    color: var(--gold-primary);
  }
}

.task-actions {
  display: flex;
  gap: 8px;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: var(--radius-sm);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-tertiary);
}
</style>
