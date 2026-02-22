<template>
  <div class="app-container">
  <Sidebar :collapsed="globalStore.sidebarCollapsed" @toggle="globalStore.toggleSidebar()" />
  <main class="main-content" :class="{ 'sidebar-collapsed': globalStore.sidebarCollapsed }">
      <RouterView v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </RouterView>
    </main>
    <LoadingOverlay v-if="globalStore.loading" />
    <Toast />
  </div>
</template>

<script setup>
import Sidebar from '@/components/layout/Sidebar.vue'
import LoadingOverlay from '@/components/common/LoadingOverlay.vue'
import Toast from '@/components/common/Toast.vue'
import { useGlobalStore } from '@/stores/global'

const globalStore = useGlobalStore()
</script>

<style lang="scss">
.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.main-content {
  flex: 1;
  margin-left: 260px;
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
  min-width: 0;
  background: transparent;
  transition: margin-left 0.3s ease;

  &.sidebar-collapsed {
    margin-left: 80px;
  }

  &::-webkit-scrollbar {
    width: 10px;
  }

  &::-webkit-scrollbar-track {
    background: #1a1815;
  }

  &::-webkit-scrollbar-thumb {
    background: #8B7355;
    border-radius: 5px;
    border: 2px solid transparent;
    background-clip: padding-box;

    &:hover {
      background: #D4AF37;
    }
  }
}

.fade-enter-active {
  transition: opacity 0.2s ease;
}

.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
