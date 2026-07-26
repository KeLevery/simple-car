import { computed, type Ref } from 'vue'

/**
 * 关键字过滤：对指定字段做不区分大小写的包含匹配。
 */
export function useKeywordFilter<T extends object>(
  rows: Ref<T[]>,
  query: Ref<string>,
  keys: Array<keyof T>
) {
  return computed(() => {
    const keyword = query.value.trim().toLowerCase()
    if (!keyword) return rows.value
    return rows.value.filter((row) =>
      keys.some((key) => String(row[key] || '').toLowerCase().includes(keyword))
    )
  })
}
