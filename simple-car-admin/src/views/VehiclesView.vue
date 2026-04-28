<script setup lang="ts">
import { computed, reactive, shallowRef } from 'vue'
import { Pencil, Plus, Trash2 } from 'lucide-vue-next'
import { adminApi, type UserItem, type VehicleItem, type VehiclePayload } from '@/api/admin'
import AdminDialog from '@/components/AdminDialog.vue'
import DataTable from '@/components/DataTable.vue'
import StatusBadge from '@/components/StatusBadge.vue'
import Toolbar from '@/components/Toolbar.vue'
import UserSearchSelect from '@/components/UserSearchSelect.vue'

const query = shallowRef('')
const selectedUserId = shallowRef<number | null>(null)
const selectedUser = shallowRef<UserItem | null>(null)
const formUser = shallowRef<UserItem | null>(null)
const vehicles = shallowRef<VehicleItem[]>([])
const loading = shallowRef(false)
const error = shallowRef('')
const dialogOpen = shallowRef(false)
const editingId = shallowRef<number | null>(null)

const form = reactive<VehiclePayload>({
  userId: 0,
  carName: '',
  carModels: '',
  licenseTag: '',
  frameNumber: '',
  enduranceMileage: 500,
  remainingPower: 100,
  temperature: 26,
  carState: 1
})

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'userId', label: '用户ID' },
  { key: 'userName', label: '所属用户' },
  { key: 'carName', label: '车辆' },
  { key: 'carModels', label: '车型' },
  { key: 'licenseTag', label: '车牌' },
  { key: 'remainingPower', label: '电量' },
  { key: 'enduranceMileage', label: '续航' },
  { key: 'carState', label: '状态' }
]

const dialogTitle = computed(() => (editingId.value ? '编辑车辆' : '新增车辆'))

const visibleVehicles = computed(() => {
  const keyword = query.value.trim().toLowerCase()
  if (!keyword) return vehicles.value
  return vehicles.value.filter((item) =>
    [item.carName, item.carModels, item.licenseTag, item.userName, item.username].some((value) =>
      String(value || '').toLowerCase().includes(keyword)
    )
  )
})

async function handleUserSelect(user: UserItem | null) {
  selectedUser.value = user
  selectedUserId.value = user?.id || null
  await loadVehicles()
}

async function loadVehicles() {
  if (!selectedUserId.value) {
    vehicles.value = []
    return
  }
  loading.value = true
  error.value = ''
  try {
    vehicles.value = await adminApi.vehicles(selectedUserId.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '车辆加载失败'
  } finally {
    loading.value = false
  }
}

function refresh() {
  return loadVehicles()
}

function openCreate() {
  editingId.value = null
  formUser.value = selectedUser.value
  Object.assign(form, {
    userId: selectedUserId.value || 0,
    carName: '',
    carModels: '',
    licenseTag: '',
    frameNumber: '',
    enduranceMileage: 500,
    remainingPower: 100,
    temperature: 26,
    carState: 1
  })
  dialogOpen.value = true
}

function openEdit(row: VehicleItem) {
  editingId.value = row.id
  formUser.value = vehicleUser(row)
  Object.assign(form, {
    userId: row.userId,
    carName: row.carName,
    carModels: row.carModels,
    licenseTag: row.licenseTag,
    frameNumber: row.frameNumber,
    enduranceMileage: row.enduranceMileage,
    remainingPower: row.remainingPower,
    temperature: row.temperature,
    carState: row.carState
  })
  dialogOpen.value = true
}

async function saveVehicle() {
  if (!form.userId) {
    error.value = '请先搜索并选择所属用户'
    return
  }
  if (editingId.value) {
    await adminApi.updateVehicle(editingId.value, { ...form })
  } else {
    await adminApi.createVehicle({ ...form })
  }
  selectedUserId.value = form.userId
  selectedUser.value = formUser.value
  dialogOpen.value = false
  await loadVehicles()
}

async function removeVehicle(row: VehicleItem) {
  if (!window.confirm(`确认删除车辆 ${row.licenseTag}？`)) return
  await adminApi.deleteVehicle(row.id)
  await loadVehicles()
}

function handleFormUserSelect(user: UserItem | null) {
  formUser.value = user
  form.userId = user?.id || 0
}

function vehicleUser(row: VehicleItem): UserItem {
  return {
    id: row.userId,
    username: row.username,
    nickName: row.userName,
    phone: '',
    status: 1,
    createdAt: ''
  }
}

function toVehicle(row: unknown) {
  return row as VehicleItem
}
</script>

<template>
  <Toolbar v-model="query" title="车辆资产" placeholder="搜索当前用户下的车辆、车型、车牌" :loading="loading" @refresh="refresh">
    <template #actions>
      <UserSearchSelect
        v-model="selectedUserId"
        :selected-user="selectedUser"
        placeholder="搜用户ID / 账号 / 昵称 / 手机号"
        @select="handleUserSelect"
      />
      <button class="primary-button compact" type="button" @click="openCreate">
        <Plus :size="16" />
        <span>新增车辆</span>
      </button>
    </template>
  </Toolbar>

  <div v-if="!selectedUserId" class="empty-hint">先搜索并选择一个用户，再管理该用户名下车辆。</div>

  <DataTable v-else :columns="columns" :rows="visibleVehicles" :loading="loading">
    <template #remainingPower="{ value }">{{ value ?? 0 }}%</template>
    <template #enduranceMileage="{ value }">{{ value ?? 0 }} km</template>
    <template #carState="{ value }">
      <StatusBadge :value="Number(value)" :labels="{ 0: '离线', 1: '在线', 2: '静止', 3: '启动' }" />
    </template>
    <template #actions="{ row }">
      <div class="row-actions">
        <button class="text-button" type="button" @click="openEdit(toVehicle(row))">
          <Pencil :size="15" />
          <span>编辑</span>
        </button>
        <button class="danger-button" type="button" @click="removeVehicle(toVehicle(row))">
          <Trash2 :size="15" />
          <span>删除</span>
        </button>
      </div>
    </template>
  </DataTable>

  <AdminDialog :open="dialogOpen" :title="dialogTitle" @close="dialogOpen = false" @submit="saveVehicle">
    <label class="field">
      <span>所属用户</span>
      <UserSearchSelect
        v-model="form.userId"
        :selected-user="formUser"
        placeholder="搜用户ID / 账号 / 昵称 / 手机号"
        @select="handleFormUserSelect"
      />
    </label>
    <label class="field">
      <span>车辆名称</span>
      <input v-model="form.carName" required />
    </label>
    <label class="field">
      <span>车型</span>
      <input v-model="form.carModels" required />
    </label>
    <label class="field">
      <span>车牌</span>
      <input v-model="form.licenseTag" required />
    </label>
    <label class="field">
      <span>车架号</span>
      <input v-model="form.frameNumber" required />
    </label>
    <div class="form-grid">
      <label class="field">
        <span>续航 km</span>
        <input v-model.number="form.enduranceMileage" type="number" min="0" />
      </label>
      <label class="field">
        <span>电量 %</span>
        <input v-model.number="form.remainingPower" type="number" min="0" max="100" />
      </label>
      <label class="field">
        <span>温度</span>
        <input v-model.number="form.temperature" type="number" />
      </label>
      <label class="field">
        <span>状态</span>
        <select v-model.number="form.carState">
          <option :value="0">离线</option>
          <option :value="1">在线</option>
          <option :value="2">静止</option>
          <option :value="3">启动</option>
        </select>
      </label>
    </div>
  </AdminDialog>

  <p v-if="error" class="inline-error">{{ error }}</p>
</template>
