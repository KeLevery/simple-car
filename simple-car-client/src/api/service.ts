import request, { type PageResult } from '@/util/request'

// 查询经销商列表
export function dealerList(query: Record<string, unknown>) {
  return request({
    url: '/bs-vehicle-owner/dealer/page',
    method: 'get',
    params: query
  })
}

// 查询维保服务站列表（分页）
export function stationList(query: Record<string, unknown>): Promise<PageResult> {
    return request({
      url: '/bs-vehicle-owner/maintenance-service-station/page',
      method: 'get',
      params: query
    }) as unknown as Promise<PageResult>
}

// 查询车辆信息列表
export function carInfoList(id: number | string) {
    return request({
      url: '/bs-vehicle-owner/userCar/queryByUserId/'+id,
      method: 'get'
    })
}

// 查询维保预约列表
// 查询维保预约列表（分页）
export function appointmentList(id: number | string, num: number): Promise<PageResult> {
    return request({
      url: '/bs-vehicle-owner/maintenance-appointment/page?reasonable=false&carId='+id+'&pageNum='+num,
      method: 'get'
    }) as unknown as Promise<PageResult>
}

// 新增维保预约
export function appointmentAdd(data: Record<string, unknown>) {
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
export function commonUpload(data: FormData) {
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
export function addCar(data: Record<string, unknown>) {
  return request({
    url: '/bs-vehicle-owner/userCar/add',
    method: 'post',
    data: data
  })
}
