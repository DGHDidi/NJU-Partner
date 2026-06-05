<script setup>
import { computed, onMounted, ref, watch } from 'vue'
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
const postId = computed(() => Number(route.params.id))

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
const detailItems = computed(() => {
  if (!post.value) return []
  return [
    { label: '发布者', value: post.value.publisher?.nickname || post.value.publisher?.username || '-', important: true },
    { label: '具体地点', value: post.value.location || '-' },
    { label: '活动时间', value: formatDateTime(post.value.activityTime), important: true },
    { label: '联系方式', value: post.value.contact || '-' },
  ]
})
const peopleCountText = computed(() => {
  if (!post.value) return '-'
  return `${post.value.currentCount} / ${post.value.needCount + 1}`
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
    post.value = await getPostDetail(postId.value)
  } finally {
    loading.value = false
  }
}

async function loadComments() {
  comments.value = await getCommentList(postId.value)
}

async function loadApplications() {
  if (!isOwner.value) return
  applications.value = await getApplicationList(postId.value)
}

async function loadMyApplication() {
  if (!userStore.isLoggedIn || isOwner.value) {
    myApplication.value = null
    return
  }
  myApplication.value = await getMyApplication(postId.value)
}

async function loadApprovedMembers() {
  approvedMembers.value = await getApprovedMembers(postId.value)
}

async function initData() {
  replyToComment.value = null
  commentContent.value = ''
  applyMessage.value = ''
  await loadDetail()
  await Promise.all([loadComments(), loadApplications(), loadMyApplication(), loadApprovedMembers()])
}

