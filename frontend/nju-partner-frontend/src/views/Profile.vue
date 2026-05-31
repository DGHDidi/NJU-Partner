<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, getMyPosts, getMyApplications, getMyFavorites } from '@/api/user'
import { APPLICATION_STATUS_MAP, POST_STATUS_MAP } from '@/constants'
import { useUserStore } from '@/stores/user'
import Navbar from '@/components/Navbar.vue'

const userStore = useUserStore()
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

onMounted(loadAll)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="loading">
      <el-card class="section">
        <template #header>我的资料</template>
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

      <el-tabs type="border-card">
        <el-tab-pane label="我的发布">
          <el-empty v-if="myPosts.length === 0" description="暂无发布" />
          <el-table v-else :data="myPosts" border>
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">{{ POST_STATUS_MAP[scope.row.status] }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="我的报名">
          <el-empty v-if="myApplications.length === 0" description="暂无报名" />
          <el-table v-else :data="myApplications" border>
            <el-table-column prop="postTitle" label="帖子" min-width="180" />
            <el-table-column prop="message" label="留言" min-width="160" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">{{ APPLICATION_STATUS_MAP[scope.row.status] }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="我的收藏">
          <el-empty v-if="myFavorites.length === 0" description="暂无收藏" />
          <el-table v-else :data="myFavorites" border>
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">{{ POST_STATUS_MAP[scope.row.status] }}</template>
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
  padding: 24px 20px 40px;
}

.section {
  margin-bottom: 16px;
}

.profile-form {
  max-width: 520px;
}
</style>
