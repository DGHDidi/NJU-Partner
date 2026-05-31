<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createPost } from '@/api/post'
import { CAMPUS_OPTIONS, POST_TYPE_OPTIONS } from '@/constants'
import Navbar from '@/components/Navbar.vue'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  title: '',
  type: '',
  description: '',
  location: '',
  activityTime: '',
  needCount: 1,
  campus: '',
  gradeLimit: '',
  majorLimit: '',
  contact: '',
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }],
  activityTime: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
  needCount: [{ required: true, message: '请输入人数', trigger: 'change' }],
  campus: [{ required: true, message: '请选择校区', trigger: 'change' }],
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await createPost({ ...form, activityTime: form.activityTime || null })
    ElMessage.success('发布成功')
    router.push({ name: 'Home' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main">
      <el-card>
        <template #header>
          <h2>发布组队帖</h2>
        </template>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="标题" prop="title" required>
            <el-input v-model="form.title" placeholder="请输入标题" />
          </el-form-item>
          <el-form-item label="活动类型" prop="type" required>
            <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
              <el-option v-for="item in POST_TYPE_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="活动校区" prop="campus" required>
            <el-select v-model="form.campus" placeholder="请选择校区" style="width: 100%">
              <el-option v-for="item in CAMPUS_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="具体地点">
            <el-input v-model="form.location" placeholder="请输入地点" />
          </el-form-item>
          <el-form-item label="活动时间" prop="activityTime" required>
            <el-date-picker
              v-model="form.activityTime"
              type="datetime"
              value-format="YYYY-MM-DDTHH:mm:ss"
              placeholder="请选择时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="需要人数" prop="needCount" required>
            <el-input-number v-model="form.needCount" :min="1" />
          </el-form-item>
          <el-form-item label="年级限制">
            <el-input v-model="form.gradeLimit" placeholder="不限" />
          </el-form-item>
          <el-form-item label="专业限制">
            <el-input v-model="form.majorLimit" placeholder="不限" />
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="form.contact" placeholder="微信/QQ 等" />
          </el-form-item>
          <el-form-item label="活动描述" prop="description" required>
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述活动内容" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleSubmit">发布</el-button>
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
