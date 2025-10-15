<template>
  <div class="auth-page">
    <h1 class="auth-title">家政服务平台</h1>
    <div class="auth-card">
      <form class="auth-form" @submit.prevent="handleLogin">
        <div class="form-row">
          <label class="form-label" for="account">请输入账号</label>
          <input
            id="account"
            v-model="account"
            class="form-input"
            type="text"
            autocomplete="username"
          />
        </div>
        <div class="form-row">
          <label class="form-label" for="password">请输入密码</label>
          <input
            id="password"
            v-model="password"
            class="form-input"
            type="password"
            autocomplete="current-password"
          />
        </div>
        <div class="form-row">
          <label class="form-label" for="role">请选择角色</label>
          <select id="role" v-model="role" class="form-select">
            <option value="admin">管理员</option>
            <option value="staff">家政人员</option>
            <option value="user">用户</option>
          </select>
        </div>
        <button type="submit" class="primary-button">登录</button>
      </form>
      <p class="auth-switch">
        没有账号？
        <RouterLink class="auth-link" to="/register">请注册</RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

import {
  AUTH_ACCOUNT_KEY,
  AUTH_ROLE_KEY,
  USER_STORAGE_KEY,
  type RegisteredUser,
} from '../constants/auth'

const router = useRouter()
const account = ref('')
const password = ref('')
const role = ref<'admin' | 'staff' | 'user'>('admin')

const getStoredUsers = (): RegisteredUser[] => {
  try {
    const raw = localStorage.getItem(USER_STORAGE_KEY)
    return raw ? (JSON.parse(raw) as RegisteredUser[]) : []
  } catch (error) {
    console.warn('读取本地用户信息失败：', error)
    return []
  }
}

const handleLogin = () => {
  if (!account.value || !password.value) {
    window.alert('请输入账号和密码')
    return
  }

  const users = getStoredUsers()
  const matchedUser = users.find(
    (user) =>
      user.account.trim() === account.value.trim() &&
      user.password === password.value &&
      user.role === role.value
  )

  if (!matchedUser) {
    window.alert('账号、密码或角色不匹配，请重试')
    return
  }

  if (matchedUser.role !== 'user') {
    window.alert('仅支持用户账号登录访问用户主界面')
    return
  }

  sessionStorage.setItem(AUTH_ROLE_KEY, matchedUser.role)
  sessionStorage.setItem(AUTH_ACCOUNT_KEY, matchedUser.account)

  router.push({ name: 'user-dashboard' })
}
</script>

<style scoped>
.auth-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 32px 16px;
  background: linear-gradient(180deg, #f5f7fb 0%, #ffffff 100%);
}

.auth-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #1f2a44;
  letter-spacing: 2px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  padding: 40px 32px;
  border-radius: 16px;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.12);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-label {
  flex: 0 0 110px;
  font-size: 16px;
  color: #3a4660;
}

.form-input,
.form-select {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #d5d9e2;
  border-radius: 8px;
  background-color: #fafbff;
  font-size: 16px;
  color: #1f2a44;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.form-select {
  appearance: none;
  background-image: linear-gradient(45deg, transparent 50%, #3b82f6 50%),
    linear-gradient(135deg, #3b82f6 50%, transparent 50%),
    linear-gradient(to right, #d5d9e2, #d5d9e2);
  background-position: calc(100% - 20px) calc(50% - 3px),
    calc(100% - 15px) calc(50% - 3px),
    calc(100% - 2.5rem) 50%;
  background-size: 5px 5px, 5px 5px, 1px 50%;
  background-repeat: no-repeat;
  cursor: pointer;
}

.primary-button {
  margin-top: 8px;
  width: 100%;
  padding: 12px 16px;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.25);
}

.primary-button:active {
  transform: translateY(0);
}

.auth-switch {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #6b7280;
}

.auth-link {
  color: #2563eb;
  text-decoration: none;
  margin-left: 4px;
}

.auth-link:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .auth-card {
    padding: 32px 24px;
  }

  .form-row {
    flex-direction: column;
    align-items: stretch;
  }

  .form-label {
    flex: unset;
    width: 100%;
  }
}
</style>
