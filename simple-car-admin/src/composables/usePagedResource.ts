import { computed, onBeforeUnmount, onMounted, shallowRef } from 'vue'
import type { PageQuery } from '@/api/admin'
import type { PageResult } from '@/api/http'

/**
 * 服务端分页资源：管理 pageNum/pageSize/keyword 状态，keyword 防抖并重置页码，
 * 写操作成功后刷新当前页（删空当前页时自动回退一页）。
 */
export function usePagedResource<T>(loader: (query: PageQuery) => Promise<PageResult<T>>) {
  const items = shallowRef<T[]>([])
  const total = shallowRef(0)
  const pageNum = shallowRef(1)
  const pageSize = shallowRef(10)
  const keyword = shallowRef('')
  const loading = shallowRef(false)
  const error = shallowRef('')
  let keywordTimer: ReturnType<typeof setTimeout> | undefined

  const hasData = computed(() => items.value.length > 0)

  // 请求序号：翻页/搜索防抖并发时丢弃过期响应，防止旧数据覆盖新数据
  let requestSeq = 0

  async function refresh() {
    const seq = ++requestSeq
    loading.value = true
    error.value = ''
    try {
      const trimmed = keyword.value.trim()
      const result = await loader({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        ...(trimmed ? { keyword: trimmed } : {})
      })
      if (seq !== requestSeq) return
      items.value = result.rows
      total.value = result.total
      // 当前页被删空（或越界）时回退到有效页
      if (result.rows.length === 0 && result.total > 0 && pageNum.value > 1) {
        pageNum.value = Math.max(1, Math.ceil(result.total / pageSize.value))
        await refresh()
        return
      }
    } catch (err) {
      if (seq === requestSeq) {
        error.value = err instanceof Error ? err.message : '加载失败'
      }
    } finally {
      if (seq === requestSeq) {
        loading.value = false
      }
    }
  }

  function setPage(page: number) {
    pageNum.value = page
    void refresh()
  }

  function setPageSize(size: number) {
    pageSize.value = size
    pageNum.value = 1
    void refresh()
  }

  function setKeyword(value: string) {
    keyword.value = value
    if (keywordTimer) clearTimeout(keywordTimer)
    keywordTimer = setTimeout(() => {
      pageNum.value = 1
      void refresh()
    }, 300)
  }

  /**
   * 包装写操作：失败时写入 error 并返回 false，成功后自动刷新当前页。
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
  onBeforeUnmount(() => {
    if (keywordTimer) clearTimeout(keywordTimer)
  })

  return {
    items,
    total,
    pageNum,
    pageSize,
    keyword,
    loading,
    error,
    hasData,
    refresh,
    mutate,
    setPage,
    setPageSize,
    setKeyword
  }
}
