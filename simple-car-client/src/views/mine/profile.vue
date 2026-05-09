<template>
  <div class="profile-container">
    <van-nav-bar
      title="个人资料"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <van-cell-group inset class="mt-15">
      <van-field
        v-model="userForm.nickName"
        label="昵称"
        placeholder="请输入昵称"
        input-align="right"
      />
      <van-field
        v-model="userForm.phone"
        label="手机号"
        placeholder="请输入手机号"
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
        保存
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { updateProfile } from '@/api/user'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

defineOptions({ name: 'ProfileEdit' })
const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const userForm = ref({
        nickName: '',
        phone: ''
      })
const loading = ref(false)
async function handleSave() {
      if (!userForm.value.nickName.trim()) {
        toast('昵称不能为空')
        return
      }
      
      loading.value = true
      try {
        const res = await updateProfile(userForm.value)
        if (res.code === 200) {
          toast.success('更新成功')
          // 更新本地存储
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          userInfo.nickName = userForm.value.nickName
          userInfo.phone = userForm.value.phone
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          
          setTimeout(() => {
            router.back()
          }, 1000)
        } else {
          toast.fail(res.msg || '更新失败')
        }
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo) {
      userForm.value.nickName = userInfo.nickName || ''
      userForm.value.phone = userInfo.phone || ''
    }
</script>

<style lang="scss" scoped>
.profile-container {
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
