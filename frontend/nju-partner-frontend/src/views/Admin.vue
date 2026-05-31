<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
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

async function loadData() {
  loading.value = true
  try {
    const [posts, users, comments] = await Promise.all([
      getAdminPosts({ pageNum: 1, pageSize: 50 }),
      getAdminUsers({ pageNum: 1, pageSize: 50 }),
      getAdminComments({ pageNum: 1, pageSize: 50 }),
    ])
    postList.value = posts.records || []
    userList.value = users.records || []
    commentList.value = comments.records || []
  } finally {
    loading.value = false
  }
}

async function handleDeletePost(id) {
  await deleteAdminPost(id)
  ElMessage.success('帖子已删除')
  await loadData()
}

async function handleToggleUser(user) {
  if (user.status === 0) {
    await unbanUser(user.id)
    ElMessage.success('用户已解封')
  } else {
    await banUser(user.id)
    ElMessage.success('用户已封禁')
  }
  await loadData()
}

async function handleDeleteComment(id) {
  await deleteAdminComment(id)
  ElMessage.success('评论已删除')
  await loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="loading">
      <h2>后台管理</h2>

      <el-tabs type="border-card">
        <el-tab-pane label="帖子管理">
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
        </el-tab-pane>

        <el-tab-pane label="用户管理">
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
        </el-tab-pane>

        <el-tab-pane label="评论管理">
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
</style>
