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

  /**
   * 包装写操作：失败时写入 error 并返回 false，成功后自动刷新列表。
   */
  async function mutate(action: () => Promise<unknown>): Promise<boolean> {
    error.value = ''
    try {
      await action()
    } catch (err) {
      error.value = err instanceof Error ? err.message : '操作失败'
      return false
    }
    await refresh()
    return true
  }

  onMounted(refresh)

  return {
    items,
    loading,
    error,
    hasData,
    refresh,
    mutate
  }
}
