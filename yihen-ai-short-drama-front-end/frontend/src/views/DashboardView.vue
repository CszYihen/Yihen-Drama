<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="title-h1">我的工作台</h1>
          <p class="header-subtitle">管理您的短剧创作项目</p>
        </div>
        <div class="header-right">
          <button class="btn btn-primary" @click="createProject">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            新建项目
          </button>
        </div>
      </div>
    </header>
    
    <div class="dashboard-content">
      <!-- 缁熻鍗＄墖 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2z"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalProjects }}</span>
            <span class="stat-label">项目总数</span>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ completedCount }}</span>
            <span class="stat-label">已完成</span>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon warning">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ processingCount }}</span>
            <span class="stat-label">处理中</span>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon accent">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">85%</span>
            <span class="stat-label">一致性</span>
          </div>
        </div>
      </div>
      
      <!-- 项目列表 -->
      <div class="projects-section">
        <div class="section-header">
          <h2 class="title-h3">我的项目</h2>
          <div class="section-header-right">
            <div class="search-box" @click.stop>
              <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/>
                <path d="m21 21-4.35-4.35"/>
              </svg>
              <input
                v-model.trim="searchKeyword"
                class="search-input"
                type="text"
                placeholder="搜索项目名称或描述..."
                @input="handleSearchInput"
                @focus="handleSearchFocus"
              />
              <button v-if="searchKeyword" class="search-clear" @click="clearSearch">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
              <div v-if="showSuggestDropdown" class="search-suggest-dropdown">
                <button
                  v-for="item in searchSuggestions"
                  :key="item"
                  class="search-suggest-item"
                  @click="applySuggestion(item)"
                >
                  <span v-html="highlightKeyword(item)"></span>
                </button>
              </div>
            </div>
            <div class="filter-tabs">
              <button 
                v-for="tab in filterTabs" 
                :key="tab.value"
                class="filter-tab"
                :class="{ active: activeFilter === tab.value }"
                @click="handleFilterChange(tab.value)"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>
        </div>
        
        <div class="projects-grid">
          <div 
            v-for="project in filteredProjects" 
            :key="project.id"
            class="project-card card"
            @click="openProject(project)"
          >
            <div class="project-cover">
              <div class="project-cover-content">
                <div class="cover-decoration">
                  <div class="deco-line deco-line-1"></div>
                  <div class="deco-line deco-line-2"></div>
                  <div class="deco-circle"></div>
                </div>
                <div class="cover-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <rect x="3" y="3" width="18" height="18" rx="2"/>
                    <circle cx="8.5" cy="8.5" r="1.5"/>
                    <path d="M21 15l-5-5L5 21"/>
                  </svg>
                </div>
                <div class="cover-glow"></div>
              </div>
              <div class="project-card-header">
                <div class="project-status" :class="statusClass[project.status]">
                  {{ statusText[project.status] }}
                </div>
                <div class="project-actions">
                  <button class="card-menu-btn" @click.stop="toggleMenu(project.id)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                      <circle cx="12" cy="12" r="1"/>
                      <circle cx="12" cy="5" r="1"/>
                      <circle cx="12" cy="19" r="1"/>
                    </svg>
                  </button>
                  <div v-if="openMenuId === project.id" class="card-dropdown">
                    <button class="dropdown-item" @click.stop="openEditModal(project)">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                      </svg>
                      编辑项目
                    </button>
                    <button class="dropdown-item danger" @click.stop="handleDeleteProject(project.id)">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                      </svg>
                      删除项目
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <div class="project-info">
              <h3 class="project-name" v-html="highlightKeyword(project.name)"></h3>
              <p class="project-desc" v-html="highlightKeyword(project.description || '')"></p>
              <div class="project-meta">
                <span class="project-chapters">{{ project.chapterCount || 0 }}集</span>
                <span class="project-date">{{ formatDate(project.updateTime) }}</span>
              </div>
              <div class="project-progress" v-if="project.status === 'processing'">
                <div class="progress">
                  <div class="progress-bar" :style="{ width: project.progress + '%' }"></div>
                </div>
                <span class="progress-text">{{ project.progress }}%</span>
              </div>
            </div>
          </div>
          
          <!-- 新建项目卡片 -->
          <div v-if="!hasSearchKeyword && projects.length < pageSize" class="project-card card new-project" @click="createProject">
            <div class="new-project-content">
              <div class="new-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
              </div>
              <span>新建项目</span>
            </div>
          </div>
        </div>
        <div class="pagination" v-if="totalProjects > pageSize">
          <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
          <button
            v-for="page in totalPages"
            :key="page"
            class="page-btn"
            :class="{ active: page === currentPage }"
            @click="changePage(page)"
          >
            {{ page }}
          </button>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
        </div>
      </div>
    </div>
    
    <!-- 新建项目弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
          <div class="modal">
            <div class="modal-header">
              <h3 class="title-h3">新建项目</h3>
              <button class="close-btn" @click="showCreateModal = false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-label">项目名称</label>
                <input type="text" class="input" v-model="newProject.name" placeholder="输入项目名称" />
              </div>
              <div class="form-group">
                <label class="form-label">项目描述</label>
                <textarea class="input textarea" v-model="newProject.description" placeholder="输入项目描述（可选）"></textarea>
              </div>
              <div class="form-group">
                <label class="form-label">选择风格</label>
                <div class="style-options">
                  <label 
                    v-for="style in styleOptions" 
                    :key="style.value"
                    class="style-option"
                    :class="{ selected: newProject.style === style.value }"
                  >
                    <input type="radio" v-model="newProject.style" :value="style.value" />
                    <div class="style-preview" :style="{ background: style.preview }"></div>
                    <span>{{ style.label }}</span>
                  </label>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showCreateModal = false">取消</button>
              <button class="btn btn-primary" @click="handleCreateProject" :disabled="!newProject.name">
                创建项目
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 编辑项目弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
          <div class="modal">
            <div class="modal-header">
              <h3 class="title-h3">编辑项目</h3>
              <button class="close-btn" @click="showEditModal = false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-label">项目名称</label>
                <input type="text" class="input" v-model="editProject.name" placeholder="输入项目名称" />
              </div>
              <div class="form-group">
                <label class="form-label">项目描述</label>
                <textarea class="input textarea" v-model="editProject.description" placeholder="输入项目描述（可选）"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showEditModal = false">取消</button>
              <button class="btn btn-primary" @click="handleUpdateProject" :disabled="!editProject.name || loading">
                {{ loading ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <ConfirmDialog
      v-model="showDeleteProjectConfirm"
      title="删除项目"
      message="确定要删除这个项目吗？相关章节也将被删除。"
      confirm-text="删除"
      cancel-text="取消"
      @confirm="confirmDeleteProject"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { projectApi } from '@/api'
import { useGlobalStore } from '@/stores/global'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const globalStore = useGlobalStore()
const toast = useToast()

const activeFilter = ref('all')
const showCreateModal = ref(false)
const showEditModal = ref(false)
const loading = ref(false)
const openMenuId = ref(null)
const searchKeyword = ref('')
const searchSuggestions = ref([])
const showSuggestDropdown = ref(false)
let searchDebounceTimer = null
let suggestDebounceTimer = null
const showDeleteProjectConfirm = ref(false)
const pendingDeleteProjectId = ref(null)

const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '已完成', value: 'completed' },
  { label: '处理中', value: 'processing' },
  { label: '草稿', value: 'draft' }
]

