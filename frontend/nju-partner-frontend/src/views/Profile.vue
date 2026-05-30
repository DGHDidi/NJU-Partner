<script setup>
import Navbar from '@/components/Navbar.vue'

// TODO: 我的资料、我的发布、我的报名、我的收藏
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main">
      <el-card class="section">
        <template #header>我的资料</template>
        <el-empty description="待接入个人信息接口" />
      </el-card>

      <el-tabs type="border-card">
        <el-tab-pane label="我的发布">
          <el-empty description="待接入 GET /api/user/my-posts" />
        </el-tab-pane>
        <el-tab-pane label="我的报名">
          <el-empty description="待接入 GET /api/user/my-applications" />
        </el-tab-pane>
        <el-tab-pane label="我的收藏">
          <el-empty description="待接入 GET /api/user/favorites" />
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
</style>
