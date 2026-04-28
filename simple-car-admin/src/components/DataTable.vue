<script setup lang="ts">
defineProps<{
  columns: Array<{ key: string; label: string }>
  rows: unknown[]
  loading?: boolean
  emptyText?: string
}>()

function getCell(row: unknown, key: string) {
  if (!row || typeof row !== 'object') return undefined
  return (row as Record<string, unknown>)[key]
}

function getRowKey(row: unknown, index: number) {
  const id = getCell(row, 'id')
  return id == null ? index : String(id)
}
</script>

<template>
  <div class="table-frame">
    <table class="data-table">
      <thead>
        <tr>
          <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
          <th v-if="$slots.actions" class="action-column">操作</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="loading">
          <tr>
            <td :colspan="columns.length + ($slots.actions ? 1 : 0)" class="table-state">加载中</td>
          </tr>
        </template>
        <template v-else-if="rows.length === 0">
          <tr>
            <td :colspan="columns.length + ($slots.actions ? 1 : 0)" class="table-state">
              {{ emptyText || '暂无数据' }}
            </td>
          </tr>
        </template>
        <template v-else>
          <tr v-for="(row, index) in rows" :key="getRowKey(row, index)">
            <td v-for="column in columns" :key="column.key">
              <slot :name="column.key" :row="row" :value="getCell(row, column.key)">
                {{ getCell(row, column.key) ?? '-' }}
              </slot>
            </td>
            <td v-if="$slots.actions" class="action-cell">
              <slot name="actions" :row="row"></slot>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
</template>
