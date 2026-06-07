<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createPost, getPostDetail, updatePost } from '@/api/post'
import { CAMPUS_OPTIONS, GRADE_OPTIONS, POST_TYPE_OPTIONS } from '@/constants'
import { formatTypeLabel } from '@/utils/typeDisplay'
import Navbar from '@/components/Navbar.vue'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const pageLoading = ref(false)
const activeStep = ref(0)
const isEdit = computed(() => route.name === 'PostEdit')
const postId = computed(() => Number(route.params.id))

const form = reactive({
  title: '',
  type: '',
  description: '',
  location: '',
  activityTime: '',
  needCount: 1,
  campus: '',
  gradeLimit: [],
  majorLimit: '',
  contact: '',
})

const validateNotBlank = (message) => (_rule, value, callback) => {
  if (typeof value !== 'string' || value.trim().length === 0) {
    callback(new Error(message))
    return
  }
  callback()
}

const rules = {
  title: [{ required: true, validator: validateNotBlank('标题不能为空'), trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }],
  activityTime: [
    { required: true, message: '请选择活动时间', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (!value || new Date(value).getTime() > Date.now()) {
          callback()
          return
        }
        callback(new Error('活动时间必须晚于当前时间'))
      },
      trigger: 'change',
    },
  ],
  needCount: [{ required: true, message: '请输入人数', trigger: 'change' }],
  campus: [{ required: true, message: '请选择校区', trigger: 'change' }],
  contact: [{ required: true, validator: validateNotBlank('联系方式不能为空'), trigger: 'blur' }],
}

const pickerOptions = {
  disabledDate(date) {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    return date.getTime() < today.getTime()
  },
}

const stepFields = [
  ['title', 'type', 'description'],
  ['campus', 'activityTime', 'needCount'],
  ['contact'],
]

async function validateCurrentStep() {
  const fields = stepFields[activeStep.value]
  if (!fields.length) return true
  return formRef.value?.validateField(fields).then(() => true).catch(() => false)
}

async function handleNextStep() {
  const valid = await validateCurrentStep()
  if (!valid) return
  activeStep.value = Math.min(activeStep.value + 1, 2)
}

function handlePrevStep() {
  activeStep.value = Math.max(activeStep.value - 1, 0)
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const payload = {
      ...form,
      title: form.title.trim(),
      contact: form.contact.trim(),
      activityTime: form.activityTime || null,
      gradeLimit: form.gradeLimit.length ? form.gradeLimit.join(',') : '',
    }
    if (isEdit.value) {
      await updatePost(postId.value, payload)
      ElMessage.success('修改成功')
      router.push({ name: 'PostDetail', params: { id: postId.value } })
    } else {
      await createPost(payload)
      ElMessage.success('发布成功')
      router.push({ name: 'Home' })
    }
  } finally {
    loading.value = false
  }
}

async function loadPostForEdit() {
  if (!isEdit.value || !postId.value) return
  pageLoading.value = true
  try {
    const post = await getPostDetail(postId.value)
    Object.assign(form, {
      title: post.title || '',
      type: post.type || '',
      description: post.description || '',
      location: post.location || '',
      activityTime: post.activityTime || '',
      needCount: post.needCount || 1,
      campus: post.campus || '',
      gradeLimit: parseGradeLimit(post.gradeLimit),
      majorLimit: post.majorLimit || '',
      contact: post.contact || '',
    })
  } finally {
    pageLoading.value = false
  }
}

function parseGradeLimit(value) {
  if (!value || value === '不限') {
    return []
  }
  return value.split(',').map((item) => item.trim()).filter(Boolean)
}

function toggleGrade(grade) {
  const index = form.gradeLimit.indexOf(grade)
  if (index >= 0) {
    form.gradeLimit.splice(index, 1)
  } else {
    form.gradeLimit.push(grade)
  }
}

function clearGradeLimit() {
  form.gradeLimit = []
}

onMounted(loadPostForEdit)
</script>

