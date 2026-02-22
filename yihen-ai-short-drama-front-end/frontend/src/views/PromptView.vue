<template>
  <div class="prompt-page">
    <header class="page-header">
      <h1 class="title-h1">提示词管理</h1>
      <p class="page-desc">管理各场景的AI提示词模板</p>
    </header>
    
    <div class="prompt-content">
      <div class="prompt-header">
        <span class="prompt-count">共 {{ prompts.length }} 个提示词</span>
        <button class="btn btn-primary" @click="handleCreateNew">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          新建提示词
        </button>
      </div>
      
      <div v-if="prompts.length > 0" class="prompt-sections">
        <div v-for="group in groupedPrompts" :key="group.key" class="prompt-section">
          <div class="section-header">
            <h3 class="section-title">
              <span class="scene-icon" :class="getSceneIconClass(group.code)">
                <svg v-if="group.code === 1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14.5 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V7.5L14.5 2z"/>
                  <polyline points="14 2 14 8 20 8"/>
                  <line x1="16" y1="13" x2="8" y2="13"/>
                  <line x1="16" y1="17" x2="8" y2="17"/>
                </svg>
                <svg v-else-if="group.code === 2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8"/>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                  <line x1="11" y1="8" x2="11" y2="14"/>
                  <line x1="8" y1="11" x2="14" y2="11"/>
                </svg>
                <svg v-else-if="group.code === 3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                  <circle cx="8.5" cy="8.5" r="1.5"/>
                  <polyline points="21 15 16 10 5 21"/>
                </svg>
                <svg v-else-if="group.code === 4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                  <line x1="8" y1="21" x2="16" y2="21"/>
                  <line x1="12" y1="17" x2="12" y2="21"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="23 7 16 12 23 17 23 7"/>
                  <rect x="1" y="5" width="15" height="14" rx="2" ry="2"/>
                </svg>
              </span>
              {{ group.name }}
            </h3>
            <span class="section-count">{{ group.prompts.length }}个模板</span>
          </div>
          
          <div class="prompt-grid">
            <div 
              v-for="prompt in group.prompts" 
              :key="prompt.id"
              class="prompt-card"
              :class="{ 'is-default': isDefaultPrompt(prompt.id, group.code) }"
            >
              <div class="prompt-card-header">
                <div class="prompt-info">
                  <span class="prompt-name">{{ prompt.promptName }}</span>
                  <span v-if="isDefaultPrompt(prompt.id, group.code)" class="default-badge">默认</span>
                </div>
                <span class="prompt-code">{{ prompt.promptCode }}</span>
              </div>
              
              <div class="prompt-content-preview">
                {{ truncateContent(prompt.promptContent) }}
              </div>
              
              <div class="prompt-actions">
                <button class="action-btn" @click="handleEdit(prompt)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                  编辑
                </button>
                <button 
                  v-if="!isDefaultPrompt(prompt.id, group.code)"
                  class="action-btn primary"
                  @click="handleSetDefault(prompt)"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                  设为默认
                </button>
                <button class="action-btn danger" @click="handleDelete(prompt.id)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                  </svg>
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="empty-state" v-else>
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M14.5 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V7.5L14.5 2z"/>
            <polyline points="14 2 14 8 20 8"/>
          </svg>
        </div>
        <p>暂无提示词</p>
        <span>创建第一个提示词开始使用</span>
      </div>
    </div>
    
    <!-- 新建/编辑弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
          <div class="modal prompt-modal">
            <div class="modal-header">
              <h3>{{ isEditing ? '编辑提示词' : '新建提示词' }}</h3>
              <button class="modal-close" @click="closeModal">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label>提示词编码</label>
                <input 
                  type="text" 
                  class="config-input" 
                  v-model="currentConfig.promptCode" 
                  placeholder="如: INFO_EXTRACT_V1"
                />
              </div>
              <div class="form-group">
                <label>提示词名称</label>
                <input 
                  type="text" 
                  class="config-input" 
                  v-model="currentConfig.promptName" 
                  placeholder="如: 信息提取模板"
                />
              </div>
              <div class="form-group">
                <label>场景</label>
                <select class="config-input" v-model="currentConfig.sceneCode">
                  <option :value="null">请选择场景</option>
                  <option v-for="scene in sceneOptions" :key="scene.code" :value="scene.code">
                    {{ scene.label }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>提示词内容</label>
                <textarea 
                  class="config-input content-area" 
                  v-model="currentConfig.promptContent" 
                  placeholder="支持 {{变量}} 占位符"
                  rows="6"
                ></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="closeModal">取消</button>
              <button 
                class="btn btn-primary" 
                @click="handleSave" 
                :disabled="!currentConfig.promptCode || !currentConfig.promptName || currentConfig.sceneCode === null || saving"
              >
                {{ saving ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
    
    <!-- 结果弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showResult" class="modal-overlay" @click.self="showResult = false">
          <div class="modal result-modal">
            <div class="result-content">
              <div class="result-icon" :class="result.success ? 'success' : 'error'">
                <svg v-if="result.success" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </div>
              <div class="result-text">
                <h3>{{ result.success ? '操作成功' : '操作失败' }}</h3>
                <p>{{ result.message }}</p>
              </div>
            </div>
            <button class="btn btn-primary btn-block" @click="showResult = false">确定</button>
          </div>
        </div>
      </transition>

      <ConfirmDialog
        v-model="showDeleteConfirm"
        title="删除提示词"
        :message="`确定要删除「${deleteTargetName}」吗？此操作不可恢复。`"
        confirm-text="删除"
        cancel-text="取消"
        @confirm="confirmDelete"
      />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { promptApi } from '@/api/prompt'
import { useGlobalStore } from '@/stores/global'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const globalStore = useGlobalStore()

const prompts = ref([])
const showModal = ref(false)
const showResult = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const result = ref({ success: false, message: '' })

const showDeleteConfirm = ref(false)
const deleteTargetId = ref(null)
const deleteTargetName = ref('')

const currentConfig = reactive({
  id: null,
  promptCode: '',
  promptName: '',
  sceneCode: null,
  promptContent: ''
})

const defaultPrompts = ref({})

const sceneOptions = [
  { code: 1, label: '场景生成', key: 'SCENE_GEN' },
  { code: 2, label: '信息提取', key: 'INFO_EXTRACT' },
  { code: 3, label: '角色生成', key: 'CHARACTER_GEN' },
  { code: 4, label: '分镜生成', key: 'STORYBOARD_GEN' },
  { code: 5, label: '视频生成', key: 'VIDEO_GEN' },
  { code: 6, label: '音频生成', key: 'AUDIO_GEN' },
  { code: 7, label: '首帧生成', key: 'FIRST_FRAME_GEN' }
]

const sceneLabels = {
  1: '场景生成',
  2: '信息提取',
  3: '角色生成',
  4: '分镜生成',
  5: '视频生成',
  6: '音频生成',
  7: '首帧生成'
}

const sceneCodeToNumber = {
  SCENE_GEN: 1,
  INFO_EXTRACT: 2,
  CHARACTER_GEN: 3,
  STORYBOARD_GEN: 4,
  VIDEO_GEN: 5,
  AUDIO_GEN: 6,
  FIRST_FRAME_GEN: 7
}

const sceneNumberToKey = {
  1: 'SCENE_GEN',
  2: 'INFO_EXTRACT',
  3: 'CHARACTER_GEN',
  4: 'STORYBOARD_GEN',
  5: 'VIDEO_GEN',
  6: 'AUDIO_GEN',
  7: 'FIRST_FRAME_GEN'
}

const validSceneCodes = [1, 2, 3, 4, 5, 6, 7]

const getSceneName = (sceneCode) => {
  if (!sceneCode) return null
  const code = typeof sceneCode === 'string' ? (sceneCodeToNumber[sceneCode] || parseInt(sceneCode, 10) || null) : sceneCode
  const numCode = typeof code === 'number' ? code : parseInt(code, 10)
  if (isNaN(numCode) || !validSceneCodes.includes(numCode)) return null
  return sceneLabels[numCode]
}

const getSceneCodeNumber = (sceneCode) => {
  if (!sceneCode) return null
  if (typeof sceneCode === 'string') {
    return sceneCodeToNumber[sceneCode] || parseInt(sceneCode, 10) || null
  }
  return sceneCode
}

const getSceneKey = (sceneCode) => {
  const num = getSceneCodeNumber(sceneCode)
  return sceneNumberToKey[num] || sceneCode
}

const getSceneIconClass = (code) => {
  const classes = {
    1: 'text',
    2: 'extract',
    3: 'image',
    4: 'storyboard',
    5: 'video'
  }
  return classes[code] || ''
}

const isDefaultPrompt = (promptId, sceneCode) => {
  const key = getSceneKey(sceneCode)
  return defaultPrompts.value[key] === promptId
}

const groupedPrompts = computed(() => {
  const groups = {}
  prompts.value.forEach(prompt => {
    const sceneCodeNum = getSceneCodeNumber(prompt.sceneCode)
    const sceneName = getSceneName(prompt.sceneCode)
    
    if (!sceneCodeNum || !sceneName) {
      return
    }
    
    const key = `${sceneCodeNum}-${sceneName}`
    if (!groups[key]) {
      groups[key] = { 
        key, 
        name: sceneName, 
        code: sceneCodeNum, 
        prompts: [] 
      }
    }
    groups[key].prompts.push(prompt)
  })
  return groups
})

const loadDefaultPrompts = async () => {
  try {
    const defaults = {}
    for (const scene of sceneOptions) {
      try {
        const res = await promptApi.getDefault(scene.key)
        if (res.code === 200 && res.data) {
          defaults[scene.key] = res.data.id
        }
      } catch (e) {
        console.warn(`获取默认提示词失败: ${scene.key}`)
      }
    }
    defaultPrompts.value = defaults
  } catch (err) {
    console.error('加载默认提示词失败:', err)
  }
}

const loadPrompts = async () => {
  globalStore.setLoading(true, '加载中...')
  try {
    const res = await promptApi.listAll()
    if (res.code === 200 && res.data) {
      prompts.value = res.data
    }
  } catch (err) {
    console.error('加载提示词失败:', err)
    result.value = { success: false, message: err.message || '加载失败' }
    showResult.value = true
  } finally {
    globalStore.setLoading(false)
  }
}

const truncateContent = (content) => {
  if (!content) return ''
  return content.length > 80 ? content.substring(0, 80) + '...' : content
}

const handleCreateNew = () => {
  currentConfig.id = null
  currentConfig.promptCode = ''
  currentConfig.promptName = ''
  currentConfig.sceneCode = null
  currentConfig.promptContent = ''
  isEditing.value = false
  showModal.value = true
}

const handleEdit = (prompt) => {
  currentConfig.id = prompt.id
  currentConfig.promptCode = prompt.promptCode
  currentConfig.promptName = prompt.promptName
  currentConfig.sceneCode = getSceneCodeNumber(prompt.sceneCode)
  currentConfig.promptContent = prompt.promptContent
  isEditing.value = true
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  isEditing.value = false
}

const handleSave = async () => {
  if (!currentConfig.promptCode.trim() || !currentConfig.promptName.trim()) {
    result.value = { success: false, message: '请填写编码和名称' }
    showResult.value = true
    return
  }
  
  saving.value = true
  try {
    const data = {
      promptCode: currentConfig.promptCode,
      promptName: currentConfig.promptName,
      sceneCode: currentConfig.sceneCode,
      promptContent: currentConfig.promptContent
    }
    
    if (isEditing.value) {
      data.id = currentConfig.id
      await promptApi.update(data)
    } else {
      await promptApi.create(data)
    }
    
    await loadPrompts()
    await loadDefaultPrompts()
    closeModal()
    result.value = { success: true, message: isEditing.value ? '更新成功' : '创建成功' }
    showResult.value = true
  } catch (err) {
    console.error('保存提示词失败:', err)
    result.value = { success: false, message: err.message || '操作失败' }
    showResult.value = true
  } finally {
    saving.value = false
  }
}

const handleSetDefault = async (prompt) => {
  const sceneKey = getSceneKey(prompt.sceneCode)
  
  globalStore.setLoading(true, '设置中...')
  try {
    await promptApi.setDefault(prompt.id, sceneKey)
    defaultPrompts.value[sceneKey] = prompt.id
    result.value = { success: true, message: '已设为默认模板' }
    showResult.value = true
  } catch (err) {
    console.error('设置默认失败:', err)
    result.value = { success: false, message: err.message || '设置失败' }
    showResult.value = true
  } finally {
    globalStore.setLoading(false)
  }
}

const handleDelete = (id) => {
  const prompt = prompts.value.find(p => p.id === id)
  if (prompt) {
    deleteTargetId.value = id
    deleteTargetName.value = prompt.promptName
    showDeleteConfirm.value = true
  }
}

const confirmDelete = async () => {
  const id = deleteTargetId.value
  if (!id) return

  globalStore.setLoading(true, '删除中...')
  try {
    await promptApi.delete(id)
    await loadPrompts()
    await loadDefaultPrompts()
    result.value = { success: true, message: '删除成功' }
    showResult.value = true
  } catch (err) {
    console.error('删除提示词失败:', err)
    result.value = { success: false, message: err.message || '删除失败' }
    showResult.value = true
  } finally {
    globalStore.setLoading(false)
    deleteTargetId.value = null
    deleteTargetName.value = ''
  }
}

onMounted(async () => {
  await loadPrompts()
  await loadDefaultPrompts()
})
</script>

<style lang="scss" scoped>
.prompt-page {
  padding: 32px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header { margin-bottom: 32px; }
.page-desc { font-size: 14px; color: var(--text-tertiary); margin-top: 8px; }

.prompt-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.prompt-count { font-size: 13px; color: var(--text-tertiary); }

.prompt-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.prompt-section {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  
  .scene-icon {
    width: 28px;
    height: 28px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    svg { width: 16px; height: 16px; }
    
    &.text { background: rgba(139, 92, 246, 0.15); color: #8B5CF6; }
    &.extract { background: rgba(16, 185, 129, 0.15); color: var(--success); }
    &.image { background: rgba(6, 182, 212, 0.15); color: var(--accent); }
    &.storyboard { background: rgba(245, 158, 11, 0.15); color: var(--warning); }
    &.video { background: rgba(239, 68, 68, 0.15); color: var(--error); }
  }
}

.section-count {
  font-size: 12px;
  color: var(--text-muted);
  background: var(--bg-tertiary);
  padding: 4px 10px;
  border-radius: 4px;
}

.prompt-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.prompt-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 16px;
  display: flex;
  flex-direction: column;
  transition: all var(--transition-base);
  
  &:hover {
    border-color: var(--gold-primary);
    box-shadow: 0 0 15px rgba(212, 175, 55, 0.1);
  }
  
  &.is-default {
    border-color: rgba(212, 175, 55, 0.3);
    background: linear-gradient(135deg, rgba(212, 175, 55, 0.05) 0%, transparent 100%);
  }
}

.prompt-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.prompt-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.prompt-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.default-badge {
  padding: 2px 6px;
  font-size: 10px;
  background: var(--gold-primary);
  color: #1A1A1E;
  border-radius: 4px;
  font-weight: 600;
  flex-shrink: 0;
}

.prompt-code {
  font-size: 11px;
  color: var(--text-muted);
  padding: 3px 6px;
  background: var(--bg-tertiary);
  border-radius: 4px;
  flex-shrink: 0;
}

.prompt-content-preview {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.6;
  padding: 12px;
  background: var(--bg-tertiary);
  border-radius: 4px;
  margin-bottom: 12px;
  flex: 1;
  min-height: 50px;
  max-height: 70px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.prompt-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 8px 10px;
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  cursor: pointer;
  transition: all var(--transition-base);
  
  svg { width: 12px; height: 12px; }
  
  &:hover {
    background: var(--bg-glass);
    color: var(--text-primary);
    border-color: var(--border-hover);
  }
  
  &.primary {
    background: rgba(212, 175, 55, 0.1);
    border-color: rgba(212, 175, 55, 0.3);
    color: var(--gold-light);
    
    &:hover {
      background: rgba(212, 175, 55, 0.2);
      border-color: var(--gold-primary);
    }
  }
  
  &.danger:hover {
    color: var(--error);
    border-color: var(--error);
    background: rgba(239, 68, 68, 0.1);
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}
.empty-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  svg { width: 100%; height: 100%; color: var(--gold-muted); }
}
.empty-state p { font-size: 15px; font-weight: 500; margin-bottom: 4px; }
.empty-state span { font-size: 13px; color: var(--text-muted); }

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 24px;
  font-size: 14px;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-base);
  svg { width: 16px; height: 16px; }
  &.btn-block { width: 100%; }
}
.btn-primary {
  background: var(--gold-gradient);
  color: #1A1A1E;
  border: 1px solid var(--gold-primary);
  &:hover { box-shadow: 0 0 15px rgba(212, 175, 55, 0.3); }
}
.btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  &:hover { background: var(--bg-glass); color: var(--text-primary); }
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  will-change: opacity;
}

.prompt-modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  will-change: transform, opacity;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  position: relative;
  h3 { font-size: 16px; font-weight: 600; }
}

