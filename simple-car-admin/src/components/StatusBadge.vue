<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value: number | string | null | undefined
  labels?: Record<string, string>
}>()

const defaultLabels: Record<string, string> = {
  '0': '待处理',
  '1': '正常',
  '2': '已完成',
  '3': '已关闭'
}

const statusText = computed(() => {
  const key = String(props.value ?? '')
  return props.labels?.[key] || defaultLabels[key] || '未知'
})

const tone = computed(() => {
  const value = Number(props.value)
  if (value === 0) return 'warning'
  if (value === 1) return 'success'
  if (value === 2) return 'info'
  return 'muted'
})
</script>

<template>
  <span class="status-badge" :class="`status-badge--${tone}`">{{ statusText }}</span>
</template>
