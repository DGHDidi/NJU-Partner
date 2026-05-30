<script setup>
import Navbar from '@/components/Navbar.vue'

// TODO: 实现发布组队帖，调用 api/post.js createPost
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main">
      <el-card>
        <template #header>
          <h2>发布组队帖</h2>
        </template>

        <el-form label-width="100px">
          <el-form-item label="标题" required>
            <el-input placeholder="请输入标题" />
          </el-form-item>
          <el-form-item label="活动类型" required>
            <el-select placeholder="请选择类型" style="width: 100%" />
          </el-form-item>
          <el-form-item label="活动校区" required>
            <el-select placeholder="请选择校区" style="width: 100%" />
          </el-form-item>
          <el-form-item label="具体地点">
            <el-input placeholder="请输入地点" />
          </el-form-item>
          <el-form-item label="活动时间" required>
            <el-date-picker type="datetime" placeholder="请选择时间" style="width: 100%" />
          </el-form-item>
          <el-form-item label="需要人数" required>
            <el-input-number :min="1" />
          </el-form-item>
          <el-form-item label="年级限制">
            <el-input placeholder="不限" />
          </el-form-item>
          <el-form-item label="专业限制">
            <el-input placeholder="不限" />
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input placeholder="微信/QQ 等" />
          </el-form-item>
          <el-form-item label="活动描述" required>
            <el-input type="textarea" :rows="4" placeholder="请描述活动内容" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary">发布</el-button>
            <el-button @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </main>
  </div>
</template>

<style scoped>
.page-main {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px 20px 40px;
}

h2 {
  margin: 0;
}
</style>
