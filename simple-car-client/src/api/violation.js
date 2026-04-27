import request from '@/util/request'

export function violationList(params) {
  return request({
    url: '/violation/list',
    method: 'get',
    params
  })
}
