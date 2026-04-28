<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { Activity, Car, LayoutDashboard, LogOut, MessageSquare, Users, Wrench } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

const navItems = [
  { to: '/dashboard', label: '总览', icon: LayoutDashboard },
  { to: '/users', label: '用户', icon: Users },
  { to: '/vehicles', label: '车辆', icon: Car },
  { to: '/operations', label: '服务', icon: Wrench },
  { to: '/community', label: '社区', icon: MessageSquare }
]

const pageTitle = computed(() => {
  const match = navItems.find((item) => item.to === route.path)
  return match?.label || '后台'
})

function logout() {
  window.localStorage.removeItem('adminToken')
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">
          <Activity :size="20" />
        </div>
        <div>
          <strong>Simple Car</strong>
          <span>Admin Console</span>
        </div>
      </div>

      <nav class="nav-list" aria-label="后台导航">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-item"
        >
          <component :is="item.icon" :size="18" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

      <button class="logout-button" type="button" title="退出登录" @click="logout">
        <LogOut :size="18" />
        <span>退出</span>
      </button>
    </aside>

    <main class="workspace">
      <header class="topbar">
        <div>
          <p class="eyebrow">运营中台</p>
          <h1>{{ pageTitle }}</h1>
        </div>
        <div class="topbar-status">
          <span class="status-dot"></span>
          API /dev-api
        </div>
      </header>

      <RouterView />
    </main>
  </div>
</template>
