import request from '@/util/request'

// 获取充电站列表
export function stationList(params?: Record<string, unknown>) {
  return request({
    url: '/charging-station/list',
    method: 'get',
    params
  })
}
