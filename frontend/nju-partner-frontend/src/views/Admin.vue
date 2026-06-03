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

onMounted(loadPosts)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="loading">
      <h2>后台管理</h2>

      <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
        <el-tab-pane label="帖子管理" name="posts">
          <div class="toolbar">
            <el-input v-model="postQuery.keyword" placeholder="搜索标题或描述" clearable />
            <el-button type="primary" @click="searchPosts">搜索</el-button>
          </div>
          <el-table :data="postList" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">{{ POST_STATUS_MAP[scope.row.status] }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="danger" link @click="handleDeletePost(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
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
          <el-table :data="userList" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" min-width="120" />
            <el-table-column prop="nickname" label="昵称" min-width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">{{ scope.row.status === 0 ? '已封禁' : '正常' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="danger" link @click="handleToggleUser(scope.row)">
                  {{ scope.row.status === 0 ? '解封' : '封禁' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
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
          <el-table :data="commentList" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="postTitle" label="帖子" min-width="160" />
            <el-table-column prop="content" label="内容" min-width="220" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="danger" link @click="handleDeleteComment(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
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
  padding: 24px 20px 40px;
}

h2 {
  margin: 0 0 16px;
}

.toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  max-width: 360px;
}

.pagination {
  justify-content: center;
  margin-top: 16px;
}
</style>