async function handleApply() {
  applying.value = true
  try {
    await applyPost(postId.value, { message: applyMessage.value.trim() })
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
  await closePost(postId.value)
  ElMessage.success('帖子已关闭')
  await loadDetail()
}

async function handleCreateComment() {
  if (!commentContent.value.trim()) return
  const isReply = !!replyToComment.value
  await createComment(postId.value, {
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

function getApplicationTagType(status) {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  if (status === 3) return 'info'
  return 'info'
}

function getApplicationActionText(status) {
  if (status === 1) return '已通过'
  if (status === 2) return '已拒绝'
  if (status === 3) return '对方已取消'
  return '无需操作'
}

function goLoginForComment() {
  ElMessage.warning('请先登录后发表评论')
  router.push({ name: 'Login' })
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
    await removeFavorite(postId.value)
    ElMessage.success('已取消收藏')
  } else {
    await addFavorite(postId.value)
    ElMessage.success('已收藏')
  }
  await loadDetail()
}

onMounted(initData)

watch(
  () => route.params.id,
  () => {
    initData()
  },
)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="loading">
      <el-card class="section" v-if="post">
        <template #header>
          <div class="detail-header">
            <div>
              <h2>{{ post.title }}</h2>
              <p>{{ post.type }} · {{ post.campus || '校区不限' }}</p>
            </div>
            <div class="detail-summary">
              <el-tag :type="post.status === 0 ? 'success' : 'info'" size="large">
                {{ POST_STATUS_MAP[post.status] }}
              </el-tag>
              <div class="people-badge">
                <strong>{{ peopleCountText }}</strong>
                <span>当前人数</span>
              </div>
            </div>
          </div>
        </template>
        <div class="detail-grid">
          <div
            v-for="item in detailItems"
            :key="item.label"
            class="detail-item"
            :class="{ important: item.important, count: item.count }"
          >
            <span class="detail-label">{{ item.label }}</span>
            <strong class="detail-value">
              {{ item.value }}
            </strong>
          </div>
        </div>
        <div class="description-block">
          <span class="detail-label">活动描述</span>
          <p>{{ post.description || '-' }}</p>
        </div>

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
        <template #header>
          <div class="section-header">
            <span>已报名成员</span>
            <small>{{ enrolledMembers.length }} 人</small>
          </div>
        </template>
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
        <template #header>
          <div class="section-header">
            <span>报名列表</span>
            <small>发布者可见</small>
          </div>
        </template>
        <el-empty v-if="applications.length === 0" description="暂无报名" />
        <el-table v-else :data="applications" class="application-table">
          <el-table-column prop="applicant.nickname" label="报名者" min-width="140">
            <template #default="scope">
              <div class="application-user">
                <el-popover placement="top" trigger="hover" width="220">
                  <template #reference>
                    <span class="application-avatar">{{ (scope.row.applicant?.nickname || scope.row.applicant?.username || '?').slice(0, 1) }}</span>
                  </template>
                  <div class="profile-popover-content">
                    <strong>{{ scope.row.applicant?.nickname || scope.row.applicant?.username || '-' }}</strong>
                    <span>年级：{{ scope.row.applicant?.grade || '-' }}</span>
                    <span>校区：{{ scope.row.applicant?.campus || '-' }}</span>
                    <span>专业：{{ scope.row.applicant?.major || '-' }}</span>
                  </div>
                </el-popover>
                <strong>{{ scope.row.applicant?.nickname || scope.row.applicant?.username || '-' }}</strong>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="message" label="报名说明" min-width="160" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getApplicationTagType(scope.row.status)" effect="light">
                {{ APPLICATION_STATUS_MAP[scope.row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="190">
            <template #default="scope">
              <div v-if="scope.row.status === 0" class="application-actions">
                <el-button type="success" plain @click="handlePass(scope.row.id)">通过</el-button>
                <el-button type="danger" plain @click="handleReject(scope.row.id)">拒绝</el-button>
              </div>
              <span v-else class="application-action-text" :class="`status-${scope.row.status}`">
                {{ getApplicationActionText(scope.row.status) }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="section">
        <template #header>
          <div class="section-header">
            <span>评论列表</span>
            <small>{{ comments.length }} 条</small>
          </div>
        </template>

        <div class="comment-create" v-if="userStore.isLoggedIn">
          <div v-if="replyToComment" class="reply-target">
            正在回复 @{{ replyTargetName }}
            <el-button link type="primary" @click="handleCancelReply">取消回复</el-button>
          </div>
          <el-input v-model="commentContent" :placeholder="replyToComment ? `回复 @${replyTargetName}` : '输入评论内容'" />
          <el-button class="comment-submit" type="primary" @click="handleCreateComment">{{ replyToComment ? '发表回复' : '发表评论' }}</el-button>
        </div>
        <div v-else class="comment-login">
          <span>登录后可以发表评论和回复。</span>
          <el-button type="primary" @click="goLoginForComment">登录后发表评论</el-button>
        </div>

        <el-empty v-if="comments.length === 0" description="暂无评论" />
        <div v-for="(item, index) in threadedComments" :key="item.id" class="comment-thread">
          <div class="comment-item">
            <el-popover placement="top" trigger="hover" width="220">
              <template #reference>
                <div class="comment-avatar">{{ (item.user?.nickname || item.user?.username || '匿').slice(0, 1) }}</div>
              </template>
              <div class="profile-popover-content">
                <strong>{{ item.user?.nickname || item.user?.username || '匿名用户' }}</strong>
                <span>年级：{{ item.user?.grade || '-' }}</span>
                <span>校区：{{ item.user?.campus || '-' }}</span>
                <span>专业：{{ item.user?.major || '-' }}</span>
              </div>
            </el-popover>
            <div class="comment-body">
              <div class="comment-head">
                <strong>{{ item.user?.nickname || item.user?.username || '匿名用户' }}</strong>
                <el-tag v-if="post?.userId === item.userId" size="small" type="success">楼主</el-tag>
                <span class="floor">#{{ index + 1 }}</span>
                <span class="comment-time">{{ formatDateTime(item.createdTime) }}</span>
              </div>
              <div class="comment-content">{{ item.content }}</div>
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
          </div>

          <div v-if="item.replies.length" class="reply-list">
            <div v-for="reply in item.replies" :key="reply.id" class="comment-item reply">
              <el-popover placement="top" trigger="hover" width="220">
                <template #reference>
                  <div class="comment-avatar small">{{ (reply.user?.nickname || reply.user?.username || '匿').slice(0, 1) }}</div>
                </template>
                <div class="profile-popover-content">
                  <strong>{{ reply.user?.nickname || reply.user?.username || '匿名用户' }}</strong>
                  <span>年级：{{ reply.user?.grade || '-' }}</span>
                  <span>校区：{{ reply.user?.campus || '-' }}</span>
                  <span>专业：{{ reply.user?.major || '-' }}</span>
                </div>
              </el-popover>
              <div class="comment-body">
                <div class="comment-head">
                  <strong>{{ reply.user?.nickname || reply.user?.username || '匿名用户' }}</strong>
                  <el-tag v-if="post?.userId === reply.userId" size="small" type="success">楼主</el-tag>
                  <span v-if="reply.replyToUser" class="reply-label">回复 @{{ reply.replyToUser.nickname || reply.replyToUser.username }}</span>
                  <span class="comment-time">{{ formatDateTime(reply.createdTime) }}</span>
                </div>
                <div class="comment-content">{{ reply.content }}</div>
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
        </div>
      </el-card>
    </main>
  </div>
</template>

<style scoped>
.page-main {
  max-width: 900px;
  margin: 0 auto;
  padding: 28px 20px 44px;
}

.section {
  margin-bottom: 18px;
  border-color: rgba(106, 44, 138, 0.15);
  border-radius: 20px;
}

.detail-header,
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.detail-header h2 {
  margin: 0 0 6px;
  color: #172033;
  font-size: 24px;
}

.detail-header p {
  margin: 0;
  color: #738295;
  font-size: 13px;
}

.detail-summary {
  display: flex;
  align-items: center;
  gap: 12px;
}

.people-badge {
  width: fit-content;
  padding: 10px 14px;
  border: 1px solid rgba(64, 158, 255, 0.28);
  border-radius: 18px;
  text-align: center;
  background:
    linear-gradient(135deg, rgba(238, 244, 255, 0.96), rgba(247, 243, 255, 0.88));
  box-shadow: 0 12px 24px rgba(64, 158, 255, 0.13);
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.people-badge strong {
  display: block;
  color: #1f2d3d;
  font-size: 22px;
  font-weight: 900;
  line-height: 1;
  white-space: nowrap;
}

.people-badge span {
  display: block;
  margin-top: 5px;
  color: #7b8794;
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
}

.section-header span {
  color: #172033;
  font-weight: 800;
}

.section-header small {
  color: #8793a3;
  font-size: 12px;
  font-weight: 500;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-item,
.description-block {
  padding: 14px 16px;
  border: 1px solid rgba(106, 44, 138, 0.14);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.82);
}

.detail-item.important {
  border-color: rgba(106, 44, 138, 0.18);
  background: rgba(255, 255, 255, 0.82);
}

.detail-label {
  display: block;
  margin-bottom: 7px;
  color: #7b8794;
  font-size: 12px;
  font-weight: 700;
}

.detail-value {
  color: #172033;
  font-size: 15px;
  line-height: 1.35;
}

.description-block {
  margin-top: 12px;
}

.description-block p {
  margin: 0;
  color: #2f3f53;
  line-height: 1.7;
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

.comment-submit {
  min-width: 104px;
  box-shadow: 0 8px 18px rgba(23, 78, 166, 0.2);
}

.comment-login {
  margin-bottom: 12px;
  padding: 14px 16px;
  border: 1px solid rgba(213, 224, 236, 0.9);
  border-radius: 10px;
  background: rgba(248, 251, 255, 0.86);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #66758a;
}

.reply-target {
  flex-basis: 100%;
  color: #606266;
  font-size: 13px;
}

.comment-thread {
  border-bottom: 1px solid rgba(106, 44, 138, 0.1);
}

.comment-item {
  padding: 14px 0;
  display: flex;
  gap: 12px;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  flex: 0 0 auto;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #409eff, #8b7cf6);
  color: #fff;
  font-weight: 800;
}

.comment-avatar.small {
  width: 30px;
  height: 30px;
  font-size: 12px;
}

.comment-body {
  min-width: 0;
  flex: 1;
}

.comment-head {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.comment-content {
  margin-top: 6px;
  color: #2f3f53;
  line-height: 1.65;
}

.reply-list {
  margin-left: 24px;
  margin-bottom: 8px;
  padding-left: 12px;
  border-left: 3px solid rgba(139, 124, 246, 0.34);
  background: rgba(248, 251, 255, 0.78);
  border-radius: 0 14px 14px 0;
}

.comment-item.reply {
  padding: 8px 0;
}

.comment-actions {
  margin-top: 4px;
}

.comment-actions :deep(.el-button.is-link),
.reply-target :deep(.el-button.is-link) {
  height: auto;
  padding: 0;
  border: 0;
  background: transparent;
  box-shadow: none;
}

.comment-actions :deep(.el-button.is-link:hover),
.reply-target :deep(.el-button.is-link:hover) {
  transform: none;
  background: transparent;
}

.reply-label,
.comment-time,
.floor {
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
  border: 1px solid #e3eaf3;
  border-radius: 8px;
  background: #f8fafc;
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

.application-table {
  border: 1px solid rgba(106, 44, 138, 0.15);
  border-radius: 16px;
  overflow: hidden;
}

.application-table :deep(.el-table__inner-wrapper::before),
.application-table :deep(.el-table__border-left-patch) {
  display: none;
}

.application-table :deep(.el-table__cell) {
  border-color: rgba(106, 44, 138, 0.1);
}

.application-table :deep(.el-table__header th) {
  background: rgba(247, 243, 255, 0.72);
  color: #5f6f84;
  font-weight: 800;
}

.application-table :deep(.el-table__row:hover > td.el-table__cell) {
  background: rgba(248, 251, 255, 0.72);
}

.application-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.application-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.9), rgba(139, 124, 246, 0.9));
  color: #fff;
  font-weight: 900;
}

.profile-popover-content {
  display: grid;
  gap: 6px;
  color: #5f6f84;
  font-size: 13px;
}

.profile-popover-content strong {
  color: #1f2d3d;
  font-size: 14px;
}

.application-actions {
  display: flex;
  gap: 8px;
}

.application-actions :deep(.el-button) {
  min-width: 58px;
  border-radius: 12px;
}

.application-actions :deep(.el-button:hover) {
  transform: none;
}

.application-action-text {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.68);
  color: #7a8495;
  border: 1px solid rgba(106, 44, 138, 0.12);
  font-size: 13px;
  font-weight: 700;
}

.application-action-text.status-1 {
  color: #529b2e;
  border-color: rgba(103, 194, 58, 0.24);
  background: rgba(240, 249, 235, 0.82);
}

.application-action-text.status-2 {
  color: #c45656;
  border-color: rgba(245, 108, 108, 0.22);
  background: rgba(254, 240, 240, 0.82);
}

.application-action-text.status-3 {
  color: #7a8495;
  border-color: rgba(144, 147, 153, 0.22);
  background: rgba(244, 244, 245, 0.82);
}

h2 {
  margin: 0;
}

@media (max-width: 760px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-header,
  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .detail-summary {
    width: 100%;
    justify-content: space-between;
  }

  .comment-login {
    align-items: stretch;
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
