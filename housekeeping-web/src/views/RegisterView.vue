<template>
  <div class="auth-page">
    <h1 class="auth-title">注册账号</h1>
    <div class="auth-card">
      <p class="auth-description">
        填写以下信息即可创建家政服务平台账号，发布需求或加入成为家政人员。
      </p>
      <form class="auth-form" @submit.prevent="handleRegister">
        <div class="form-row">
          <label class="form-label" for="register-account">设置账号</label>
          <input
            id="register-account"
            v-model="account"
            class="form-input"
            type="text"
            placeholder="请输入账号"
            autocomplete="username"
          />
        </div>
        <div class="form-row">
          <label class="form-label" for="register-password">设置密码</label>
          <input
            id="register-password"
            v-model="password"
            class="form-input"
            type="password"
            placeholder="请输入密码"
            autocomplete="new-password"
          />
        </div>
        <div class="form-row">
          <label class="form-label" for="register-confirm">确认密码</label>
          <input
            id="register-confirm"
            v-model="confirmPassword"
            class="form-input"
            type="password"
            placeholder="请再次输入密码"
            autocomplete="new-password"
          />
        </div>
        <div class="form-row">
          <label class="form-label" for="register-role">请选择角色</label>
          <select id="register-role" v-model="role" class="form-select">
            <option value="admin">管理员</option>
            <option value="staff">家政人员</option>
            <option value="user">用户</option>
          </select>
        </div>
        <button type="submit" class="primary-button">注册</button>
      </form>
      <p class="auth-switch">
        已有账号？
        <RouterLink class="auth-link" to="/login">返回登录</RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

import {
  USER_STORAGE_KEY,
  type RegisteredUser,
  AUTH_ROLE_KEY,
} from '../constants/auth'

const router = useRouter()

const account = ref('')
const password = ref('')
const confirmPassword = ref('')
const role = ref<'admin' | 'staff' | 'user'>('user')

const getStoredUsers = (): RegisteredUser[] => {
  try {
    const raw = localStorage.getItem(USER_STORAGE_KEY)
    return raw ? (JSON.parse(raw) as RegisteredUser[]) : []
  } catch (error) {
    console.warn('读取本地用户信息失败：', error)
    return []
  }
}

const persistUsers = (users: RegisteredUser[]) => {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(users))
}

const handleRegister = () => {
  if (!account.value || !password.value || !confirmPassword.value) {
    window.alert('请完整填写账号和密码信息')
    return
  }

  if (password.value !== confirmPassword.value) {
    window.alert('两次输入的密码不一致')
    return
  }

  const users = getStoredUsers()
  const duplicated = users.some(
    (user) => user.account.trim() === account.value.trim()
  )

  if (duplicated) {
    window.alert('该账号已存在，请更换账号')
    return
  }

  users.push({
    account: account.value.trim(),
    password: password.value,
    role: role.value,
  })

  persistUsers(users)
  sessionStorage.removeItem(AUTH_ROLE_KEY)

  window.alert('注册成功，请使用账号登录')
  router.push({ name: 'login' })
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

.auth-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 20px;
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
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
}

.form-select {
  appearance: none;
  background-image: linear-gradient(45deg, transparent 50%, #10b981 50%),
    linear-gradient(135deg, #10b981 50%, transparent 50%),
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
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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
  box-shadow: 0 10px 24px rgba(5, 150, 105, 0.25);
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
  color: #059669;
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
