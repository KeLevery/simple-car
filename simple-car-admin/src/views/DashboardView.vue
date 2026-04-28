<script setup lang="ts">
import { computed, onMounted, shallowRef } from 'vue'
import { BatteryCharging, Car, CircleDollarSign, MessageSquare, Siren, Users } from 'lucide-vue-next'
import { adminApi, type Overview } from '@/api/admin'

const overview = shallowRef<Overview | null>(null)
const loading = shallowRef(true)
const error = shallowRef('')

const totalRevenue = computed(() => {
  const data = overview.value
  if (!data) return '0.00'
  return Number(data.chargingRevenue + data.maintenanceRevenue).toFixed(2)
})

const statItems = computed(() => {
  const data = overview.value
  return [
    { label: '用户数', value: data?.userCount ?? '-', icon: Users, tone: 'cyan' },
    { label: '车辆数', value: data?.vehicleCount ?? '-', icon: Car, tone: 'green' },
    { label: '维保预约', value: data?.appointmentCount ?? '-', icon: BatteryCharging, tone: 'amber' },
    { label: '救援请求', value: data?.rescueCount ?? '-', icon: Siren, tone: 'coral' },
    { label: '社区动态', value: data?.postCount ?? '-', icon: MessageSquare, tone: 'violet' },
    { label: '总营收', value: `¥${totalRevenue.value}`, icon: CircleDollarSign, tone: 'ink' }
  ]
})

async function loadOverview() {
  loading.value = true
  error.value = ''
  try {
    overview.value = await adminApi.overview()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '总览加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(loadOverview)
</script>

<template>
  <section class="dashboard-grid">
    <article v-for="item in statItems" :key="item.label" class="stat-tile" :class="`stat-tile--${item.tone}`">
      <component :is="item.icon" :size="22" />
      <span>{{ item.label }}</span>
      <strong>{{ loading ? '...' : item.value }}</strong>
    </article>
  </section>

  <section class="ops-strip">
    <div>
      <p class="eyebrow">今日待办</p>
      <h2>{{ overview?.todayAppointmentCount ?? 0 }} 个今日预约</h2>
    </div>
    <div>
      <p class="eyebrow">救援队列</p>
      <h2>{{ overview?.pendingRescueCount ?? 0 }} 个待处理</h2>
    </div>
    <div>
      <p class="eyebrow">站点资源</p>
      <h2>{{ (overview?.chargingStationCount ?? 0) + (overview?.serviceStationCount ?? 0) }} 个服务站点</h2>
    </div>
  </section>

  <p v-if="error" class="inline-error">{{ error }}</p>
</template>
