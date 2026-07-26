import axios from 'axios'
import { showConfirmDialog, showFailToast } from 'vant'
import errorCode from '@/util/errorCode'
import { getBaseUrl } from '@/util/env'
import router from '@/router'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: getBaseUrl(),
  // 超时
  timeout: 30000
})

// 清除登录态（供 request 拦截器与退出登录复用）
export function clearAuth() {
  window.localStorage.removeItem('token');
  window.localStorage.removeItem('hasLogin');
  window.localStorage.removeItem('userInfo');
  window.localStorage.removeItem('carInfo');
  window.localStorage.removeItem('carList');
}

// 401 统一处理：提示后清除登录态并跳转登录页
let unauthorizedDialogShowing = false
function handleUnauthorized(message) {
  // 已在登录页则不弹窗（避免阻断登录流程）
  if (router.currentRoute && router.currentRoute.value.path === '/') {
    return
  }
  if (unauthorizedDialogShowing) {
    return
  }
  unauthorizedDialogShowing = true
  showConfirmDialog({
    title: '系统提示',
    message: message || '登录状态已过期，请重新登录',
    showCancelButton: false,
    confirmButtonText: '确定'
  }).then(() => {
    clearAuth();
    router.push('/').catch(() => {})
  }).catch(() => {
    // on cancel
  }).finally(() => {
    unauthorizedDialogShowing = false
  });
}

// request拦截器
service.interceptors.request.use(config => {
  let token = window.localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token; // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器：业务码非 200 统一提示并 reject，调用方只需处理成功分支
service.interceptors.response.use(res => {
  // 未设置状态码则默认成功状态
  const code = res.data.code || 200;
  if (code === 200) {
    return res.data
  }
  // 获取错误信息
  const msg = errorCode[code] || res.data.msg || errorCode['default']
  if (code === 401) {
    handleUnauthorized('登录状态已过期，请重新登录')
  } else {
    showFailToast(msg)
  }
  return Promise.reject(new Error(msg))
},
  error => {
    const status = error.response && error.response.status
    const data = error.response && error.response.data
    if (status === 401 || (data && data.code === 401)) {
      handleUnauthorized((data && data.msg) || '登录状态已过期，请重新登录')
      return Promise.reject(error)
    }
    if (status === 403 || (data && data.code === 403)) {
      showFailToast((data && data.msg) || '无访问权限');
      return Promise.reject(error)
    }
    let { message } = error;
    if (message == "Network Error") {
      message = "后端接口连接异常";
    }
    else if (message.includes("timeout")) {
      message = "系统接口请求超时";
    }
    else if (message.includes("Request failed with status code")) {
      message = "系统接口" + message.substr(message.length - 3) + "异常";
    }
    showFailToast(message);
    return Promise.reject(error)
  }
)

export default service