const statusText = {
  0: '草稿',
  1: '处理中',
  2: '已完成'
}

const statusClass = {
  0: 'draft',
  1: 'processing',
  2: 'completed'
}

const styleOptions = [
  { value: 1, label: '写实风格', preview: 'linear-gradient(135deg, #D4AF37 0%, #B8860B 100%)' },
  { value: 2, label: '动漫风格', preview: 'linear-gradient(135deg, #E8C49A 0%, #CD7F32 100%)' },
  { value: 3, label: '油画质感', preview: 'linear-gradient(135deg, #F4E4BA 0%, #D4AF37 100%)' },
  { value: 4, label: '赛博朋克', preview: 'linear-gradient(135deg, #FFD700 0%, #B8860B 100%)' }
]

const newProject = ref({
  name: '',
  description: '',
  style: 1
})

const editProject = ref({
  id: null,
  name: '',
  description: ''
})

const projects = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const totalProjects = ref(0)
const totalPages = computed(() => Math.max(1, Math.ceil(totalProjects.value / pageSize.value)))

const completedCount = computed(() => projects.value.filter(p => p.status === 2).length)
const processingCount = computed(() => projects.value.filter(p => p.status === 1).length)

const hasSearchKeyword = computed(() => searchKeyword.value.trim().length > 0)

