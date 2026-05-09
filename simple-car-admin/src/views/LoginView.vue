<script setup lang="ts">
import { computed, reactive, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { LogIn, ShieldCheck } from 'lucide-vue-next'
import { login } from '@/api/admin'

const router = useRouter()
const form = reactive({
  username: '',
  password: ''
})
const loading = shallowRef(false)
const error = shallowRef('')

const canSubmit = computed(() => form.username.trim().length > 0 && form.password.length > 0 && !loading.value)

async function submit() {
  if (!canSubmit.value) return
  loading.value = true
  error.value = ''
  try {
    const data = await login(form.username.trim(), form.password)
    window.localStorage.setItem('adminToken', data.token)
    router.push({ name: 'dashboard' })
  } catch (err) {
    error.value = err instanceof Error ? err.message : '登录失败，请检查账号或密码'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-screen">
    <section class="login-panel">
      <div class="login-copy">
        <p class="eyebrow">Simple Car Admin</p>
        <h1>车辆服务运营后台</h1>
        <p>统一管理用户、车辆、维保、救援、站点和社区内容，让运营团队快速发现问题并处理工单。</p>
        <div class="login-security">
          <ShieldCheck :size="18" />
          <span>JWT 鉴权 · CORS 白名单 · 无状态会话</span>
        </div>
      </div>

      <form class="login-form" @submit.prevent="submit">
        <div>
          <p class="eyebrow">Sign in</p>
          <h2>管理员登录</h2>
        </div>
        <label>
          <span>账号</span>
          <input v-model="form.username" autocomplete="username" placeholder="请输入后台账号" />
        </label>
        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="current-password" placeholder="请输入密码" />
        </label>
        <p v-if="error" class="form-error">{{ error }}</p>
        <button class="primary-button" type="submit" :disabled="!canSubmit">
          <LogIn :size="18" />
          <span>{{ loading ? '登录中' : '进入后台' }}</span>
        </button>
      </form>
    </section>
  </main>
</template>
