/**
 * 模型配置组合式函数 - 完整版
 * 对接后端API
 */
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { modelInstanceApi } from '@/api'

// 模型类型定义
export const modelTypes = {
  text: { label: '文本模型', icon: 'text', color: 'purple' },
  image: { label: '文生图模型', icon: 'image', color: 'cyan' },
  video: { label: '视频模型', icon: 'video', color: 'green' },
  audio: { label: '语音合成', icon: 'audio', color: 'orange' }
}

// 映射UI类型到API类型
export const mapTypeToApi = (type) => {
  const mapping = { text: 'TEXT', image: 'IMAGE', video: 'VIDEO', audio: 'AUDIO', vector: 'VECTOR' }
  return mapping[type] || type
}

modelTypes.vector = modelTypes.vector || { label: 'Vector Model', icon: 'vector', color: 'blue' }

// 提供商列表（可从API获取）
export const modelProviders = {
  text: [
    { value: 'openai', name: 'OpenAI (GPT-4)', description: 'GPT-4o、GPT-4 Turbo' },
    { value: 'anthropic', name: 'Anthropic (Claude)', description: 'Claude 3.5 Sonnet' },
    { value: 'ali', name: '阿里云通义千问', description: 'Qwen-Max、Qwen-Plus' },
    { value: 'baidu', name: '百度文心一言', description: 'ERNIE-4.5' },
    { value: 'moonshot', name: '月之暗面 (Kimi)', description: 'Kimi Moonshot' },
    { value: 'deepseek', name: 'DeepSeek', description: 'DeepSeek Chat' }
  ],
  image: [
    { value: 'openai', name: 'OpenAI (DALL-E 3)', description: '高质量图像生成' },
    { value: 'midjourney', name: 'Midjourney', description: '艺术风格图像' },
    { value: 'stability', name: 'Stability AI', description: 'Stable Diffusion' },
    { value: 'ali', name: '阿里云通义万相', description: '中文优化模型' },
    { value: 'tencent', name: '腾讯混元', description: '腾讯AI图像生成' },
    { value: 'kuaishou', name: '快手可图', description: '快手图像生成' }
  ],
  video: [
    { value: 'kling', name: '可灵 (Kling)', description: '快手AI视频生成' },
    { value: 'sora', name: 'Sora', description: 'OpenAI视频生成' },
    { value: 'pika', name: 'Pika Labs', description: '创意视频生成' },
    { value: 'runway', name: 'Runway Gen-2', description: '专业视频生成' },
    { value: 'luma', name: 'Luma Dream Machine', description: '高质量视频生成' },
    { value: 'kuaishou', name: '快手可灵', description: '快手视频生成' }
  ],
  audio: [
    { value: 'azure', name: 'Azure TTS', description: '微软语音合成' },
    { value: 'elevenlabs', name: 'ElevenLabs', description: '高质量语音克隆' },
    { value: 'ali', name: '阿里云语音合成', description: '中文语音优化' },
    { value: 'baidu', name: '百度语音合成', description: '多种音色选择' },
    { value: 'openai', name: 'OpenAI TTS', description: '支持多种声音' }
  ]
}

modelProviders.vector = modelProviders.vector || [
  { value: 'openai', name: 'OpenAI Embedding', description: 'text-embedding-3 series' },
  { value: 'ali', name: 'Ali Embedding', description: 'Embedding model from Ali' },
  { value: 'bge', name: 'BGE', description: 'Open source embedding model' }
]

// 场景代码选项（匹配后端SceneCode枚举）
// 后端SceneCode: SCENE_GEN, INFO_EXTRACT, CHARACTER_GEN, STORYBOARD_GEN, VIDEO_GEN
export const sceneCodeOptions = {
  text: [
    { value: 'SCENE_GEN', label: '场景生成' },
    { value: 'INFO_EXTRACT', label: '信息提取' },
    { value: 'STORYBOARD_GEN', label: '分镜生成' }
  ],
  image: [
    { value: 'CHARACTER_GEN', label: '角色生成' }
  ],
  video: [
    { value: 'VIDEO_GEN', label: '视频生成' }
  ],
  audio: [
    { value: 'SCENE_GEN', label: '场景生成' }
  ]
}

sceneCodeOptions.vector = sceneCodeOptions.vector || [
  { value: 'INFO_EXTRACT', label: '信息提取' }
]

// 分辨率选项
export const resolutionOptions = [
  { value: '1024x1024', label: '1024 x 1024 (1:1)' },
  { value: '1280x720', label: '1280 x 720 (16:9)' },
  { value: '720x1280', label: '720 x 1280 (9:16)' },
  { value: '1024x768', label: '1024 x 768 (4:3)' },
  { value: '1920x1080', label: '1920 x 1080 (Full HD)' }
]

