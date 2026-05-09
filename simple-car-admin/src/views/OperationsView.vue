<script setup lang="ts">
import { computed, reactive, shallowRef } from 'vue'
import { CheckCircle2, Clock3, PauseCircle, Pencil, Plus, Trash2 } from 'lucide-vue-next'
import {
  adminApi,
  type AppointmentItem,
  type ChargingStationItem,
  type ChargingStationPayload,
  type RescueItem,
  type ServiceStationItem,
  type ServiceStationPayload
} from '@/api/admin'
import AdminDialog from '@/components/AdminDialog.vue'
import DataTable from '@/components/DataTable.vue'
import StatusBadge from '@/components/StatusBadge.vue'
import Toolbar from '@/components/Toolbar.vue'
import { useAdminResource } from '@/composables/useAdminResource'

type TabKey = 'appointments' | 'rescues' | 'charging' | 'service'

const query = shallowRef('')
const activeTab = shallowRef<TabKey>('appointments')
const stationDialog = shallowRef<'charging' | 'service' | null>(null)
const editingStationId = shallowRef<number | null>(null)

const appointments = useAdminResource<AppointmentItem>(adminApi.appointments)
const rescues = useAdminResource<RescueItem>(adminApi.rescues)
const chargingStations = useAdminResource<ChargingStationItem>(adminApi.chargingStations)
const serviceStations = useAdminResource<ServiceStationItem>(adminApi.serviceStations)

const chargingForm = reactive<ChargingStationPayload>({
  stationName: '',
  cityId: '',
  address: '',
  totalPiles: 0,
  availablePiles: 0,
  status: 1
})

const serviceForm = reactive<ServiceStationPayload>({
  serviceStationName: '',
  cityId: '',
  address: '',
  phone: '',
  status: 1
})

const tabs = [
  { key: 'appointments', label: '维保预约' },
  { key: 'rescues', label: '救援请求' },
  { key: 'charging', label: '充电站' },
  { key: 'service', label: '服务站' }
] as const

const appointmentColumns = [
  { key: 'workNo', label: '工单号' },
  { key: 'customerName', label: '客户' },
  { key: 'customerPhone', label: '电话' },
  { key: 'appointDate', label: '预约日期' },
  { key: 'totalAmount', label: '金额' },
  { key: 'status', label: '状态' }
]

const rescueColumns = [
  { key: 'id', label: 'ID' },
  { key: 'contactName', label: '联系人' },
  { key: 'contactPhone', label: '电话' },
  { key: 'location', label: '位置' },
  { key: 'status', label: '状态' },
  { key: 'createTime', label: '创建时间' }
]

const chargingColumns = [
  { key: 'stationName', label: '充电站' },
  { key: 'cityId', label: '城市' },
  { key: 'address', label: '地址' },
  { key: 'availablePiles', label: '可用桩' },
  { key: 'totalPiles', label: '总桩数' },
  { key: 'status', label: '状态' }
]

const serviceColumns = [
  { key: 'serviceStationName', label: '服务站' },
  { key: 'cityId', label: '城市' },
  { key: 'address', label: '地址' },
  { key: 'phone', label: '电话' },
  { key: 'status', label: '状态' }
]

const appointmentStatusLabels: Record<string, string> = {
  '0': '待确认',
  '1': '处理中',
  '2': '已完成'
}

const rescueStatusLabels: Record<string, string> = {
  '0': '待处理',
  '1': '救援中',
  '2': '已完成'
}

const stationStatusLabels: Record<string, string> = {
  '0': '停用',
  '1': '运营'
}

const filteredAppointments = computed(() => filterRows(appointments.items.value, ['workNo', 'customerName', 'customerPhone']))
const filteredRescues = computed(() => filterRows(rescues.items.value, ['contactName', 'contactPhone', 'location']))
const filteredChargingStations = computed(() => filterRows(chargingStations.items.value, ['stationName', 'cityId', 'address']))
const filteredServiceStations = computed(() => filterRows(serviceStations.items.value, ['serviceStationName', 'cityId', 'address']))

const loading = computed(() => {
  if (activeTab.value === 'appointments') return appointments.loading.value
  if (activeTab.value === 'rescues') return rescues.loading.value
  if (activeTab.value === 'charging') return chargingStations.loading.value
  return serviceStations.loading.value
})

const error = computed(() =>
  appointments.error.value || rescues.error.value || chargingStations.error.value || serviceStations.error.value
)

const stationDialogTitle = computed(() => {
  const prefix = editingStationId.value ? '编辑' : '新增'
  return stationDialog.value === 'service' ? `${prefix}服务站` : `${prefix}充电站`
})

