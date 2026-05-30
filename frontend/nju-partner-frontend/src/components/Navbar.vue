<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const nickname = computed(() => userStore.userInfo?.nickname || '未登录')

function goHome() {
  router.push({ name: 'Home' })
}

function goProfile() {
  router.push({ name: 'Profile' })
}

function goAdmin() {
  router.push({ name: 'Admin' })
}

function goLogin() {
  router.push({ name: 'Login' })
}

function handleLogout() {
  userStore.logout()
  router.push({ name: 'Login' })
}
</script>

<template>
  <header class="navbar">
    <div class="navbar-inner">
      <div class="brand" @click="goHome">
        <span class="brand-title">南大轻搭子</span>
        <span class="brand-subtitle">找搭子，更轻松</span>
      </div>

      <nav class="nav-links">
        <el-button link type="primary" @click="goHome">首页</el-button>
        <el-button
          v-if="userStore.isLoggedIn"
          link
          type="primary"
          @click="router.push({ name: 'PostCreate' })"
        >
          发布组队
        </el-button>
        <el-button
          v-if="userStore.isAdmin"
          link
          type="primary"
          @click="goAdmin"
        >
          后台管理
        </el-button>
      </nav>

      <div class="nav-actions">
        <template v-if="userStore.isLoggedIn">
          <span class="nickname">{{ nickname }}</span>
          <el-button link type="primary" @click="goProfile">个人中心</el-button>
          <el-button link @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="goLogin">登录</el-button>
          <el-button @click="router.push({ name: 'Register' })">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.navbar-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.brand {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
}

.brand-subtitle {
  font-size: 12px;
  color: #909399;
}

.nav-links,
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  color: #606266;
  font-size: 14px;
}
</style>
