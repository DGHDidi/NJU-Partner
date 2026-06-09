<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const showImageFallback = ref(false)
const illustrationSrc = ref('/images/login-illustration.png')

function onIllustrationError() {
  if (illustrationSrc.value.endsWith('.png')) {
    illustrationSrc.value = '/images/login-illustration.svg'
    return
  }
  showImageFallback.value = true
}

const form = reactive({
  username: '',
  password: '',
})

const validateNoSpace = (_rule, value, callback) => {
  if (value && /\s/.test(value)) {
    callback(new Error('不能包含空格'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 10, message: '用户名长度为 1-10 个字符', trigger: 'blur' },
    { validator: validateNoSpace, trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度至少 6 位', trigger: 'blur' },
    { validator: validateNoSpace, trigger: 'blur' },
  ],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const data = await login({
      username: form.username,
      password: form.password,
    })
    userStore.setLogin(data)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect
    router.push(typeof redirect === 'string' && redirect ? redirect : { name: 'Home' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-bg">
      <span class="blob blob-1" />
      <span class="blob blob-2" />
      <span class="blob blob-3" />
    </div>

    <div class="login-shell">
      <aside class="login-visual">
        <img
          v-show="!showImageFallback"
          :src="illustrationSrc"
          alt="南大轻搭子"
          class="login-image"
          @error="onIllustrationError"
        />

        <div v-show="showImageFallback" class="visual-fallback" aria-hidden="true">
          <div class="fallback-icon">
            <svg viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="60" cy="60" r="52" fill="rgba(255,255,255,0.12)" />
              <path
                d="M38 78c0-12 10-22 22-22s22 10 22 22"
                stroke="rgba(255,255,255,0.9)"
                stroke-width="4"
                stroke-linecap="round"
              />
              <circle cx="60" cy="46" r="12" fill="rgba(255,255,255,0.9)" />
              <path
                d="M28 38h64M34 28h52"
                stroke="rgba(255,255,255,0.35)"
                stroke-width="3"
                stroke-linecap="round"
              />
            </svg>
          </div>
        </div>

        <div class="visual-copy">
          <p class="visual-tag">NJU Partner</p>
          <h1>南大轻搭子</h1>
          <p class="visual-desc">找自习搭子、运动搭子、竞赛队友、讲座同行</p>
        </div>
      </aside>

      <section class="login-panel">
        <div class="panel-inner">
          <div class="panel-header">
            <h2>欢迎回来</h2>
            <p class="subtitle">登录你的账号，继续寻找校园搭子</p>
          </div>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
            class="login-form"
            @submit.prevent="handleLogin"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="form.username"
                size="large"
                clearable
              />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </el-button>

            <p class="register-tip">
              还没有账号？
              <el-button link type="primary" @click="router.push({ name: 'Register' })">
                立即注册
              </el-button>
            </p>
          </el-form>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  overflow: hidden;
  background:
    radial-gradient(circle at 12% 16%, rgba(82, 99, 132, 0.18), transparent 30%),
    radial-gradient(circle at 86% 14%, rgba(111, 101, 128, 0.16), transparent 32%),
    linear-gradient(145deg, #f2f5f8 0%, #f7f5f7 48%, #eef5f2 100%);
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.55;
}

.blob-1 {
  width: 320px;
  height: 320px;
  top: -80px;
  left: -60px;
  background: #596a83;
}

.blob-2 {
  width: 280px;
  height: 280px;
  right: -40px;
  bottom: 10%;
  background: #74677d;
}

.blob-3 {
  width: 200px;
  height: 200px;
  left: 35%;
  bottom: -60px;
  background: #7b9189;
}

.login-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  width: min(920px, 100%);
  min-height: 520px;
  border-radius: 28px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.75);
  box-shadow:
    0 30px 70px rgba(42, 52, 66, 0.16),
    0 12px 28px rgba(86, 75, 99, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.95);
}

.login-visual {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 36px 32px;
  background:
    linear-gradient(180deg, rgba(23, 32, 51, 0.18), rgba(23, 32, 51, 0.7)),
    url('https://picsum.photos/seed/nju-evening-campus/900/1200') center / cover;
  overflow: hidden;
}

.login-visual::before,
.login-visual::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(6px);
}

.login-visual::before {
  width: 220px;
  height: 220px;
  top: -70px;
  right: -50px;
}

.login-visual::after {
  width: 140px;
  height: 140px;
  bottom: 120px;
  left: -40px;
}

.login-image {
  position: absolute;
  top: 50%;
  left: 50%;
  width: min(72%, 280px);
  transform: translate(-50%, -58%);
  object-fit: contain;
  filter: saturate(0.75) contrast(1.05) drop-shadow(0 18px 28px rgba(0, 0, 0, 0.16));
  pointer-events: none;
}

.visual-fallback {
  position: absolute;
  top: 42%;
  left: 50%;
  transform: translate(-50%, -60%);
}

.fallback-icon {
  width: 160px;
  height: 160px;
}

.fallback-icon svg {
  width: 100%;
  height: 100%;
}

.visual-copy {
  position: relative;
  z-index: 1;
  color: #fff;
}

.visual-tag {
  display: inline-block;
  margin: 0 0 10px;
  padding: 4px 12px;
  font-size: 12px;
  letter-spacing: 0.06em;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.26);
  backdrop-filter: blur(10px);
}

.visual-copy h1 {
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.visual-desc {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.88);
}

.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 36px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94) 0%, rgba(248, 250, 252, 0.9) 100%);
}

.panel-inner {
  width: 100%;
  max-width: 360px;
}

.panel-header {
  margin-bottom: 28px;
}

.panel-header h2 {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  color: #1f2d3d;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #606266;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 14px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #b8c7da inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow:
    0 0 0 1px #44546a inset,
    0 0 0 3px rgba(68, 84, 106, 0.12);
}

.login-btn {
  width: 100%;
  margin-top: 8px;
  border: none;
  border-radius: 14px;
  font-weight: 600;
  letter-spacing: 0.04em;
  box-shadow:
    0 12px 24px rgba(68, 84, 106, 0.24),
    inset 0 1px 0 rgba(255, 255, 255, 0.25);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow:
    0 16px 30px rgba(68, 84, 106, 0.28),
    inset 0 1px 0 rgba(255, 255, 255, 0.25);
}

.register-tip {
  margin: 18px 0 0;
  text-align: center;
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .login-shell {
    grid-template-columns: 1fr;
    min-height: auto;
    border-radius: 24px;
  }

  .login-visual {
    min-height: 220px;
    padding: 28px 24px;
  }

  .login-image,
  .visual-fallback {
    display: none;
  }

  .visual-copy h1 {
    font-size: 24px;
  }

  .login-panel {
    padding: 28px 24px 32px;
  }
}
</style>
