<script setup lang="ts">
import { computed, reactive, shallowRef } from 'vue'
import { Ban, CheckCircle2, Pencil, Plus, Trash2 } from 'lucide-vue-next'
import { adminApi, type UserItem, type UserPayload } from '@/api/admin'
import AdminDialog from '@/components/AdminDialog.vue'
import DataTable from '@/components/DataTable.vue'
import Pagination from '@/components/Pagination.vue'
import StatusBadge from '@/components/StatusBadge.vue'
import Toolbar from '@/components/Toolbar.vue'
import { usePagedResource } from '@/composables/usePagedResource'

const dialogOpen = shallowRef(false)
const editingId = shallowRef<number | null>(null)
const form = reactive<UserPayload>({
  username: '',
  password: '',
  nickName: '',
  phone: '',
  status: 1
})
const {
  items, total, pageNum, pageSize, keyword, loading, error,
  refresh, mutate, setPage, setPageSize, setKeyword
} = usePagedResource<UserItem>(adminApi.users)

const query = computed({
  get: () => keyword.value,
  set: (value: string) => setKeyword(value)
})

const userStatusLabels: Record<string, string> = {
  '0': '禁用',
  '1': '正常'
}

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'username', label: '账号' },
  { key: 'nickName', label: '昵称' },
  { key: 'phone', label: '手机号' },
  { key: 'status', label: '状态' },
  { key: 'createdAt', label: '注册时间' }
]

const dialogTitle = computed(() => (editingId.value ? '编辑用户' : '新增用户'))

function openCreate() {
  editingId.value = null
  Object.assign(form, { username: '', password: '', nickName: '', phone: '', status: 1 })
  dialogOpen.value = true
}

function openEdit(row: UserItem) {
  editingId.value = row.id
  Object.assign(form, {
    username: row.username,
    password: '',
    nickName: row.nickName,
    phone: row.phone,
    status: row.status
  })
  dialogOpen.value = true
}

async function saveUser() {
  const payload = { ...form }
  if (editingId.value) {
    // 编辑时账号只读，不回传避免契约耦合
    delete payload.username
    if (!payload.password) {
      delete payload.password
    }
  }
  const ok = await mutate(() =>
    editingId.value ? adminApi.updateUser(editingId.value, payload) : adminApi.createUser(payload)
  )
  if (ok) {
    dialogOpen.value = false
  }
}

async function toggleStatus(user: UserItem) {
  await mutate(() => adminApi.updateUserStatus(user.id, user.status === 1 ? 0 : 1))
}

async function removeUser(user: UserItem) {
  if (!window.confirm(`确认删除用户 ${user.username}？`)) return
  await mutate(() => adminApi.deleteUser(user.id))
}
</script>

<template>
  <Toolbar v-model="query" title="用户管理" placeholder="搜索账号、昵称、手机号" :loading="loading" @refresh="refresh">
    <template #actions>
      <button class="primary-button compact" type="button" @click="openCreate">
        <Plus :size="16" />
        <span>新增用户</span>
      </button>
    </template>
  </Toolbar>

  <DataTable :columns="columns" :rows="items" :loading="loading">
    <template #status="{ value }">
      <StatusBadge :value="Number(value)" :labels="userStatusLabels" />
    </template>
    <template #actions="{ row }">
      <div class="row-actions">
        <button class="text-button" type="button" @click="openEdit(row)">
          <Pencil :size="15" />
          <span>编辑</span>
        </button>
        <button class="text-button" type="button" @click="toggleStatus(row)">
          <component :is="row.status === 1 ? Ban : CheckCircle2" :size="15" />
          <span>{{ row.status === 1 ? '禁用' : '启用' }}</span>
        </button>
        <button class="danger-button" type="button" @click="removeUser(row)">
          <Trash2 :size="15" />
          <span>删除</span>
        </button>
      </div>
    </template>
  </DataTable>

  <Pagination
    :total="total"
    :page-num="pageNum"
    :page-size="pageSize"
    @update:page-num="setPage"
    @update:page-size="setPageSize"
  />

  <AdminDialog :open="dialogOpen" :title="dialogTitle" :error="error" @close="dialogOpen = false" @submit="saveUser">
    <label class="field">
      <span>账号</span>
      <input v-model="form.username" :disabled="Boolean(editingId)" required />
    </label>
    <label class="field">
      <span>{{ editingId ? '新密码（留空不改）' : '密码' }}</span>
      <input v-model="form.password" type="password" :required="!editingId" />
    </label>
    <label class="field">
      <span>昵称</span>
      <input v-model="form.nickName" />
    </label>
    <label class="field">
      <span>手机号</span>
      <input v-model="form.phone" />
    </label>
    <label class="field">
      <span>状态</span>
      <select v-model.number="form.status">
        <option :value="1">正常</option>
        <option :value="0">禁用</option>
      </select>
    </label>
  </AdminDialog>

  <p v-if="error" class="inline-error">{{ error }}</p>
</template>
