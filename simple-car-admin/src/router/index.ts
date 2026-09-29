import { createRouter, createWebHistory } from 'vue-router'
import { adminApi } from '@/api/admin'
import { clearAdminToken, getAdminToken } from '@/api/token'
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
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ]
})

/**
 * 仅认证类失败才清 token 登出：
 * - http.ts 对业务错误抛的是普通 Error（无 isAxiosError），如 403「无后台访问权限」
 * - axios 错误中 401/403 属认证失败；网络断开/超时/5xx 属瞬时故障，不应误杀登录态
 */
function isAuthFailure(err: unknown): boolean {
  const axiosErr = err as { isAxiosError?: boolean; response?: { status?: number } } | null
  if (!axiosErr?.isAxiosError) return true
  const status = axiosErr.response?.status
  return status === 401 || status === 403
}

// 每次导航都向后端校验会话，避免本地缓存导致服务端吊销/过期后守卫失效
async function verifyAdminToken() {
  try {
    await adminApi.session()
    return true
  } catch (err) {
    if (isAuthFailure(err)) {
      clearAdminToken()
      return false
    }
    // 网络/服务器瞬时故障：放行，交给业务接口自身的 401 处理兜底
    return true
  }
}

router.beforeEach(async (to) => {
  const token = getAdminToken()
  if (to.meta.requiresAuth && !token) {
    return { name: 'login' }
  }
  if (to.name === 'login' && token) {
    const valid = await verifyAdminToken()
    return valid ? { name: 'dashboard' } : true
  }
  if (to.meta.requiresAuth && token) {
    const valid = await verifyAdminToken()
    if (!valid) {
      return { name: 'login' }
    }
  }
  return true
})
