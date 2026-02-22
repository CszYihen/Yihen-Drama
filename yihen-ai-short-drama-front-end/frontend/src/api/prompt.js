import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''
const TIMEOUT = 300000

const apiClient = axios.create({
  baseURL: API_BASE,
  timeout: TIMEOUT,
  headers: { 'Content-Type': 'application/json' }
})

apiClient.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result.code === 200 || result.code === 0) {
      return result
    }
    return Promise.reject(new Error(result.message || `请求失败 (${result.code})`))
  },
  (error) => {
    let errorMessage = '网络请求失败'
    if (error.response) {
      const { status, data } = error.response
      if (status === 500) {
        errorMessage = '服务器错误，请稍后重试'
      } else if (data && data.message) {
        errorMessage = data.message
      } else {
        errorMessage = `请求失败 (${status})`
      }
    } else if (error.message) {
      if (error.message.includes('timeout')) {
        errorMessage = '请求超时，请稍后重试'
      } else {
        errorMessage = error.message
      }
    }
    return Promise.reject(new Error(errorMessage))
  }
)

export const promptApi = {
  list: (pageNum = 1, pageSize = 10) => 
    apiClient.get('/api/prompts/list', { params: { pageNum, pageSize } }),
  
  listAll: () => 
    apiClient.get('/api/prompts/list/all'),
  
  get: (id) => 
    apiClient.get(`/api/prompts/${id}`),
  
  getDefault: (sceneCode) => 
    apiClient.get(`/api/prompts/default/${sceneCode}`),
  
  create: (data) => 
    apiClient.post('/api/prompts/create', data),
  
  update: (data) => 
    apiClient.put('/api/prompts/update', data),
  
  delete: (id) => 
    apiClient.delete(`/api/prompts/${id}`),
  
  setDefault: (promptTemplateId, sceneCode) => 
    apiClient.put('/api/prompts/update-default-prompt-template', { promptTemplateId, sceneCode })
}

export default apiClient
