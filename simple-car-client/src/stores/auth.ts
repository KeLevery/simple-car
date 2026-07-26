import { defineStore } from 'pinia'
import { getJSON, setJSON, remove } from '@/util/storage'
import { useCarStore } from './car'

export interface UserInfo {
  id?: number
  userId?: number
  username?: string
  nickName?: string
  phone?: string
  [key: string]: unknown
}

/**
 * 登录态 store。持久化沿用旧 localStorage key 与格式（token 裸串、
 * hasLogin 字符串 "true"、userInfo JSON），保证旧数据升级不掉线，
 * 且未迁移页面直接读 localStorage 仍能取到一致数据（双写共存期）。
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: window.localStorage.getItem('token') || '',
    hasLogin: window.localStorage.getItem('hasLogin') === 'true',
    userInfo: getJSON<UserInfo>('userInfo')
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token && state.hasLogin),
    userId: (state) => state.userInfo?.userId ?? state.userInfo?.id ?? null
  },
  actions: {
    setToken(token: string) {
      this.token = token
      window.localStorage.setItem('token', token)
    },
    setLoginUser(user: UserInfo) {
      this.userInfo = user
      this.hasLogin = true
      setJSON('userInfo', user)
      window.localStorage.setItem('hasLogin', 'true')
    },
    logout() {
      this.token = ''
      this.hasLogin = false
      this.userInfo = null
      remove('token')
      remove('hasLogin')
      remove('userInfo')
      useCarStore().clear()
    }
  }
})
