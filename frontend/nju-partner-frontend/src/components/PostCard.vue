<script setup>
import { POST_STATUS_MAP } from '@/constants'

defineProps({
  post: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['click'])

function handleClick() {
  emit('click')
}
</script>

<template>
  <el-card class="post-card" shadow="hover" @click="handleClick">
    <div class="post-header">
      <h3 class="post-title">{{ post.title || '帖子标题占位' }}</h3>
      <el-tag size="small">{{ POST_STATUS_MAP[post.status] ?? '招募中' }}</el-tag>
    </div>

    <div class="post-meta">
      <span>类型：{{ post.type || '-' }}</span>
      <span>校区：{{ post.campus || '-' }}</span>
      <span>地点：{{ post.location || '-' }}</span>
      <span>时间：{{ post.activityTime || '-' }}</span>
    </div>

    <p class="post-desc">{{ post.description || '帖子描述占位' }}</p>

    <div class="post-footer">
      <span>人数：{{ post.currentCount ?? 0 }} / {{ post.needCount ?? 0 }}</span>
      <span>发布者：{{ post.publisher?.nickname || post.publisher?.username || '-' }}</span>
    </div>
  </el-card>
</template>

<style scoped>
.post-card {
  cursor: pointer;
  margin-bottom: 12px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.post-title {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.post-meta,
.post-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #606266;
}

.post-desc {
  margin: 10px 0;
  color: #909399;
  font-size: 14px;
  line-height: 1.5;
}
</style>
