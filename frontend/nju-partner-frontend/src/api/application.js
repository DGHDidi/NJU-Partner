import request from '@/utils/request'

/** 报名参加 POST /api/posts/{id}/apply */
export function applyPost(id, data) {
  return request.post(`/posts/${id}/apply`, data)
}

/** 查看报名列表 GET /api/posts/{id}/applications */
export function getApplicationList(id) {
  return request.get(`/posts/${id}/applications`)
}

/** 通过报名 PUT /api/applications/{id}/pass */
/** GET /api/posts/{id}/applications/me */
export function getMyApplication(id) {
  return request.get(`/posts/${id}/applications/me`)
}

/** GET /api/posts/{id}/members */
export function getApprovedMembers(id) {
  return request.get(`/posts/${id}/members`)
}

export function passApplication(id) {
  return request.put(`/applications/${id}/pass`)
}

/** 拒绝报名 PUT /api/applications/{id}/reject */
export function rejectApplication(id) {
  return request.put(`/applications/${id}/reject`)
}

/** 取消报名 PUT /api/applications/{id}/cancel */
export function cancelApplication(id) {
  return request.put(`/applications/${id}/cancel`)
}
