<template>
  <div class="assets-page">
    <header class="page-header">
      <h1 class="title-h1">资产管理</h1>
      <p class="page-desc">按项目管理角色与场景资产，支持增删改查与搜索</p>
    </header>

    <section class="assets-panel">
      <div class="toolbar">
        <div class="project-select-wrap">
          <label class="toolbar-label">项目</label>
          <select v-model="selectedProjectId" class="toolbar-select" @change="handleProjectChange">
            <option :value="''">请选择项目</option>
            <option v-for="project in projects" :key="project.id" :value="String(project.id)">{{ project.name }}</option>
          </select>
        </div>

        <div class="toolbar-right">
          <div class="search-box" @click.stop>
            <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="m21 21-4.35-4.35"/>
            </svg>
            <input
              v-model.trim="searchKeyword"
              class="search-input"
              type="text"
              :placeholder="activeTab === 'character' ? '搜索角色名称/描述' : '搜索场景名称/描述'"
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
                v-for="item in activeTab === 'character' ? characterSuggestions : sceneSuggestions"
                :key="`${item.id || 's'}-${item.name}`"
                class="search-suggest-item"
                @click="activeTab === 'character' ? applyCharacterSuggestion(item) : applySceneSuggestion(item)"
              >
                <img
                  v-if="activeTab === 'character' ? item.avatar : item.thumbnail"
                  :src="activeTab === 'character' ? item.avatar : item.thumbnail"
                  :alt="item.name"
                  class="suggest-avatar"
                />
                <span>{{ item.name }}</span>
              </button>
            </div>
          </div>

          <button class="btn btn-primary" :disabled="!selectedProjectId" @click="openCreateModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            新增{{ activeTab === 'character' ? '角色' : '场景' }}
          </button>
        </div>
      </div>

      <div class="tab-bar">
        <button class="tab-btn" :class="{ active: activeTab === 'character' }" @click="switchTab('character')">
          角色 ({{ characterTotalCount }})
        </button>
        <button class="tab-btn" :class="{ active: activeTab === 'scene' }" @click="switchTab('scene')">
          场景 ({{ sceneTotalCount }})
        </button>
      </div>

      <div v-if="!selectedProjectId" class="empty-state">
        <p>请先选择项目</p>
      </div>

      <div v-else-if="displayList.length === 0" class="empty-state">
        <p>暂无{{ activeTab === 'character' ? '角色' : '场景' }}数据</p>
      </div>

      <div v-else class="asset-grid">
        <article v-for="item in displayList" :key="item.id" class="asset-card">
          <div class="asset-thumb">
            <img v-if="activeTab === 'character' ? item.avatar : item.thumbnail" :src="activeTab === 'character' ? item.avatar : item.thumbnail" :alt="item.name" />
            <div v-else class="thumb-placeholder">{{ (item.name || '?').charAt(0) }}</div>
          </div>
          <div class="asset-content">
            <h3 class="asset-title">{{ item.name }}</h3>
            <p class="asset-desc">{{ item.description || '暂无描述' }}</p>
            <div class="asset-actions">
              <button class="action-btn" @click="openEditModal(item)">编辑</button>
              <button class="action-btn danger" @click="askDelete(item)">删除</button>
            </div>
          </div>
        </article>
      </div>

      <div v-if="selectedProjectId && showPagination" class="pagination-wrap">
        <button class="page-btn" :disabled="currentPageInfo.current <= 1" @click="changePage(currentPageInfo.current - 1)">
          上一页
        </button>
        <button
          v-for="p in pageNumbers"
          :key="`page-${p}`"
          class="page-btn"
          :class="{ active: p === currentPageInfo.current }"
          @click="changePage(p)"
        >
          {{ p }}
        </button>
        <button class="page-btn" :disabled="currentPageInfo.current >= currentPageInfo.pages" @click="changePage(currentPageInfo.current + 1)">
          下一页
        </button>
      </div>
    </section>

    <Teleport to="body">
      <transition name="modal">
        <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
          <div class="modal">
            <div class="modal-header">
              <h3>{{ editMode === 'create' ? `新增${activeTab === 'character' ? '角色' : '场景'}` : `编辑${activeTab === 'character' ? '角色' : '场景'}` }}</h3>
              <button class="modal-close" @click="closeEditModal">×</button>
            </div>
            <div class="modal-body">
              <div class="form-group" v-if="editMode === 'create'">
                <label>所属章节</label>
                <select v-model="form.episodeId" class="toolbar-select">
                  <option :value="''">请选择章节</option>
                  <option v-for="ep in episodes" :key="ep.id" :value="ep.id">第{{ ep.chapterNumber }}章 - {{ ep.name }}</option>
                </select>
              </div>
              <div class="form-group">
                <label>名称</label>
                <input v-model.trim="form.name" class="form-input" type="text" placeholder="请输入名称" />
              </div>
              <div class="form-group">
                <label>描述</label>
                <textarea v-model.trim="form.description" class="form-input textarea" placeholder="请输入描述"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="closeEditModal">取消</button>
              <button class="btn btn-primary" :disabled="saving || !canSubmit" @click="submitForm">{{ saving ? '保存中...' : '保存' }}</button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <ConfirmDialog
      v-model="showDeleteConfirm"
      :title="`删除${activeTab === 'character' ? '角色' : '场景'}`"
      :message="`确定删除「${deleteTarget?.name || ''}」吗？`"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { projectApi, episodeApi, characterApi, sceneApi } from '@/api'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()

