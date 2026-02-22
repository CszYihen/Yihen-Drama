import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''
const TIMEOUT = 300000

const apiClient = axios.create({
  baseURL: API_BASE,
  timeout: TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.response.use(
  (response) => {
    const result = response.data
    // 检查业务状态码（后端Result的code字段）
    if (result.code === 200 || result.code === 0 || response.status === 200) {
      return result
    }
    // 业务码不为200时，提取错误信息
    return Promise.reject(new Error(result.message || `操作失败 (${result.code})`))
  },
  (error) => {
    let errorMessage = '网络请求失败'
    
    if (error.response) {
      const { status, data } = error.response
      // 如果HTTP状态码是200但业务码失败（后端用200 HTTP返回业务错误）
      if (status === 200 && data && data.message) {
        errorMessage = data.message
      } else if (status === 401) {
        errorMessage = '未登录或登录已过期，请重新登录'
      } else if (status === 403) {
        errorMessage = '没有权限执行此操作'
      } else if (status === 404) {
        errorMessage = '请求的资源不存在'
      } else if (status === 500) {
        errorMessage = '服务器错误，请稍后重试'
      } else if (data && data.message) {
        errorMessage = data.message
      } else {
        errorMessage = `请求失败 (${status})`
      }
    } else if (error.request) {
      errorMessage = '网络连接失败，请检查网络'
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

const colorSchemes = [
  ['#667eea', '#764ba2'],
  ['#f093fb', '#f5576c'],
  ['#4facfe', '#00f2fe'],
  ['#fa709a', '#fee140'],
  ['#D4AF37', '#B8860B'],
  ['#E8C49A', '#CD7F32']
]

const generateRandomCover = (name, style) => {
  const schemeIndex = style ? (style - 1) % colorSchemes.length : Math.floor(Math.random() * colorSchemes.length)
  const [color1, color2] = colorSchemes[schemeIndex]
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="225" viewBox="0 0 400 225">
    <defs>
      <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" style="stop-color:${color1}"/>
        <stop offset="100%" style="stop-color:${color2}"/>
      </linearGradient>
    </defs>
    <rect fill="url(#bg)" width="400" height="225"/>
    <text fill="#FFFFFF" font-family="Arial" font-size="24" font-weight="600" x="50%" y="50%" text-anchor="middle" dominant-baseline="middle">${(name || '新项目').substring(0, 12)}</text>
  </svg>`
  return 'data:image/svg+xml,' + encodeURIComponent(svg)
}

export const projectApi = {
  list: (params = {}) => apiClient.get('/api/projects/list', { params }),
  search: (params = {}) => apiClient.get('/api/projects/search', { params }),
  suggest: (params = {}) => apiClient.get('/api/projects/suggest', { params }),
  get: (id) => apiClient.get(`/api/projects/${id}`),
  getProperty: (id) => apiClient.get(`/api/projects/property/${id}`),
  create: (data) => apiClient.post('/api/projects/create', {
    name: data.name,
    description: data.description || '',
    style: data.style,
    cover: generateRandomCover(data.name, data.style)
  }),
  update: (data) => apiClient.put('/api/projects/update', data),
  delete: (id) => apiClient.delete(`/api/projects/${id}`)
}

export const episodeApi = {
  list: (projectId) => apiClient.get(`/api/episodes/project/${projectId}`),
  get: (id) => apiClient.get(`/api/episodes/${id}`),
  create: (projectId, chapterNumber, name, content = '') => 
    apiClient.post('/api/episodes/create', {
      projectId,
      chapterNumber,
      name,
      content
    }),
  update: (data) => apiClient.put('/api/episodes/update', data),
  delete: (id) => apiClient.delete(`/api/episodes/${id}`),
  extract: (episodeId, modelId = null) => 
    apiClient.post('/api/episodes/extract', {
      episodeId,
      modelId
    }),
  getProperty: (episodeId) =>
    apiClient.get(`/api/episodes/property/${episodeId}`),
  generateCharacterImage: (data) =>
    apiClient.post('/api/episodes/generate-character-img', data),
  generateSceneImage: (data) =>
    apiClient.post('/api/episodes/generate-scene-img', data),
  generateCharacterVideo: (data) =>
    apiClient.post('/api/episodes/generate-character-video', data)
}

export const storyboardApi = {
  get: (episodeId) => apiClient.get(`/api/storyboard/${episodeId}`),
  save: (episodeId, data) => apiClient.put(`/api/storyboard`, data, { params: { episodeId } }),
  generate: (data) => apiClient.post(`/api/storyboard/generate`, data),
  deleteShot: (shotId) => apiClient.delete(`/api/storyboard/delete/${shotId}`),
  update: (data) => apiClient.put(`/api/storyboard/update`, data),
  generateFirstFrame: (data) => apiClient.post(`/api/storyboard/first-frame-prompt`, data),
  generateFirstFrameImage: (data) => apiClient.post(`/api/storyboard/first-frame`, data),
  generateShotVideoPrompt: (data) => apiClient.post(`/api/storyboard/shot-video-prompt`, data),
  generateShotVideo: (data) => apiClient.post(`/api/storyboard/generate-shot-video`, data),
  updateFirstFramePrompt: (data) => apiClient.put(`/api/storyboard/update/first-frame-prompt`, data)
}

export const videoTaskApi = {
  get: (id) => apiClient.get(`/api/video-tasks/${id}`),
  getByProject: (projectId) => apiClient.get(`/api/video-tasks/project/${projectId}`),
  submit: (data) => apiClient.post('/api/video-tasks', data),
  getStatus: (taskId) => apiClient.get(`/api/video-tasks/${taskId}/status`),
  getProgress: (taskId) => apiClient.get(`/api/video-tasks/${taskId}/progress`),
  cancel: (taskId) => apiClient.post(`/api/video-tasks/${taskId}/cancel`),
  retry: (taskId) => apiClient.post(`/api/video-tasks/${taskId}/retry`)
}

export const characterApi = {
  add: (data) => apiClient.post('/api/character/add', data),
  update: (data) => apiClient.post('/api/character/update', data),
  delete: (id) => apiClient.delete(`/api/character/${id}`),
  listByProject: (projectId, params = {}) => apiClient.get(`/api/character/project/${projectId}`, { params }),
  searchInProject: (projectId, params = {}) => apiClient.get(`/api/character/${projectId}/search`, { params }),
  suggestInProject: (projectId, params = {}) => apiClient.get(`/api/character/${projectId}/suggest`, { params }),
  upload: (characterId, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post(`/api/character/upload/${characterId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const sceneApi = {
  add: (data) => apiClient.post('/api/scene/add', data),
  update: (data) => apiClient.post('/api/scene/update', data),
  delete: (id) => apiClient.delete(`/api/scene/${id}`),
  listByProject: (projectId, params = {}) => apiClient.get(`/api/scene/project/${projectId}`, { params }),
  searchInProject: (projectId, params = {}) => apiClient.get(`/api/scene/${projectId}/search`, { params }),
  suggestInProject: (projectId, params = {}) => apiClient.get(`/api/scene/${projectId}/suggest`, { params }),
  upload: (sceneId, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post(`/api/scene/upload/${sceneId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const modelInstanceApi = {
  list: (modelType, params = {}) => apiClient.get(`/api/models/model-instance-by-type/${modelType}`, { params }),
  get: (id) => apiClient.get(`/api/models/mod/${id}`),
  create: (data) => apiClient.post('/api/models/add-model-instance', data),
  update: (id, data) => {
    data.id = id
    return apiClient.post('/api/models/update-model-instance', data)
  },
  delete: (id) => apiClient.delete(`/api/models/model-instance/${id}`),
  setDefault: (id) => apiClient.put(`/api/models/mod/${id}/default`),
  duplicate: (id) => apiClient.post(`/api/models/mod/${id}/duplicate`),
  test: (id) => apiClient.post(`/api/models/mod/${id}/test`),
  getDefault: (modelType) => apiClient.get(`/api/models/default-model-instance-by-type/${modelType}`),
  getAllDefaults: () => apiClient.get('/api/models/default-model-instance'),
  updateDefault: (data) => apiClient.put('/api/models/update-default-model-instance', data)
}

export const modelDefinitionApi = {
  list: (params = {}) => apiClient.get('/api/models/model-definition/list', { params }),
  get: (id) => apiClient.get(`/api/models/model-definition/${id}`),
  create: (data) => apiClient.post('/api/models/add-model-definition', data),
  update: (id, data) => apiClient.put(`/api/models/model-definition`, { ...data, id }),
  delete: (id) => apiClient.delete(`/api/models/model-definition/${id}`)
}

export const modelConfigApi = {
  get: () => apiClient.get('/api/model-config'),
  save: (data) => apiClient.put('/api/model-config', data),
  reset: () => apiClient.post('/api/model-config/reset')
}

export const modelProviderApi = {
  list: () => apiClient.get('/api/model-providers'),
  validate: (provider, apiKey) => apiClient.post('/api/model-providers/validate', { provider, apiKey })
}

export default apiClient
