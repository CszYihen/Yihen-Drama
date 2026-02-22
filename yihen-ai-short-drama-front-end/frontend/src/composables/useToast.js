import { ref } from 'vue'

const toasts = ref([])

export function useToast() {
  function show(message, type = 'info', duration = 3000) {
    const id = Date.now()
    toasts.value.push({ id, message, type })
    setTimeout(() => {
      remove(id)
    }, duration)
  }

  function success(message) {
    show(message, 'success', 3000)
  }

  function error(message) {
    show(message, 'error', 4000)
  }

  function warning(message) {
    show(message, 'warning', 3000)
  }

  function info(message) {
    show(message, 'info', 3000)
  }

  function remove(id) {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  return {
    toasts,
    show,
    success,
    error,
    warning,
    info,
    remove
  }
}