const projects = ref([])
const episodes = ref([])
const selectedProjectId = ref('')
const activeTab = ref('character')

const characters = ref([])
const scenes = ref([])
const sceneBaseList = ref([])
const characterTotalCount = ref(0)
const sceneTotalCount = ref(0)

const searchKeyword = ref('')
const characterSuggestions = ref([])
const sceneSuggestions = ref([])
const showSuggestDropdown = ref(false)
let searchTimer = null
let suggestTimer = null
const PAGE_SIZE = 8
const characterPage = ref({ current: 1, total: 0, pages: 1 })
const scenePage = ref({ current: 1, total: 0, pages: 1 })

const showEditModal = ref(false)
const editMode = ref('create')
const editingId = ref(null)
const saving = ref(false)
const form = ref({ episodeId: '', name: '', description: '' })

const showDeleteConfirm = ref(false)
const deleteTarget = ref(null)

const canSubmit = computed(() => {
  if (!form.value.name) return false
  if (editMode.value === 'create') return !!form.value.episodeId
  return true
})

const displayList = computed(() => (activeTab.value === 'character' ? characters.value : scenes.value))
const currentPageInfo = computed(() => (activeTab.value === 'character' ? characterPage.value : scenePage.value))
const currentTotalCount = computed(() => (activeTab.value === 'character' ? characterTotalCount.value : sceneTotalCount.value))
const showPagination = computed(() => {
  const tabTotal = Number(currentTotalCount.value || 0)
  if (tabTotal > PAGE_SIZE) return true
  const pageTotal = Number(currentPageInfo.value.total || 0)
  if (pageTotal > PAGE_SIZE) return true
  return displayList.value.length > PAGE_SIZE
})
const pageNumbers = computed(() => {
  const pagesByTabTotal = Math.max(1, Math.ceil(Number(currentTotalCount.value || 0) / PAGE_SIZE))
  const pages = Math.max(Number(currentPageInfo.value.pages || 1), pagesByTabTotal)
  return Array.from({ length: pages }, (_, i) => i + 1)
})

const normalizeCharacter = (item) => {
  if (!item) return null
  const id = item.characterId ?? item.id
  if (!id) return null
  return {
    ...item,
    id,
    name: item.name || item.characterName || '未命名角色',
    description: item.description || '',
    avatar: item.avatar || item.thumbnail || ''
  }
}

const normalizeCharacterSuggest = (item) => {
  if (!item) return null
  if (typeof item === 'string') return { id: null, name: item, avatar: '' }
  return {
    id: item.characterId ?? item.id ?? null,
    name: item.name || item.characterName || item.text || '',
    avatar: item.avatar || item.thumbnail || ''
  }
}

const normalizeScene = (item) => {
  if (!item) return null
  const id = item.sceneId ?? item.id
  if (!id) return null
  return {
    ...item,
    id,
    name: item.name || item.sceneName || '未命名场景',
    description: item.description || '',
    thumbnail: item.thumbnail || ''
  }
}

const normalizeSceneSuggest = (item) => {
  if (!item) return null
  if (typeof item === 'string') return { id: null, name: item, thumbnail: '' }
  return {
    id: item.sceneId ?? item.id ?? null,
    name: item.name || item.sceneName || item.text || '',
    thumbnail: item.thumbnail || ''
  }
}

