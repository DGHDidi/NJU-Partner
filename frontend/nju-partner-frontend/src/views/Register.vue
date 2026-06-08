<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'
import { CAMPUS_OPTIONS, GRADE_OPTIONS } from '@/constants'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const isShaking = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  campus: '',
  grade: '',
  major: '',
})

const validateNoSpace = (_rule, value, callback) => {
  if (value && /\s/.test(value)) {
    callback(new Error('不能包含空格'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (_rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 10, message: '用户名长度为 2-10 个字符', trigger: 'blur' },
    { validator: validateNoSpace, trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度至少 6 位', trigger: 'blur' },
    { validator: validateNoSpace, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 10, message: '昵称不能超过 10 个字符', trigger: 'blur' },
    { validator: validateNoSpace, trigger: 'blur' },
  ],
  campus: [
    { required: true, message: '请选择校区', trigger: 'change' },
  ],
  grade: [
    { required: true, message: '请选择年级', trigger: 'change' },
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' },
    { max: 100, message: '专业不能超过 100 个字符', trigger: 'blur' },
  ],
}

function showValidationFeedback() {
  ElMessage.warning('请先修正表单中的红色提示项')
  isShaking.value = false
  requestAnimationFrame(() => {
    isShaking.value = true
    window.setTimeout(() => {
      isShaking.value = false
    }, 420)
  })

  window.setTimeout(() => {
    const firstError = document.querySelector('.auth-card .el-form-item.is-error input, .auth-card .el-form-item.is-error textarea')
    firstError?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    firstError?.focus()
  }, 80)
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    showValidationFeedback()
    return
  }

  loading.value = true
  try {
    await register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      campus: form.campus,
      grade: form.grade,
      major: form.major,
    })
    ElMessage.success('注册成功，请登录')
    router.push({ name: 'Login' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page page-auth">
    <el-card class="auth-card" :class="{ 'is-shaking': isShaking }">
      <template #header>
        <div class="register-header">
          <span class="register-mark">N</span>
          <div>
            <h2>创建账号</h2>
            <p class="subtitle">加入南大轻搭子，找到你的校园搭子</p>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        @submit.prevent="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="1-10 个字符，不能含空格" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="至少 6 位，不能含空格"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="不超过 10 个字符，不能含空格" clearable />
        </el-form-item>
        <el-form-item label="校区" prop="campus">
          <el-select v-model="form.campus" placeholder="请选择校区" style="width: 100%">
            <el-option
              v-for="item in CAMPUS_OPTIONS"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
            <el-option
              v-for="item in GRADE_OPTIONS"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" clearable />
        </el-form-item>
        <el-form-item>
          <el-button class="register-btn" type="primary" :loading="loading" @click="handleRegister">
            注册
          </el-button>
          <el-button link type="primary" @click="router.push({ name: 'Login' })">
            已有账号，去登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page-auth {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at 12% 18%, rgba(82, 99, 132, 0.16), transparent 28%),
    radial-gradient(circle at 88% 12%, rgba(116, 103, 137, 0.14), transparent 30%),
    linear-gradient(145deg, rgba(243, 246, 249, 0.98), rgba(247, 245, 248, 0.94));
}

.auth-card {
  width: min(560px, 100%);
  border-radius: 22px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.86));
  box-shadow:
    0 28px 64px rgba(42, 52, 66, 0.14),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.auth-card.is-shaking {
  animation: form-shake 0.42s ease;
}

.register-header {
  display: flex;
  align-items: center;
  gap: 14px;
}

.register-mark {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 800;
  background: linear-gradient(135deg, #44546a, #63714f);
  box-shadow: 0 12px 24px rgba(68, 84, 106, 0.22);
}

.auth-card h2 {
  margin: 0;
  color: #172033;
}

.subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: #738295;
}

.auth-card :deep(.el-form-item__label) {
  color: #526173;
  font-weight: 700;
}

.auth-card :deep(.el-input__wrapper),
.auth-card :deep(.el-select__wrapper) {
  min-height: 42px;
  border-radius: 12px;
}

.auth-card :deep(.el-input__wrapper.is-focus),
.auth-card :deep(.el-select__wrapper.is-focused) {
  box-shadow:
    0 0 0 1px #44546a inset,
    0 0 0 3px rgba(68, 84, 106, 0.1);
}

.register-btn {
  min-width: 112px;
  border-radius: 14px;
  box-shadow: 0 12px 24px rgba(68, 84, 106, 0.22);
}

@keyframes form-shake {
  0%,
  100% {
    transform: translateX(0);
  }

  20%,
  60% {
    transform: translateX(-8px);
  }

  40%,
  80% {
    transform: translateX(8px);
  }
}
</style>