const filteredProjects = computed(() => {
  if (hasSearchKeyword.value) return projects.value
  if (activeFilter.value === 'all') return projects.value
  const statusMap = { 'completed': 2, 'processing': 1, 'draft': 0 }
  return projects.value.filter(p => p.status === statusMap[activeFilter.value])
})

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const escapeHtml = (value = '') => {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

const escapeRegExp = (value = '') => {
  return String(value).replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

const highlightKeyword = (value = '') => {
  const safeText = escapeHtml(value)
  const keyword = searchKeyword.value.trim()
  if (!keyword) return safeText
  const pattern = new RegExp(escapeRegExp(keyword), 'gi')
  return safeText.replace(pattern, (matched) => `<span class="search-highlight">${matched}</span>`)
}

const getSearchStatus = () => {
  const statusMap = { completed: 2, processing: 1, draft: 0 }
  return statusMap[activeFilter.value]
}

const handleFilterChange = (filter) => {
  activeFilter.value = filter
  currentPage.value = 1
  fetchProjects()
}

const handleSearchInput = () => {
  if (suggestDebounceTimer) clearTimeout(suggestDebounceTimer)
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer)

  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    searchSuggestions.value = []
    showSuggestDropdown.value = false
    currentPage.value = 1
    fetchProjects()
    return
  }

  suggestDebounceTimer = setTimeout(() => {
    fetchSearchSuggestions(keyword)
  }, 200)

  searchDebounceTimer = setTimeout(() => {
    currentPage.value = 1
    fetchProjects()
  }, 300)
}

