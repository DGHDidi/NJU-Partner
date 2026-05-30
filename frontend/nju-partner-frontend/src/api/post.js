import request from '@/utils/request'

/** 发布帖子 POST /api/posts */
export function createPost(data) {
  return request.post('/posts', data)
}

/** 查询帖子列表 GET /api/posts */
export function getPostList(params) {
  return request.get('/posts', { params })
}

/** 查询帖子详情 GET /api/posts/{id} */
export function getPostDetail(id) {
  return request.get(`/posts/${id}`)
}

/** 修改帖子 PUT /api/posts/{id} */
export function updatePost(id, data) {
  return request.put(`/posts/${id}`, data)
}

/** 关闭帖子 PUT /api/posts/{id}/close */
export function closePost(id) {
  return request.put(`/posts/${id}/close`)
}

/** 删除帖子 DELETE /api/posts/{id} */
export function deletePost(id) {
  return request.delete(`/posts/${id}`)
}
