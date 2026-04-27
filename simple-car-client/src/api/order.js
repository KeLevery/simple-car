import request from '@/util/request'

// 获取订单列表
export function orderList() {
  return request({
    url: '/order/list',
    method: 'get'
  })
}