const handleSearchFocus = () => {
  if (searchSuggestions.value.length > 0) {
    showSuggestDropdown.value = true
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
  searchSuggestions.value = []
  showSuggestDropdown.value = false
  currentPage.value = 1
  fetchProjects()
}

const applySuggestion = (text) => {
  searchKeyword.value = text
  showSuggestDropdown.value = false
  searchSuggestions.value = []
  currentPage.value = 1
  fetchProjects()
}

const fetchSearchSuggestions = async (prefix) => {
  if (!prefix) {
    searchSuggestions.value = []
    showSuggestDropdown.value = false
    return
  }
  try {
    const result = await projectApi.suggest({ prefix, size: 8 })
    if (result.code === 200 && Array.isArray(result.data)) {
      searchSuggestions.value = result.data
      showSuggestDropdown.value = result.data.length > 0
    } else {
      searchSuggestions.value = []
      showSuggestDropdown.value = false
    }
  } catch (err) {
    console.error('鑾峰彇鎼滅储寤鸿澶辫触:', err)
    searchSuggestions.value = []
    showSuggestDropdown.value = false
  }
}

const createProject = () => {
  showCreateModal.value = true
}

const handleCreateProject = async () => {
  if (!newProject.value.name) return
  
  loading.value = true
  try {
    const result = await projectApi.create({
      name: newProject.value.name,
      description: newProject.value.description,
      style: newProject.value.style
    })
    
    if (result.code === 200 && result.data) {
      projects.value.unshift(result.data)
      showCreateModal.value = false
      newProject.value = { name: '', description: '', style: 1 }
      toast.success('创建成功')
      router.push(`/workspace/${result.data.id}`)
    }
  } catch (err) {
    console.error('创建项目失败:', err)
    toast.error(err.message || '创建失败')
  } finally {
    loading.value = false
  }
}

const openProject = (project) => {
  router.push(`/workspace/${project.id}`)
}

const toggleMenu = (id) => {
  openMenuId.value = openMenuId.value === id ? null : id
}

const handleDeleteProject = (id) => {
  pendingDeleteProjectId.value = id
  showDeleteProjectConfirm.value = true
}

const confirmDeleteProject = async () => {
  const id = pendingDeleteProjectId.value
  if (!id) return
  globalStore.setLoading(true, '删除中...')
  try {
    await projectApi.delete(id)
    projects.value = projects.value.filter(p => p.id !== id)
    openMenuId.value = null
    toast.success('删除成功')
  } catch (err) {
    console.error('删除项目失败:', err)
    toast.error(err.message || '删除失败')
  } finally {
    pendingDeleteProjectId.value = null
    globalStore.setLoading(false)
  }
}

const openEditModal = (project) => {
  editProject.value = {
    id: project.id,
    name: project.name,
    description: project.description || ''
  }
  showEditModal.value = true
  openMenuId.value = null
}

const handleUpdateProject = async () => {
  if (!editProject.value.name) return
  
  loading.value = true
  try {
    const result = await projectApi.update({
      id: editProject.value.id,
      name: editProject.value.name,
      description: editProject.value.description
    })
    
    if (result.code === 200) {
      const index = projects.value.findIndex(p => p.id === editProject.value.id)
      if (index > -1) {
        projects.value[index].name = editProject.value.name
        projects.value[index].description = editProject.value.description
      }
      showEditModal.value = false
      toast.success('修改成功')
    }
  } catch (err) {
    console.error('鏇存柊椤圭洰澶辫触:', err)
    toast.error(err.message || '修改失败')
  } finally {
    loading.value = false
  }
}

const fetchProjects = async () => {
  try {
    const keyword = searchKeyword.value.trim()
    const params = { page: currentPage.value, size: pageSize.value }
    let result
    if (keyword) {
      const status = getSearchStatus()
      result = await projectApi.search({
        ...params,
        keyword,
        status: typeof status === 'number' ? status : undefined
      })
    } else {
      result = await projectApi.list(params)
    }
    if (result.code === 200 && result.data) {
      const pageData = result.data
      projects.value = (pageData.records || pageData.list || []).map((item) => ({
        ...item,
        chapterCount: item.chapterCount ?? item.episodeCount ?? 0
      }))
      totalProjects.value = pageData.total || projects.value.length
      currentPage.value = pageData.current || currentPage.value
    }
  } catch (err) {
    console.error('鑾峰彇椤圭洰鍒楄〃澶辫触:', err)
  }
}

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchProjects()
}

const handleDocumentClick = () => {
  openMenuId.value = null
  showSuggestDropdown.value = false
}

onMounted(() => {
  fetchProjects()
  document.addEventListener('click', handleDocumentClick)
})

onBeforeUnmount(() => {
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer)
  if (suggestDebounceTimer) clearTimeout(suggestDebounceTimer)
  document.removeEventListener('click', handleDocumentClick)
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 32px 40px;
}

.dashboard-header {
  margin-bottom: 40px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-subtitle {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-top: 8px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  backdrop-filter: blur(20px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  background: rgba(212, 175, 55, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 24px;
    height: 24px;
    color: var(--gold-light);
  }
  
  &.success {
    background: rgba(16, 185, 129, 0.15);
    svg { color: var(--success); }
  }
  
  &.warning {
    background: rgba(245, 158, 11, 0.15);
    svg { color: var(--warning); }
  }
  
  &.accent {
    background: rgba(212, 175, 55, 0.15);
    svg { color: var(--gold-primary); }
  }
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-tertiary);
}

.projects-section {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 18px;
}

.page-btn {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: var(--gold-primary);
  color: var(--gold-light);
}

.page-btn.active {
  background: rgba(212, 175, 55, 0.18);
  border-color: rgba(212, 175, 55, 0.55);
  color: var(--gold-light);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-box {
  position: relative;
  width: 320px;
  height: 38px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-glass);
  display: flex;
  align-items: center;
  transition: all var(--transition-base);

  &:focus-within {
    border-color: rgba(212, 175, 55, 0.55);
    box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.12);
  }
}

