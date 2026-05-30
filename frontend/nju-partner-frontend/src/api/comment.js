import request from '@/utils/request'

/** 发表评论 POST /api/posts/{id}/comments */
export function createComment(postId, data) {
  return request.post(`/posts/${postId}/comments`, data)
}

/** 查看评论 GET /api/posts/{id}/comments */
export function getCommentList(postId) {
  return request.get(`/posts/${postId}/comments`)
}

/** 删除评论 DELETE /api/comments/{id} */
export function deleteComment(id) {
  return request.delete(`/comments/${id}`)
}
