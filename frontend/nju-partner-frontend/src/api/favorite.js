import request from '@/utils/request'

/** 收藏帖子 POST /api/posts/{id}/favorite */
export function addFavorite(postId) {
  return request.post(`/posts/${postId}/favorite`)
}

/** 取消收藏 DELETE /api/posts/{id}/favorite */
export function removeFavorite(postId) {
  return request.delete(`/posts/${postId}/favorite`)
}

/** 查看我的收藏 GET /api/user/favorites */
export function getFavoriteList(params) {
  return request.get('/user/favorites', { params })
}