const parsePagedMeta = (data, records, fallbackPage = 1) => {
  const total = Number(data?.total || 0)
  const pages = Number(data?.pages || 0)
  const current = Number(data?.current || fallbackPage)
  if (total > 0) {
    return { total, pages: pages || Math.max(1, Math.ceil(total / PAGE_SIZE)), current }
  }
  const inferredTotal = Array.isArray(records) ? records.length : 0
  return {
    total: inferredTotal,
    pages: Math.max(1, Math.ceil(inferredTotal / PAGE_SIZE)),
    current
  }
}

const applyPaginationFallback = (records, page, meta) => {
  const list = Array.isArray(records) ? records : []
  const shouldFallback = list.length > PAGE_SIZE
  if (!shouldFallback) {
    return { records: list, meta }
  }
  const total = Math.max(meta.total, list.length)
  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE))
  const current = Math.min(Math.max(1, Number(page) || 1), pages)
  const start = (current - 1) * PAGE_SIZE
  return {
    records: list.slice(start, start + PAGE_SIZE),
    meta: { total, pages, current }
  }
}

const loadAssetTotals = async () => {
  if (!selectedProjectId.value) return
  try {
    const [charRes, sceneRes, propertyRes] = await Promise.all([
      characterApi.listByProject(selectedProjectId.value, { page: 1, size: 1 }),
      sceneApi.listByProject(selectedProjectId.value, { page: 1, size: 1 }),
      projectApi.getProperty(selectedProjectId.value)
    ])
    const propertyCharacterTotal = Number(propertyRes?.data?.characters?.length || 0)
    const propertySceneTotal = Number(propertyRes?.data?.scenes?.length || 0)
    if (charRes?.code === 200 && charRes?.data) {
      const records = charRes.data.records || []
      const meta = parsePagedMeta(charRes.data, records, 1)
      characterTotalCount.value = Math.max(meta.total, propertyCharacterTotal)
    } else {
      characterTotalCount.value = propertyCharacterTotal
    }
    if (sceneRes?.code === 200 && sceneRes?.data) {
      const records = sceneRes.data.records || []
      const meta = parsePagedMeta(sceneRes.data, records, 1)
      sceneTotalCount.value = Math.max(meta.total, propertySceneTotal)
    } else {
      sceneTotalCount.value = propertySceneTotal
    }
  } catch {
    // ignore totals failure
  }
}

const loadProjects = async () => {
  try {
    const res = await projectApi.list({ page: 1, size: 100 })
    if (res.code === 200 && res.data) {
      projects.value = res.data.records || []
      if (!selectedProjectId.value && projects.value.length > 0) {
        selectedProjectId.value = String(projects.value[0].id)
        await refreshCurrentProjectAssets()
      }
    }
  } catch (err) {
    toast.error(err.message || '获取项目失败')
  }
}

const loadEpisodes = async () => {
  if (!selectedProjectId.value) return
  try {
    const res = await episodeApi.list(selectedProjectId.value)
    if (res.code === 200 && Array.isArray(res.data)) {
      episodes.value = res.data
    } else {
      episodes.value = []
    }
  } catch {
    episodes.value = []
  }
}

const refreshCurrentProjectAssets = async () => {
  if (!selectedProjectId.value) return
  await loadEpisodes()
  await loadAssetTotals()
  if (activeTab.value === 'character' && searchKeyword.value.trim()) {
    await searchCharacters(searchKeyword.value.trim(), 1)
    return
  }
  if (activeTab.value === 'scene' && searchKeyword.value.trim()) {
    await searchScenes(searchKeyword.value.trim(), 1)
    return
  }
  if (activeTab.value === 'character') {
    await loadCharactersPage(1)
  } else {
    await loadScenesPage(1)
  }
}

const handleProjectChange = async () => {
  clearSearch()
  await refreshCurrentProjectAssets()
}

const switchTab = async (tab) => {
  activeTab.value = tab
  clearSearch()
  await refreshCurrentProjectAssets()
}

