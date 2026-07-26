<script setup lang="ts">
import { computed } from 'vue'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'

const props = defineProps<{
  total: number
  pageNum: number
  pageSize: number
}>()

const emit = defineEmits<{
  'update:pageNum': [value: number]
  'update:pageSize': [value: number]
}>()

const pageSizes = [10, 20, 50]

const pageCount = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))

function goTo(page: number) {
  const next = Math.min(Math.max(1, page), pageCount.value)
  if (next !== props.pageNum) {
    emit('update:pageNum', next)
  }
}

function changeSize(event: Event) {
  const size = Number((event.target as HTMLSelectElement).value)
  emit('update:pageSize', size)
}
</script>

<template>
  <div class="pagination">
    <span class="pagination-total">共 {{ total }} 条</span>
    <button class="icon-button" type="button" title="上一页" :disabled="pageNum <= 1" @click="goTo(pageNum - 1)">
      <ChevronLeft :size="16" />
    </button>
    <span class="pagination-page">{{ pageNum }} / {{ pageCount }}</span>
    <button class="icon-button" type="button" title="下一页" :disabled="pageNum >= pageCount" @click="goTo(pageNum + 1)">
      <ChevronRight :size="16" />
    </button>
    <select class="pagination-size" :value="pageSize" @change="changeSize">
      <option v-for="size in pageSizes" :key="size" :value="size">{{ size }} 条/页</option>
    </select>
  </div>
</template>
