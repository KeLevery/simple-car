import request from '@/util/request'

// 获取通知列表
export function noticeList() {
    return request({
      url: '/notice/list',
      method: 'get'
    })
}
