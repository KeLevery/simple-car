import request from '@/util/request'

// 修改维保支付
export function paymentUpdate(data: Record<string, unknown>) {
    return request({
      url: '/bs-vehicle-owner/maintenance-pay',
      method: 'put',
      data: data
    })
}
