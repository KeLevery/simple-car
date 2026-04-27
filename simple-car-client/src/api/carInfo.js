import request from '@/util/request'

// 查询剩余电量、里程、温度
export function carInfo(id) {
  return request({
    url: '/bs-vehicle-owner/carInfo/getByCarId/'+id,
    method: 'get'
  })
}

// 远程车辆控制
export function doCarStart(data) {
  console.log(data)
    return request({
      url: '/bs-vehicle-owner/rc/doCarStart',
      method: 'post',
      data: data
    })
}

// 分页查询空调状态列表
export function fanState(query) {
  return request({
    url: '/bs-vehicle-owner/bsAirConditioningStatus/page',
    method: 'get',
    params: query
  })
}

// 查询车辆状态（启动/关闭）carState(1:离线;2:关机;3:开机)
export function CarState(id) {
  return request({
    url: '/bs-smart-charger-biz/CarState/'+id,
    method: 'get'
  })
}

// 查询实时充电数据
export function homeChargingData(id) {
  return request({
    url: '/bs-home-charging-station/homeChargingOrders/queryRealTimeChargingData/'+id,
    method: 'get'
  })
}

// 查询家用充电桩列表
export function homestationList(query) {
  return request({
    url: '/bs-home-charging-station/homestation/list',
    method: 'get',
    params: query
  })
}

// 检查家用是否充电中
export function homesChargeChecked(id) {
  return request({
    url: '/bs-home-charging-station/homeChargingOrders/checkIsCharged/'+id,
    method: 'get'
  })
}

// 检查车辆充电链接
export function getHomeCheckLink(id) {
  return request({
    url: '/bs-home-charging-station/homeChargingOrders/checkLink/'+id,
    method: 'get'
  })
}