// 帧率选项
export const fpsOptions = [
  { value: 24, label: '24 fps (电影质感)' },
  { value: 30, label: '30 fps (标准)' },
  { value: 60, label: '60 fps (流畅)' }
]

// 语音选项
export const voiceOptions = {
  'zh-CN': [
    { value: 'zh-CN-Xiaoxiao', label: '晓晓 (女, 标准)' },
    { value: 'zh-CN-Yunxi', label: '云希 (男, 磁性)' },
    { value: 'zh-CN-Yunyang', label: '云扬 (男, 专业)' },
    { value: 'zh-CN-Xiaohan', label: '晓涵 (女, 温柔)' },
    { value: 'zh-CN-Xiaorui', label: '晓睿 (女, 知性)' }
  ],
  'en-US': [
    { value: 'en-US-Aria', label: 'Aria (女, 自然)' },
    { value: 'en-US-Guy', label: 'Guy (男, 专业)' },
    { value: 'en-US-Jenny', label: 'Jenny (女, 清晰)' }
  ]
}

// 视频时长选项
export const durationOptions = [
  { value: 3, label: '3秒' },
  { value: 5, label: '5秒' },
  { value: 10, label: '10秒' }
]

// 画质选项
export const qualityOptions = [
  { value: 'standard', label: '标准' },
  { value: 'high', label: '高清' },
  { value: 'ultra', label: '超清' }
]

// 默认配置模板 - 基础配置保持一致
const defaultConfigs = {
  text: {
    provider: '',
    modelDefId: null,
    apiKey: '',
    model: '',
    baseUrl: '',
    path: '',
    temperature: 0.7,
    max_tokens: 4000,
    sceneCode: 'SCENE_GEN'
  },
  image: {
    provider: '',
    modelDefId: null,
    apiKey: '',
    model: '',
    baseUrl: '',
    path: '',
    resolution: '1024x1024',
    quality: 'standard',
    sceneCode: 'CHARACTER_GEN'
  },
  video: {
    provider: '',
    modelDefId: null,
    apiKey: '',
    model: '',
    baseUrl: '',
    path: '',
    fps: 30,
    duration: 5,
    sceneCode: 'VIDEO_GEN'
  },
  audio: {
    provider: '',
    modelDefId: null,
    apiKey: '',
    model: '',
    baseUrl: '',
    path: '',
    voice: 'zh-CN-Xiaoxiao',
    speed: 1.0,
    sceneCode: 'AUDIO_GEN'
  }
}

defaultConfigs.vector = defaultConfigs.vector || {
  provider: '',
  modelDefId: null,
  apiKey: '',
  model: '',
  baseUrl: '',
  path: '',
  dimension: 1024,
  metric: 'cosine',
  topK: 10,
  sceneCode: 'INFO_EXTRACT'
}

/**
 * 模型配置组合式函数
 */