.modal-close {
  position: absolute;
  right: 12px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  svg { width: 16px; height: 16px; color: var(--text-muted); }
  &:hover { background: var(--bg-tertiary); svg { color: var(--text-primary); } }
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
  &:last-child { margin-bottom: 0; }
  label {
    display: block;
    font-size: 13px;
    color: var(--text-secondary);
    margin-bottom: 8px;
  }
}

.config-input {
  width: 100%;
  padding: 10px 14px;
  font-size: 13px;
  color: var(--text-primary);
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  outline: none;
  &:focus { border-color: var(--gold-primary); box-shadow: 0 0 0 3px rgba(212, 175, 55, 0.15); }
}

.content-area {
  resize: vertical;
  min-height: 120px;
  font-family: inherit;
  line-height: 1.6;
}

.modal-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
}

.result-modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 24px 32px;
  min-width: 320px;
  max-width: 420px;
  will-change: transform, opacity;
}

.result-content { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.result-icon { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; svg { width: 24px; height: 24px; } &.success { background: rgba(16, 185, 129, 0.15); svg { color: var(--success); } } &.error { background: rgba(239, 68, 68, 0.15); svg { color: var(--error); } } }
.result-text { text-align: left; flex: 1; min-width: 0; h3 { font-size: 16px; font-weight: 600; margin-bottom: 4px; } p { font-size: 14px; color: var(--text-tertiary); line-height: 1.5; word-break: break-all; } }
.btn-block { width: 100%; padding: 10px; }

.modal-enter-active {
  transition: opacity 0.25s ease;
  
  .prompt-modal, .result-modal {
    transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.25s ease;
  }
}

.modal-leave-active {
  transition: opacity 0.2s ease;
  
  .prompt-modal, .result-modal {
    transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.2s ease;
  }
}

.modal-enter-from {
  opacity: 0;
  
  .prompt-modal, .result-modal {
    transform: scale(0.92) translateY(-10px);
    opacity: 0;
  }
}

.modal-leave-to {
  opacity: 0;
  
  .prompt-modal, .result-modal {
    transform: scale(0.95) translateY(5px);
    opacity: 0;
  }
}
</style>
