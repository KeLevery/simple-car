<script setup lang="ts">
import { onBeforeUnmount, shallowRef, watch } from 'vue'
import { Search } from 'lucide-vue-next'
import { adminApi, type UserItem } from '@/api/admin'

const props = defineProps<{
  selectedUser?: UserItem | null
  placeholder?: string
}>()

const model = defineModel<number | null>({ default: null })
const emit = defineEmits<{
  select: [user: UserItem | null]
}>()

const keyword = shallowRef('')
const matches = shallowRef<UserItem[]>([])
const loading = shallowRef(false)
const panelOpen = shallowRef(false)
let timer: ReturnType<typeof setTimeout> | undefined

watch(
  () => props.selectedUser,
  (user) => {
    if (user) {
      keyword.value = formatUser(user)
    }
  },
  { immediate: true }
)

watch(keyword, (value) => {
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => searchUsers(value), 260)
})

onBeforeUnmount(() => {
  if (timer) clearTimeout(timer)
})

async function searchUsers(value = keyword.value) {
  loading.value = true
  try {
    matches.value = await adminApi.users(value.trim() || undefined, 20)
  } finally {
    loading.value = false
  }
}

async function openPanel() {
  panelOpen.value = true
  if (matches.value.length === 0) {
    await searchUsers()
  }
}

function selectUser(user: UserItem) {
  model.value = user.id
  keyword.value = formatUser(user)
  panelOpen.value = false
  emit('select', user)
}

function clearUser() {
  model.value = null
  keyword.value = ''
  matches.value = []
  emit('select', null)
}

function formatUser(user: UserItem) {
  return `#${user.id} ${user.nickName || user.username}${user.phone ? ` · ${user.phone}` : ''}`
}
</script>

<template>
  <div class="user-search">
    <div class="user-search-input">
      <Search :size="16" />
      <input
        v-model="keyword"
        :placeholder="placeholder || '输入用户ID / 账号 / 昵称 / 手机号'"
        @focus="openPanel"
      />
      <button v-if="model" type="button" title="清空用户" @click="clearUser">×</button>
    </div>
    <div v-if="panelOpen" class="user-search-panel">
      <div v-if="loading" class="user-search-state">搜索中</div>
      <template v-else>
        <button
          v-for="user in matches"
          :key="user.id"
          type="button"
          class="user-option"
          @mousedown.prevent="selectUser(user)"
        >
          <strong>#{{ user.id }} {{ user.nickName || user.username }}</strong>
          <span>{{ user.username }} · {{ user.phone || '未填写手机号' }}</span>
        </button>
      </template>
      <div v-if="!loading && matches.length === 0" class="user-search-state">没有匹配用户</div>
    </div>
  </div>
</template>
