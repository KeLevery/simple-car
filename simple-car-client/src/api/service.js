import request from '@/util/request'

// 查询经销商列表
export function dealerList(query) {
  return request({
    url: '/bs-vehicle-owner/dealer/page',
    method: 'get',
    params: query
  })
}

// 查询维保服务站列表
export function stationList(query) {
    return request({
      url: '/bs-vehicle-owner/maintenance-service-station/page',
      method: 'get',
      params: query
    })
}

// 查询车辆信息列表
export function carInfoList(id) {
    return request({
      url: '/bs-vehicle-owner/userCar/queryByUserId/'+id,
      method: 'get'
    })
}

// 查询维保预约列表
export function appointmentList(id,num) {
    return request({
      url: '/bs-vehicle-owner/maintenance-appointment/page?reasonable=false&carId='+id+'&pageNum='+num,
      method: 'get'
    })
}

// 新增维保预约
export function appointmentAdd(data) {
    return request({
      url: '/bs-vehicle-owner/maintenance-appointment',
      method: 'post',
      data: data
    })
}

// 获取车辆维修计划随机列表
export function planRandomList() {
    return request({
      url: '/bs-vehicle-owner/maintenance-plan/randomList',
      method: 'get'
    })
}

// 通用图片上传
export function commonUpload(data) {
  return request({
    url: '/common/upload',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  })
}

// 添加车辆
export function addCar(data) {
  return request({
    url: '/bs-vehicle-owner/userCar/add',
    method: 'post',
    data: data
  })
}
