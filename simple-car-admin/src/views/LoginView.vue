<script setup lang="ts">
import { computed, reactive, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { LogIn } from 'lucide-vue-next'
import { login } from '@/api/admin'

const router = useRouter()
const form = reactive({
  username: '13800000000',
  password: '123456'
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
    error.value = err instanceof Error ? err.message : '登录失败'
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
        <p>统一查看用户、车辆、维保、救援和社区运营数据。</p>
      </div>
      <form class="login-form" @submit.prevent="submit">
        <label>
          <span>账号</span>
          <input v-model="form.username" autocomplete="username" />
        </label>
        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" autocomplete="current-password" />
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
