<template>
  <Teleport to="body">
    <transition name="loading">
      <div v-if="visible" class="loading-overlay">
        <div class="loading-content">
          <div class="loading-spinner">
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
          </div>
          <p class="loading-text">{{ text || '正在处理...' }}</p>
          <div class="loading-progress" v-if="progress > 0">
            <div class="progress-bar" :style="{ width: progress + '%' }"></div>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useGlobalStore } from '@/stores/global'

const globalStore = useGlobalStore()
const visible = computed(() => globalStore.loading)
const text = computed(() => globalStore.loadingText)
const progress = ref(0)

const setProgress = (value) => {
  progress.value = value
}

defineExpose({ setProgress })
</script>

<style lang="scss" scoped>
.loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(10, 10, 15, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-content {
  text-align: center;
}

.loading-spinner {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
}

.spinner-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 3px solid transparent;
  
  &:nth-child(1) {
    border-top-color: var(--primary);
    animation: spin 1s linear infinite;
  }
  
  &:nth-child(2) {
    border-right-color: var(--accent);
    animation: spin 1.5s linear infinite reverse;
  }
  
  &:nth-child(3) {
    border-bottom-color: var(--primary-light);
    animation: spin 2s linear infinite;
  }
}

.loading-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.loading-progress {
  width: 200px;
  height: 4px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
  overflow: hidden;
  margin: 0 auto;
  
  .progress-bar {
    height: 100%;
    background: linear-gradient(90deg, var(--primary), var(--accent));
    transition: width 0.2s ease;
  }
}

.loading-enter-active {
  transition: opacity 0.25s ease;
}

.loading-leave-active {
  transition: opacity 0.2s ease;
}

.loading-enter-from,
.loading-leave-to {
  opacity: 0;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