.search-icon {
  width: 16px;
  height: 16px;
  color: var(--text-tertiary);
  margin-left: 12px;
  flex: 0 0 auto;
}

.search-input {
  flex: 1;
  height: 100%;
  padding: 0 8px;
  border: none;
  background: transparent;
  color: var(--text-primary);
  font-size: 13px;
  outline: none;

  &::placeholder {
    color: var(--text-muted);
  }
}

.search-clear {
  width: 26px;
  height: 26px;
  margin-right: 6px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-base);

  svg {
    width: 14px;
    height: 14px;
  }

  &:hover {
    background: rgba(212, 175, 55, 0.15);
    color: var(--gold-light);
  }
}

.search-suggest-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  z-index: 30;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  box-shadow: var(--shadow-lg);
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  max-height: 220px;
  overflow-y: auto;
}

.search-suggest-item {
  width: 100%;
  padding: 8px 10px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-secondary);
  text-align: left;
  font-size: 13px;
  cursor: pointer;
  transition: all var(--transition-base);

  &:hover {
    background: rgba(212, 175, 55, 0.12);
    color: var(--gold-light);
  }
}

:deep(.search-highlight) {
  color: #F7E7BE;
  font-weight: 600;
  padding: 0 4px;
  border-radius: 4px;
  background: linear-gradient(
    135deg,
    rgba(232, 196, 130, 0.28) 0%,
    rgba(212, 175, 55, 0.34) 48%,
    rgba(184, 134, 11, 0.26) 100%
  );
  box-shadow:
    inset 0 0 0 1px rgba(212, 175, 55, 0.22),
    0 0 10px rgba(212, 175, 55, 0.16);
  text-shadow: 0 0 10px rgba(212, 175, 55, 0.18);
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.filter-tab {
  padding: 8px 16px;
  font-size: 13px;
  color: var(--text-tertiary);
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  cursor: pointer;
  transition: all var(--transition-base);
  
  &:hover {
    color: var(--text-primary);
    border-color: var(--border-hover);
  }
  
  &.active {
    background: var(--gold-primary);
    color: #1A1A1E;
    border-color: var(--gold-primary);
  }
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.project-card {
  cursor: pointer;
  transition: all var(--transition-base);
  overflow: visible;
  position: relative;
  
  &:hover {
    transform: translateY(-4px);
    border-color: var(--gold-primary);
    box-shadow: var(--shadow-gold);
  }
}

.project-cover {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.project-card-header {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 12px;
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  z-index: 10;
  pointer-events: none;
}

.project-actions {
  position: relative;
  z-index: 20;
  pointer-events: auto;
}

.card-menu-btn {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base);
  
  svg {
    color: rgba(255, 255, 255, 0.6);
    width: 14px;
    height: 14px;
  }
  
  &:hover {
    background: rgba(212, 175, 55, 0.2);
    
    svg {
      color: var(--gold-light);
    }
  }
}

.card-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 6px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-lg);
  min-width: 140px;
  z-index: 9999;
  overflow: hidden;
  
  .dropdown-item {
    width: 100%;
    padding: 10px 14px;
    font-size: 13px;
    color: var(--text-secondary);
    background: transparent;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    text-align: left;
    transition: all var(--transition-base);
    
    svg {
      width: 14px;
      height: 14px;
    }
    
    &:hover {
      background: var(--bg-glass);
      color: var(--text-primary);
    }
    
    &.danger {
      color: var(--error);
      
      &:hover {
        background: rgba(239, 68, 68, 0.1);
      }
    }
  }
}

