<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminPosts,
  deleteAdminPost,
  getAdminUsers,
  banUser,
  unbanUser,
  getAdminComments,
  deleteAdminComment,
} from '@/api/admin'
import { POST_STATUS_MAP } from '@/constants'
import Navbar from '@/components/Navbar.vue'

const loading = ref(false)
const postList = ref([])
const userList = ref([])
const commentList = ref([])
const activeTab = ref('posts')
const postQuery = reactive({ keyword: '', pageNum: 1, pageSize: 10, total: 0 })
const userQuery = reactive({ keyword: '', pageNum: 1, pageSize: 10, total: 0 })
const commentQuery = reactive({ keyword: '', pageNum: 1, pageSize: 10, total: 0 })

async function loadPosts() {
  loading.value = true
  try {
    const posts = await getAdminPosts(postQuery)
    postList.value = posts.records || []
    postQuery.total = posts.total || 0
  } finally {
    loading.value = false
  }
}

async function loadUsers() {
  loading.value = true
  try {
    const users = await getAdminUsers(userQuery)
    userList.value = users.records || []
    userQuery.total = users.total || 0
  } finally {
    loading.value = false
  }
}

async function loadComments() {
  loading.value = true
  try {
    const comments = await getAdminComments(commentQuery)
    commentList.value = comments.records || []
    commentQuery.total = comments.total || 0
  } finally {
    loading.value = false
  }
}

async function handleDeletePost(id) {
  await ElMessageBox.confirm('确认删除这个帖子吗？', '提示', { type: 'warning' })
  await deleteAdminPost(id)
  ElMessage.success('帖子已删除')
  await loadPosts()
}

async function handleToggleUser(user) {
  if (user.status === 0) {
    await ElMessageBox.confirm('确认解封这个用户吗？', '提示', { type: 'warning' })
    await unbanUser(user.id)
    ElMessage.success('用户已解封')
  } else {
    await ElMessageBox.confirm('确认封禁这个用户吗？', '提示', { type: 'warning' })
    await banUser(user.id)
    ElMessage.success('用户已封禁')
  }
  await loadUsers()
}

async function handleDeleteComment(id) {
  await ElMessageBox.confirm('确认删除这条评论吗？', '提示', { type: 'warning' })
  await deleteAdminComment(id)
  ElMessage.success('评论已删除')
  await loadComments()
}

function handleTabChange(name) {
  if (name === 'posts') loadPosts()
  if (name === 'users') loadUsers()
  if (name === 'comments') loadComments()
}

function searchPosts() {
  postQuery.pageNum = 1
  loadPosts()
}

function searchUsers() {
  userQuery.pageNum = 1
  loadUsers()
}

function searchComments() {
  commentQuery.pageNum = 1
  loadComments()
}

function getPostStatusTagClass(status) {
  if (status === 0) return 'is-recruiting'
  if (status === 1) return 'is-full'
  if (status === 2) return 'is-closed'
  if (status === 3) return 'is-expired'
  return 'is-muted'
}

function getUserStatusText(status) {
  return status === 0 ? '已封禁' : '正常'
}

function getUserStatusTagClass(status) {
  return status === 0 ? 'is-banned' : 'is-normal'
}

onMounted(loadPosts)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="loading">
      <div class="admin-header">
        <div>
          <h2>后台管理</h2>
          <p>帖子、用户与评论的统一管理入口</p>
        </div>
      </div>

      <div class="admin-stats">
        <div><strong>{{ postQuery.total }}</strong><span>帖子总数</span></div>
        <div><strong>{{ userQuery.total }}</strong><span>用户总数</span></div>
        <div><strong>{{ commentQuery.total }}</strong><span>评论总数</span></div>
      </div>

      <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
        <el-tab-pane label="帖子管理" name="posts">
          <div class="toolbar">
            <el-input v-model="postQuery.keyword" placeholder="搜索标题或描述" clearable />
            <el-button type="primary" @click="searchPosts">搜索</el-button>
          </div>
          <div class="admin-table-frame">
            <el-table :data="postList" border class="admin-table">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="标题" min-width="180" />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="scope">
                  <span class="status-pill" :class="getPostStatusTagClass(scope.row.status)">
                    {{ POST_STATUS_MAP[scope.row.status] }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button type="danger" link @click="handleDeletePost(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <el-pagination
            v-model:current-page="postQuery.pageNum"
            v-model:page-size="postQuery.pageSize"
            :total="postQuery.total"
            layout="total, prev, pager, next"
            class="pagination"
            @current-change="loadPosts"
          />
        </el-tab-pane>

        <el-tab-pane label="用户管理" name="users">
          <div class="toolbar">
            <el-input v-model="userQuery.keyword" placeholder="搜索用户名或昵称" clearable />
            <el-button type="primary" @click="searchUsers">搜索</el-button>
          </div>
          <div class="admin-table-frame">
            <el-table :data="userList" border class="admin-table">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" min-width="120" />
              <el-table-column prop="nickname" label="昵称" min-width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <span class="status-pill" :class="getUserStatusTagClass(scope.row.status)">
                    {{ getUserStatusText(scope.row.status) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button type="danger" link @click="handleToggleUser(scope.row)">
                    {{ scope.row.status === 0 ? '解封' : '封禁' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <el-pagination
            v-model:current-page="userQuery.pageNum"
            v-model:page-size="userQuery.pageSize"
            :total="userQuery.total"
            layout="total, prev, pager, next"
            class="pagination"
            @current-change="loadUsers"
          />
        </el-tab-pane>

        <el-tab-pane label="评论管理" name="comments">
          <div class="toolbar">
            <el-input v-model="commentQuery.keyword" placeholder="搜索评论内容" clearable />
            <el-button type="primary" @click="searchComments">搜索</el-button>
          </div>
          <div class="admin-table-frame">
            <el-table :data="commentList" border class="admin-table">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="postTitle" label="帖子" min-width="160" />
              <el-table-column prop="content" label="内容" min-width="220" />
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button type="danger" link @click="handleDeleteComment(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <el-pagination
            v-model:current-page="commentQuery.pageNum"
            v-model:page-size="commentQuery.pageSize"
            :total="commentQuery.total"
            layout="total, prev, pager, next"
            class="pagination"
            @current-change="loadComments"
          />
        </el-tab-pane>
      </el-tabs>
    </main>
  </div>
</template>

<style scoped>
.page-main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 32px 20px 48px;
}

h2 {
  margin: 0 0 6px;
  color: #1f2d3d;
}

.admin-header {
  margin-bottom: 16px;
  padding: 24px 26px;
  border: 1px solid rgba(79, 100, 127, 0.16);
  border-radius: 24px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.86), rgba(247, 249, 252, 0.72));
  backdrop-filter: blur(12px);
  box-shadow: 0 18px 38px rgba(42, 52, 66, 0.1);
}

.admin-header p {
  margin: 0;
  color: #7a8495;
}

.admin-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 20px;
}

.admin-stats div {
  padding: 18px 18px 16px;
  border: 1px solid rgba(79, 100, 127, 0.16);
  border-radius: 20px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.84), rgba(247, 249, 252, 0.74));
  backdrop-filter: blur(12px);
  box-shadow: 0 14px 28px rgba(42, 52, 66, 0.08);
}