export function useModelConfig() {
  // 状态
  const instances = ref([])
  const instanceTotals = reactive({ text: 0, image: 0, video: 0, audio: 0, vector: 0 })
  const instancesLoading = ref(false)
  const instancesError = ref(null)
  const saving = ref(false)
  const testing = ref(null) // 当前测试的实例ID
  
  // 当前编辑的模型配置
  const currentConfig = reactive({
    id: null,
    type: 'text',
    name: '',
    isDefault: false,
    config: { ...defaultConfigs.text }
  })
  
  // 展开的折叠面板
  const expandedAdvanced = reactive({})
  
  // 计算属性
  const textInstances = computed(() => instances.value.filter(i => i.type === 'text'))
  const imageInstances = computed(() => instances.value.filter(i => i.type === 'image'))
  const videoInstances = computed(() => instances.value.filter(i => i.type === 'video'))
  const audioInstances = computed(() => instances.value.filter(i => i.type === 'audio'))
  const vectorInstances = computed(() => instances.value.filter(i => i.type === 'vector'))
  
    const defaultInstance = computed(() => instances.value.find(i => i.isDefault))
    const isEditing = computed(() => !!currentConfig.id)

   // ============ 数据转换方法 ============

   // 映射API类型到UI类型
  const mapApiToType = (apiType) => {
    const mapping = { TEXT: 'text', IMAGE: 'image', VIDEO: 'video', AUDIO: 'audio', VECTOR: 'vector' }
    return mapping[apiType?.toUpperCase()] || apiType?.toLowerCase() || 'text'
  }

  // 转换API数据到UI格式
  const transformInstance = (item) => {
    const type = mapApiToType(item.modelType)
    let config = {}
    
    try {
      config = item.params ? (typeof item.params === 'string' ? JSON.parse(item.params) : item.params) : {}
    } catch (e) {
      config = {}
    }

    if (type === 'text' && config.max_tokens !== undefined && config.max_tokens === undefined) {
      config.max_tokens = config.max_tokens
    }

    return {
      id: item.id,
      type: type,
      name: item.instanceName,
      isDefault: false,
      sceneCode: item.sceneCode || '',
      config: {
        provider: item.providerCode || 'custom',
        apiKey: item.apiKey || '',
        model: item.modelCode || '',
        baseUrl: item.baseUrl || '',
        path: item.path || '',
        sceneCode: item.sceneCode || '',
        ...config
      }
    }
  }

  // 转换UI数据到API格式（对接后端 ModelInstanceRequestVO）
  const transformToApi = (uiData) => {
    const type = mapTypeToApi(uiData.type)
    const sceneCode = uiData.config?.sceneCode || getDefaultSceneCode(type)

    // 构建参数对象（后端params是Map<String, Object>）
    const params = {}
    if (uiData.type === 'text') {
      params.temperature = Number(uiData.config?.temperature) || 0.7
      params.max_tokens = Number(uiData.config?.max_tokens ) || 4000
    } else if (uiData.type === 'image') {
      params.resolution = uiData.config?.resolution || '1024x1024'
      params.quality = uiData.config?.quality || 'standard'
    } else if (uiData.type === 'video') {
      params.fps = Number(uiData.config?.fps) || 30
      params.duration = Number(uiData.config?.duration) || 5
    } else if (uiData.type === 'audio') {
      params.voice = uiData.config?.voice || 'zh-CN-Xiaoxiao'
      params.speed = Number(uiData.config?.speed) || 1.0
    }

    return {
      instanceName: uiData.name,
      modelDefId: uiData.config?.modelDefId || null,
      modelType: type,
      modelCode: uiData.config?.model || '',
      sceneCode: sceneCode,
      apiKey: uiData.config?.apiKey || '',
      path: uiData.config?.path || '',
      params: params,
      status: 1,
      isDefault: uiData.isDefault || false
    }
  }

  // 获取默认场景代码
  const getDefaultSceneCode = (type) => {
    const defaults = {
      'TEXT': 'SCENE_GEN',
      'IMAGE': 'CHARACTER_GEN',
      'VIDEO': 'VIDEO_GEN',
      'AUDIO': 'SCENE_GEN',
      'VECTOR': 'INFO_EXTRACT'
    }
    return defaults[type] || 'SCENE_GEN'
  }

  // 映射场景代码到类型
  const mapSceneToType = (sceneCode) => {
    if (sceneCode?.includes('STORY')) return 'text'
    if (sceneCode?.includes('IMAGE')) return 'image'
    if (sceneCode?.includes('VIDEO')) return 'video'
    if (sceneCode?.includes('AUDIO') || sceneCode?.includes('VOICE')) return 'audio'
    if (sceneCode?.includes('VECTOR') || sceneCode?.includes('EMBED')) return 'vector'
    return 'text'
  }

  // ============ API调用方法 ============

  // 加载所有实例
  const getDefaultIdFromValue = (val) => {
    if (!val) return null
    return val.modelInstanceId ?? val.instanceId ?? val.id ?? null
  }

  const applyDefaultFlags = (defaultMap) => {
    if (!instances.value.length) return
    instances.value.forEach(i => { i.isDefault = false })
    Object.entries(defaultMap || {}).forEach(([key, val]) => {
      const type = mapApiToType(key)
      const id = getDefaultIdFromValue(val)
      if (!id) return
      const target = instances.value.find(i => i.type === type && String(i.id) === String(id))
      if (target) target.isDefault = true
    })
  }

  const loadInstances = async (type = null, page = 1, size = 6) => {
    instancesLoading.value = true
    instancesError.value = null

    if (!type) {
      type = 'text'
    }
    // 加载指定类型
    try {
      const modelType = mapTypeToApi(type)
      const response = await modelInstanceApi.list(modelType, { page, size })

      if (response.data?.records) {
        instances.value = response.data.records.map(transformInstance)
        instanceTotals[type] = response.data.total ?? instances.value.length
      } else if (response.data?.modelInstances) {
        instances.value = response.data.modelInstances.map(transformInstance)
        instanceTotals[type] = response.data.total ?? instances.value.length
      } else if (Array.isArray(response.data)) {
        instances.value = response.data.map(transformInstance)
        instanceTotals[type] = instances.value.length
      }
      try {
        const defRes = await modelInstanceApi.getDefault(modelType)
        const defaultId = getDefaultIdFromValue(defRes?.data)
        if (defaultId) {
          instances.value.forEach(i => { i.isDefault = String(i.id) === String(defaultId) })
        }
      } catch (err) {
        console.warn('加载默认模型失败:', err)
      }
    } catch (err) {
      console.error('加载模型实例失败:', err)
      instancesError.value = err.message || '加载失败'
      instances.value = getMockInstances().filter(i => i.type === type)
      instanceTotals[type] = instances.value.length
    }
    instancesLoading.value = false
  }

  const loadInstanceTotals = async () => {
    const allTypes = ['TEXT', 'IMAGE', 'VIDEO', 'AUDIO', 'VECTOR']
    await Promise.all(allTypes.map(async (modelType) => {
      try {
        const response = await modelInstanceApi.list(modelType, { page: 1, size: 1 })
        const key = mapApiToType(modelType)
        if (response.data?.total != null) {
          instanceTotals[key] = response.data.total
        } else if (response.data?.records) {
          instanceTotals[key] = response.data.records.length
        } else {
          instanceTotals[key] = 0
        }
      } catch (err) {
        const key = mapApiToType(modelType)
        instanceTotals[key] = 0
      }
    }))
  }
  
  // 创建新实例
  const createInstance = async (data) => {
    saving.value = true
    
    try {
      const response = await modelInstanceApi.create(data)
      instances.value.push(response.data)
      return response.data
    } catch (err) {
      console.error('创建实例失败:', err)
      throw err
    } finally {
      saving.value = false
    }
  }
  
  // 更新实例
  const updateInstance = async (id, data) => {
    saving.value = true
    
    try {
      const response = await modelInstanceApi.update(id, data)
      const index = instances.value.findIndex(i => i.id === id)
      if (index !== -1 && response.data) {
        instances.value[index] = response.data
      }
      return response.data
    } catch (err) {
      console.error('更新实例失败:', err)
      throw err
    } finally {
      saving.value = false
    }
  }
  
  // 删除实例
  const deleteInstance = async (id) => {
    try {
      const result = await modelInstanceApi.delete(id)
      if (result && typeof result.code !== 'undefined' && result.code !== 200 && result.code !== 0) {
        throw new Error(result.message || `删除失败 (${result.code})`)
      }
      instances.value = instances.value.filter(i => i.id !== id)
      
      if (currentConfig.id === id) {
        resetCurrent()
      }
      await loadInstanceTotals()
    } catch (err) {
      console.error('删除实例失败:', err)
      throw err
    }
  }
  
  // 设为默认
  const setAsDefault = async (id) => {
    try {
      const instance = instances.value.find(i => i.id === id)
      if (!instance) {
        throw new Error('实例不存在')
      }
      
      const modelType = mapTypeToApi(instance.type)
      
      await modelInstanceApi.updateDefault({
        modelType: modelType,
        modelInstanceId: id,
        status: 1
      })
      
      // 更新本地状态
      instances.value
        .filter(i => i.type === instance.type)
        .forEach(i => { i.isDefault = false })
      instance.isDefault = true
    } catch (err) {
      console.error('设置默认失败:', err)
      throw err
    }
  }
  
  // 复制实例
  const duplicateInstance = async (id) => {
    try {
      const response = await modelInstanceApi.duplicate(id)
      instances.value.push(response.data)
      return response.data
    } catch (err) {
      console.error('复制实例失败:', err)
      throw err
    }
  }
  
  // 测试连接
  const testConnection = async (id) => {
    testing.value = id
    
    try {
      const response = await modelInstanceApi.test(id)
      return response.data || { success: true, message: '连接成功' }
    } catch (err) {
      console.error('测试连接失败:', err)
      return { success: false, message: err.message || '连接失败' }
    } finally {
      testing.value = null
    }
  }
  
  // ============ 业务方法 ============

  // 获取某类型的所有实例
  const getInstancesByType = (type) => {
    const apiType = mapTypeToApi(type)
    return instances.value.filter(i => {
      const instanceType = mapApiToType(i.modelType || i.type)
      return instanceType === type || i.type === type
    })
  }
  
  // 获取某类型的提供商列表
  const getProviders = (type) => {
    return modelProviders[type] || []
  }
  
  // 获取提供商名称
  const getProviderName = (type, providerValue) => {
    if (providerValue === 'custom') return '自定义'
    const providers = getProviders(type)
    const found = providers.find(p => p.value === providerValue)
    return found ? found.name : providerValue
  }
  
  // 获取提供商描述
  const getProviderDesc = (type) => {
    const provider = currentConfig.config.provider
    if (provider === 'custom') return '使用自定义API端点'
    const providers = getProviders(type)
    const found = providers.find(p => p.value === provider)
    return found ? found.description : ''
  }
  
  // 获取语音列表
  const getVoices = () => {
    return voiceOptions['zh-CN'] || []
  }
  
  // 选择实例进行编辑
  const selectInstance = (instance) => {
    currentConfig.id = instance.id
    currentConfig.type = instance.type
    currentConfig.name = instance.name
    currentConfig.isDefault = instance.isDefault || false
    // 合并配置，保留默认值并覆盖实例配置
    if (instance.config) {
      currentConfig.config = {
        ...defaultConfigs[currentConfig.type],
        ...instance.config
      }
    } else {
      currentConfig.config = { ...defaultConfigs[currentConfig.type] }
    }
  }
  
  // 新建实例
  const handleCreateNew = () => {
    currentConfig.id = null
    currentConfig.name = ''
    currentConfig.isDefault = false
    currentConfig.config = { ...defaultConfigs[currentConfig.type] }
    expandedAdvanced[currentConfig.type] = false
  }
  
  // 重置当前编辑
  const resetCurrent = () => {
    currentConfig.id = null
    currentConfig.name = ''
    currentConfig.isDefault = false
    currentConfig.config = { ...defaultConfigs[currentConfig.type] }
  }
  
  // 切换高级设置展开
  const toggleAdvanced = (key) => {
    expandedAdvanced[key] = !expandedAdvanced[key]
  }
  
  // 保存配置（创建或更新）
  const saveConfig = async () => {
    if (!currentConfig.name.trim()) {
      throw new Error('请输入实例名称')
    }
    
    const configData = transformToApi({
      name: currentConfig.name,
      type: currentConfig.type,
      isDefault: currentConfig.isDefault,
      config: currentConfig.config
    })
    
    if (isEditing.value) {
      configData.id = currentConfig.id
      return await updateInstance(currentConfig.id, configData)
    } else {
      return await createInstance(configData)
    }
  }
  
  // 获取默认配置
  const getDefaultConfig = (type) => {
    return defaultConfigs[type] || {}
  }
  
  // 初始化
  const init = async () => {
    await loadInstances()
    Object.keys(modelTypes).forEach(key => {
      expandedAdvanced[key] = false
    })
  }

  return {
    // 状态
    instances,
    instanceTotals,
    instancesLoading,
    instancesError,
    saving,
    testing,
    currentConfig,
    expandedAdvanced,
    
    // 计算属性
    textInstances,
    imageInstances,
    videoInstances,
    audioInstances,
    vectorInstances,
    defaultInstance,
    isEditing,

    // API方法
    loadInstances,
    loadInstanceTotals,
    createInstance,
    updateInstance,
    deleteInstance,
    setAsDefault,
    duplicateInstance,
    testConnection,
    
    // 业务方法
    getInstancesByType,
    getProviders,
    getProviderName,
    getProviderDesc,
    getVoices,
    selectInstance,
    handleCreateNew,
    resetCurrent,
    toggleAdvanced,
    saveConfig,
    getDefaultConfig,
    init
  }
}

// 模拟数据（API不可用时降级使用）
function getMockInstances() {
  return [
    { id: 1, type: 'text', name: 'GPT-4 默认', isDefault: true, config: { provider: 'openai', apiKey: '***', model: 'gpt-4o', temperature: 0.7, max_tokens: 4000 } },
    { id: 2, type: 'text', name: 'Claude 3.5', isDefault: false, config: { provider: 'anthropic', apiKey: '***', model: 'claude-3-5-sonnet', temperature: 0.7, max_tokens: 4000 } },
    { id: 3, type: 'image', name: 'DALL-E 3', isDefault: true, config: { provider: 'openai', apiKey: '***', resolution: '1024x1024', quality: 'standard' } },
    { id: 4, type: 'video', name: '可灵视频', isDefault: true, config: { provider: 'kling', apiKey: '***', fps: 30, duration: 5 } },
    { id: 5, type: 'audio', name: 'Azure 语音', isDefault: true, config: { provider: 'azure', apiKey: '***', voice: 'zh-CN-Xiaoxiao', speed: 1.0 } }
  ]
}

export default useModelConfig
