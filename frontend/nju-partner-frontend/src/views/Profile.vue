<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelApplication } from '@/api/application'
import { getProfile, updateProfile, getMyPosts, getMyApplications, getMyFavorites } from '@/api/user'
import { APPLICATION_STATUS_MAP, POST_STATUS_MAP } from '@/constants'
import { useUserStore } from '@/stores/user'
import Navbar from '@/components/Navbar.vue'

const userStore = useUserStore()
const router = useRouter()
const loading = ref(false)
const profileForm = reactive({
  nickname: '',
  avatar: '',
  campus: '',
  grade: '',
  major: '',
})
const myPosts = ref([])
const myApplications = ref([])
const myFavorites = ref([])

async function loadAll() {
  loading.value = true
  try {
    const [profile, posts, applications, favorites] = await Promise.all([
      getProfile(),
      getMyPosts({ pageNum: 1, pageSize: 20 }),
      getMyApplications({ pageNum: 1, pageSize: 20 }),
      getMyFavorites({ pageNum: 1, pageSize: 20 }),
    ])

    Object.assign(profileForm, {
      nickname: profile.nickname || '',
      avatar: profile.avatar || '',
      campus: profile.campus || '',
      grade: profile.grade || '',
      major: profile.major || '',
    })

    userStore.setUserInfo(profile)
    myPosts.value = posts.records || []
    myApplications.value = applications.records || []
    myFavorites.value = favorites.records || []
  } finally {
    loading.value = false
  }
}

async function handleSaveProfile() {
  const profile = await updateProfile(profileForm)
  userStore.setUserInfo(profile)
  ElMessage.success('资料更新成功')
}

async function handleCancelApplication(id) {
  await ElMessageBox.confirm('确认取消这条报名吗？', '提示', { type: 'warning' })
  await cancelApplication(id)
  ElMessage.success('报名已取消')
  await loadAll()
}

function getPostStatusTagType(status) {
  if (status === 0) return 'success'
  if (status === 1) return 'warning'
  return 'info'
}

function getApplicationTagType(status) {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
}

