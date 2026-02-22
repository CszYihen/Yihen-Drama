<template>
  <aside class="sidebar" :class="{ collapsed: collapsed }">
    <div class="sidebar-header">
      <div class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 40 40" fill="none">
            <rect x="4" y="8" width="32" height="24" rx="4" stroke="url(#logo-grad)" stroke-width="2"/>
            <circle cx="12" cy="16" r="2" fill="url(#logo-grad)"/>
            <circle cx="20" cy="16" r="2" fill="url(#logo-grad)"/>
            <circle cx="28" cy="16" r="2" fill="url(#logo-grad)"/>
            <line x1="8" y1="24" x2="32" y2="24" stroke="url(#logo-grad)" stroke-width="2" stroke-linecap="round"/>
            <defs>
              <linearGradient id="logo-grad" x1="4" y1="8" x2="36" y2="32">
                <stop stop-color="#8B5CF6"/>
                <stop offset="1" stop-color="#06B6D4"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <span class="logo-text" v-if="!collapsed">AI短剧生成</span>
        <button class="collapse-btn" @click="$emit('toggle')">
          <svg v-if="collapsed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
    </div>

    <nav class="sidebar-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ active: $route.path === item.path }"
      >
        <span class="nav-icon" v-html="item.icon"></span>
        <span class="nav-label" v-if="!collapsed">{{ item.label }}</span>
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <div class="user-info">
        <div class="avatar">
          <span>U</span>
        </div>
        <div class="user-detail" v-if="!collapsed">
          <span class="user-name">用户</span>
          <span class="user-plan">专业版</span>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle'])

const navItems = [
  {
    path: '/',
    label: '工作台',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <rect x="3" y="3" width="7" height="9" rx="1"/>
      <rect x="14" y="3" width="7" height="5" rx="1"/>
      <rect x="14" y="12" width="7" height="9" rx="1"/>
      <rect x="3" y="16" width="7" height="5" rx="1"/>
    </svg>`
  },
  {
    path: '/tasks',
    label: '任务管理',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <polygon points="5 3 19 12 5 21 5 3"/>
    </svg>`
  },
  {
    path: '/assets',
    label: '资产管理',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <rect x="3" y="4" width="18" height="6" rx="1.5"/>
      <rect x="3" y="14" width="18" height="6" rx="1.5"/>
      <path d="M8 7h8M8 17h8"/>
    </svg>`
  },
  {
    path: '/prompts',
    label: '提示词管理',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path d="M14.5 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V7.5L14.5 2z"/>
      <polyline points="14 2 14 8 20 8"/>
      <line x1="16" y1="13" x2="8" y2="13"/>
      <line x1="16" y1="17" x2="8" y2="17"/>
      <polyline points="10 9 9 9 8 9"/>
    </svg>`
  },
  {
    path: '/settings',
    label: '模型管理',
    icon: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <circle cx="12" cy="12" r="3"/>
      <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>
    </svg>`
  }
]
</script>

<style lang="scss" scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 260px;
  background: rgba(20, 18, 15, 0.95);
  backdrop-filter: blur(10px);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-base);
  z-index: 100;
  overflow: hidden;
  min-height: 0;

  &.collapsed {
    width: 80px;
  }

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(139, 115, 85, 0.5);
    border-radius: 3px;

    &:hover {
      background: #8B7355;
    }
  }
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid var(--border-color);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.collapse-btn {
  position: absolute;
  right: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base);
  z-index: 10;

  svg {
    width: 14px;
    height: 14px;
  }

  &:hover {
    background: var(--bg-hover);
    color: var(--text-primary);
    border-color: var(--gold-primary);
  }
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  flex-shrink: 0;

  svg {
    width: 100%;
    height: 100%;
  }
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--gold-light), var(--gold-primary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  white-space: nowrap;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 4px;
  color: var(--text-tertiary);
  text-decoration: none;
  transition: all var(--transition-base);
  cursor: pointer;

  &:hover {
    background: var(--bg-glass);
    color: var(--text-primary);
  }

  &.active {
    background: linear-gradient(135deg, rgba(212, 175, 55, 0.15), rgba(212, 175, 55, 0.08));
    color: var(--gold-light);
    border: 1px solid rgba(212, 175, 55, 0.3);
  }
}

.nav-icon {
  width: 22px;
  height: 22px;
  flex-shrink: 0;

  :deep(svg) {
    width: 100%;
    height: 100%;
  }
}

.nav-label {
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: var(--gold-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  color: #1A1A1E;
  flex-shrink: 0;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.user-plan {
  font-size: 12px;
  color: var(--text-tertiary);
}
</style>
