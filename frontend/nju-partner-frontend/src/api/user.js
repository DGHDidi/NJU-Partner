import request from '@/utils/request'

/** 用户注册 POST /api/user/register */
export function register(data) {
  return request.post('/user/register', data)
}

/** 用户登录 POST /api/user/login */
export function login(data) {
  return request.post('/user/login', data)
}

/** 获取个人信息 GET /api/user/profile */
export function getProfile() {
  return request.get('/user/profile')
}

/** 修改个人信息 PUT /api/user/profile */
export function updateProfile(data) {
  return request.put('/user/profile', data)
}

/** 我的发布 GET /api/user/my-posts */
export function getMyPosts(params) {
  return request.get('/user/my-posts', { params })
}

/** 我的报名 GET /api/user/my-applications */
export function getMyApplications(params) {
  return request.get('/user/my-applications', { params })
}

/** 我的收藏 GET /api/user/my-favorites */
export function getMyFavorites(params) {
  return request.get('/user/favorites', { params })
}
