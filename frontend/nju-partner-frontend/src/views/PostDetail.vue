<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail, closePost } from '@/api/post'
import { applyPost, getApplicationList, passApplication, rejectApplication } from '@/api/application'
import { createComment, deleteComment, getCommentList } from '@/api/comment'
import { addFavorite, removeFavorite } from '@/api/favorite'
import { APPLICATION_STATUS_MAP, POST_STATUS_MAP } from '@/constants'
import { useUserStore } from '@/stores/user'
import Navbar from '@/components/Navbar.vue'

const route = useRoute()
const userStore = useUserStore()
const postId = Number(route.params.id)

const post = ref(null)
const comments = ref([])
const applications = ref([])
const commentContent = ref('')
const applyMessage = ref('')
const loading = ref(false)

const currentUserId = computed(() => userStore.userInfo?.id)
const isOwner = computed(() => post.value?.userId && post.value.userId === currentUserId.value)
const canApply = computed(() => userStore.isLoggedIn && !isOwner.value && post.value?.status === 0)
const canClose = computed(() => isOwner.value && post.value?.status === 0)

async function loadDetail() {
  loading.value = true
  try {
    post.value = await getPostDetail(postId)
  } finally {
    loading.value = false
  }
}

async function loadComments() {
  comments.value = await getCommentList(postId)
}

async function loadApplications() {
  if (!isOwner.value) return
  applications.value = await getApplicationList(postId)
}

async function initData() {
  await loadDetail()
  await Promise.all([loadComments(), loadApplications()])
}

async function handleApply() {
  await applyPost(postId, { message: applyMessage.value })
  ElMessage.success('报名成功')
  applyMessage.value = ''
  await loadDetail()
}

async function handlePass(id) {
  await passApplication(id)
  ElMessage.success('已通过报名')
  await Promise.all([loadDetail(), loadApplications()])
}

async function handleReject(id) {
  await rejectApplication(id)
  ElMessage.success('已拒绝报名')
  await loadApplications()
}

async function handleClosePost() {
  await ElMessageBox.confirm('确认关闭该帖子招募吗？', '提示', { type: 'warning' })
  await closePost(postId)
  ElMessage.success('帖子已关闭')
  await loadDetail()
}

async function handleCreateComment() {
  if (!commentContent.value.trim()) return
  await createComment(postId, { content: commentContent.value.trim() })
  commentContent.value = ''
  ElMessage.success('评论成功')
  await loadComments()
}

async function handleDeleteComment(id) {
  await deleteComment(id)
  ElMessage.success('评论已删除')
  await loadComments()
}

async function handleToggleFavorite() {
  if (post.value?.favorited) {
    await removeFavorite(postId)
    ElMessage.success('已取消收藏')
  } else {
    await addFavorite(postId)
    ElMessage.success('已收藏')
  }
  await loadDetail()
}

onMounted(initData)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="loading">
      <el-card class="section" v-if="post">
        <template #header>
          <h2>{{ post.title }}</h2>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="发布者">{{ post.publisher?.nickname || post.publisher?.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="活动类型">{{ post.type }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ POST_STATUS_MAP[post.status] }}</el-descriptions-item>
          <el-descriptions-item label="活动校区">{{ post.campus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="具体地点">{{ post.location || '-' }}</el-descriptions-item>
          <el-descriptions-item label="活动时间">{{ post.activityTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人数">{{ post.currentCount }} / {{ post.needCount }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ post.contact || '-' }}</el-descriptions-item>
          <el-descriptions-item label="活动描述">{{ post.description || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="actions">
          <el-input v-if="canApply" v-model="applyMessage" placeholder="报名留言（可选）" class="apply-input" />
          <el-button v-if="canApply" type="primary" @click="handleApply">报名</el-button>
          <el-button v-if="userStore.isLoggedIn" @click="handleToggleFavorite">
            {{ post.favorited ? '取消收藏' : '收藏' }}
          </el-button>
          <el-button v-if="canClose" type="danger" plain @click="handleClosePost">关闭招募</el-button>
        </div>
      </el-card>

      <el-card class="section">
        <template #header>评论列表</template>

        <div class="comment-create" v-if="userStore.isLoggedIn">
          <el-input v-model="commentContent" placeholder="输入评论内容" />
          <el-button type="primary" @click="handleCreateComment">发表评论</el-button>
        </div>

        <el-empty v-if="comments.length === 0" description="暂无评论" />
        <div v-for="item in comments" :key="item.id" class="comment-item">
          <div>
            <strong>{{ item.user?.nickname || item.user?.username || '匿名用户' }}</strong>
            <span class="comment-time">{{ item.createdTime }}</span>
          </div>
          <div>{{ item.content }}</div>
          <el-button
            v-if="currentUserId === item.userId || userStore.isAdmin"
            type="danger"
            link
            @click="handleDeleteComment(item.id)"
          >
            删除
          </el-button>
        </div>
      </el-card>

      <el-card class="section" v-if="isOwner">
        <template #header>报名列表（发布者可见）</template>
        <el-empty v-if="applications.length === 0" description="暂无报名" />
        <el-table v-else :data="applications" border>
          <el-table-column prop="applicant.nickname" label="报名者" min-width="140">
            <template #default="scope">
              {{ scope.row.applicant?.nickname || scope.row.applicant?.username || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="message" label="留言" min-width="160" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              {{ APPLICATION_STATUS_MAP[scope.row.status] }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="scope">
              <el-button v-if="scope.row.status === 0" type="primary" link @click="handlePass(scope.row.id)">通过</el-button>
              <el-button v-if="scope.row.status === 0" type="danger" link @click="handleReject(scope.row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </main>
  </div>
</template>

<style scoped>
.page-main {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px 40px;
}

.section {
  margin-bottom: 16px;
}

.actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.apply-input {
  max-width: 320px;
}

.comment-create {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.comment-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-time {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}

h2 {
  margin: 0;
}
</style>
