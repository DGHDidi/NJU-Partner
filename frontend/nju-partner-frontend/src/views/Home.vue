<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '@/api/post'
import Navbar from '@/components/Navbar.vue'
import FilterBar from '@/components/FilterBar.vue'
import PostCard from '@/components/PostCard.vue'

const router = useRouter()
const postList = ref([])
const loading = ref(false)
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const filters = reactive({
  keyword: '',
  campus: '',
  type: '',
  status: '',
  grade: '',
})

async function loadPosts() {
  loading.value = true
  try {
    const data = await getPostList({
      ...filters,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    })
    postList.value = data.records || []
    pagination.total = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch(newFilters) {
  Object.assign(filters, newFilters)
  pagination.pageNum = 1
  loadPosts()
}

function handlePostClick(post) {
  router.push({ name: 'PostDetail', params: { id: post.id } })
}

watch(() => pagination.pageNum, loadPosts)

onMounted(loadPosts)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main">
      <section class="hero">
        <h1>南大轻搭子</h1>
        <p>找自习搭子、运动搭子、竞赛队友、讲座同行、跨校区同行</p>
      </section>

      <FilterBar @search="handleSearch" />

      <el-skeleton :loading="loading" animated :count="3">
        <template #template>
          <el-skeleton-item variant="rect" style="height: 120px; margin-bottom: 12px" />
        </template>

        <template #default>
          <div v-if="postList.length === 0" class="empty">
            <el-empty description="暂无组队帖" />
          </div>

          <PostCard
            v-for="post in postList"
            :key="post.id"
            :post="post"
            @click="handlePostClick(post)"
          />
        </template>
      </el-skeleton>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
        />
      </div>
    </main>
  </div>
</template>

<style scoped>
.page-main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px 20px 40px;
}

.hero {
  text-align: center;
  margin-bottom: 24px;
}

.hero h1 {
  margin: 0 0 8px;
  color: #409eff;
}

.hero p {
  margin: 0;
  color: #606266;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
