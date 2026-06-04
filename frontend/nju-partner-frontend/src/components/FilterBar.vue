<script setup>
import { reactive } from 'vue'
import { CAMPUS_OPTIONS, GRADE_OPTIONS, POST_TYPE_OPTIONS, POST_STATUS_MAP } from '@/constants'

const emit = defineEmits(['search'])

const filters = reactive({
  keyword: '',
  campus: '',
  type: '',
  status: '',
  grade: '',
  timeRange: [],
})

const statusOptions = Object.entries(POST_STATUS_MAP).map(([value, label]) => ({
  value: Number(value),
  label,
}))

function handleSearch() {
  emit('search', { ...filters })
}

function handleReset() {
  filters.keyword = ''
  filters.campus = ''
  filters.type = ''
  filters.status = ''
  filters.grade = ''
  filters.timeRange = []
  emit('search', { ...filters })
}
</script>

<template>
  <el-card class="filter-bar" shadow="never">
    <el-form class="filter-form" :inline="true" @submit.prevent="handleSearch">
      <el-form-item label="关键词">
        <el-input v-model="filters.keyword" class="keyword-input" placeholder="搜索标题或描述" clearable />
      </el-form-item>

      <el-form-item label="校区">
        <el-select v-model="filters.campus" class="select-campus" placeholder="全部校区" clearable>
          <el-option
            v-for="item in CAMPUS_OPTIONS"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="类型">
        <el-select v-model="filters.type" class="select-type" placeholder="全部类型" clearable>
          <el-option
            v-for="item in POST_TYPE_OPTIONS"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="状态">
        <el-select v-model="filters.status" class="select-status" placeholder="全部状态" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="年级">
        <el-select v-model="filters.grade" class="select-grade" placeholder="全部年级" clearable>
          <el-option
            v-for="item in GRADE_OPTIONS"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="时间">
        <el-date-picker
          v-model="filters.timeRange"
          class="time-picker"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
        />
      </el-form-item>

      <el-form-item class="filter-actions">
        <el-button type="primary" @click="handleSearch">筛选</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style scoped>
.filter-bar {
  margin-bottom: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
  align-items: flex-start;
}

.filter-form :deep(.el-form-item) {
  margin-right: 0;
  margin-bottom: 0;
}

.keyword-input {
  width: 330px;
}

.select-campus,
.select-type {
  width: 160px;
}

.select-status,
.select-grade {
  width: 130px;
}

.time-picker {
  width: 420px;
}

.filter-actions {
  white-space: nowrap;
}

@media (max-width: 768px) {
  .filter-form :deep(.el-form-item) {
    width: 100%;
  }

  .keyword-input,
  .select-campus,
  .select-type,
  .select-status,
  .select-grade,
  .time-picker {
    width: 100%;
  }
}
</style>
