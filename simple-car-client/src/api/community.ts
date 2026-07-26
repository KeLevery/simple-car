import request, { type PageResult } from '@/util/request'

export interface PostItem {
  id: number
  userId: number
  content: string
  images?: string
  likeCount: number
  commentCount: number
  shareCount?: number
  isHot?: number
  createTime: string
  nickname?: string
  avatar?: string
  isLiked?: boolean
  [key: string]: unknown
}

export interface CommentItem {
  id: number
  postId: number
  userId: number
  content: string
  createTime: string
  nickname?: string
  avatar?: string
  [key: string]: unknown
}

// 获取动态列表（分页）
export function postList(params?: { pageNum?: number; pageSize?: number }) {
  return request({
    url: '/community/post/list',
    method: 'get',
    params
  }) as unknown as Promise<PageResult<PostItem>>
}

// 点赞/取消点赞
export function toggleLike(postId: number) {
  return request({
    url: `/community/post/like/${postId}`,
    method: 'post'
  })
}

// 发布动态
export function createPost(data: { content: string; images?: string }) {
  return request<PostItem>({
    url: '/community/post/create',
    method: 'post',
    data
  })
}

// 获取评论列表（分页）
export function commentList(postId: number, params?: { pageNum?: number; pageSize?: number }) {
  return request({
    url: `/community/post/${postId}/comments`,
    method: 'get',
    params
  }) as unknown as Promise<PageResult<CommentItem>>
}

// 发表评论
export function createComment(postId: number, data: { content: string }) {
  return request<CommentItem>({
    url: `/community/post/${postId}/comments`,
    method: 'post',
    data
  })
}