const loadCharactersPage = async (page = 1) => {
  if (!selectedProjectId.value) return
  try {
    const res = await characterApi.listByProject(selectedProjectId.value, { page, size: PAGE_SIZE })
    if (res.code === 200 && res.data) {
      const records = (res.data.records || []).map(normalizeCharacter).filter(Boolean)
      const meta = parsePagedMeta(res.data, records, page)
      const fallback = applyPaginationFallback(records, page, meta)
      characters.value = fallback.records
      const total = Math.max(meta.total, Number(characterTotalCount.value || 0))
      const pages = Math.max(fallback.meta.pages, Math.ceil(total / PAGE_SIZE))
      characterPage.value = { current: fallback.meta.current, total: Math.max(total, fallback.meta.total), pages }
      characterTotalCount.value = total
    }
  } catch (err) {
    toast.error(err.message || '获取角色失败')
  }
}

const loadScenesPage = async (page = 1) => {
  if (!selectedProjectId.value) return
  try {
    const res = await sceneApi.listByProject(selectedProjectId.value, { page, size: PAGE_SIZE })
    if (res.code === 200 && res.data) {
      const records = (res.data.records || []).map(normalizeScene).filter(Boolean)
      const meta = parsePagedMeta(res.data, records, page)
      const fallback = applyPaginationFallback(records, page, meta)
      scenes.value = fallback.records
      sceneBaseList.value = [...fallback.records]
      const total = Math.max(meta.total, Number(sceneTotalCount.value || 0))
      const pages = Math.max(fallback.meta.pages, Math.ceil(total / PAGE_SIZE))
      scenePage.value = { current: fallback.meta.current, total: Math.max(total, fallback.meta.total), pages }
      sceneTotalCount.value = total
    }
  } catch (err) {
    toast.error(err.message || '获取场景失败')
  }
}

const searchCharacters = async (keyword, page = 1) => {
  if (!selectedProjectId.value) return
  if (!keyword) {
    await loadCharactersPage(page)
    return
  }
  try {
    const res = await characterApi.searchInProject(selectedProjectId.value, { keyword, page, size: PAGE_SIZE })
    if (res.code === 200 && res.data) {
      const records = (res.data.records || []).map(normalizeCharacter).filter(Boolean)
      const meta = parsePagedMeta(res.data, records, page)
      const fallback = applyPaginationFallback(records, page, meta)
      characters.value = fallback.records
      characterPage.value = { current: fallback.meta.current, total: fallback.meta.total, pages: fallback.meta.pages }
    }
  } catch (err) {
    toast.error(err.message || '角色搜索失败')
  }
}

const searchScenes = async (keyword, page = 1) => {
  if (!selectedProjectId.value) return
  if (!keyword) {
    await loadScenesPage(page)
    return
  }
  try {
    const res = await sceneApi.searchInProject(selectedProjectId.value, { keyword, page, size: PAGE_SIZE })
    if (res.code === 200 && res.data) {
      const records = (res.data.records || []).map(normalizeScene).filter(Boolean)
      const meta = parsePagedMeta(res.data, records, page)
      const fallback = applyPaginationFallback(records, page, meta)
      scenes.value = fallback.records
      scenePage.value = { current: fallback.meta.current, total: fallback.meta.total, pages: fallback.meta.pages }
    }
  } catch (err) {
    toast.error(err.message || '场景搜索失败')
  }
}

const fetchCharacterSuggest = async (prefix) => {
  if (!selectedProjectId.value || !prefix) {
    characterSuggestions.value = []
    showSuggestDropdown.value = false
    return
  }
  try {
    const res = await characterApi.suggestInProject(selectedProjectId.value, { prefix, size: 8 })
    if (res.code === 200 && Array.isArray(res.data)) {
      characterSuggestions.value = res.data.map(normalizeCharacterSuggest).filter((item) => item && item.name)
      showSuggestDropdown.value = characterSuggestions.value.length > 0
    }
  } catch {
    characterSuggestions.value = []
    showSuggestDropdown.value = false
  }
}

const fetchSceneSuggest = async (prefix) => {
  if (!selectedProjectId.value || !prefix) {
    sceneSuggestions.value = []
    showSuggestDropdown.value = false
    return
  }
  try {
    const res = await sceneApi.suggestInProject(selectedProjectId.value, { prefix, size: 8 })
    if (res.code === 200 && Array.isArray(res.data)) {
      sceneSuggestions.value = res.data.map(normalizeSceneSuggest).filter((item) => item && item.name)
      showSuggestDropdown.value = sceneSuggestions.value.length > 0
    } else {
      sceneSuggestions.value = []
      showSuggestDropdown.value = false
    }
  } catch {
    sceneSuggestions.value = []
    showSuggestDropdown.value = false
  }
}

