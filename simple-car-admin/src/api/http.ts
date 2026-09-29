import axios, { type AxiosRequestConfig } from 'axios'
import { clearAdminToken, getAdminToken } from './token'

export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export const http = axios.create({
  baseURL: '/dev-api',
  timeout: 12000
})

http.interceptors.request.use((config) => {
  const token = getAdminToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function handleUnauthorized(message?: string) {
  clearAdminToken()
  if (window.location.pathname !== '/login') {
    // 带上回跳路径，登录成功后返回原页面
    const redirect = encodeURIComponent(window.location.pathname + window.location.search)
    window.location.assign(`/login?redirect=${redirect}`)
  }
  return Promise.reject(new Error(message || '登录已过期'))
}

http.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResponse<unknown>
    if (result && result.code === 401) {
      return handleUnauthorized(result.msg)
    }
    if (result && typeof result.code === 'number' && result.code !== 200) {
      return Promise.reject(new Error(result.msg || '请求失败'))
    }
    return response
  },
  (error) => {
    const status = error.response?.status
    const result = error.response?.data as ApiResponse<unknown> | undefined
    if (status === 401 || result?.code === 401) {
      return handleUnauthorized(result?.msg)
    }
    if (status === 403 || result?.code === 403) {
      return Promise.reject(new Error(result?.msg || '无后台访问权限'))
    }
    // 非 401/403 的 HTTP 错误转为中文提示，避免界面直接显示英文报错
    const message =
      result?.msg ||
      (status ? `系统接口 ${status} 异常` : '后端接口连接异常')
    return Promise.reject(new Error(message))
  }
)

export async function request<T>(url: string, options: AxiosRequestConfig = {}) {
  const response = await http<ApiResponse<T>>(url, options)
  return response.data.data
}

export interface PageResult<T> {
  rows: T[]
  total: number
}

/**
 * 分页请求：PageResponse 的 rows/total 与 data 平级，request<T> 解不出来，单独解包。
 */
export async function requestPage<T>(url: string, options: AxiosRequestConfig = {}): Promise<PageResult<T>> {
  const response = await http<ApiResponse<null> & { rows?: T[]; total?: number }>(url, options)
  return {
    rows: response.data.rows ?? [],
    total: response.data.total ?? 0
  }
}
