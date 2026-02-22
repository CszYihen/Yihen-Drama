// 模型配置服务 - API调用层
import axios from 'axios'

const API_BASE = '/api/v1'

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加认证令牌
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器 - 统一错误处理
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || error.message || '请求失败'
    console.error('[ModelAPI]', message)
    return Promise.reject(error)
  }
)

/**
 * 模型配置相关API
 */
export const ModelConfigAPI = {
  // 获取所有模型配置
  getAll() {
    return apiClient.get('/model-config')
  },
  
  // 保存模型配置
  save(config) {
    return apiClient.put('/model-config', config)
  },
  
  // 测试单个模型连接
  test(modelType) {
    return apiClient.post(`/model-config/test/${modelType}`)
  },
  
  // 测试所有启用的模型
  testAll() {
    return apiClient.post('/model-config/test-all')
  },
  
  // 重置为默认配置
  reset() {
    return apiClient.post('/model-config/reset')
  },
  
  // 获取模型列表（支持情况）
  getSupportedModels() {
    return apiClient.get('/model-config/supported')
  }
}

/**
 * 文本模型配置
 */
export const TextModelAPI = {
  get() {
    return apiClient.get('/model-config/text')
  },
  
  update(config) {
    return apiClient.put('/model-config/text', config)
  },
  
  test() {
    return apiClient.post('/model-config/text/test')
  }
}

/**
 * 图像模型配置
 */
export const ImageModelAPI = {
  get() {
    return apiClient.get('/model-config/image')
  },
  
  update(config) {
    return apiClient.put('/model-config/image', config)
  },
  
  test() {
    return apiClient.post('/model-config/image/test')
  }
}

/**
 * 视频模型配置
 */
export const VideoModelAPI = {
  get() {
    return apiClient.get('/model-config/video')
  },
  
  update(config) {
    return apiClient.put('/model-config/video', config)
  },
  
  test() {
    return apiClient.post('/model-config/video/test')
  }
}

/**
 * 语音模型配置
 */
export const AudioModelAPI = {
  get() {
    return apiClient.get('/model-config/audio')
  },
  
  update(config) {
    return apiClient.put('/model-config/audio', config)
  },
  
  test() {
    return apiClient.post('/model-config/audio/test')
  }
}

export default {
  ModelConfigAPI,
  TextModelAPI,
  ImageModelAPI,
  VideoModelAPI,
  AudioModelAPI
}
