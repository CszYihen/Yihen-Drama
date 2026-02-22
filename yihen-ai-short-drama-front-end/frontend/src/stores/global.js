import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useGlobalStore = defineStore('global', () => {
  const loading = ref(false)
  const loadingText = ref('')
  const user = ref(null)
  const currentProject = ref(null)
  const sidebarCollapsed = ref(false)

  function setLoading(value, text = '') {
    loading.value = value
    loadingText.value = text
  }

  function setUser(userData) {
    user.value = userData
  }

  function setCurrentProject(project) {
    currentProject.value = project
  }

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return {
    loading,
    loadingText,
    user,
    currentProject,
    sidebarCollapsed,
    setLoading,
    setUser,
    setCurrentProject,
    toggleSidebar
  }
})
