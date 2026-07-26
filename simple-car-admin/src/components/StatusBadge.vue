<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value: number | string | null | undefined
  labels?: Record<string, string>
  tones?: Record<string, string>
}>()

const defaultLabels: Record<string, string> = {
  '0': '待处理',
  '1': '正常',
  '2': '已完成',
  '3': '已关闭'
}

const defaultTones: Record<string, string> = {
  '0': 'warning',
  '1': 'success',
  '2': 'info'
}

const statusText = computed(() => {
  const key = String(props.value ?? '')
  return props.labels?.[key] || defaultLabels[key] || '未知'
})

const tone = computed(() => {
  const key = String(props.value ?? '')
  return props.tones?.[key] || defaultTones[key] || 'muted'
})
</script>

<template>
  <span class="status-badge" :class="`status-badge--${tone}`">{{ statusText }}</span>
</template>
