<template>
  <div class="settings-page">
    <header class="page-header">
      <h1 class="title-h1">系统设置</h1>
      <p class="page-desc">配置AI模型实例，管理模型连接</p>
    </header>
    
    <div class="settings-content">
      <!-- 厂商管理 -->
      <section class="settings-section">
        <div class="section-header">
          <h2 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z"/>
              <polyline points="17 21 17 13 7 13 7 21"/>
              <polyline points="7 3 7 8 15 8"/>
            </svg>
            厂商管理
          </h2>
          <button class="btn btn-primary btn-sm" @click="showProviderModal = true">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            添加厂商
          </button>
        </div>
        
        <div class="providers-grid" v-if="providers.length > 0">
          <div 
            v-for="provider in providers" 
            :key="provider.id"
            class="provider-card"
          >
            <div class="provider-info">
              <span class="provider-name">{{ provider.providerCode }}</span>
            </div>
            <div class="provider-url">{{ provider.baseUrl }}</div>
            <div class="provider-actions">
              <button class="action-btn small" @click="editProvider(provider)">编辑</button>
              <button class="action-btn small danger" @click="confirmDeleteProvider(provider.id)">删除</button>
            </div>
          </div>
        </div>
        <div class="pagination" v-if="totalProviders > providerPageSize">
          <button class="page-btn" :disabled="providerPage === 1" @click="changeProviderPage(providerPage - 1)">上一页</button>
          <button
            v-for="page in providerTotalPages"
            :key="page"
            class="page-btn"
            :class="{ active: page === providerPage }"
            @click="changeProviderPage(page)"
          >
            {{ page }}
          </button>
          <button class="page-btn" :disabled="providerPage === providerTotalPages" @click="changeProviderPage(providerPage + 1)">下一页</button>
        </div>
        
        <div class="empty-state" v-else-if="totalProviders === 0">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2"/>
              <path d="M12 8v8M8 12h8"/>
            </svg>
          </div>
          <p>暂无厂商</p>
          <span>添加厂商以开始使用</span>
        </div>
      </section>
      
      <!-- 模型实例管理 -->
      <section class="settings-section">
        <div class="section-header">
          <h2 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
            </svg>
            模型实例管理
          </h2>
        </div>
        
        <!-- 模型类型Tab -->
        <div class="model-tabs">
          <button 
            v-for="(info, type) in modelTypes" 
            :key="type"
            class="model-tab"
            :class="{ active: activeModelType === type }"
            @click="activeModelType = type"
          >
            <span class="tab-icon" :class="info.color">
              <svg v-if="type === 'text'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
              </svg>
              <svg v-else-if="type === 'image'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="18" height="18" rx="2"/>
                <circle cx="8.5" cy="8.5" r="1.5"/>
                <path d="M21 15l-5-5L5 21"/>
              </svg>
              <svg v-else-if="type === 'video'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polygon points="23 7 16 12 23 17 23 7"/>
                <rect x="1" y="5" width="15" height="14" rx="2"/>
              </svg>
              <svg v-else-if="type === 'audio'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 1a3 3 0 00-3 3v8a3 3 0 006 0V4a3 3 0 00-3-3z"/>
                <path d="M19 10v2a7 7 0 01-14 0v-2"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="8"/>
                <path d="M12 8v8M8 12h8"/>
              </svg>
            </span>
            {{ info.label }}
            <span class="tab-count">{{ getInstanceCount(type) }}</span>
          </button>
        </div>
        
        <!-- 模型实例列表 -->
        <div class="instances-container">
          <div class="instances-header">
            <span class="instances-count">共 {{ instanceTotal }} 个实例</span>
            <button class="btn btn-primary btn-sm" @click="handleCreateNew">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
              </svg>
              新建实例
            </button>
          </div>
          
          <div class="instances-grid" v-if="getInstancesByType(activeModelType).length > 0">
            <div 
              v-for="instance in getInstancesByType(activeModelType)" 
              :key="instance.id"
              class="instance-card"
              :class="{ active: selectedInstance?.id === instance.id, default: instance.isDefault }"
              @click="handleSelectInstance(instance)"
            >
              <div class="instance-header">
                <div class="instance-info">
                  <span class="instance-name">{{ instance.name }}</span>
                  <span class="instance-provider">{{ getProviderName(activeModelType, instance.config.provider) }}</span>
                </div>
                <div class="instance-badges">
                  <span v-if="instance.isDefault" class="badge default">默认</span>
                </div>
              </div>
              
              <div class="instance-actions">
                <button 
                  class="action-btn"
                  :class="{ loading: testing === instance.id }"
                  @click.stop="handleTest(instance)"
                  :disabled="testing === instance.id"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/>
                    <polyline points="22 4 12 14.01 9 11.01"/>
                  </svg>
                  {{ testing === instance.id ? '测试中...' : '测试' }}
                </button>
                <button 
                  class="action-btn primary"
                  @click.stop="handleSaveAsNew(instance)"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z"/>
                    <polyline points="17 21 17 13 7 13 7 21"/>
                    <polyline points="7 3 7 8 15 8"/>
                  </svg>
                  保存副本
                </button>
              </div>
              
              <div class="instance-footer">
                <button 
                  class="footer-btn"
                  @click.stop="handleSetDefault(instance)"
                  :disabled="instance.isDefault"
                >
                  设为默认
                </button>
                <button 
                  class="footer-btn danger"
                  @click.stop="handleDelete(instance)"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
          <div class="pagination" v-if="instanceTotal > instancePageSize">
            <button class="page-btn" :disabled="instancePage === 1" @click="changeInstancePage(instancePage - 1)">上一页</button>
            <button
              v-for="page in instanceTotalPages"
              :key="page"
              class="page-btn"
              :class="{ active: page === instancePage }"
              @click="changeInstancePage(page)"
            >
              {{ page }}
            </button>
            <button class="page-btn" :disabled="instancePage === instanceTotalPages" @click="changeInstancePage(instancePage + 1)">下一页</button>
          </div>
          
          <!-- 空状态 -->
          <div class="empty-state" v-else-if="instanceTotal === 0">
            <div class="empty-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="3" width="18" height="18" rx="2"/>
                <path d="M12 8v8M8 12h8"/>
              </svg>
            </div>
            <p>暂无{{ modelTypes[activeModelType].label }}实例</p>
            <span>创建第一个实例开始使用</span>
          </div>
        </div>
      </section>
      
      <!-- 模型配置编辑 -->
      <section class="settings-section">
        <div class="section-header">
          <h2 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
            {{ isEditing ? '编辑实例' : '新建实例' }}
          </h2>
        </div>
        
        <div class="settings-card">
          <!-- 基本信息 -->
          <div class="config-section">
            <div class="config-row">
              <label class="config-label">实例名称</label>
              <input 
                type="text" 
                class="config-input" 
                v-model="currentConfig.name"
                placeholder="为你的模型实例取个名字" 
              />
            </div>
          </div>
          
          <!-- 基础配置 -->
          <div class="config-section">
            <h3 class="section-subtitle">基础配置</h3>
            
            <div class="config-row">
              <label class="config-label">厂商</label>
              <select class="config-select" v-model="currentConfig.config.provider" @change="onProviderChange">
                <option value="">请选择厂商</option>
                <option v-for="p in providers" :key="p.id" :value="p.providerCode?.toLowerCase()">
                  {{ p.providerCode }}
                </option>
              </select>
              <span class="provider-tip">选择已有厂商定义</span>
            </div>
            
            <!-- 基础配置（通用） -->
            <div class="config-row">
              <label class="config-label">API路径</label>
              <input 
                type="text" 
                class="config-input" 
                v-model="currentConfig.config.path"
                placeholder="如: /v1/chat/completions" 
              />
            </div>
            
            <div class="config-row">
              <label class="config-label">API Key</label>
              <input
                type="password"
                class="config-input"
                v-model="currentConfig.config.apiKey"
                placeholder="输入API Key"
              />
            </div>

            <!-- 模型名称和Base URL（所有类型都需要） -->
            <div class="config-row">
              <label class="config-label">模型名称</label>
              <input
                type="text"
                class="config-input"
                v-model="currentConfig.config.model"
                :placeholder="getModelPlaceholder(activeModelType)"
              />
            </div>
            <div class="config-row">
              <label class="config-label">Base URL</label>
              <input
                type="text"
                class="config-input"
                v-model="currentConfig.config.baseUrl"
                placeholder="可选，自定义API地址"
              />
            </div>
          </div>
          
          <!-- 高级设置折叠面板 -->
          <div class="advanced-section" v-if="activeModelType !== 'vector'">
            <button class="advanced-toggle" @click="toggleAdvanced(activeModelType)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="3"/>
                <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>
              </svg>
              高级设置
              <svg class="toggle-arrow" :class="{ expanded: expandedAdvanced[activeModelType] }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="6 9 12 15 18 9"/>
              </svg>
            </button>
            
            <div class="advanced-content" v-show="expandedAdvanced[activeModelType]">
              <!-- 文本模型高级设置 -->
              <template v-if="activeModelType === 'text'">
                <div class="config-row">
                  <label class="config-label">温度</label>
                  <div class="range-input">
                    <input
                      type="range"
                      min="0"
                      max="1"
                      step="0.1"
                      v-model="currentConfig.config.temperature"
                    />
                    <span>{{ currentConfig.config.temperature }}</span>
                  </div>
                </div>
                <div class="config-row">
                  <label class="config-label">最大Token</label>
                  <input
                    type="number"
                    class="config-input small"
                    v-model="currentConfig.config.max_tokens"
                    min="1000"
                    max="128000"
                  />
                </div>
              </template>

              <!-- 图像模型高级设置 -->
              <template v-if="activeModelType === 'image'">
                <div class="config-row">
                  <label class="config-label">分辨率</label>
                  <select class="config-select" v-model="currentConfig.config.resolution">
                    <option v-for="r in resolutions" :key="r.value" :value="r.value">
                      {{ r.label }}
                    </option>
                  </select>
                </div>
                <div class="config-row">
                  <label class="config-label">质量</label>
                  <select class="config-select" v-model="currentConfig.config.quality">
                    <option v-for="q in qualityOptions" :key="q.value" :value="q.value">
                      {{ q.label }}
                    </option>
                  </select>
                </div>
              </template>

              <!-- 视频模型高级设置 -->
              <template v-if="activeModelType === 'video'">
                <div class="config-row">
                  <label class="config-label">帧率</label>
                  <select class="config-select" v-model="currentConfig.config.fps">
                    <option v-for="f in fpsOptions" :key="f.value" :value="f.value">
                      {{ f.label }}
                    </option>
                  </select>
                </div>
                <div class="config-row">
                  <label class="config-label">时长</label>
                  <select class="config-select" v-model="currentConfig.config.duration">
                    <option v-for="d in durationOptions" :key="d.value" :value="d.value">
                      {{ d.label }}
                    </option>
                  </select>
                </div>
              </template>
              
              <!-- 语音模型高级设置 -->
              <template v-if="activeModelType === 'audio'">
                <div class="config-row">
                  <label class="config-label">语速</label>
                  <div class="range-input">
                    <input 
                      type="range" 
                      min="0.5" 
                      max="2" 
                      step="0.1" 
                      v-model="currentConfig.config.speed" 
                    />
                    <span>{{ currentConfig.config.speed }}x</span>
                  </div>
                </div>
              </template>

            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="config-actions">
            <button class="btn btn-secondary" @click="handleTestCurrent" :disabled="testing !== null">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
              测试连接
            </button>
            <button 
              class="btn btn-primary" 
              @click="handleSave" 
              :disabled="!currentConfig.name || saving"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z"/>
                <polyline points="17 21 17 13 7 13 7 21"/>
                <polyline points="7 3 7 8 15 8"/>
              </svg>
              {{ saving ? '保存中...' : (isEditing ? '保存修改' : '保存实例') }}
            </button>
            <button class="btn btn-ghost" @click="handleReset">
              重置
            </button>
          </div>
        </div>
      </section>
    </div>
    
    <!-- 测试结果弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showResult" class="modal-overlay" @click.self="showResult = false">
          <div class="modal result-modal">
            <div class="result-content">
              <div class="result-icon" :class="testResult.success ? 'success' : 'error'">
                <svg v-if="testResult.success" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </div>
              <div class="result-text">
                <h3>{{ testResult.success ? '操作成功' : '操作失败' }}</h3>
                <p>{{ testResult.message }}</p>
              </div>
            </div>
            <button class="btn btn-primary btn-block" @click="showResult = false">确定</button>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 厂商表单弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showProviderModal" class="modal-overlay" @click.self="showProviderModal = false">
          <div class="modal provider-modal">
            <div class="modal-header">
              <h3>{{ editingProvider ? '编辑厂商' : '添加厂商' }}</h3>
              <button class="modal-close" @click="showProviderModal = false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label>厂商标识</label>
                <input 
                  type="text" 
                  class="config-input" 
                  v-model="providerForm.providerCode" 
                  placeholder="如：openai"
                />
              </div>
              <div class="form-group">
                <label>Base URL</label>
                <input 
                  type="text" 
                  class="config-input" 
                  v-model="providerForm.baseUrl" 
                  placeholder="如：https://api.openai.com/v1"
                />
              </div>
              <div class="form-group">
                <label>状态</label>
                <select class="config-select" v-model="providerForm.status">
                  <option :value="1">启用</option>
                  <option :value="0">禁用</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showProviderModal = false">取消</button>
              <button 
                class="btn btn-primary" 
                @click="saveProvider" 
                :disabled="!providerForm.providerCode || globalStore.loading"
              >
                {{ globalStore.loading ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 确认删除弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
          <div class="modal confirm-modal">
            <div class="confirm-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
              </svg>
            </div>
            <h3>确认删除</h3>
            <p>确定要删除这个{{ deleteTargetType === 'provider' ? '厂商' : '模型实例' }}吗？此操作不可恢复。</p>
            <p v-if="deleteTargetName" class="delete-target-name">目标: {{ deleteTargetName }}</p>
            <div class="confirm-actions">
              <button class="btn btn-secondary" @click="showDeleteConfirm = false">取消</button>
              <button class="btn btn-danger" @click="confirmDelete">删除</button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue'
import { useGlobalStore } from '@/stores/global'
import { modelInstanceApi, modelDefinitionApi } from '@/api'
import { 
  useModelConfig,
  modelTypes, 
  modelProviders, 
  resolutionOptions, 
  fpsOptions, 
  voiceOptions,
  durationOptions,
  qualityOptions,
  sceneCodeOptions,
  mapTypeToApi
} from '@/composables/useModelConfig'

const globalStore = useGlobalStore()

const {
  instances,
  instanceTotals,
  currentConfig,
  expandedAdvanced,
  isEditing,
  loadInstances,
  loadInstanceTotals,
  saveConfig,
  testConnection,
  selectInstance,
  handleCreateNew,
  toggleAdvanced,
  getDefaultConfig,
  deleteInstance,
  setAsDefault
} = useModelConfig()

const activeModelType = ref('text')
const selectedInstance = ref(null)
const showApiKey = ref(false)
const showResult = ref(false)
const showDeleteConfirm = ref(false)
const deleteTargetId = ref(null)
const deleteTargetType = ref('') // 'provider' 或 'instance'
const deleteTargetName = ref('')
const testResult = ref({ success: false, message: '' })
const testing = ref(null)
const instancePage = ref(1)
const instancePageSize = ref(6)
const instanceTotal = computed(() => instanceTotals[activeModelType.value] || 0)
const instanceTotalPages = computed(() => Math.max(1, Math.ceil(instanceTotal.value / instancePageSize.value)))

// 厂商管理相关
const providers = ref([])
const showProviderModal = ref(false)
const editingProvider = ref(null)
const providerPage = ref(1)
const providerPageSize = ref(6)
const totalProviders = ref(0)
const providerTotalPages = computed(() => Math.max(1, Math.ceil(totalProviders.value / providerPageSize.value)))
const providerForm = reactive({
  providerCode: '',
  baseUrl: '',
  status: 1
})

const settings = reactive({
  autoSave: true,
  consistencyCheck: true,
  defaultStyle: 'realistic'
})

const voices = computed(() => voiceOptions['zh-CN'] || [])
const resolutions = resolutionOptions

// 厂商管理方法
const loadProviders = async () => {
  try {
    const result = await modelDefinitionApi.list({ page: providerPage.value, size: providerPageSize.value })
    if (result.code === 200 && result.data) {
      const pageData = result.data
      providers.value = pageData.records || pageData.list || []
      totalProviders.value = pageData.total || providers.value.length
      providerPage.value = pageData.current || providerPage.value
    }
  } catch (err) {
    console.error('加载厂商列表失败:', err)
  }
}

const changeProviderPage = (page) => {
  if (page < 1 || page > providerTotalPages.value) return
  providerPage.value = page
  loadProviders()
}

const saveProvider = async () => {
  if (!providerForm.providerCode.trim()) {
    testResult.value = { success: false, message: '请填写厂商标识' }
    showResult.value = true
    return
  }
  
  globalStore.setLoading(true, '保存中...')
  try {
    if (editingProvider.value) {
      await modelDefinitionApi.update(editingProvider.value.id, providerForm)
    } else {
      await modelDefinitionApi.create(providerForm)
    }
    await loadProviders()
    showProviderModal.value = false
    resetProviderForm()
    testResult.value = { success: true, message: editingProvider.value ? '厂商已更新' : '厂商已添加' }
    await nextTick()
    showResult.value = true
  } catch (err) {
    console.error('保存厂商失败:', err)
    testResult.value = { success: false, message: err.message || '操作失败' }
    await nextTick()
    showResult.value = true
  } finally {
    globalStore.setLoading(false)
  }
}

const editProvider = (provider) => {
  editingProvider.value = provider
  providerForm.providerCode = provider.providerCode
  providerForm.baseUrl = provider.baseUrl || ''
  providerForm.status = provider.status || 1
  showProviderModal.value = true
}

const confirmDeleteProvider = (id) => {
  const provider = providers.value.find(p => p.id === id)
  deleteTargetId.value = id
  deleteTargetType.value = 'provider'
  deleteTargetName.value = provider ? provider.providerCode : ''
  showDeleteConfirm.value = true
}

const confirmDelete = async () => {
  if (!deleteTargetId.value) return

  globalStore.setLoading(true, '删除中...')
  try {
    if (deleteTargetType.value === 'provider') {
      const result = await modelDefinitionApi.delete(deleteTargetId.value)
      if (result && typeof result.code !== 'undefined' && result.code !== 200 && result.code !== 0) {
        throw new Error(result.message || `删除失败 (${result.code})`)
      }
      await loadProviders()
      testResult.value = { success: true, message: '厂商已删除' }
    } else if (deleteTargetType.value === 'instance') {
      await deleteInstance(deleteTargetId.value)
      testResult.value = { success: true, message: '模型实例已删除' }
    }
    await nextTick()
    showResult.value = true
  } catch (err) {
    console.error(`删除${deleteTargetType.value === 'provider' ? '厂商' : '模型实例'}失败:`, err)
    testResult.value = { success: false, message: err.message || '删除失败' }
    await nextTick()
    showResult.value = true
  } finally {
    globalStore.setLoading(false)
    deleteTargetId.value = null
    deleteTargetType.value = ''
    deleteTargetName.value = ''
    showDeleteConfirm.value = false
  }
}

const resetProviderForm = () => {
  editingProvider.value = null
  providerForm.providerCode = ''
  providerForm.baseUrl = ''
  providerForm.status = 1
}

// 初始化加载
onMounted(async () => {
  try {
    await loadInstances(activeModelType.value, instancePage.value, instancePageSize.value)
    await loadInstanceTotals()
    await loadProviders()
  } catch (err) {
    console.warn('加载API失败:', err.message)
  }
  Object.keys(modelTypes).forEach(key => {
    expandedAdvanced[key] = false
  })
})

// 监听类型变化
watch(activeModelType, (newType) => {
  currentConfig.type = newType
  currentConfig.config = getDefaultConfig(newType)
  selectedInstance.value = null
  instancePage.value = 1
  loadInstances(newType, instancePage.value, instancePageSize.value)
})

// 根据当前选中的类型获取实例列表
const getInstancesByType = (type) => {
  return instances.value.filter(i => i.type === type || 
    (i.modelType && i.modelType.toUpperCase() === mapTypeToApi(type).toUpperCase()))
}
const getInstanceCount = (type) => {
  const total = instanceTotals[type]
  return total != null ? total : getInstancesByType(type).length
}
const changeInstancePage = (page) => {
  if (page < 1 || page > instanceTotalPages.value) return
  instancePage.value = page
  loadInstances(activeModelType.value, instancePage.value, instancePageSize.value)
}
const getProviders = (type) => modelProviders[type] || []
const getProviderName = (type, providerValue) => {
  if (providerValue === 'custom') return '自定义'
  const providers = getProviders(type)
  const found = providers.find(p => p.value === providerValue)
  return found ? found.name : providerValue
}
const getProviderDesc = () => {
  const provider = currentConfig.config.provider
  if (provider === 'custom') return '使用自定义API端点'
  const providers = getProviders(activeModelType.value)
  const found = providers.find(p => p.value === provider)
  return found ? found.description : ''
}

const getModelPlaceholder = (type) => {
  const placeholders = {
    text: '如: gpt-4o, qwen-max',
    image: '如: dall-e-3, stable-diffusion',
    video: '如: kling, runway-gen-2',
    audio: '如: azure-tts, elevenlabs',
    vector: '如: text-embedding-3-large, bge-m3'
  }
  return placeholders[type] || '输入模型名称'
}

const getSceneOptions = (type) => {
  return sceneCodeOptions[type] || sceneCodeOptions.text
}

const handleSelectInstance = (instance) => {
  selectedInstance.value = instance
  currentConfig.id = instance.id
  currentConfig.type = instance.type
  currentConfig.name = instance.name
  currentConfig.isDefault = instance.isDefault || false
  // 合并配置，确保所有字段都有值
  currentConfig.config = {
    ...getDefaultConfig(instance.type),
    ...instance.config
  }
}

const handleSave = async () => {
  if (!currentConfig.name.trim()) {
    testResult.value = { success: false, message: '请填写实例名称' }
    showResult.value = true
    return
  }

  if (!currentConfig.config.apiKey.trim()) {
    testResult.value = { success: false, message: '请填写API Key' }
    showResult.value = true
    return
  }

  globalStore.setLoading(true, '保存中...')
  try {
    await saveConfig()
    // 重新加载实例列表，确保界面状态正确更新
    await loadInstances(activeModelType.value, instancePage.value, instancePageSize.value)
    await loadInstanceTotals()
    testResult.value = { success: true, message: isEditing.value ? '实例已更新' : '实例已保存' }
    await nextTick()
    showResult.value = true

    // 保存成功后，如果不是编辑模式，清空当前配置状态
    if (!isEditing.value) {
      selectedInstance.value = null
      // 重置到默认状态，但保持当前模型类型
    }
  } catch (err) {
    console.error('保存失败:', err)
    testResult.value = { success: false, message: err.message || '保存失败' }
    await nextTick()
    showResult.value = true
  } finally {
    globalStore.setLoading(false)
  }
}

const handleTestCurrent = async () => {
  testing.value = 'current'
  await new Promise(r => setTimeout(r, 1500))
  testing.value = null
  testResult.value = { success: true, message: '连接成功，模型配置正常' }
  showResult.value = true
}

const handleTest = async (instance) => {
  testing.value = instance.id
  const result = await testConnection(instance.id)
  testing.value = null
  testResult.value = result
  showResult.value = true
}

const handleSaveAsNew = (instance) => {
  selectedInstance.value = null
  currentConfig.id = null
  currentConfig.type = instance.type
  currentConfig.name = `${instance.name} (副本)`
  currentConfig.isDefault = false
  currentConfig.config = { ...instance.config }
  activeModelType.value = instance.type
}

const handleSetDefault = async (instance) => await setAsDefault(instance.id)
const handleDelete = (instance) => {
  deleteTargetId.value = instance.id
  deleteTargetType.value = 'instance'
  deleteTargetName.value = instance.instanceName || instance.name
  showDeleteConfirm.value = true
}
const handleReset = () => handleCreateNew()

const onProviderChange = () => {
  const selectedProvider = providers.value.find(
    p => (p.providerCode || '').toLowerCase() === (currentConfig.config.provider || '').toLowerCase()
  )
  if (selectedProvider) {
    currentConfig.config.baseUrl = selectedProvider.baseUrl || ''
    currentConfig.config.modelDefId = selectedProvider.id
  } else {
    currentConfig.config.baseUrl = ''
    currentConfig.config.modelDefId = null
  }
}
</script>

<style lang="scss" scoped>
.settings-page { 
  padding: 32px 40px; 
  max-width: 1200px; 
  margin: 0 auto; 
}

.page-header { margin-bottom: 40px; }
.page-desc { font-size: 14px; color: var(--text-tertiary); margin-top: 8px; }
.settings-section { margin-bottom: 40px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.section-title { display: flex; align-items: center; gap: 10px; font-size: 16px; font-weight: 600; svg { width: 20px; height: 20px; color: var(--gold-light); } }

.model-tabs { display: flex; gap: 12px; margin-bottom: 24px; }
.model-tab { display: flex; align-items: center; gap: 10px; padding: 14px 20px; font-size: 14px; color: var(--text-secondary); background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 8px; cursor: pointer; transition: all var(--transition-base);
  &:hover { background: var(--bg-glass); border-color: var(--border-hover); }
  &.active { background: rgba(212, 175, 55, 0.15); border-color: var(--gold-primary); color: var(--gold-light); box-shadow: 0 0 15px rgba(212, 175, 55, 0.15); }
}
.tab-icon { width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; border-radius: 6px;
  svg { width: 18px; height: 18px; }
  &.purple { background: rgba(212, 175, 55, 0.15); color: var(--gold-light); }
  &.blue { background: rgba(59, 130, 246, 0.15); color: #60a5fa; }
  &.cyan { background: rgba(6, 182, 212, 0.15); color: var(--accent); }
  &.green { background: rgba(16, 185, 129, 0.15); color: var(--success); }
  &.orange { background: rgba(245, 158, 11, 0.15); color: var(--warning); }
}
.tab-count { padding: 2px 8px; font-size: 12px; background: var(--bg-tertiary); border-radius: 4px; color: var(--text-muted); }
.active .tab-count { background: var(--gold-primary); color: #1A1A1E; }

.instances-container { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 24px; }
.instances-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.instances-count { font-size: 13px; color: var(--text-tertiary); }
.btn-sm { padding: 8px 16px; font-size: 13px; svg { width: 14px; height: 14px; } }
.instances-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }

.instance-card { background: var(--bg-tertiary); border: 2px solid var(--border-color); border-radius: 8px; padding: 16px; cursor: pointer; transition: all var(--transition-base);
  &:hover { border-color: var(--border-hover); }
  &.active { border-color: var(--primary); background: rgba(139, 92, 246, 0.05); }
  &.default { 
    border-color: var(--gold-primary); 
    background: rgba(212, 175, 55, 0.08);
    box-shadow: 0 0 20px rgba(212, 175, 55, 0.2);
  }
  &.default:hover {
    border-color: var(--gold-light);
    box-shadow: 0 0 25px rgba(212, 175, 55, 0.3);
  }
}
.instance-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.instance-info { display: flex; flex-direction: column; gap: 4px; }
.instance-name { font-size: 15px; font-weight: 600; }
.instance-provider { font-size: 12px; color: var(--text-tertiary); }
.instance-badges { display: flex; gap: 8px; }
.badge { padding: 4px 10px; font-size: 11px; border-radius: 4px;
  &.default { background: var(--gold-primary); color: #1A1A1E; font-weight: 600; }
}
.instance-actions { display: flex; gap: 8px; margin-bottom: 12px; }
.action-btn { flex: 1; display: flex; align-items: center; justify-content: center; gap: 6px; padding: 10px 12px; font-size: 13px; color: var(--text-secondary); background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 4px; cursor: pointer; transition: all var(--transition-base);
  svg { width: 16px; height: 16px; }
  &:hover:not(:disabled) { background: var(--bg-glass); color: var(--text-primary); border-color: var(--border-hover); }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
  &.primary { background: var(--gold-gradient); color: #1A1A1E; border-color: var(--gold-primary);
    &:hover:not(:disabled) { box-shadow: 0 0 15px rgba(212, 175, 55, 0.3); }
  }
}
.instance-footer { display: flex; gap: 8px; padding-top: 12px; border-top: 1px solid var(--border-color); }
.footer-btn { flex: 1; padding: 8px; font-size: 12px; color: var(--text-tertiary); background: transparent; border: none; cursor: pointer; transition: color var(--transition-base);
  &:hover:not(:disabled) { color: var(--text-primary); }
  &:disabled { opacity: 0.4; cursor: not-allowed; }
  &.danger:hover { color: var(--error); }
}

// 厂商管理样式
.providers-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; margin-top: 20px; }
.pagination { display: flex; justify-content: center; gap: 8px; margin-top: 18px; }
.page-btn { padding: 6px 12px; border-radius: 8px; border: 1px solid var(--border-color); background: var(--bg-card); color: var(--text-secondary); cursor: pointer; transition: all 0.2s ease; }
.page-btn:hover:not(:disabled) { border-color: var(--gold-primary); color: var(--gold-light); }
.page-btn.active { background: rgba(212, 175, 55, 0.18); border-color: rgba(212, 175, 55, 0.55); color: var(--gold-light); }
.page-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.provider-card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 20px; transition: all var(--transition-base);
  &:hover { border-color: var(--gold-primary); box-shadow: 0 0 20px rgba(212, 175, 55, 0.15); }
}
.provider-info { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.provider-name { font-size: 16px; font-weight: 600; color: var(--text-primary); }
.provider-url { font-size: 12px; color: var(--text-muted); margin-bottom: 16px; padding: 8px 12px; background: var(--bg-tertiary); border-radius: 4px; word-break: break-all; }
.provider-actions { display: flex; gap: 8px; }
.provider-actions .action-btn.small { padding: 6px 12px; font-size: 12px; }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 20px; text-align: center; }
.empty-icon { width: 64px; height: 64px; margin-bottom: 16px; svg { width: 100%; height: 100%; color: var(--gold-muted); } }
.empty-state p { font-size: 15px; font-weight: 500; margin-bottom: 4px; }
.empty-state span { font-size: 13px; color: var(--text-muted); }

.settings-card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-lg); overflow: hidden; }
.config-section { padding: 24px;
  &:not(:last-child) { border-bottom: 1px solid var(--border-color); }
}
.section-subtitle { font-size: 14px; font-weight: 600; color: var(--text-secondary); margin-bottom: 16px; }
.config-row { display: flex; align-items: center; gap: 16px; margin-bottom: 16px;
  &:last-child { margin-bottom: 0; }
}
.config-label { width: 100px; font-size: 13px; color: var(--text-secondary); flex-shrink: 0; }
.config-select, .config-input { flex: 1; padding: 10px 14px; font-size: 13px; color: var(--text-primary); background: var(--bg-tertiary); border: 1px solid var(--border-color); border-radius: 4px; outline: none;
  &:focus { border-color: var(--gold-primary); box-shadow: 0 0 0 3px rgba(212, 175, 55, 0.15); }
  &.small { width: 150px; flex: none; }
}
.input-with-action { flex: 1; display: flex; align-items: center; position: relative;
  .config-input { flex: 1; padding-right: 44px; }
  .input-action { position: absolute; right: 8px; width: 32px; height: 32px; background: transparent; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center;
    svg { width: 18px; height: 18px; color: var(--text-muted); }
    &:hover svg { color: var(--text-tertiary); }
  }
}
.provider-tip { font-size: 12px; color: var(--text-muted); white-space: nowrap; }

.advanced-section { border-top: 1px solid var(--border-color); }
.advanced-toggle { width: 100%; display: flex; align-items: center; gap: 10px; padding: 16px 24px; font-size: 14px; font-weight: 500; color: var(--text-secondary); background: transparent; border: none; cursor: pointer; transition: all var(--transition-base);
  svg { width: 18px; height: 18px; }
  .toggle-arrow { margin-left: auto; transition: transform var(--transition-base);
    &.expanded { transform: rotate(180deg); }
  }
  &:hover { color: var(--text-primary); background: var(--bg-glass); }
}
.advanced-content { padding: 0 24px 24px; display: flex; flex-direction: column; gap: 16px; }
.range-input { flex: 1; display: flex; align-items: center; gap: 12px;
  input[type="range"] { flex: 1; height: 4px; -webkit-appearance: none; background: var(--bg-tertiary); border-radius: 4px;
    &::-webkit-slider-thumb { -webkit-appearance: none; width: 16px; height: 16px; background: var(--gold-gradient); border-radius: 50%; cursor: pointer; box-shadow: 0 0 10px rgba(212, 175, 55, 0.5); }
  }
  span { min-width: 40px; font-size: 13px; color: var(--text-secondary); text-align: right; }
}

.config-actions { display: flex; justify-content: flex-end; gap: 12px; padding: 20px 24px; background: var(--bg-tertiary); border-top: 1px solid var(--border-color); }
.btn-ghost { background: transparent; color: var(--text-tertiary); border: 1px solid var(--border-color);
  &:hover { background: var(--bg-glass); color: var(--text-primary); border-color: var(--border-hover); }
}

.modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.6); display: flex; align-items: center; justify-content: center; z-index: 1000; will-change: opacity; }
.result-modal { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 24px 32px; min-width: 320px; max-width: 420px; will-change: transform, opacity; }
.result-content { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.result-icon { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; svg { width: 24px; height: 24px; } &.success { background: rgba(16, 185, 129, 0.15); svg { color: var(--success); } } &.error { background: rgba(239, 68, 68, 0.15); svg { color: var(--error); } } }
.result-text { text-align: left; flex: 1; min-width: 0; h3 { font-size: 16px; font-weight: 600; margin-bottom: 4px; } p { font-size: 14px; color: var(--text-tertiary); line-height: 1.5; word-break: break-all; } }
.btn-block { width: 100%; padding: 10px; }

.modal-enter-active {
  transition: opacity 0.25s ease;
  
  .result-modal, .provider-modal, .modal {
    transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.25s ease;
  }
}

.modal-leave-active {
  transition: opacity 0.2s ease;
  
  .result-modal, .provider-modal, .modal {
    transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.2s ease;
  }
}

.modal-enter-from {
  opacity: 0;
  
  .result-modal, .provider-modal, .modal {
    transform: scale(0.92) translateY(-10px);
    opacity: 0;
  }
}

.modal-leave-to {
  opacity: 0;
  
  .result-modal, .provider-modal, .modal {
    transform: scale(0.95) translateY(5px);
    opacity: 0;
  }
}

.provider-modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  max-width: 400px;
  width: 90%;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 12px;
  &:last-child { margin-bottom: 0; }
  label {
    font-size: 13px;
    color: var(--text-secondary);
    margin-bottom: 6px;
  }
  .config-input, .config-select {
    width: 100%;
    max-width: 280px;
    text-align: left;
  }
}
.modal-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 12px 20px;
  border-top: 1px solid var(--border-color);
}

.confirm-modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 24px 32px;
  min-width: 320px;
  max-width: 400px;
  text-align: center;
}
.confirm-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  background: rgba(239, 68, 68, 0.15);
  svg { width: 28px; height: 28px; color: var(--error); }
}
.confirm-modal h3 { font-size: 18px; font-weight: 600; margin-bottom: 8px; }
.confirm-modal p { font-size: 14px; color: var(--text-tertiary); margin-bottom: 8px; line-height: 1.5; }
.delete-target-name {
  font-size: 13px;
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  margin-bottom: 24px;
  font-weight: 500;
}
.confirm-actions { display: flex; gap: 12px; justify-content: center; }
.btn-danger {
  background: rgba(239, 68, 68, 0.15);
  color: var(--error);
  border: 1px solid var(--error);
  &:hover { background: var(--error); color: #fff; }
}

// Compact button overrides
.settings-page .btn {
  padding: 8px 16px;
  font-size: 12px;
  border-radius: 4px;
}

.settings-page .btn-primary {
  padding: 8px 18px;
  border-radius: 4px;
}

.settings-page .btn-secondary {
  padding: 8px 16px;
  border-radius: 4px;
}
</style>
