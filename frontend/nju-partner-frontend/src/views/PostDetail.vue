<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail, closePost } from '@/api/post'
import { applyPost, cancelApplication, getApplicationList, getApprovedMembers, getMyApplication, passApplication, rejectApplication } from '@/api/application'
import { createComment, deleteComment, getCommentList } from '@/api/comment'
import { addFavorite, removeFavorite } from '@/api/favorite'
import { APPLICATION_STATUS_MAP, POST_STATUS_MAP } from '@/constants'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/date'
import Navbar from '@/components/Navbar.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const postId = Number(route.params.id)

const post = ref(null)
const comments = ref([])
const applications = ref([])
const approvedMembers = ref([])
const myApplication = ref(null)
const commentContent = ref('')
const replyToComment = ref(null)
const applyMessage = ref('')
const loading = ref(false)
const applyDialogVisible = ref(false)
const applying = ref(false)

const currentUserId = computed(() => userStore.userInfo?.id)
const isOwner = computed(() => post.value?.userId && post.value.userId === currentUserId.value)
const hasActiveApplication = computed(() => myApplication.value && myApplication.value.status !== 3)
const canCancelApplication = computed(() => myApplication.value && [0, 2].includes(myApplication.value.status))
const canApply = computed(() => userStore.isLoggedIn && !isOwner.value && post.value?.status === 0 && !hasActiveApplication.value)
const canClose = computed(() => isOwner.value && post.value?.status === 0)
const enrolledMembers = computed(() => {
  if (!post.value?.publisher) return []

  const owner = {
    id: `owner-${post.value.userId}`,
    user: post.value.publisher,
    owner: true,
  }
  const members = approvedMembers.value
    .filter((item) => item.applicant?.id !== post.value.userId)
    .map((item) => ({
      id: item.id,
      user: item.applicant,
      owner: false,
    }))

  return [owner, ...members]
})
const applyDisabledText = computed(() => {
  if (!userStore.isLoggedIn || isOwner.value || !post.value || canApply.value) return ''
  if (hasActiveApplication.value) {
    return `报名状态：${APPLICATION_STATUS_MAP[myApplication.value.status] || '-'}`
  }
  if (post.value.status === 0) return ''
  return post.value.status === 1 ? '已成团，无法报名' : '已关闭，无法报名'
})
const replyTargetName = computed(() => replyToComment.value?.user?.nickname || replyToComment.value?.user?.username || '')
const threadedComments = computed(() => {
  const commentMap = new Map(comments.value.map((item) => [item.id, { ...item, replies: [] }]))
  const roots = []

  commentMap.forEach((item) => {
    const parent = item.parentId ? commentMap.get(findRootCommentId(item.parentId, commentMap)) : null
    if (parent) {
      parent.replies.push(item)
    } else {
      roots.push(item)
    }
  })

  roots.forEach((item) => {
    item.replies.sort((a, b) => new Date(a.createdTime) - new Date(b.createdTime))
  })

  return roots
})

function findRootCommentId(commentId, commentMap) {
  let current = commentMap.get(commentId)
  while (current?.parentId && commentMap.has(current.parentId)) {
    current = commentMap.get(current.parentId)
  }
  return current?.id || commentId
}

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

async function loadMyApplication() {
  if (!userStore.isLoggedIn || isOwner.value) {
    myApplication.value = null
    return
  }
  myApplication.value = await getMyApplication(postId)
}

async function loadApprovedMembers() {
  approvedMembers.value = await getApprovedMembers(postId)
}

async function initData() {
  await loadDetail()
  await Promise.all([loadComments(), loadApplications(), loadMyApplication(), loadApprovedMembers()])
}

async function handleApply() {
  applying.value = true
  try {
    await applyPost(postId, { message: applyMessage.value.trim() })
    ElMessage.success('报名成功')
    applyMessage.value = ''
    applyDialogVisible.value = false
    await Promise.all([loadDetail(), loadMyApplication()])
  } finally {
    applying.value = false
  }
}

async function handleCancelApplication() {
  await ElMessageBox.confirm('确认取消这次报名吗？', '提示', { type: 'warning' })
  await cancelApplication(myApplication.value.id)
  ElMessage.success('报名已取消')
  await Promise.all([loadDetail(), loadMyApplication(), loadApprovedMembers()])
}

async function handlePass(id) {
  await passApplication(id)
  ElMessage.success('已通过报名')
  await Promise.all([loadDetail(), loadApplications(), loadApprovedMembers()])
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
  const isReply = !!replyToComment.value
  await createComment(postId, {
    content: commentContent.value.trim(),
    parentId: replyToComment.value?.id || null,
  })
  commentContent.value = ''
  replyToComment.value = null
  ElMessage.success(isReply ? '回复成功' : '评论成功')
  await loadComments()
}

function handleReplyComment(comment) {
  replyToComment.value = comment
}

function handleCancelReply() {
  replyToComment.value = null
}

