// 模型实例服务 - API调用层
import axios from 'axios'

const API_BASE = '/api/v1'

const apiClient = axios.create({
  baseURL: API_BASE,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

apiClient.interceptors.response.use((response) => response.data, (error) => {
  console.error('[ModelInstanceAPI]', error.message)
  return Promise.reject(error)
})

/**
 * 模型实例API
 */
export const ModelInstanceAPI = {
  // 获取所有模型实例
  getAll() {
    return apiClient.get('/model-instances')
  },
  
  // 获取单个实例
  getById(id) {
    return apiClient.get(`/model-instances/${id}`)
  },
  
  // 创建模型实例
  create(data) {
    return apiClient.post('/model-instances', data)
  },
  
  // 更新模型实例
  update(id, data) {
    return apiClient.put(`/model-instances/${id}`, data)
  },
  
  // 删除模型实例
  delete(id) {
    return apiClient.delete(`/model-instances/${id}`)
  },
  
  // 设为默认
  setDefault(id) {
    return apiClient.put(`/model-instances/${id}/default`)
  },
  
  // 复制实例
  duplicate(id) {
    return apiClient.post(`/model-instances/${id}/duplicate`)
  },
  
  // 测试连接
  test(id) {
    return apiClient.post(`/model-instances/${id}/test`)
  },
  
  // 根据类型获取实例列表
  getByType(type) {
    return apiClient.get(`/model-instances?type=${type}`)
  },
  
  // 批量删除
  batchDelete(ids) {
    return apiClient.delete('/model-instances/batch', { data: { ids } })
  }
}

/**
 * 模型配置API（全局配置）
 */
export const ModelConfigAPI = {
  getAll() {
    return apiClient.get('/model-config')
  },
  
  save(config) {
    return apiClient.put('/model-config', config)
  },
  
  reset() {
    return apiClient.post('/model-config/reset')
  }
}

export default {
  ModelInstanceAPI,
  ModelConfigAPI
}
