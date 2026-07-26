import request from '@/util/request'

export interface ViolationItem {
  id: number
  carId: number
  violationType?: string
  location?: string
  fineAmount?: number
  deductPoints?: number
  status: number
  violationTime?: string
  createTime?: string
}

// data 结构：list 为当前页，统计字段为全量口径
export interface ViolationData {
  list: ViolationItem[]
  total: number
  untreated: number
  totalFine: number
  totalPoints: number
}

export function violationList(params?: { carId?: number; pageNum?: number; pageSize?: number }) {
  return request<ViolationData>({
    url: '/violation/list',
    method: 'get',
    params
  })
}
