<template>
  <div class="auth-card">
    <h2>欢迎登录</h2>
    <p class="subtitle">请输入账号与密码以进入系统</p>

    <form class="auth-form" @submit.prevent="submit">
      <label>
        账号
        <input v-model.trim="form.username" type="text" autocomplete="username" required />
      </label>
      <label>
        密码
        <input v-model="form.password" type="password" autocomplete="current-password" required />
      </label>
      <button type="submit" :disabled="loading">
        {{ loading ? '登录中…' : '登录' }}
      </button>
    </form>

    <p class="error" v-if="error">{{ error }}</p>

    <p class="switch">
      还没有账号？<RouterLink to="/register">立即注册</RouterLink>
    </p>

    <section class="tips">
      <h3>体验账号</h3>
      <ul>
        <li>管理员：admin / Admin@123</li>
        <li>普通用户：demo_user / User@123</li>
        <li>家政服务人员：demo_provider / Provider@123</li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { getSession, login } from '../utils/api'

const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

if (getSession()) {
  router.replace({ name: 'dashboard' })
}

const submit = async () => {
  if (!form.username || !form.password) {
    error.value = '请输入完整的登录信息'
    return
  }

  loading.value = true
  error.value = ''
  try {
    await login({ username: form.username, password: form.password })
    router.replace({ name: 'dashboard' })
  } catch (err: any) {
    error.value = err?.message ?? '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-card {
  max-width: 420px;
  margin: 0 auto;
  text-align: center;
}

h2 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #475569;
  margin-bottom: 2rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.3rem;
  text-align: left;
}

label {
  display: flex;
  flex-direction: column;
  font-weight: 600;
  color: #1e293b;
}

input {
  margin-top: 0.5rem;
  border: 1px solid #cbd5f5;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  transition: border-color 0.2s ease;
}

input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.15);
}

button {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 0.85rem;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s ease;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  margin-top: 1rem;
  color: #dc2626;
}

.switch {
  margin-top: 1.5rem;
}

.switch a {
  color: #2563eb;
  font-weight: 600;
}

.tips {
  margin-top: 2.5rem;
  text-align: left;
  background: #f8fafc;
  padding: 1.2rem 1.4rem;
  border-radius: 16px;
}

.tips h3 {
  margin: 0 0 0.6rem;
  font-size: 1rem;
}

.tips ul {
  margin: 0;
  padding-left: 1.1rem;
  color: #334155;
}

.tips li + li {
  margin-top: 0.3rem;
}
</style>