function filterRows<T extends object>(rows: T[], keys: Array<keyof T>) {
  const keyword = query.value.trim().toLowerCase()
  if (!keyword) return rows
  return rows.filter((row) => keys.some((key) => String(row[key] || '').toLowerCase().includes(keyword)))
}

async function refreshActive() {
  if (activeTab.value === 'appointments') await appointments.refresh()
  if (activeTab.value === 'rescues') await rescues.refresh()
  if (activeTab.value === 'charging') await chargingStations.refresh()
  if (activeTab.value === 'service') await serviceStations.refresh()
}

async function nextAppointmentStatus(row: AppointmentItem) {
  await adminApi.updateAppointmentStatus(row.id, row.status >= 2 ? 0 : row.status + 1)
  await appointments.refresh()
}

async function nextRescueStatus(row: RescueItem) {
  await adminApi.updateRescueStatus(row.id, row.status >= 2 ? 0 : row.status + 1)
  await rescues.refresh()
}

async function toggleChargingStation(row: ChargingStationItem) {
  await adminApi.updateChargingStationStatus(row.id, row.status === 1 ? 0 : 1)
  await chargingStations.refresh()
}

function openCreateStation(type: 'charging' | 'service') {
  stationDialog.value = type
  editingStationId.value = null
  Object.assign(chargingForm, { stationName: '', cityId: '', address: '', totalPiles: 0, availablePiles: 0, status: 1 })
  Object.assign(serviceForm, { serviceStationName: '', cityId: '', address: '', phone: '', status: 1 })
}

function openEditChargingStation(row: ChargingStationItem) {
  stationDialog.value = 'charging'
  editingStationId.value = row.id
  Object.assign(chargingForm, {
    stationName: row.stationName,
    cityId: row.cityId,
    address: row.address,
    totalPiles: row.totalPiles,
    availablePiles: row.availablePiles,
    status: row.status
  })
}

function openEditServiceStation(row: ServiceStationItem) {
  stationDialog.value = 'service'
  editingStationId.value = row.id
  Object.assign(serviceForm, {
    serviceStationName: row.serviceStationName,
    cityId: row.cityId,
    address: row.address,
    phone: row.phone,
    status: row.status
  })
}

async function saveStation() {
  if (stationDialog.value === 'charging') {
    if (editingStationId.value) {
      await adminApi.updateChargingStation(editingStationId.value, { ...chargingForm })
    } else {
      await adminApi.createChargingStation({ ...chargingForm })
    }
    await chargingStations.refresh()
  }
  if (stationDialog.value === 'service') {
    if (editingStationId.value) {
      await adminApi.updateServiceStation(editingStationId.value, { ...serviceForm })
    } else {
      await adminApi.createServiceStation({ ...serviceForm })
    }
    await serviceStations.refresh()
  }
  stationDialog.value = null
}

async function deleteChargingStation(row: ChargingStationItem) {
  if (!window.confirm(`确认删除充电站 ${row.stationName}？`)) return
  await adminApi.deleteChargingStation(row.id)
  await chargingStations.refresh()
}

async function deleteServiceStation(row: ServiceStationItem) {
  if (!window.confirm(`确认删除服务站 ${row.serviceStationName}？`)) return
  await adminApi.deleteServiceStation(row.id)
  await serviceStations.refresh()
}

function toAppointment(row: unknown) {
  return row as AppointmentItem
}

function toRescue(row: unknown) {
  return row as RescueItem
}

function toChargingStation(row: unknown) {
  return row as ChargingStationItem
}

function toServiceStation(row: unknown) {
  return row as ServiceStationItem
}
</script>

