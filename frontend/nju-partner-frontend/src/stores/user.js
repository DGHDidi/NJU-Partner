import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getStoredJson } from '@/utils/storage'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(getStoredJson('userInfo'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

  function setLogin(data) {
    token.value = data.token || ''
    userInfo.value = data.userInfo || null
    localStorage.setItem('token', data.token || '')
    if (data.userInfo) {
      localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    setLogin,
    setUserInfo,
    logout,
  }
})
