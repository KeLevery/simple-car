/**
 * localStorage JSON 读写小工具：解析失败返回 null，写入失败静默。
 */
export function getJSON<T>(key: string): T | null {
  try {
    const raw = window.localStorage.getItem(key)
    return raw ? (JSON.parse(raw) as T) : null
  } catch {
    return null
  }
}

export function setJSON(key: string, value: unknown): void {
  try {
    window.localStorage.setItem(key, JSON.stringify(value))
  } catch {
    // 存储满等异常忽略
  }
}

export function remove(key: string): void {
  window.localStorage.removeItem(key)
}
