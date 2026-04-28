<script setup lang="ts">
import { computed, shallowRef } from 'vue'
import { Trash2 } from 'lucide-vue-next'
import { adminApi, type CommunityPostItem } from '@/api/admin'
import DataTable from '@/components/DataTable.vue'
import StatusBadge from '@/components/StatusBadge.vue'
import Toolbar from '@/components/Toolbar.vue'
import { useAdminResource } from '@/composables/useAdminResource'

const query = shallowRef('')
const { items, loading, error, refresh } = useAdminResource<CommunityPostItem>(adminApi.communityPosts)

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'userId', label: '用户' },
  { key: 'content', label: '内容' },
  { key: 'likeCount', label: '点赞' },
  { key: 'commentCount', label: '评论' },
  { key: 'isHot', label: '热度' },
  { key: 'createTime', label: '发布时间' }
]

const visiblePosts = computed(() => {
  const keyword = query.value.trim().toLowerCase()
  if (!keyword) return items.value
  return items.value.filter((item) => String(item.content || '').toLowerCase().includes(keyword))
})

async function removePost(row: CommunityPostItem) {
  const ok = window.confirm(`确认删除动态 #${row.id}？`)
  if (!ok) return
  await adminApi.deleteCommunityPost(row.id)
  await refresh()
}

function toPost(row: unknown) {
  return row as CommunityPostItem
}
</script>

<template>
  <Toolbar v-model="query" title="社区内容" placeholder="搜索动态内容" :loading="loading" @refresh="refresh" />
  <DataTable :columns="columns" :rows="visiblePosts" :loading="loading">
    <template #content="{ value }">
      <span class="clamped-text">{{ value }}</span>
    </template>
    <template #isHot="{ value }">
      <StatusBadge :value="Number(value)" :labels="{ 0: '普通', 1: '热门' }" />
    </template>
    <template #actions="{ row }">
      <button class="danger-button" type="button" @click="removePost(toPost(row))">
        <Trash2 :size="15" />
        <span>删除</span>
      </button>
    </template>
  </DataTable>
  <p v-if="error" class="inline-error">{{ error }}</p>
</template>
