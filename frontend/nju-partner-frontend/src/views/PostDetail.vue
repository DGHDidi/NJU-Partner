<script setup>
import { useRoute } from 'vue-router'
import Navbar from '@/components/Navbar.vue'

const route = useRoute()
const postId = route.params.id

// TODO: 调用 api/post.js getPostDetail 加载详情
// TODO: 报名、评论、收藏、报名管理区域
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main">
      <el-card class="section">
        <template #header>
          <h2>帖子详情 #{{ postId }}</h2>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="标题">—</el-descriptions-item>
          <el-descriptions-item label="发布者">—</el-descriptions-item>
          <el-descriptions-item label="活动类型">—</el-descriptions-item>
          <el-descriptions-item label="活动校区">—</el-descriptions-item>
          <el-descriptions-item label="具体地点">—</el-descriptions-item>
          <el-descriptions-item label="活动时间">—</el-descriptions-item>
          <el-descriptions-item label="人数">0 / 0</el-descriptions-item>
          <el-descriptions-item label="联系方式">—</el-descriptions-item>
          <el-descriptions-item label="活动描述">—</el-descriptions-item>
        </el-descriptions>

        <div class="actions">
          <el-button type="primary">报名</el-button>
          <el-button>收藏</el-button>
          <el-button type="danger" plain>关闭招募</el-button>
        </div>
      </el-card>

      <el-card class="section">
        <template #header>评论列表</template>
        <el-empty description="待接入评论接口" />
      </el-card>

      <el-card class="section">
        <template #header>报名列表（发布者可见）</template>
        <el-empty description="待接入报名管理接口" />
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
}

h2 {
  margin: 0;
}
</style>
