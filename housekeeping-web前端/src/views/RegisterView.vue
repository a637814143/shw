<template>
  <div class="auth-card">
    <h2>注册新账号</h2>
    <p class="subtitle">支持注册普通用户与家政服务人员</p>

    <form class="auth-form" @submit.prevent="submit">
      <label>
        账号
        <input v-model.trim="form.username" type="text" required />
      </label>
      <label>
        密码
        <input v-model="form.password" type="password" required />
      </label>
      <label>
        确认密码
        <input v-model="form.confirm" type="password" required />
      </label>
      <label>
        角色
        <select v-model="form.type" required>
          <option value="USER">普通用户</option>
          <option value="PROVIDER">家政服务人员</option>
        </select>
      </label>
      <button type="submit" :disabled="loading">
        {{ loading ? '提交中…' : '注册账号' }}
      </button>
    </form>

    <p class="error" v-if="error">{{ error }}</p>
    <p class="success" v-if="success">{{ success }}</p>

    <p class="switch">
      已经拥有账号？<RouterLink to="/login">返回登录</RouterLink>
    </p>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { getSession, register } from '../utils/api'

const router = useRouter()
const form = reactive({
  username: '',
  password: '',
  confirm: '',
  type: 'USER' as 'USER' | 'PROVIDER',
})
const loading = ref(false)
const error = ref('')
const success = ref('')

if (getSession()) {
  router.replace({ name: 'dashboard' })
}

const submit = async () => {
  if (!form.username || !form.password) {
    error.value = '请填写账号和密码'
    return
  }
  if (form.password !== form.confirm) {
    error.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  error.value = ''
  success.value = ''
  try {
    await register({ username: form.username, password: form.password, type: form.type })
    success.value = '注册成功，请使用该账号登录'
    setTimeout(() => router.push({ name: 'login' }), 800)
  } catch (err: any) {
    error.value = err?.message ?? '注册失败，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-card {
  max-width: 440px;
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
  gap: 1.2rem;
  text-align: left;
}

label {
  display: flex;
  flex-direction: column;
  font-weight: 600;
  color: #1e293b;
}

input,
select {
  margin-top: 0.5rem;
  border: 1px solid #cbd5f5;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  transition: border-color 0.2s ease;
}

input:focus,
select:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.15);
}

button {
  background: linear-gradient(135deg, #10b981, #059669);
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

.success {
  margin-top: 1rem;
  color: #047857;
}

.switch {
  margin-top: 1.5rem;
}

.switch a {
  color: #2563eb;
  font-weight: 600;
}
</style>
