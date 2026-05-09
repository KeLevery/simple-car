<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import {
  Activity,
  Car,
  LayoutDashboard,
  LogOut,
  MessageSquare,
  ShieldCheck,
  Users,
  Wrench
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

const navItems = [
  { to: '/dashboard', label: '运营总览', icon: LayoutDashboard },
  { to: '/users', label: '用户管理', icon: Users },
  { to: '/vehicles', label: '车辆资产', icon: Car },
  { to: '/operations', label: '服务运营', icon: Wrench },
  { to: '/community', label: '社区内容', icon: MessageSquare }
]

const pageTitle = computed(() => {
  const match = navItems.find((item) => item.to === route.path)
  return match?.label || '后台管理'
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
          <span>运营管理台</span>
        </div>
      </div>

      <nav class="nav-list" aria-label="后台导航">
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="nav-item">
          <component :is="item.icon" :size="18" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-card">
        <ShieldCheck :size="18" />
        <div>
          <strong>JWT 已接入</strong>
          <span>所有管理接口需携带 Bearer Token</span>
        </div>
      </div>

      <button class="logout-button" type="button" title="退出登录" @click="logout">
        <LogOut :size="18" />
        <span>退出登录</span>
      </button>
    </aside>

    <main class="workspace">
      <header class="topbar">
        <div>
          <p class="eyebrow">Operations Console</p>
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
