<script setup>
import { reactive } from 'vue'
import { CAMPUS_OPTIONS, GRADE_OPTIONS, POST_TYPE_OPTIONS, POST_STATUS_MAP } from '@/constants'
import { formatTypeLabel } from '@/utils/typeDisplay'

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
    <div class="filter-heading">
      <strong>筛选组队</strong>
      <span>按校区、类型、年级和时间快速定位</span>
    </div>
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
            :label="formatTypeLabel(item)"
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
  margin-bottom: 18px;
  border-radius: 20px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.94), rgba(248, 247, 250, 0.82));
  border-color: rgba(106, 44, 138, 0.15);
  box-shadow: 0 16px 34px rgba(71, 85, 105, 0.07);
}

.filter-heading {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 16px;
}

.filter-heading strong {
  color: #172033;
  font-size: 17px;
  letter-spacing: 0.01em;
}

.filter-heading span {
  color: #8793a3;
  font-size: 13px;
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

.filter-actions :deep(.el-button) {
  border-radius: 14px;
}

.filter-actions :deep(.el-button--primary) {
  --el-button-bg-color: #4f647f;
  --el-button-border-color: #4f647f;
  --el-button-hover-bg-color: #5f526e;
  --el-button-hover-border-color: #5f526e;
  box-shadow: 0 12px 22px rgba(71, 85, 105, 0.14);
}

.filter-form :deep(.el-form-item__label) {
  color: #5f6f84;
  font-weight: 700;
}

.filter-form :deep(.el-input__wrapper),
.filter-form :deep(.el-select__wrapper) {
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.72);
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
