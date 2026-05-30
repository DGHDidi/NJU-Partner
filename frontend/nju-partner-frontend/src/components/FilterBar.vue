<script setup>
import { reactive } from 'vue'
import { CAMPUS_OPTIONS, POST_TYPE_OPTIONS, POST_STATUS_MAP } from '@/constants'

const emit = defineEmits(['search'])

const filters = reactive({
  keyword: '',
  campus: '',
  type: '',
  status: '',
  grade: '',
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
  emit('search', { ...filters })
}
</script>

<template>
  <el-card class="filter-bar" shadow="never">
    <el-form :inline="true" @submit.prevent="handleSearch">
      <el-form-item label="关键词">
        <el-input v-model="filters.keyword" placeholder="搜索标题或描述" clearable />
      </el-form-item>
      <el-form-item label="校区">
        <el-select v-model="filters.campus" placeholder="全部校区" clearable>
          <el-option
            v-for="item in CAMPUS_OPTIONS"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="filters.type" placeholder="全部类型" clearable>
          <el-option
            v-for="item in POST_TYPE_OPTIONS"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" placeholder="全部状态" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
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
</style>
