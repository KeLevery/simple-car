import axios from 'axios'

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
  const token = window.localStorage.getItem('adminToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResponse<unknown>
    if (result && result.code === 401) {
      window.localStorage.removeItem('adminToken')
      if (window.location.pathname !== '/login') {
        window.location.assign('/login')
      }
      return Promise.reject(new Error(result.msg || '登录已过期'))
    }
    if (result && result.code && result.code !== 200) {
      return Promise.reject(new Error(result.msg || '请求失败'))
    }
    return response
  },
  (error) => {
    const status = error.response?.status
    const result = error.response?.data as ApiResponse<unknown> | undefined
    if (status === 401 || result?.code === 401) {
      window.localStorage.removeItem('adminToken')
      if (window.location.pathname !== '/login') {
        window.location.assign('/login')
      }
      return Promise.reject(new Error(result?.msg || '登录已过期'))
    }
    if (status === 403 || result?.code === 403) {
      return Promise.reject(new Error(result?.msg || '无后台访问权限'))
    }
    return Promise.reject(error)
  }
)

export async function request<T>(url: string, options = {}) {
  const response = await http<ApiResponse<T>>(url, options)
  return response.data.data
}
