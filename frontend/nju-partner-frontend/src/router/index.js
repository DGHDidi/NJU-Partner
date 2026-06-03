import { createRouter, createWebHistory } from 'vue-router'
import { getStoredJson } from '@/utils/storage'

const routes = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', guest: true },
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/post/create',
    name: 'PostCreate',
    component: () => import('@/views/PostCreate.vue'),
    meta: { title: '发布组队帖', requiresAuth: true },
  },
  {
    path: '/post/:id/edit',
    name: 'PostEdit',
    component: () => import('@/views/PostCreate.vue'),
    meta: { title: '编辑组队帖', requiresAuth: true },
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/PostDetail.vue'),
    meta: { title: '帖子详情' },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/Admin.vue'),
    meta: { title: '后台管理', requiresAuth: true, requiresAdmin: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  document.title = to.meta.title
    ? `${to.meta.title} - 南大轻搭子`
    : '南大轻搭子'

  const token = localStorage.getItem('token')
  const userInfo = getStoredJson('userInfo')

  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requiresAdmin && userInfo?.role !== 'ADMIN') {
    next({ name: 'Home' })
    return
  }

  if (to.meta.guest && token && (to.name === 'Login' || to.name === 'Register')) {
    next({ name: 'Home' })
    return
  }

  next()
})

export default router
