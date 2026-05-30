<script setup>
import Navbar from '@/components/Navbar.vue'

// TODO: 管理员帖子/用户/评论管理，调用 api/admin.js
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main">
      <h2>后台管理</h2>

      <el-tabs type="border-card">
        <el-tab-pane label="帖子管理">
          <el-table :data="[]" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty description="待接入 GET /api/admin/posts" />
        </el-tab-pane>

        <el-tab-pane label="用户管理">
          <el-table :data="[]" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="danger" link>封禁</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty description="待接入 GET /api/admin/users" />
        </el-tab-pane>

        <el-tab-pane label="评论管理">
          <el-table :data="[]" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="content" label="内容" />
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty description="待接入评论管理" />
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