.project-cover-content {
  width: 100%;
  height: 100%;
  background: linear-gradient(145deg, #1C1814 0%, #0D0B08 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.cover-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  
  .deco-line {
    position: absolute;
    background: linear-gradient(90deg, transparent, rgba(212, 175, 55, 0.1), transparent);
    height: 1px;
    
    &-1 {
      top: 20%;
      left: -100%;
      width: 200%;
      transform: rotate(-15deg);
    }
    
    &-2 {
      bottom: 20%;
      left: -50%;
      width: 150%;
      transform: rotate(10deg);
      background: linear-gradient(90deg, transparent, rgba(212, 175, 55, 0.05), transparent);
    }
  }
  
  .deco-circle {
    position: absolute;
    width: 120px;
    height: 120px;
    border: 1px solid rgba(212, 175, 55, 0.08);
    border-radius: 50%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
}

.cover-icon {
  position: relative;
  z-index: 2;
  width: 56px;
  height: 56px;
  background: rgba(212, 175, 55, 0.08);
  border: 1px solid rgba(212, 175, 55, 0.15);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 28px;
    height: 28px;
    color: var(--gold-primary);
    opacity: 0.8;
  }
}

.cover-glow {
  position: absolute;
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, rgba(212, 175, 55, 0.12) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

.project-status {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
  backdrop-filter: blur(10px);
  
  &.completed {
    background: rgba(16, 185, 129, 0.9);
    color: white;
  }
  
  &.processing {
    background: rgba(245, 158, 11, 0.9);
    color: white;
  }
  
  &.draft {
    background: rgba(255, 255, 255, 0.2);
    color: white;
  }
}

.project-info {
  padding: 20px;
}

.project-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.project-desc {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-bottom: 16px;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-muted);
}

.project-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  
  .progress {
    flex: 1;
  }
  
  .progress-text {
    font-size: 12px;
    color: var(--text-tertiary);
    min-width: 36px;
  }
}

.new-project {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 280px;
  border: 2px dashed var(--border-color);
  background: transparent;
  
  &:hover {
    border-color: var(--gold-primary);
    background: rgba(212, 175, 55, 0.05);
  }
}

.new-project-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--text-tertiary);
}

.new-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: var(--bg-glass);
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 24px;
    height: 24px;
  }
}

// 寮圭獥鏍峰紡
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  width: 100%;
  max-width: 520px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid var(--border-color);
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 20px;
    height: 20px;
    color: var(--text-tertiary);
  }
  
  &:hover {
    background: var(--bg-glass);
  }
}

.modal-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.textarea {
  min-height: 100px;
  resize: vertical;
}

.style-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.style-option {
  cursor: pointer;
  
  input {
    display: none;
  }
  
  > div {
    aspect-ratio: 1;
    border-radius: 4px;
    margin-bottom: 8px;
    border: 2px solid transparent;
    transition: all var(--transition-base);
  }
  
  span {
    display: block;
    font-size: 12px;
    color: var(--text-tertiary);
    text-align: center;
  }
  
  &.selected > div {
    border-color: var(--gold-primary);
    box-shadow: 0 0 0 3px var(--gold-glow);
  }
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid var(--border-color);
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

.modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  width: 100%;
  max-width: 520px;
  overflow: hidden;
  will-change: transform, opacity;
}

.modal-enter-active {
  transition: opacity 0.25s ease;
  
  .modal {
    transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.25s ease;
  }
}

.modal-leave-active {
  transition: opacity 0.2s ease;
  
  .modal {
    transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.2s ease;
  }
}

.modal-enter-from {
  opacity: 0;
  
  .modal {
    transform: scale(0.92) translateY(-10px);
    opacity: 0;
  }
}

.modal-leave-to {
  opacity: 0;
  
  .modal {
    transform: scale(0.95) translateY(5px);
    opacity: 0;
  }
}

// Compact button overrides
.dashboard .btn {
  padding: 8px 16px;
  font-size: 12px;
  border-radius: 4px;
}

.dashboard .btn-primary {
  padding: 8px 18px;
  border-radius: 4px;
}

.dashboard .btn-secondary {
  padding: 8px 16px;
  border-radius: 4px;
}
</style>