onMounted(loadAll)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="loading">
      <el-card class="section">
        <template #header>
          <div class="profile-header">
            <div class="profile-avatar">{{ (profileForm.nickname || userStore.userInfo?.username || 'N').slice(0, 1) }}</div>
            <div>
              <h2>个人中心</h2>
              <p>维护个人资料，查看发布、报名和收藏</p>
            </div>
          </div>
        </template>
        <el-form label-width="90px" class="profile-form">
          <el-form-item label="昵称">
            <el-input v-model="profileForm.nickname" />
          </el-form-item>
          <el-form-item label="头像 URL">
            <el-input v-model="profileForm.avatar" />
          </el-form-item>
          <el-form-item label="校区">
            <el-input v-model="profileForm.campus" />
          </el-form-item>
          <el-form-item label="年级">
            <el-input v-model="profileForm.grade" />
          </el-form-item>
          <el-form-item label="专业">
            <el-input v-model="profileForm.major" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSaveProfile">保存</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="profile-stats">
        <div><strong>{{ myPosts.length }}</strong><span>我的发布</span></div>
        <div><strong>{{ myApplications.length }}</strong><span>我的报名</span></div>
        <div><strong>{{ myFavorites.length }}</strong><span>我的收藏</span></div>
      </div>

      <el-tabs type="border-card">
        <el-tab-pane label="我的发布">
          <el-empty v-if="myPosts.length === 0" description="暂无发布" />
          <el-table v-else :data="myPosts" class="profile-table">
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getPostStatusTagType(scope.row.status)" effect="light">
                  {{ POST_STATUS_MAP[scope.row.status] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button link type="primary" @click="router.push({ name: 'PostDetail', params: { id: scope.row.id } })">查看</el-button>
                <el-button link type="primary" @click="router.push({ name: 'PostEdit', params: { id: scope.row.id } })">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="我的报名">
          <el-empty v-if="myApplications.length === 0" description="暂无报名" />
          <el-table v-else :data="myApplications" class="profile-table">
            <el-table-column prop="postTitle" label="帖子" min-width="180" />
            <el-table-column prop="message" label="报名说明" min-width="160" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getApplicationTagType(scope.row.status)" effect="light">
                  {{ APPLICATION_STATUS_MAP[scope.row.status] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button link type="primary" @click="router.push({ name: 'PostDetail', params: { id: scope.row.postId } })">查看</el-button>
                <el-button
                  v-if="[0, 2].includes(scope.row.status)"
                  link
                  type="danger"
                  @click="handleCancelApplication(scope.row.id)"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="我的收藏">
          <el-empty v-if="myFavorites.length === 0" description="暂无收藏" />
          <el-table v-else :data="myFavorites" class="profile-table">
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getPostStatusTagType(scope.row.status)" effect="light">
                  {{ POST_STATUS_MAP[scope.row.status] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90">
              <template #default="scope">
                <el-button link type="primary" @click="router.push({ name: 'PostDetail', params: { id: scope.row.id } })">查看</el-button>
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
  max-width: 900px;
  margin: 0 auto;
  padding: 28px 20px 44px;
}

.section {
  margin-bottom: 18px;
  border-radius: 24px;
  border-color: rgba(106, 44, 138, 0.15);
}

.profile-form {
  max-width: 520px;
}

.profile-header h2 {
  margin: 0 0 6px;
  color: #172033;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 14px;
}

.profile-avatar {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #409eff, #8b7cf6);
  color: #fff;
  font-size: 22px;
  font-weight: 900;
  box-shadow: 0 14px 26px rgba(64, 158, 255, 0.2);
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.profile-stats div {
  padding: 16px;
  border: 1px solid rgba(106, 44, 138, 0.15);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(12px);
  box-shadow: 0 12px 28px rgba(64, 158, 255, 0.08);
}

.profile-stats strong,
.profile-stats span {
  display: block;
}

.profile-stats strong {
  color: #1f2d3d;
  font-size: 24px;
}

.profile-stats span {
  margin-top: 4px;
  color: #7a8495;
}

.profile-header p {
  margin: 0;
  color: #738295;
  font-size: 13px;
}

.page-main :deep(.el-tabs--border-card) {
  border: 1px solid rgba(106, 44, 138, 0.15);
  border-radius: 20px;
  overflow: visible;
  box-shadow: 0 14px 30px rgba(64, 158, 255, 0.08);
  background: rgba(255, 255, 255, 0.78);
}

.page-main :deep(.el-tabs--border-card > .el-tabs__header) {
  padding: 14px 14px 12px;
  border-bottom: 1px solid rgba(106, 44, 138, 0.12);
  border-radius: 20px 20px 0 0;
  background: rgba(255, 255, 255, 0.46);
  overflow: visible;
}

.page-main :deep(.el-tabs--border-card > .el-tabs__header .el-tabs__nav-wrap),
.page-main :deep(.el-tabs--border-card > .el-tabs__header .el-tabs__nav-scroll),
.page-main :deep(.el-tabs--border-card > .el-tabs__header .el-tabs__nav) {
  overflow: visible;
}

.page-main :deep(.el-tabs--border-card > .el-tabs__header .el-tabs__item) {
  height: 40px;
  line-height: 40px;
  margin-right: 8px;
  border: 1px solid transparent;
  border-radius: 999px;
  color: #66758a;
  font-weight: 700;
  transition: background 0.18s ease, color 0.18s ease, box-shadow 0.18s ease;
}

.page-main :deep(.el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active) {
  color: #fff;
  border-color: transparent;
  background: linear-gradient(135deg, #409eff, #8b7cf6);
  box-shadow: 0 10px 20px rgba(64, 158, 255, 0.18);
}

.page-main :deep(.el-tabs--border-card > .el-tabs__content) {
  padding: 20px;
  border-radius: 0 0 20px 20px;
  background: rgba(255, 255, 255, 0.58);
}

.profile-table {
  border: 1px solid rgba(106, 44, 138, 0.15);
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.74);
}

.profile-table :deep(.el-table__inner-wrapper::before),
.profile-table :deep(.el-table__border-left-patch) {
  display: none;
}

.profile-table :deep(.el-table__header th) {
  background: rgba(247, 243, 255, 0.72);
  color: #5f6f84;
  font-weight: 800;
}

.profile-table :deep(.el-table__cell) {
  border-color: rgba(106, 44, 138, 0.1);
}

.profile-table :deep(.el-table__row:hover > td.el-table__cell) {
  background: rgba(248, 251, 255, 0.72);
}

.profile-table :deep(.el-button.is-link) {
  padding: 0;
  border: 0;
  background: transparent;
  box-shadow: none;
}

.profile-table :deep(.el-button.is-link:hover) {
  transform: none;
  background: transparent;
}

@media (max-width: 640px) {
  .profile-stats {
    grid-template-columns: 1fr;
  }
}

</style>
