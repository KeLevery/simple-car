import request from '@/util/request'

export function createRescue(data: Record<string, unknown>) {
  return request({
    url: '/rescue/create',
    method: 'post',
    data
  })
}

export function rescueList() {
  return request({
    url: '/rescue/list',
    method: 'get'
  })
}
