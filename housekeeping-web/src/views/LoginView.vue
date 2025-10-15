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
            <option v-for="roleOption in roleOptions" :key="roleOption.value" :value="roleOption.value">
              {{ roleOption.label }}
            </option>
          </select>
        </div>
        <button type="submit" class="primary-button" :disabled="isSubmitting">
          {{ isSubmitting ? '登录中…' : '登录' }}
        </button>
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

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY, ROLE_LABELS, type UserRole } from '../constants/auth'
import { loginAccount } from '../services/auth'

const router = useRouter()
const account = ref('')
const password = ref('')
const role = ref<UserRole>('user')
const isSubmitting = ref(false)
const roleOptions = (
  Object.entries(ROLE_LABELS) as Array<[UserRole, string]>
).map(([value, label]) => ({ value, label }))

const handleLogin = async () => {
  if (!account.value.trim() || !password.value) {
    window.alert('请输入账号和密码')
    return
  }

  isSubmitting.value = true
  try {
    const result = await loginAccount({
      account: account.value.trim(),
      password: password.value,
      role: role.value,
    })

    if (result.role !== 'user') {
      window.alert('仅支持普通用户账号登录访问用户主界面')
      return
    }

    sessionStorage.setItem(AUTH_TOKEN_KEY, result.token)
    sessionStorage.setItem(AUTH_ACCOUNT_KEY, result.account)
    sessionStorage.setItem(AUTH_ROLE_KEY, result.role)

    router.push({ name: 'user-dashboard' })
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '登录失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
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

.primary-button[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
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
