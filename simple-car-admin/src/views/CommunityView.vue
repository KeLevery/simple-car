<script setup lang="ts">
import { computed } from 'vue'
import { Trash2 } from 'lucide-vue-next'
import { adminApi, type CommunityPostItem } from '@/api/admin'
import DataTable from '@/components/DataTable.vue'
import Pagination from '@/components/Pagination.vue'
import StatusBadge from '@/components/StatusBadge.vue'
import Toolbar from '@/components/Toolbar.vue'
import { usePagedResource } from '@/composables/usePagedResource'

const {
  items, total, pageNum, pageSize, keyword, loading, error,
  refresh, mutate, setPage, setPageSize, setKeyword
} = usePagedResource<CommunityPostItem>(adminApi.communityPosts)

const query = computed({
  get: () => keyword.value,
  set: (value: string) => setKeyword(value)
})

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'userId', label: '用户' },
  { key: 'content', label: '内容' },
  { key: 'likeCount', label: '点赞' },
  { key: 'commentCount', label: '评论' },
  { key: 'isHot', label: '热度' },
  { key: 'createTime', label: '发布时间' }
]

const hotLabels: Record<string, string> = {
  '0': '普通',
  '1': '热门'
}

async function removePost(row: CommunityPostItem) {
  const ok = window.confirm(`确认删除动态 #${row.id}？`)
  if (!ok) return
  await mutate(() => adminApi.deleteCommunityPost(row.id))
}
</script>

<template>
  <Toolbar v-model="query" title="社区内容" placeholder="搜索动态内容" :loading="loading" @refresh="refresh" />
  <DataTable :columns="columns" :rows="items" :loading="loading">
    <template #content="{ value }">
      <span class="clamped-text">{{ value }}</span>
    </template>
    <template #isHot="{ value }">
      <StatusBadge :value="Number(value)" :labels="hotLabels" />
    </template>
    <template #actions="{ row }">
      <button class="danger-button" type="button" @click="removePost(row)">
        <Trash2 :size="15" />
        <span>删除</span>
      </button>
    </template>
  </DataTable>
  <Pagination
    :total="total"
    :page-num="pageNum"
    :page-size="pageSize"
    @update:page-num="setPage"
    @update:page-size="setPageSize"
  />
  <p v-if="error" class="inline-error">{{ error }}</p>
</template>