const handleSearchInput = () => {
  if (searchTimer) clearTimeout(searchTimer)
  if (suggestTimer) clearTimeout(suggestTimer)
  const keyword = searchKeyword.value.trim()

  if (!keyword) {
    clearSearch()
    refreshCurrentProjectAssets()
    return
  }

  if (activeTab.value === 'character') {
    suggestTimer = setTimeout(() => fetchCharacterSuggest(keyword), 180)
    searchTimer = setTimeout(() => searchCharacters(keyword, 1), 250)
  } else {
    suggestTimer = setTimeout(() => fetchSceneSuggest(keyword), 180)
    searchTimer = setTimeout(() => searchScenes(keyword, 1), 250)
  }
}

const handleSearchFocus = () => {
  if (activeTab.value === 'character' && characterSuggestions.value.length > 0) {
    showSuggestDropdown.value = true
    return
  }
  if (activeTab.value === 'scene' && sceneSuggestions.value.length > 0) {
    showSuggestDropdown.value = true
  }
}

const applyCharacterSuggestion = async (item) => {
  const name = item?.name || ''
  if (!name) return
  searchKeyword.value = name
  showSuggestDropdown.value = false
  await searchCharacters(name, 1)
}

const applySceneSuggestion = async (item) => {
  const name = item?.name || ''
  if (!name) return
  searchKeyword.value = name
  showSuggestDropdown.value = false
  await searchScenes(name, 1)
}

const changePage = async (page) => {
  const target = Number(page)
  if (!target || target < 1) return
  if (target === currentPageInfo.value.current) return
  const keyword = searchKeyword.value.trim()
  if (activeTab.value === 'character') {
    if (keyword) {
      await searchCharacters(keyword, target)
    } else {
      await loadCharactersPage(target)
    }
  } else {
    if (keyword) {
      await searchScenes(keyword, target)
    } else {
      await loadScenesPage(target)
    }
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
  characterSuggestions.value = []
  sceneSuggestions.value = []
  showSuggestDropdown.value = false
}

const openCreateModal = () => {
  editMode.value = 'create'
  editingId.value = null
  form.value = { episodeId: episodes.value[0]?.id || '', name: '', description: '' }
  showEditModal.value = true
}

const openEditModal = (item) => {
  editMode.value = 'edit'
  editingId.value = item.id
  form.value = { episodeId: '', name: item.name || '', description: item.description || '' }
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
  form.value = { episodeId: '', name: '', description: '' }
  editingId.value = null
}

const submitForm = async () => {
  if (!canSubmit.value || saving.value) return
  saving.value = true
  try {
    if (activeTab.value === 'character') {
      if (editMode.value === 'create') {
        await characterApi.add({
          episodeId: Number(form.value.episodeId),
          name: form.value.name,
          description: form.value.description
        })
      } else {
        await characterApi.update({
          id: editingId.value,
          name: form.value.name,
          description: form.value.description
        })
      }
    } else {
      if (editMode.value === 'create') {
        await sceneApi.add({
          episodeId: Number(form.value.episodeId),
          name: form.value.name,
          description: form.value.description
        })
      } else {
        await sceneApi.update({
          id: editingId.value,
          name: form.value.name,
          description: form.value.description
        })
      }
    }

    toast.success('保存成功')
    closeEditModal()
    await refreshCurrentProjectAssets()
  } catch (err) {
    toast.error(err.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const askDelete = (item) => {
  deleteTarget.value = item
  showDeleteConfirm.value = true
}

const confirmDelete = async () => {
  if (!deleteTarget.value) return
  try {
    if (activeTab.value === 'character') {
      await characterApi.delete(deleteTarget.value.id)
    } else {
      await sceneApi.delete(deleteTarget.value.id)
    }
    toast.success('删除成功')
    await refreshCurrentProjectAssets()
  } catch (err) {
    toast.error(err.message || '删除失败')
  } finally {
    deleteTarget.value = null
  }
}

const handleDocClick = () => {
  showSuggestDropdown.value = false
}

onMounted(async () => {
  document.addEventListener('click', handleDocClick)
  await loadProjects()
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocClick)
  if (searchTimer) clearTimeout(searchTimer)
  if (suggestTimer) clearTimeout(suggestTimer)
})
</script>

<style scoped lang="scss">
.assets-page {
  padding: 32px 40px;
}

.page-header {
  margin-bottom: 24px;
}

.page-desc {
  margin-top: 8px;
  color: var(--text-tertiary);
}

.assets-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.project-select-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 300px;
}

.toolbar-label {
  font-size: 13px;
  color: var(--text-tertiary);
}

.toolbar-select,
.form-input {
  width: 100%;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  color: var(--text-primary);
  border-radius: 8px;
  padding: 10px 12px;
  outline: none;
}

.toolbar-select:focus,
.form-input:focus {
  border-color: rgba(212, 175, 55, 0.6);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.12);
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.search-box {
  position: relative;
  width: 320px;
  height: 40px;
  border-radius: 10px;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
}

.search-icon {
  width: 15px;
  height: 15px;
  color: var(--text-muted);
  margin-left: 12px;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: var(--text-primary);
  font-size: 13px;
  padding: 0 8px;
}

.search-clear {
  width: 24px;
  height: 24px;
  margin-right: 8px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-tertiary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.search-clear svg {
  width: 13px;
  height: 13px;
}

.search-suggest-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  z-index: 30;
  border-radius: 10px;
  background: #17120d;
  border: 1px solid rgba(212, 175, 55, 0.26);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.35);
  padding: 6px;
  max-height: 220px;
  overflow-y: auto;
}

.search-suggest-item {
  width: 100%;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  border-radius: 6px;
  padding: 8px 10px;
  text-align: left;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.search-suggest-item:hover {
  background: rgba(212, 175, 55, 0.12);
  color: var(--gold-light);
}

.suggest-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(212, 175, 55, 0.28);
}

.tab-bar {
  display: flex;
  gap: 10px;
  margin-top: 18px;
  margin-bottom: 18px;
}

.tab-btn {
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;
}

.tab-btn.active {
  border-color: rgba(212, 175, 55, 0.6);
  background: rgba(212, 175, 55, 0.14);
  color: var(--gold-light);
}

.asset-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.asset-card {
  border: 1px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;
  background: var(--bg-secondary);
  display: flex;
  flex-direction: column;
  height: 350px;
}

.asset-thumb {
  width: 100%;
  height: 160px;
  background: rgba(255, 255, 255, 0.03);
}

.asset-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: var(--gold-light);
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.35), rgba(139, 115, 85, 0.35));
}

