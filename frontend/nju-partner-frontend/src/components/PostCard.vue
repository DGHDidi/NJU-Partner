<script setup>
import { computed } from 'vue'
import { POST_STATUS_MAP } from '@/constants'
import { formatDateTime, formatRelativeTime } from '@/utils/date'

const props = defineProps({
  post: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['click'])
const totalCount = computed(() => (props.post.needCount ?? 0) + 1)
const currentCount = computed(() => props.post.currentCount ?? 0)
const progress = computed(() => {
  if (!totalCount.value) return 0
  return Math.min(100, Math.round((currentCount.value / totalCount.value) * 100))
})
const typeIconMap = {
  自习搭子: '📚',
  运动搭子: '🏃',
  竞赛组队: '🏆',
  课程学习: '📝',
  拼单: '🛍️',
  饭搭子: '🍜',
  短途出行: '🚌',
  讲座同行: '🎤',
  社团活动: '🎯',
  跨校区同行: '🚇',
  其他: '✨',
}
const typeIcon = computed(() => typeIconMap[props.post.type] || '✨')
const publisherName = computed(() => props.post.publisher?.nickname || props.post.publisher?.username || '-')
const avatarText = computed(() => (publisherName.value === '-' ? 'N' : publisherName.value.slice(0, 1).toUpperCase()))
const statusType = computed(() => {
  if (props.post.status === 0) return 'success'
  if (props.post.status === 1) return 'warning'
  return 'info'
})
const relativePublishTime = computed(() => formatRelativeTime(props.post.createdTime || props.post.createTime || props.post.updatedTime))

function handleClick() {
  emit('click')
}
</script>

<template>
  <el-card class="post-card" shadow="hover" @click="handleClick">
    <div class="post-header">
      <div class="post-main-title">
        <span class="type-icon">{{ typeIcon }}</span>
        <div>
          <h3 class="post-title">{{ post.title || '帖子标题占位' }}</h3>
          <div class="publisher-line">
            <el-popover placement="top" trigger="hover" width="220" popper-class="profile-popover">
              <template #reference>
                <span class="avatar">{{ avatarText }}</span>
              </template>
              <div class="profile-popover-content">
                <strong>{{ publisherName }}</strong>
                <span>年级：{{ post.publisher?.grade || '-' }}</span>
                <span>校区：{{ post.publisher?.campus || '-' }}</span>
                <span>专业：{{ post.publisher?.major || '-' }}</span>
              </div>
            </el-popover>
            <span>{{ publisherName }}</span>
            <span class="dot">·</span>
            <span>{{ relativePublishTime }}</span>
          </div>
          <div class="post-badges">
            <el-tag size="small" effect="plain">{{ post.type || '-' }}</el-tag>
            <el-tag size="small" type="info" effect="plain">{{ post.campus || '-' }}</el-tag>
            <el-tag size="small" :type="statusType">
              {{ POST_STATUS_MAP[post.status] ?? '招募中' }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="post-meta">
        <span class="meta-pill">
          <b>地点</b>
          {{ post.location || '-' }}
        </span>
        <span class="meta-pill">
          <b>时间</b>
          {{ formatDateTime(post.activityTime) }}
        </span>
      </div>
      <div class="count-box">
        <strong>{{ currentCount }} / {{ totalCount }}</strong>
        <span>当前人数</span>
      </div>
    </div>

    <p class="post-desc">{{ post.description || '帖子描述占位' }}</p>

    <el-progress :percentage="progress" :show-text="false" class="count-progress" />

    <div class="post-footer">
      <span>还需 {{ Math.max(totalCount - currentCount, 0) }} 人</span>
    </div>
  </el-card>
</template>

<style scoped>
.post-card {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  border-radius: 20px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.94), rgba(247, 243, 255, 0.82)),
    #fff;
  animation: card-in 0.42s ease both;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.post-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 5px;
  background: linear-gradient(180deg, #409eff, #8b7cf6);
  opacity: 0.82;
}

.post-card:hover {
  transform: translateY(-3px) scale(1.006);
  border-color: rgba(106, 44, 138, 0.3);
  box-shadow:
    0 24px 46px rgba(64, 158, 255, 0.16),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.post-main-title {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  min-width: 0;
  flex: 1;
  max-width: 48%;
}

.type-icon {
  width: 42px;
  height: 42px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.16), rgba(139, 124, 246, 0.18));
  border: 1px solid rgba(106, 44, 138, 0.16);
  font-size: 20px;
}

.post-title {
  margin: 0;
  font-size: 18px;
  color: #172033;
}

.publisher-line {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  color: #7a8495;
  font-size: 12px;
}

.avatar {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: inline-grid;
  place-items: center;
  background: linear-gradient(135deg, #409eff, #8b7cf6);
  color: #fff;
  font-size: 12px;
  font-weight: 800;
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

.dot {
  color: #b0bac8;
}

.post-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-top: 12px;
}

.count-box {
  width: fit-content;
  padding: 9px 11px;
  border-radius: 16px;
  text-align: center;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(241, 246, 250, 0.92));
  border: 1px solid rgba(106, 44, 138, 0.14);
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.count-box strong {
  display: block;
  color: #1f2d3d;
  font-size: 17px;
  white-space: nowrap;
}

.count-box span {
  display: block;
  margin-top: 2px;
  color: #8793a3;
  font-size: 12px;
  white-space: nowrap;
}

.post-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #66778d;
}

.post-meta {
  flex: 0 1 360px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  align-self: center;
  font-size: 13px;
  color: #66778d;
}

.meta-pill {
  min-height: 38px;
  padding: 8px 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.58);
  border: 1px solid rgba(106, 44, 138, 0.12);
  color: #5f6f84;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.meta-pill b {
  color: #6a5f78;
  font-weight: 800;
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.post-desc {
  margin: 10px 0;
  color: #526173;
  font-size: 14px;
  line-height: 1.5;
}

.count-progress {
  margin: 12px 0;
}

.count-progress :deep(.el-progress-bar__outer) {
  height: 6px !important;
  background: #edf2f7;
}

@media (max-width: 860px) {
  .post-header {
    flex-wrap: wrap;
  }

  .post-main-title {
    max-width: none;
  }

  .post-meta {
    order: 3;
    flex: 1 1 100%;
  }
}
</style>
