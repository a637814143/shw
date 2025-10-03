<template>
  <div class="auth-layout">
    <section class="auth-panel">
      <h1 class="auth-title">家政服务平台登录</h1>
      <p class="auth-subtitle">请使用注册账号登录系统</p>

      <form class="auth-form" @submit.prevent="handleLogin">
        <label class="form-field">
          <span>账号</span>
          <input v-model.trim="form.username" type="text" placeholder="请输入账号" required />
        </label>

        <label class="form-field">
          <span>密码</span>
          <input v-model="form.password" type="password" placeholder="请输入密码" required />
        </label>

        <label class="form-field">
          <span>角色</span>
          <select v-model="form.role">
            <option value="user">普通用户</option>
            <option value="provider">家政服务人员</option>
            <option value="admin">管理员</option>
          </select>
        </label>

        <p v-if="errorMessage" class="form-error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="form-success">{{ successMessage }}</p>

        <button class="auth-button" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? '登录中...' : '登录' }}
        </button>
      </form>

      <p class="auth-extra">
        还没有账号？
        <router-link to="/register">立即注册</router-link>
      </p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI, setToken, setUserType, setSessionProfile } from '@/utils/api'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  role: 'user'
})

const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const handleLogin = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  if (!form.username || !form.password) {
    errorMessage.value = '请输入账号和密码'
    return
  }

  isSubmitting.value = true
  try {
    const response = await authAPI.login(form.role as 'user' | 'provider' | 'admin', form.username, form.password)
    if (!response || !response.data) {
      throw new Error('登录响应异常')
    }

    const { token, expiresAt, userType, profile } = response.data
    if (!token || !expiresAt || !userType) {
      throw new Error('登录信息不完整')
    }

    setToken(token, expiresAt)
    setUserType(userType)
    setSessionProfile(profile)

    successMessage.value = '登录成功，正在跳转...'

    setTimeout(() => {
      if (userType === 'ADMIN') {
        router.replace('/admin-home')
      } else if (userType === 'PROVIDER') {
        router.replace('/provider-home')
      } else {
        router.replace('/user-home')
      }
    }, 300)
  } catch (error: any) {
    console.error('登录失败:', error)
    errorMessage.value = error?.message || '登录失败，请稍后重试'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 24px;
}

.auth-panel {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  padding: 40px 36px;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.15);
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.auth-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 8px;
  text-align: center;
}

.auth-subtitle {
  color: #475569;
  text-align: center;
  margin-bottom: 32px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field span {
  font-size: 14px;
  color: #1e293b;
}

.form-field input,
.form-field select {
  height: 44px;
  padding: 0 12px;
  border: 1px solid #cbd5f5;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-field input:focus,
.form-field select:focus {
  border-color: #2563eb;
  outline: none;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
}

.auth-button {
  height: 46px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.auth-button:hover {
  box-shadow: 0 12px 20px rgba(37, 99, 235, 0.25);
  transform: translateY(-1px);
}

.auth-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.form-error {
  color: #dc2626;
  font-size: 14px;
  margin-top: -8px;
}

.form-success {
  color: #16a34a;
  font-size: 14px;
  margin-top: -8px;
}

.auth-extra {
  margin-top: 24px;
  text-align: center;
  color: #475569;
}

a {
  color: #2563eb;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>