.asset-content {
  padding: 14px;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.asset-title {
  font-size: 18px;
  margin: 0 0 8px;
}

.asset-desc {
  min-height: 42px;
  font-size: 13px;
  color: var(--text-tertiary);
  margin: 0 0 12px;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 6px;
}

.asset-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.asset-desc::-webkit-scrollbar {
  width: 6px;
}

.asset-desc::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(212, 175, 55, 0.45);
}

.asset-desc::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.04);
}

.action-btn {
  flex: 1;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-secondary);
  padding: 8px 10px;
  cursor: pointer;
}

.action-btn.danger {
  color: #f2a3a3;
  border-color: rgba(239, 68, 68, 0.35);
}

.empty-state {
  min-height: 220px;
  border: 1px dashed var(--border-color);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-tertiary);
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.page-btn {
  min-width: 36px;
  height: 34px;
  padding: 0 10px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  color: var(--text-secondary);
  cursor: pointer;
}

.page-btn:hover:not(:disabled) {
  border-color: rgba(212, 175, 55, 0.45);
  color: var(--text-primary);
}

.page-btn.active {
  border-color: rgba(212, 175, 55, 0.7);
  background: rgba(212, 175, 55, 0.16);
  color: var(--gold-light);
}

.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  width: min(560px, 92vw);
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 14px;
}

.modal-header,
.modal-footer {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-footer {
  border-bottom: none;
  border-top: 1px solid var(--border-color);
  justify-content: flex-end;
  gap: 10px;
}

.modal-close {
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  font-size: 24px;
  cursor: pointer;
}

.modal-body {
  padding: 16px 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.form-group label {
  font-size: 13px;
  color: var(--text-secondary);
}

.textarea {
  min-height: 96px;
  resize: vertical;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
