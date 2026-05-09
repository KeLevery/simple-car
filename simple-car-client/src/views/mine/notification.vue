<template>
  <div class="notification-page">
    <van-nav-bar
      title="消息通知"
      left-arrow
      fixed
      placeholder
      @click-left="$router.back()"
    />

    <van-cell-group inset title="通知偏好">
      <van-cell title="系统通知">
        <template #right-icon>
          <van-switch v-model="settings.system" size="22px" />
        </template>
      </van-cell>
      <van-cell title="维保提醒">
        <template #right-icon>
          <van-switch v-model="settings.maintenance" size="22px" />
        </template>
      </van-cell>
      <van-cell title="充电提醒">
        <template #right-icon>
          <van-switch v-model="settings.charging" size="22px" />
        </template>
      </van-cell>
      <van-cell title="社区消息">
        <template #right-icon>
          <van-switch v-model="settings.community" size="22px" />
        </template>
      </van-cell>
    </van-cell-group>

    <div class="footer-btn">
      <van-button type="primary" block round :loading="loading" @click="saveSettings">
        保存设置
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { getUserSettings, updateUserSettings } from '@/api/user'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVantCompat } from '@/composables/useVantCompat'

const router = useRouter()
const route = useRoute()
const { toast, notify, dialog } = useVantCompat()
const loading = ref(false)
const settings = ref({
				system: true,
				maintenance: true,
				charging: true,
				community: false
			})
async function loadSettings() {
			try {
				const res = await getUserSettings('notification')
				if (res.code === 200 && res.data && Object.keys(res.data).length > 0) {
					settings.value = {
						system: res.data.system === 'true',
						maintenance: res.data.maintenance === 'true',
						charging: res.data.charging === 'true',
						community: res.data.community === 'true'
					}
					return;
				}
			} catch (e) {
				console.error(e)
			}

			const saved = localStorage.getItem('notification_settings')
			if (saved) {
				try { settings.value = JSON.parse(saved) } catch (err) {}
			}
		}
async function saveSettings() {
			loading.value = true
			toast.loading({ message: '保存中...', forbidClick: true })
			try {
				const data = {
					system: String(settings.value.system),
					maintenance: String(settings.value.maintenance),
					charging: String(settings.value.charging),
					community: String(settings.value.community)
				}
				const res = await updateUserSettings('notification', data)
				toast.clear()
				if (res.code === 200) {
					localStorage.setItem('notification_settings', JSON.stringify(settings.value))
					toast.success('保存成功')
					setTimeout(() => {
						router.back()
					}, 700)
				} else {
					toast(res.msg || '保存失败')
				}
			} catch (e) {
				toast.clear()
				console.error(e)
				localStorage.setItem('notification_settings', JSON.stringify(settings.value))
				toast.success('保存成功')
				setTimeout(() => {
					router.back()
				}, 700)
			} finally {
				loading.value = false
			}
		}
loadSettings();
</script>

<style scoped>
.notification-page {
  min-height: 100vh;
  background-color: var(--bg-primary);

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
    .van-cell-group__title { color: var(--text-tertiary); }
    &::after { border: none; }
  }

  :deep(.van-cell) {
    background: transparent;
    color: var(--text-primary);
    &::after { border-bottom-color: var(--border); }
    .van-cell__title { font-weight: 300; }
  }

  :deep(.van-switch--on) {
    background-color: var(--accent);
  }
}

.footer-btn {
  margin: 32px 16px;
}

:deep(.van-button--primary) {
  background: var(--accent-gradient);
  border: none;
  box-shadow: 0 4px 12px var(--accent-glow);
}
</style>