.admin-stats strong,
.admin-stats span {
  display: block;
}

.admin-stats strong {
  color: #1f2d3d;
  font-size: 24px;
}

.admin-stats span {
  margin-top: 4px;
  color: #7a8495;
}

.toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 16px;
  max-width: 420px;
}

.pagination {
  justify-content: center;
  margin-top: 16px;
}

.page-main :deep(.el-tabs--border-card) {
  border: 1px solid rgba(79, 100, 127, 0.16);
  border-radius: 22px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.84);
  backdrop-filter: blur(12px);
  box-shadow: 0 18px 38px rgba(42, 52, 66, 0.1);
}

.page-main :deep(.el-tabs--border-card > .el-tabs__header) {
  background:
    linear-gradient(180deg, rgba(248, 250, 252, 0.96), rgba(241, 245, 249, 0.9));
  border-bottom-color: rgba(123, 137, 156, 0.18);
}

.page-main :deep(.el-tabs--border-card > .el-tabs__content) {
  padding: 18px;
}

.page-main :deep(.el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active) {
  background: rgba(255, 255, 255, 0.88);
  border-right-color: rgba(123, 137, 156, 0.16);
  border-left-color: rgba(123, 137, 156, 0.16);
}

.admin-table-frame {
  position: relative;
  padding: 1px;
  border-radius: 18px;
  border: 0;
  overflow: hidden;
  background: rgba(123, 137, 156, 0.28);
}

.admin-table {
  border: 0;
  border-radius: 17px;
  --el-table-border-color: rgba(123, 137, 156, 0.18);
}

.admin-table :deep(.el-table__inner-wrapper) {
  border-radius: 17px;
}

.admin-table :deep(.el-table__inner-wrapper::before),
.admin-table :deep(.el-table::before),
.admin-table :deep(.el-table::after) {
  display: none;
}

.admin-table :deep(.el-table__border-left-patch) {
  display: none;
}

.admin-table :deep(.el-table__cell) {
  border-color: rgba(123, 137, 156, 0.18);
}

.admin-table :deep(th.el-table__cell) {
  border-bottom-color: rgba(123, 137, 156, 0.18);
  background: #f6f8fb;
}

.admin-table :deep(.el-table__row:last-child td.el-table__cell) {
  border-bottom: 0;
}

.admin-table :deep(th.el-table__cell:first-child),
.admin-table :deep(td.el-table__cell:first-child) {
  border-left: 0;
}

.admin-table :deep(th.el-table__cell:last-child),
.admin-table :deep(td.el-table__cell:last-child) {
  border-right: 0;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 64px;
  height: 28px;
  padding: 0 10px;
  border: 1px solid transparent;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 800;
  line-height: 1;
  white-space: nowrap;
}

.status-pill::before {
  content: '';
  width: 6px;
  height: 6px;
  margin-right: 6px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.9;
}

.status-pill.is-recruiting,
.status-pill.is-normal {
  color: #2f8f6b;
  border-color: rgba(47, 143, 107, 0.22);
  background: rgba(47, 143, 107, 0.08);
}

.status-pill.is-full {
  color: #a06a16;
  border-color: rgba(217, 154, 34, 0.26);
  background: rgba(217, 154, 34, 0.1);
}

.status-pill.is-closed,
.status-pill.is-expired,
.status-pill.is-muted {
  color: #66758a;
  border-color: rgba(102, 117, 138, 0.22);
  background: rgba(102, 117, 138, 0.08);
}

.status-pill.is-banned {
  color: #bd4d4d;
  border-color: rgba(189, 77, 77, 0.24);
  background: rgba(189, 77, 77, 0.08);
}

.page-main :deep(.el-input__wrapper) {
  min-height: 40px;
  border-radius: 12px;
}

@media (max-width: 640px) {
  .admin-stats {
    grid-template-columns: 1fr;
  }
}
</style>
