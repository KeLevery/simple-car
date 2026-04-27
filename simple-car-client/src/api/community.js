import request from '@/util/request'

// 获取动态列表
export function postList() {
  return request({
    url: '/community/post/list',
    method: 'get'
  })
}

// 点赞/取消点赞
export function toggleLike(postId) {
  return request({
    url: `/community/post/like/${postId}`,
    method: 'post'
  })
}

// 发布动态
export function createPost(data) {
  return request({
    url: '/community/post/create',
    method: 'post',
    data
  })
}

// 获取评论列表
export function commentList(postId) {
  return request({
    url: `/community/post/${postId}/comments`,
    method: 'get'
  })
}

// 发表评论
export function createComment(postId, data) {
  return request({
    url: `/community/post/${postId}/comments`,
    method: 'post',
    data
  })
}
