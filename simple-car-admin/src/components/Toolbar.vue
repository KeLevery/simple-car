<script setup lang="ts">
import { RefreshCcw, Search } from 'lucide-vue-next'

const query = defineModel<string>({ default: '' })

defineProps<{
  title: string
  placeholder?: string
  loading?: boolean
}>()

const emit = defineEmits<{
  refresh: []
}>()
</script>

<template>
  <section class="toolbar">
    <div>
      <p class="eyebrow">管理列表</p>
      <h2>{{ title }}</h2>
    </div>
    <label class="search-box">
      <Search :size="16" />
      <input v-model="query" :placeholder="placeholder || '搜索'" />
    </label>
    <slot name="actions"></slot>
    <button class="icon-button" type="button" title="刷新" :disabled="loading" @click="emit('refresh')">
      <RefreshCcw :size="17" />
    </button>
  </section>
</template>
