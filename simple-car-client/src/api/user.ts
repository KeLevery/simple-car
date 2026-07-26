import request from '@/util/request'

export interface LoginPayload {
    username: string
    password: string
}

// 用户登录
export function userLogin(data: LoginPayload) {
    return request<{ token: string }>({
      url: '/login',
      method: 'post',
      data: data
    })
}

// 用户注册
export function userRegister(data: Record<string, unknown>) {
    return request({
      url: '/register',
      method: 'post',
      data: data
    })
}

// 查询用户信息
export function userInfo() {
    return request<{ user: Record<string, unknown>; cars: Record<string, unknown>[] }>({
      url: '/getInfo',
      method: 'get'
    })
}

// 更新个人资料
export function updateProfile(data: Record<string, unknown>) {
    return request({
      url: '/user/profile',
      method: 'put',
      data: data
    })
}

// 修改密码
export function changePassword(data: Record<string, unknown>) {
    return request({
      url: '/user/password',
      method: 'put',
      data: data
    })
}

// 获取用户设置
export function getUserSettings(type: string) {
    return request({
      url: '/user/settings/' + type,
      method: 'get'
    })
}

// 更新用户设置
export function updateUserSettings(type: string, data: Record<string, unknown>) {
    return request({
      url: '/user/settings/' + type,
      method: 'put',
      data: data
    })
}
