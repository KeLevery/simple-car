import request, { type PageResult } from '@/util/request'

export interface NoticeItem {
    id: number
    title?: string
    content?: string
    type?: number
    createTime?: string
    [key: string]: unknown
}

// 获取通知列表（分页）
export function noticeList(params?: { pageNum?: number; pageSize?: number }) {
    return request({
      url: '/notice/list',
      method: 'get',
      params
    }) as unknown as Promise<PageResult<NoticeItem>>
}
