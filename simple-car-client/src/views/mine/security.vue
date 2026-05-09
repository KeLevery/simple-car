<template>
  <div class="security-container">
    <van-nav-bar
      title="账号安全"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <van-cell-group inset class="mt-15">
      <van-field
        v-model="passwordForm.oldPassword"
        type="password"
        label="旧密码"
        placeholder="请输入旧密码"
        input-align="right"
      />
      <van-field
        v-model="passwordForm.newPassword"
        type="password"
        label="新密码"
        placeholder="请输入新密码"
        input-align="right"
      />
      <van-field
        v-model="passwordForm.confirmPassword"
        type="password"
        label="确认密码"
        placeholder="请再次输入新密码"
        input-align="right"
      />
    </van-cell-group>

    <div class="save-btn-wrap">
      <van-button
        round
        block
        type="info"
        color="#4b6efd"
        :loading="loading"
        @click="handleSave"
      >
        确认修改
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { changePassword } from '@/api/user'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

defineOptions({ name: 'SecuritySettings' })
const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const passwordForm = ref({
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
const loading = ref(false)
async function handleSave() {
      const { oldPassword, newPassword, confirmPassword } = passwordForm.value
      
      if (!oldPassword) {
        toast('请输入旧密码')
        return
      }
      if (!newPassword) {
        toast('请输入新密码')
        return
      }
      if (newPassword !== confirmPassword) {
        toast('两次输入的新密码不一致')
        return
      }
      
      loading.value = true
      try {
        const res = await changePassword({
          oldPassword,
          newPassword
        })
        if (res.code === 200) {
          toast.success('密码修改成功')
          setTimeout(() => {
            router.back()
          }, 1000)
        } else {
          toast.fail(res.msg || '修改失败')
        }
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
</script>

<style lang="scss" scoped>
.security-container {
  min-height: 100vh;
  background-color: var(--bg-primary);
  padding-bottom: 20px;

  :deep(.van-nav-bar) {
    background: var(--bg-glass);
    backdrop-filter: blur(var(--card-blur));
    border-bottom: 1px solid var(--border);
    .van-nav-bar__title { color: var(--text-primary); font-weight: 300; }
    .van-icon { color: var(--accent); }
  }

  :deep(.van-cell-group) {
    background: var(--bg-card);
    border: 1px solid var(--border);
    backdrop-filter: blur(var(--card-blur));
    &::after { border: none; }
  }

  :deep(.van-cell) {
    background: transparent;
    color: var(--text-primary);
    &::after { border-bottom-color: var(--border); }
    .van-field__label { color: var(--text-secondary); }
    .van-field__control { color: var(--text-primary); }
  }
}
.mt-15 {
  margin-top: 15px;
}
.save-btn-wrap {
  margin: 30px 20px;
  :deep(.van-button--info) {
    background: var(--accent-gradient);
    border: none;
    font-weight: 300;
    letter-spacing: 2px;
    box-shadow: 0 4px 12px var(--accent-glow);
  }
}
</style>