function canDeleteComment(comment) {
  return currentUserId.value === comment.userId || userStore.isAdmin
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
          <el-descriptions-item label="活动时间">{{ formatDateTime(post.activityTime) }}</el-descriptions-item>
          <el-descriptions-item label="人数">{{ post.currentCount }} / {{ post.needCount }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ post.contact || '-' }}</el-descriptions-item>
          <el-descriptions-item label="活动描述">{{ post.description || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="actions">
          <el-button v-if="canApply" type="primary" @click="applyDialogVisible = true">报名</el-button>
          <el-button v-else-if="applyDisabledText" type="primary" disabled>{{ applyDisabledText }}</el-button>
          <el-button v-if="canCancelApplication" type="danger" plain @click="handleCancelApplication">取消报名</el-button>
          <el-button v-if="userStore.isLoggedIn" @click="handleToggleFavorite">
            {{ post.favorited ? '取消收藏' : '收藏' }}
          </el-button>
          <el-button v-if="isOwner" @click="router.push({ name: 'PostEdit', params: { id: postId } })">编辑</el-button>
          <el-button v-if="canClose" type="danger" plain @click="handleClosePost">关闭招募</el-button>
        </div>
      </el-card>

      <el-card class="section">
        <template #header>已报名成员</template>
        <el-empty v-if="enrolledMembers.length === 0" description="暂无报名成员" />
        <div v-else class="member-list">
          <div v-for="item in enrolledMembers" :key="item.id" class="member-item">
            <strong>
              {{ item.user?.nickname || item.user?.username || '-' }}
              <el-tag v-if="item.owner" size="small" type="success">楼主</el-tag>
            </strong>
            <span>{{ item.user?.campus || '-' }}</span>
            <span>{{ item.user?.grade || '-' }}</span>
            <span>{{ item.user?.major || '-' }}</span>
          </div>
        </div>
      </el-card>

      <el-dialog v-model="applyDialogVisible" title="确认报名" width="420px" destroy-on-close>
        <el-input
          v-model="applyMessage"
          type="textarea"
          :rows="4"
          maxlength="255"
          show-word-limit
          placeholder="报名说明（可选），不会作为公开评论发布"
        />
        <template #footer>
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="applying" @click="handleApply">确认报名</el-button>
        </template>
      </el-dialog>

      <el-card class="section" v-if="isOwner">
        <template #header>报名列表（发布者可见）</template>
        <el-empty v-if="applications.length === 0" description="暂无报名" />
        <el-table v-else :data="applications" border>
          <el-table-column prop="applicant.nickname" label="报名者" min-width="140">
            <template #default="scope">
              {{ scope.row.applicant?.nickname || scope.row.applicant?.username || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="message" label="报名说明" min-width="160" />
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

      <el-card class="section">
        <template #header>评论列表</template>

        <div class="comment-create" v-if="userStore.isLoggedIn">
          <div v-if="replyToComment" class="reply-target">
            正在回复 @{{ replyTargetName }}
            <el-button link type="primary" @click="handleCancelReply">取消回复</el-button>
          </div>
          <el-input v-model="commentContent" :placeholder="replyToComment ? `回复 @${replyTargetName}` : '输入评论内容'" />
          <el-button type="primary" @click="handleCreateComment">{{ replyToComment ? '发表回复' : '发表评论' }}</el-button>
        </div>

        <el-empty v-if="comments.length === 0" description="暂无评论" />
        <div v-for="item in threadedComments" :key="item.id" class="comment-thread">
          <div class="comment-item">
            <div>
              <strong>{{ item.user?.nickname || item.user?.username || '匿名用户' }}</strong>
              <el-tag v-if="post?.userId === item.userId" size="small" type="success">楼主</el-tag>
              <span class="comment-time">{{ formatDateTime(item.createdTime) }}</span>
            </div>
            <div>{{ item.content }}</div>
            <div class="comment-actions">
              <el-button v-if="userStore.isLoggedIn" type="primary" link @click="handleReplyComment(item)">回复</el-button>
              <el-button
                v-if="canDeleteComment(item)"
                type="danger"
                link
                @click="handleDeleteComment(item.id)"
              >
                删除
              </el-button>
            </div>
          </div>

          <div v-if="item.replies.length" class="reply-list">
            <div v-for="reply in item.replies" :key="reply.id" class="comment-item reply">
              <div>
                <strong>{{ reply.user?.nickname || reply.user?.username || '匿名用户' }}</strong>
                <el-tag v-if="post?.userId === reply.userId" size="small" type="success">楼主</el-tag>
                <span v-if="reply.replyToUser" class="reply-label">回复 @{{ reply.replyToUser.nickname || reply.replyToUser.username }}</span>
                <span class="comment-time">{{ formatDateTime(reply.createdTime) }}</span>
              </div>
              <div>{{ reply.content }}</div>
              <div class="comment-actions">
                <el-button v-if="userStore.isLoggedIn" type="primary" link @click="handleReplyComment(reply)">回复</el-button>
                <el-button
                  v-if="canDeleteComment(reply)"
                  type="danger"
                  link
                  @click="handleDeleteComment(reply.id)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
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

.actions,
.comment-create {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.actions {
  margin-top: 16px;
}

.comment-create {
  margin-bottom: 12px;
}

.comment-create .el-input {
  flex: 1;
  min-width: 260px;
}

.reply-target {
  flex-basis: 100%;
  color: #606266;
  font-size: 13px;
}

.comment-thread {
  border-bottom: 1px solid #f0f0f0;
}

.comment-item {
  padding: 10px 0;
}

.reply-list {
  margin-left: 24px;
  margin-bottom: 8px;
  padding-left: 12px;
  border-left: 3px solid #d9ecff;
}

.comment-item.reply {
  padding: 8px 0;
}

.comment-actions {
  margin-top: 4px;
}

.reply-label,
.comment-time {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}

.member-list {
  display: grid;
  gap: 8px;
}

.member-item {
  display: grid;
  grid-template-columns: minmax(160px, 1fr) repeat(3, minmax(80px, auto));
  gap: 12px;
  align-items: center;
  padding: 10px 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
}

.member-item strong {
  display: flex;
  gap: 8px;
  align-items: center;
}

.member-item span {
  color: #606266;
  font-size: 13px;
}

h2 {
  margin: 0;
}
</style>
