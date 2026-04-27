import axios from 'axios'
import { Dialog, Toast } from 'vant'
import errorCode from '@/util/errorCode'
import { getBaseUrl } from '@/util/env'
import router from '@/router'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  // baseURL: "http://localhost:8080",
  baseURL: getBaseUrl(),
  // 超时
  timeout: 600000
})
// request拦截器
service.interceptors.request.use(config => {
  let token = window.localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token; // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  // get请求映射params参数
  if (config.method === 'get' && config.params) {
    let url = config.url + '?';
    for (const propName of Object.keys(config.params)) {
      const value = config.params[propName];
      var part = encodeURIComponent(propName) + "=";
      if (value && typeof (value) !== "undefined") {
        if (typeof value === 'object') {
          for (const key of Object.keys(value)) {
            let params = propName + '[' + key + ']';
            var subPart = encodeURIComponent(params) + "=";
            url += subPart + encodeURIComponent(value[key]) + "&";
          }
        } else {
          url += part + encodeURIComponent(value) + "&";
        }
      }
    }
    url = url.slice(0, -1);
    config.params = {};
    config.url = url;
  }
  return config
}, error => {
  console.log(error)
  Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(res => {
  // 未设置状态码则默认成功状态
  const code = res.data.code || 200;
  // 获取错误信息
  const msg = errorCode[code] || res.data.msg || errorCode['default']
  if (code === 401) {
    // 如果已经在登录页，不弹窗（避免阻断登录流程）
    if (router.currentRoute && router.currentRoute.path === '/') {
      return res.data
    }
    Dialog.confirm({
      title: '系统提示',
      message: '登录状态已过期，请重新登录',
      showCancelButton: false,
      confirmButtonText: '确定'
    }).then(() => {
      // on confirm - 只清除项目相关的 localStorage 键
      window.localStorage.removeItem('token');
      window.localStorage.removeItem('hasLogin');
      window.localStorage.removeItem('userInfo');
      window.localStorage.removeItem('carInfo');
      window.localStorage.removeItem('carList');
      const to = '/'
      if (router.currentRoute && router.currentRoute.path === to) return
      router.push(to).catch(() => {})
    }).catch(() => {
      // on cancel
    });
    return res.data
  } else {
    return res.data
  }
},
  error => {
    console.log('err' + error)
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
    Toast.fail(message);
    return Promise.reject(error)
  }
)

export default service
