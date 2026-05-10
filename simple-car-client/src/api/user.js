import request from '@/util/request'

// 用户登录
export function userLogin(data) {
    return request({
      url: '/login',
      method: 'post',
      data: data
    })
}

// 用户注册
export function userRegister(data) {
    return request({
      url: '/register',
      method: 'post',
      data: data
    })
}

// 查询用户信息
export function userInfo() {
    return request({
      url: '/getInfo',
      method: 'get'
    })
}

// 更新个人资料
export function updateProfile(data) {
    return request({
      url: '/user/profile',
      method: 'put',
      data: data
    })
}

// 修改密码
export function changePassword(data) {
    return request({
      url: '/user/password',
      method: 'put',
      data: data
    })
}

// 获取用户设置
export function getUserSettings(type) {
    return request({
      url: '/user/settings/' + type,
      method: 'get'
    })
}

// 更新用户设置
export function updateUserSettings(type, data) {
    return request({
      url: '/user/settings/' + type,
      method: 'put',
      data: data
    })
}
