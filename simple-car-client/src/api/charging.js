import request from '@/util/request'

// 获取充电站列表
export function stationList(params) {
  return request({
    url: '/charging-station/list',
    method: 'get',
    params
  })
}
