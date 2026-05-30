import request from '@/utils/request'

/** 查看所有帖子 GET /api/admin/posts */
export function getAdminPosts(params) {
  return request.get('/admin/posts', { params })
}

/** 删除帖子 DELETE /api/admin/posts/{id} */
export function deleteAdminPost(id) {
  return request.delete(`/admin/posts/${id}`)
}

/** 查看所有用户 GET /api/admin/users */
export function getAdminUsers(params) {
  return request.get('/admin/users', { params })
}

/** 封禁用户 PUT /api/admin/users/{id}/ban */
export function banUser(id) {
  return request.put(`/admin/users/${id}/ban`)
}

/** 解封用户 PUT /api/admin/users/{id}/unban */
export function unbanUser(id) {
  return request.put(`/admin/users/${id}/unban`)
}

/** 删除评论（管理员） DELETE /api/comments/{id} */
export function deleteAdminComment(id) {
  return request.delete(`/comments/${id}`)
}
