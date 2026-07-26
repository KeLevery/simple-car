import request, { type PageResult } from '@/util/request'

export interface OrderItem {
  uid: string
  id: number
  type: string
  amount: number | null
  time: string
  status: string
  detail: string
}

// 获取订单列表（分页）
export function orderList(params?: { pageNum?: number; pageSize?: number }) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  }) as unknown as Promise<PageResult<OrderItem>>
}