<template>
  <Toolbar v-model="query" title="服务运营" placeholder="搜索工单、联系人、站点" :loading="loading" @refresh="refreshActive">
    <template #actions>
      <button v-if="activeTab === 'charging'" class="primary-button compact" type="button" @click="openCreateStation('charging')">
        <Plus :size="16" />
        <span>新增充电站</span>
      </button>
      <button v-if="activeTab === 'service'" class="primary-button compact" type="button" @click="openCreateStation('service')">
        <Plus :size="16" />
        <span>新增服务站</span>
      </button>
    </template>
  </Toolbar>

  <div class="segmented-tabs" role="tablist" aria-label="服务运营分类">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      type="button"
      role="tab"
      :aria-selected="activeTab === tab.key"
      :class="{ active: activeTab === tab.key }"
      @click="activeTab = tab.key"
    >
      {{ tab.label }}
    </button>
  </div>

  <DataTable
    v-if="activeTab === 'appointments'"
    :columns="appointmentColumns"
    :rows="filteredAppointments"
    :loading="appointments.loading.value"
  >
    <template #totalAmount="{ value }">¥{{ Number(value || 0).toFixed(2) }}</template>
    <template #status="{ value }">
      <StatusBadge :value="Number(value)" :labels="appointmentStatusLabels" />
    </template>
    <template #actions="{ row }">
      <button class="text-button" type="button" @click="nextAppointmentStatus(toAppointment(row))">
        <Clock3 :size="15" />
        <span>流转</span>
      </button>
    </template>
  </DataTable>

  <DataTable
    v-else-if="activeTab === 'rescues'"
    :columns="rescueColumns"
    :rows="filteredRescues"
    :loading="rescues.loading.value"
  >
    <template #status="{ value }">
      <StatusBadge :value="Number(value)" :labels="rescueStatusLabels" />
    </template>
    <template #actions="{ row }">
      <button class="text-button" type="button" @click="nextRescueStatus(toRescue(row))">
        <CheckCircle2 :size="15" />
        <span>流转</span>
      </button>
    </template>
  </DataTable>

  <DataTable
    v-else-if="activeTab === 'charging'"
    :columns="chargingColumns"
    :rows="filteredChargingStations"
    :loading="chargingStations.loading.value"
  >
    <template #status="{ value }">
      <StatusBadge :value="Number(value)" :labels="stationStatusLabels" />
    </template>
    <template #actions="{ row }">
      <div class="row-actions">
        <button class="text-button" type="button" @click="openEditChargingStation(toChargingStation(row))">
          <Pencil :size="15" />
          <span>编辑</span>
        </button>
        <button class="text-button" type="button" @click="toggleChargingStation(toChargingStation(row))">
          <PauseCircle :size="15" />
          <span>{{ toChargingStation(row).status === 1 ? '停用' : '启用' }}</span>
        </button>
        <button class="danger-button" type="button" @click="deleteChargingStation(toChargingStation(row))">
          <Trash2 :size="15" />
          <span>删除</span>
        </button>
      </div>
    </template>
  </DataTable>

  <DataTable
    v-else
    :columns="serviceColumns"
    :rows="filteredServiceStations"
    :loading="serviceStations.loading.value"
  >
    <template #status="{ value }">
      <StatusBadge :value="Number(value)" :labels="stationStatusLabels" />
    </template>
    <template #actions="{ row }">
      <div class="row-actions">
        <button class="text-button" type="button" @click="openEditServiceStation(toServiceStation(row))">
          <Pencil :size="15" />
          <span>编辑</span>
        </button>
        <button class="danger-button" type="button" @click="deleteServiceStation(toServiceStation(row))">
          <Trash2 :size="15" />
          <span>删除</span>
        </button>
      </div>
    </template>
  </DataTable>

  <AdminDialog
    :open="Boolean(stationDialog)"
    :title="stationDialogTitle"
    @close="stationDialog = null"
    @submit="saveStation"
  >
    <template v-if="stationDialog === 'charging'">
      <label class="field">
        <span>充电站名称</span>
        <input v-model="chargingForm.stationName" required />
      </label>
      <label class="field">
        <span>城市ID</span>
        <input v-model="chargingForm.cityId" required />
      </label>
      <label class="field">
        <span>地址</span>
        <input v-model="chargingForm.address" required />
      </label>
      <div class="form-grid">
        <label class="field">
          <span>总桩数</span>
          <input v-model.number="chargingForm.totalPiles" type="number" min="0" />
        </label>
        <label class="field">
          <span>可用桩</span>
          <input v-model.number="chargingForm.availablePiles" type="number" min="0" />
        </label>
        <label class="field">
          <span>状态</span>
          <select v-model.number="chargingForm.status">
            <option :value="1">运营</option>
            <option :value="0">停用</option>
          </select>
        </label>
      </div>
    </template>

    <template v-else>
      <label class="field">
        <span>服务站名称</span>
        <input v-model="serviceForm.serviceStationName" required />
      </label>
      <label class="field">
        <span>城市ID</span>
        <input v-model="serviceForm.cityId" required />
      </label>
      <label class="field">
        <span>地址</span>
        <input v-model="serviceForm.address" required />
      </label>
      <label class="field">
        <span>电话</span>
        <input v-model="serviceForm.phone" />
      </label>
      <label class="field">
        <span>状态</span>
        <select v-model.number="serviceForm.status">
          <option :value="1">运营</option>
          <option :value="0">停用</option>
        </select>
      </label>
    </template>
  </AdminDialog>

  <p v-if="error" class="inline-error">{{ error }}</p>
</template>
