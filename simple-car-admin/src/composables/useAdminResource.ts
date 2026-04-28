import { computed, onMounted, shallowRef } from 'vue'

export function useAdminResource<T>(loader: () => Promise<T[]>) {
  const items = shallowRef<T[]>([])
  const loading = shallowRef(false)
  const error = shallowRef('')

  const hasData = computed(() => items.value.length > 0)

  async function refresh() {
    loading.value = true
    error.value = ''
    try {
      items.value = await loader()
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载失败'
    } finally {
      loading.value = false
    }
  }

  onMounted(refresh)

  return {
    items,
    loading,
    error,
    hasData,
    refresh
  }
}
