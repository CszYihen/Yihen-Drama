import { ref } from 'vue'

const loadingCount = ref(0)
const loadingMessage = ref('')

export function useGlobalLoading() {
  const isLoading = computed(() => loadingCount.value > 0)
  
  const start = (message = '加载中...') => {
    loadingCount.value++
    loadingMessage.value = message
  }
  
  const stop = () => {
    if (loadingCount.value > 0) {
      loadingCount.value--
    }
  }
  
  const clear = () => {
    loadingCount.value = 0
    loadingMessage.value = ''
  }
  
  return {
    isLoading,
    loadingMessage,
    start,
    stop,
    clear
  }
}

export function useApiWithLoading(apiFunction, options = {}) {
  const { successMessage, errorMessage } = options
  const loading = ref(false)
  const error = ref(null)
  
  const execute = async (...args) => {
    loading.value = true
    error.value = null
    try {
      const result = await apiFunction(...args)
      if (successMessage) {
        // 可以在这里触发成功提示
      }
      return result
    } catch (err) {
      error.value = err.message || errorMessage || '操作失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    loading,
    error,
    execute
  }
}
