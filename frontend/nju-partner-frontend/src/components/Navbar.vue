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
  router.push({ name: 'Profile' })
}

function goAdmin() {
  router.push({ name: 'Admin' })
}

function goLogin() {
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
                <el-button link type="primary">通知</el-button>
              </el-badge>
            </template>

            <div class="notification-panel" v-loading="notificationLoading">
              <div class="notification-header">
                <strong>通知</strong>
                <el-button
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
                  <span class="notification-title">
                    {{ item.title }}
                    <el-tag v-if="item.isRead === 0" size="small" type="danger">未读</el-tag>
                  </span>
                  <span class="notification-content">{{ item.content }}</span>
                  <span class="notification-time">{{ formatDateTime(item.createdTime) }}</span>
                </button>
              </div>
            </div>
          </el-popover>
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
  position: sticky;
  top: 0;
  z-index: 1000;
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

.notification-list {
  max-height: 360px;
  overflow-y: auto;
}

.notification-item {
  width: 100%;
  padding: 10px 4px;
  border: 0;
  border-bottom: 1px solid #ebeef5;
  background: transparent;
  cursor: pointer;
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.notification-item:hover {
  background: #f5f7fa;
}

.notification-item.unread {
  background: #fef6f6;
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
