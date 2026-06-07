<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
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
  timeRange: [],
})
const heroKeyword = ref('')

const recruitingCount = computed(() => postList.value.filter((item) => item.status === 0).length)
const visibleCampusCount = computed(() => new Set(postList.value.map((item) => item.campus).filter(Boolean)).size)
const typeCount = computed(() => new Set(postList.value.map((item) => item.type).filter(Boolean)).size)

async function loadPosts() {
  loading.value = true
  try {
    const data = await getPostList({
      keyword: filters.keyword,
      campus: filters.campus,
      type: filters.type,
      status: filters.status,
      grade: filters.grade,
      startTime: filters.timeRange?.[0] || '',
      endTime: filters.timeRange?.[1] || '',
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

function handleHeroSearch() {
  filters.keyword = heroKeyword.value
  pagination.pageNum = 1
  loadPosts()
}

watch(() => pagination.pageNum, loadPosts)

onMounted(loadPosts)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main">
      <section class="hero-panel">
        <div class="hero-blob one"></div>
        <div class="hero-blob two"></div>
        <div class="hero-copy">
          <p class="eyebrow">NJU PARTNER</p>
          <h1>发现正在发生的校园组队</h1>
          <p>找自习搭子、运动搭子、竞赛队友、讲座同行和跨校区同行。</p>
          <div class="hero-search">
            <el-input
              v-model="heroKeyword"
              placeholder="搜索自习、羽毛球、讲座、拼单..."
              clearable
              @keyup.enter="handleHeroSearch"
            />
            <el-button type="primary" @click="handleHeroSearch">快速搜索</el-button>
          </div>
          <div class="hero-chips">
            <span>即时招募</span>
            <span>报名审核</span>
            <span>站内通知</span>
            <span>评论回复</span>
          </div>
        </div>
        <div class="hero-media" aria-hidden="true">
          <div class="media-tile primary"></div>
          <div class="media-tile secondary"></div>
          <div class="media-caption">
            <strong>Campus moments</strong>
            <span>学习、运动、同行与协作</span>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span>{{ pagination.total }}</span>
            <label>全部帖子</label>
          </div>
          <div class="stat-item">
            <span>{{ recruitingCount }}</span>
            <label>本页招募中</label>
          </div>
          <div class="stat-item">
            <span>{{ visibleCampusCount }}</span>
            <label>覆盖校区</label>
          </div>
          <div class="stat-item">
            <span>{{ typeCount }}</span>
            <label>活动类型</label>
          </div>
        </div>
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

          <div v-else class="post-list">
            <PostCard
              v-for="post in postList"
              :key="post.id"
              :post="post"
              @click="handlePostClick(post)"
            />
          </div>
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
  max-width: 1180px;
  margin: 0 auto;
  padding: 28px 20px 44px;
}

.hero-panel {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 240px auto;
  gap: 28px;
  align-items: end;
  margin-bottom: 22px;
  padding: 34px 36px;
  border: 1px solid rgba(106, 44, 138, 0.15);
  border-radius: 24px;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.5) 1px, transparent 1px),
    linear-gradient(180deg, rgba(255, 255, 255, 0.48) 1px, transparent 1px),
    linear-gradient(145deg, rgba(245, 247, 251, 0.96), rgba(248, 245, 251, 0.92) 50%, rgba(243, 248, 247, 0.94));
  background-size: 34px 34px, 34px 34px, auto;
  box-shadow:
    0 28px 58px rgba(71, 85, 105, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.36);
}

.hero-blob {
  position: absolute;
  border-radius: 999px;
  filter: blur(46px);
  opacity: 0.55;
  pointer-events: none;
}

.hero-blob.one {
  right: 22%;
  top: -40px;
  width: 150px;
  height: 150px;
  background: #7e69ab;
}

.hero-blob.two {
  right: -30px;
  bottom: -50px;
  width: 190px;
  height: 190px;
  background: #556faa;
}

.eyebrow {
  margin: 0 0 8px;
  color: #6a5f78;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.hero-copy h1 {
  margin: 0 0 10px;
  color: #1f2d3d;
  font-size: 34px;
  line-height: 1.18;
}

.hero-copy p:last-child {
  margin: 0;
  color: #606266;
  font-size: 15px;
}

.hero-search {
  display: flex;
  gap: 10px;
  max-width: 520px;
  margin-top: 22px;
}

.hero-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 18px;
}

.hero-chips span {
  padding: 6px 10px;
  border: 1px solid rgba(106, 44, 138, 0.15);
  border-radius: 999px;
  color: #606266;
  background: rgba(255, 255, 255, 0.56);
  font-size: 12px;
}

.hero-media {
  position: relative;
  z-index: 1;
  min-height: 214px;
}

.media-tile {
  position: absolute;
  overflow: hidden;
  border: 1px solid rgba(106, 44, 138, 0.14);
  border-radius: 28px;
  background-size: cover;
  background-position: center;
  filter: saturate(0.72) contrast(1.08);
  box-shadow: 0 18px 34px rgba(71, 85, 105, 0.14);
}

.media-tile.primary {
  inset: 0 24px 52px 0;
  background-image:
    linear-gradient(180deg, rgba(31, 45, 61, 0.04), rgba(31, 45, 61, 0.28)),
    url('https://picsum.photos/seed/nju-campus-study/640/720');
}

.media-tile.secondary {
  right: 0;
  bottom: 0;
  width: 108px;
  height: 108px;
  background-image:
    linear-gradient(180deg, rgba(31, 45, 61, 0.02), rgba(31, 45, 61, 0.24)),
    url('https://picsum.photos/seed/nju-campus-team/420/420');
}

.media-caption {
  position: absolute;
  left: 18px;
  bottom: 18px;
  display: grid;
  gap: 4px;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.42);
  border-radius: 16px;
  color: #fff;
  background: rgba(31, 45, 61, 0.36);
  backdrop-filter: blur(12px);
}

.media-caption strong {
  font-size: 13px;
}

.media-caption span {
  color: rgba(255, 255, 255, 0.78);
  font-size: 12px;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(2, 112px);
  gap: 10px;
}

.stat-item {
  padding: 16px 14px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid rgba(106, 44, 138, 0.14);
  box-shadow: 0 12px 24px rgba(71, 85, 105, 0.08);
}

.stat-item span {
  display: block;
  color: #172033;
  font-size: 24px;
  font-weight: 800;
}

.stat-item label {
  display: block;
  margin-top: 4px;
  color: #738295;
  font-size: 12px;
}

.post-list {
  display: grid;
  gap: 12px;
}

.empty,
.pagination {
  border: 1px solid rgba(106, 44, 138, 0.15);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(12px);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 14px;
}

@media (max-width: 860px) {
  .hero-panel {
    grid-template-columns: 1fr;
    padding: 24px;
  }

  .hero-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-media {
    min-height: 220px;
  }
}

@media (max-width: 560px) {
  .hero-stats {
    grid-template-columns: 1fr;
  }

  .hero-search {
    flex-direction: column;
  }
}
</style>
