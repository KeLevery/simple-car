import { createRouter, createWebHistory } from 'vue-router'
import { adminApi } from '@/api/admin'
import AdminShell from '@/components/AdminShell.vue'
import LoginView from '@/views/LoginView.vue'

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
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'users', name: 'users', component: () => import('@/views/UsersView.vue') },
        { path: 'vehicles', name: 'vehicles', component: () => import('@/views/VehiclesView.vue') },
        { path: 'operations', name: 'operations', component: () => import('@/views/OperationsView.vue') },
        { path: 'community', name: 'community', component: () => import('@/views/CommunityView.vue') }
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
