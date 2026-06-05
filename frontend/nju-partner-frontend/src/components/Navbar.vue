<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNotifications, getUnreadCount, markAllNotificationsRead, markNotificationRead } from '@/api/notification'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/date'

const router = useRouter()
const userStore = useUserStore()

const nickname = computed(() => userStore.userInfo?.nickname || '未登录')
const notifications = ref([])
const unreadCount = ref(0)
const notificationLoading = ref(false)

const displayUnreadCount = computed(() => (unreadCount.value > 99 ? '99+' : unreadCount.value))
const avatarText = computed(() => nickname.value.slice(0, 1).toUpperCase())

function normalizeUnreadCount(value) {
  if (typeof value === 'number') {
    return value
  }

  if (value && typeof value.count === 'number') {
    return value.count
  }

  return 0
}

function goHome() {
  router.push({ name: 'Home' })
}

function goProfile() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后查看个人中心')
    router.push({ name: 'Login' })
    return
  }
  router.push({ name: 'Profile' })
}

function goAdmin() {
  router.push({ name: 'Admin' })
}

function goLogin() {
  router.push({ name: 'Login' })
}

function goPostCreate() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后发布组队')
    router.push({ name: 'Login' })
    return
  }
  router.push({ name: 'PostCreate' })
}

function handleGuestNotification() {
  ElMessage.warning('请先登录后查看通知')
  router.push({ name: 'Login' })
}

async function loadNotifications() {
  if (!userStore.isLoggedIn) {
    notifications.value = []
    unreadCount.value = 0
    return
  }

  notificationLoading.value = true
  try {
    const [noticeList, count] = await Promise.all([
      getNotifications(),
      getUnreadCount(),
    ])
    notifications.value = noticeList || []
    unreadCount.value = normalizeUnreadCount(count)
  } finally {
    notificationLoading.value = false
  }
}

async function handleReadNotification(item) {
  if (item.isRead === 0) {
    await markNotificationRead(item.id)
  }

  await loadNotifications()

  if (item.postId) {
    router.push({ name: 'PostDetail', params: { id: item.postId } })
  }
}

async function handleReadAll() {
  if (notifications.value.length === 0) {
    return
  }

  await markAllNotificationsRead()
  ElMessage.success('已全部标记为已读')
  await loadNotifications()
}

function handleLogout() {
  userStore.logout()
  notifications.value = []
  unreadCount.value = 0
  router.push({ name: 'Login' })
}

onMounted(loadNotifications)

watch(
  () => userStore.isLoggedIn,
  () => {
    loadNotifications()
  },
)
</script>

<template>
  <header class="navbar">
    <div class="navbar-inner">
      <div class="brand" @click="goHome">
        <span class="brand-mark">N</span>
        <span class="brand-copy">
          <span class="brand-title">南大轻搭子</span>
          <span class="brand-subtitle">找搭子，更轻松</span>
        </span>
      </div>

      <nav class="nav-links">
        <el-button class="nav-pill" @click="goHome">首页</el-button>
        <el-button
          class="nav-pill primary"
          @click="goPostCreate"
        >
          发布组队
        </el-button>
        <el-button
          v-if="userStore.isAdmin"
          class="nav-pill"
          link
          @click="goAdmin"
        >
          后台管理
        </el-button>
      </nav>

      <div class="nav-actions">
        <span v-if="userStore.isLoggedIn" class="user-avatar">{{ avatarText }}</span>
        <template v-if="userStore.isLoggedIn">
          <el-popover
            placement="bottom-end"
            width="360"
            trigger="click"
            popper-class="notification-popover"
            @show="loadNotifications"
          >
            <template #reference>
              <el-badge
                :value="displayUnreadCount"
                :hidden="unreadCount === 0"
                class="notification-badge"
              >
                <el-button class="nav-pill">通知</el-button>
              </el-badge>
            </template>

            <div class="notification-panel" v-loading="notificationLoading">
              <div class="notification-header">
                <strong>通知</strong>
                <el-button
                  class="read-all-btn"
                  link
                  type="primary"
                  :disabled="notifications.length === 0 || unreadCount === 0"
                  @click="handleReadAll"
                >
                  全部已读
                </el-button>
              </div>

              <el-empty
                v-if="notifications.length === 0"
                description="暂无通知"
                :image-size="72"
              />

              <div v-else class="notification-list">
                <button
                  v-for="item in notifications"
                  :key="item.id"
                  class="notification-item"
                  :class="{ unread: item.isRead === 0 }"
                  type="button"
                  @click="handleReadNotification(item)"
                >
                  <span v-if="item.isRead === 0" class="unread-dot"></span>
                  <span class="notification-title">
                    {{ item.title }}
                  </span>
                  <span class="notification-content">{{ item.content }}</span>
                  <span class="notification-time">{{ formatDateTime(item.createdTime) }}</span>
                </button>
              </div>
            </div>
          </el-popover>
        </template>
        <el-button v-else class="nav-pill" @click="handleGuestNotification">通知</el-button>
        <el-button class="nav-pill" @click="goProfile">个人中心</el-button>
        <template v-if="userStore.isLoggedIn">
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
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(18px);
  border-bottom: 1px solid rgba(106, 44, 138, 0.15);
  box-shadow: 0 10px 30px rgba(64, 158, 255, 0.1);
}