<template>
  <div class="page">
    <Navbar />

    <main class="page-main" v-loading="pageLoading">
      <el-card>
        <template #header>
          <div class="form-header">
            <h2>{{ isEdit ? '编辑组队帖' : '发布组队帖' }}</h2>
            <p>{{ isEdit ? '更新活动信息和报名要求' : '填写活动信息，找到合适的校园搭子' }}</p>
          </div>
        </template>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-steps class="post-steps" :active="activeStep" finish-status="success" align-center>
            <el-step title="基本信息" />
            <el-step title="活动详情" />
            <el-step title="报名要求" />
          </el-steps>

          <div v-show="activeStep === 0" class="form-step">
            <div class="step-title"><span>1</span>基本信息</div>
            <el-form-item label="标题" prop="title" required>
              <el-input v-model="form.title" placeholder="请输入标题" />
            </el-form-item>
            <el-form-item label="活动类型" prop="type" required>
              <div class="type-cards">
                <button
                  v-for="item in POST_TYPE_OPTIONS"
                  :key="item"
                  type="button"
                  class="type-card"
                  :class="{ active: form.type === item }"
                  @click="form.type = item"
                >
                  {{ formatTypeLabel(item) }}
                </button>
              </div>
            </el-form-item>
            <el-form-item label="活动描述" prop="description" required>
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述活动内容" />
            </el-form-item>
          </div>

          <div v-show="activeStep === 1" class="form-step">
            <div class="step-title"><span>2</span>活动详情</div>
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
                :disabled-date="pickerOptions.disabledDate"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="需要人数" prop="needCount" required>
              <div class="need-count-field">
                <el-input-number v-model="form.needCount" :min="1" />
                <span class="field-tip">除你自己以外还需要的人数，页面会显示为总人数 {{ form.needCount + 1 }} 人</span>
              </div>
            </el-form-item>
          </div>

          <div v-show="activeStep === 2" class="form-step">
            <div class="step-title"><span>3</span>报名要求与联系</div>
            <el-form-item label="年级限制">
              <div class="grade-tags">
                <el-check-tag :checked="form.gradeLimit.length === 0" @change="clearGradeLimit">
                  不限
                </el-check-tag>
                <el-check-tag
                  v-for="item in GRADE_OPTIONS"
                  :key="item"
                  :checked="form.gradeLimit.includes(item)"
                  @change="toggleGrade(item)"
                >
                  {{ item }}
                </el-check-tag>
              </div>
            </el-form-item>
            <el-form-item label="专业限制">
              <el-input v-model="form.majorLimit" placeholder="不限" />
            </el-form-item>
            <el-form-item label="联系方式" prop="contact" required>
              <el-input v-model="form.contact" placeholder="微信/QQ 等" />
            </el-form-item>
          </div>

          <el-form-item class="submit-row">
            <el-button v-if="activeStep > 0" class="cancel-btn" @click="handlePrevStep">上一步</el-button>
            <el-button v-if="activeStep < 2" class="submit-btn" type="primary" @click="handleNextStep">下一步</el-button>
            <el-button v-else class="submit-btn" type="primary" :loading="loading" @click="handleSubmit">
              {{ isEdit ? '保存修改' : '发布组队' }}
            </el-button>
            <el-button class="cancel-btn" @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </main>
  </div>
</template>

<style scoped>
.page-main {
  max-width: 820px;
  margin: 0 auto;
  padding: 28px 20px 44px;
}

.page-main :deep(.el-card) {
  border-radius: 24px;
  border-color: rgba(106, 44, 138, 0.15);
}

h2 {
  margin: 0;
}

.form-header h2 {
  margin: 0 0 6px;
  color: #172033;
}

.form-header p {
  margin: 0;
  color: #738295;
  font-size: 13px;
}

.page-main :deep(.el-form-item) {
  margin-bottom: 20px;
}

.page-main :deep(.el-form-item__label) {
  color: #526173;
  font-weight: 700;
}

.post-steps {
  margin-bottom: 22px;
  padding: 16px;
  border: 1px solid rgba(106, 44, 138, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.58);
}

.form-step {
  margin-bottom: 18px;
  padding: 18px 18px 2px;
  border: 1px solid rgba(106, 44, 138, 0.13);
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.82), rgba(247, 243, 255, 0.64));
}

.step-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  color: #1f2d3d;
  font-size: 16px;
  font-weight: 800;
}

.step-title span {
  width: 26px;
  height: 26px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #4f647f, #7a6b90);
  color: #fff;
  font-size: 13px;
}

.type-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(104px, 1fr));
  gap: 10px;
  width: 100%;
}

.type-card {
  min-height: 42px;
  border: 1px solid rgba(106, 44, 138, 0.14);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.72);
  color: #526173;
  cursor: pointer;
  font-weight: 700;
  transition: transform 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
}

.type-card:hover,
.type-card.active {
  transform: translateY(-1px);
  border-color: rgba(106, 44, 138, 0.3);
  color: #4f647f;
  box-shadow: 0 10px 22px rgba(71, 85, 105, 0.12);
}

.grade-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.need-count-field {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.field-tip {
  color: #909399;
  font-size: 12px;
}

.submit-row {
  padding-top: 4px;
}

.submit-btn {
  min-width: 128px;
  box-shadow: 0 12px 24px rgba(71, 85, 105, 0.18);
}

.cancel-btn {
  min-width: 88px;
  background: rgba(255, 255, 255, 0.28);
  border-color: rgba(123, 137, 156, 0.24);
  color: #526173;
  box-shadow: none;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.5);
  border-color: rgba(79, 100, 127, 0.3);
}
</style>
