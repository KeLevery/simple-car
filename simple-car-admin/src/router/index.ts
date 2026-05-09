import { createRouter, createWebHistory } from 'vue-router'
import { adminApi } from '@/api/admin'
import AdminShell from '@/components/AdminShell.vue'
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import UsersView from '@/views/UsersView.vue'
import VehiclesView from '@/views/VehiclesView.vue'
import OperationsView from '@/views/OperationsView.vue'
import CommunityView from '@/views/CommunityView.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      component: AdminShell,
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'dashboard', component: DashboardView },
        { path: 'users', name: 'users', component: UsersView },
        { path: 'vehicles', name: 'vehicles', component: VehiclesView },
        { path: 'operations', name: 'operations', component: OperationsView },
        { path: 'community', name: 'community', component: CommunityView }
      ]
    }
  ]
})

const verifiedTokens = new Set<string>()

async function verifyAdminToken(token: string) {
  if (verifiedTokens.has(token)) return true
  try {
    await adminApi.session()
    verifiedTokens.add(token)
    return true
  } catch {
    verifiedTokens.delete(token)
    window.localStorage.removeItem('adminToken')
    return false
  }
}

router.beforeEach(async (to) => {
  const token = window.localStorage.getItem('adminToken')
  if (to.meta.requiresAuth && !token) {
    return { name: 'login' }
  }
  if (to.name === 'login' && token) {
    const valid = await verifyAdminToken(token)
    return valid ? { name: 'dashboard' } : true
  }
  if (to.meta.requiresAuth && token) {
    const valid = await verifyAdminToken(token)
    if (!valid) {
      return { name: 'login' }
    }
  }
  return true
})