.navbar-inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.brand {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-mark {
  width: 34px;
  height: 34px;
  border-radius: 13px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #409eff, #8b7cf6);
  color: #fff;
  font-weight: 800;
  letter-spacing: 0;
  box-shadow: 0 8px 18px rgba(64, 158, 255, 0.24);
}

.brand-copy {
  display: flex;
  flex-direction: column;
  line-height: 1.18;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #142033;
}

.brand-subtitle {
  font-size: 12px;
  color: #7b8794;
}

.nav-links,
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-links :deep(.el-button),
.nav-actions :deep(.el-button) {
  border-radius: 14px;
}

.nav-links :deep(.nav-pill),
.nav-actions :deep(.nav-pill) {
  min-height: 34px;
  padding: 0 12px;
  color: #1f3554;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(106, 44, 138, 0.15);
}

.nav-links :deep(.nav-pill:hover),
.nav-actions :deep(.nav-pill:hover) {
  color: #174ea6;
  border-color: rgba(106, 44, 138, 0.3);
  background: rgba(255, 255, 255, 0.9);
}

.nav-links :deep(.nav-pill.primary) {
  color: #fff;
  background: linear-gradient(135deg, #409eff, #8b7cf6);
  border-color: transparent;
  box-shadow: 0 8px 18px rgba(64, 158, 255, 0.22);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #409eff, #8b7cf6);
  color: #fff;
  font-weight: 800;
  box-shadow: 0 10px 20px rgba(64, 158, 255, 0.2);
}

.notification-badge {
  line-height: 1;
}

.notification-panel {
  min-height: 96px;
}

.notification-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.read-all-btn {
  padding: 0;
  border: 0;
  background: transparent;
  box-shadow: none;
}

.read-all-btn:hover {
  background: transparent;
  transform: none;
}

.notification-list {
  max-height: 360px;
  overflow-y: auto;
}

.notification-item {
  position: relative;
  width: 100%;
  padding: 10px 4px 10px 16px;
  border: 0;
  border-bottom: 1px solid rgba(106, 44, 138, 0.1);
  background: transparent;
  cursor: pointer;
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.notification-item:hover {
  background: rgba(248, 251, 255, 0.66);
}

.notification-item.unread {
  background: transparent;
}

.unread-dot {
  position: absolute;
  left: 4px;
  top: 15px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #f56c6c;
  box-shadow: 0 0 0 3px rgba(245, 108, 108, 0.12);
}

.notification-title {
  color: #303133;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}

.notification-content {
  color: #606266;
  font-size: 13px;
  line-height: 1.45;
}

.notification-time {
  color: #909399;
  font-size: 12px;
}

@media (max-width: 760px) {
  .navbar-inner {
    height: auto;
    padding: 10px 16px;
    flex-wrap: wrap;
  }

  .nav-links,
  .nav-actions {
    flex-wrap: wrap;
  }
}
</style>
