<template>
  <div class="role-page">
    <header class="role-header">
      <h1 class="role-title">家政服务平台</h1>
      <p class="role-subtitle">欢迎登录</p>
    </header>
    <main class="role-content">
      <section class="role-card">
        <h2 class="card-title">{{ roleLabel }} 登录成功</h2>
        <p class="card-info" v-if="account">
          当前账号：<strong>{{ account }}</strong>
        </p>
        <p class="card-message">
          当前前端仅开放普通用户功能页面。管理员与家政公司请通过后台管理系统或联系平台工作人员使用完整功能。
        </p>
        <div class="card-actions">
          <button type="button" class="primary-button" @click="goToLogin">返回登录</button>
          <RouterLink class="secondary-link" to="/user">前往用户界面（需普通用户账号）</RouterLink>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, ROLE_LABELS, type UserRole } from '../constants/auth'

const route = useRoute()
const router = useRouter()

const isUserRole = (value: string): value is UserRole =>
  value === 'admin' || value === 'company' || value === 'user'

const roleFromQuery = computed(() => {
  const { role } = route.query
  if (typeof role === 'string' && isUserRole(role)) {
    return role
  }
  return null
})

const storedRole = sessionStorage.getItem(AUTH_ROLE_KEY)

const role = computed<UserRole>(() => {
  if (roleFromQuery.value) {
    return roleFromQuery.value
  }
  if (storedRole && isUserRole(storedRole)) {
    return storedRole
  }
  return 'user'
})

const account = sessionStorage.getItem(AUTH_ACCOUNT_KEY) || ''

const roleLabel = computed(() => ROLE_LABELS[role.value])

const goToLogin = () => {
  router.push({ name: 'login' })
}
</script>

<style scoped>
.role-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #f5f7fb 0%, #ffffff 100%);
  color: #1f2a44;
}

.role-header {
  text-align: center;
  padding: 48px 16px 24px;
}

.role-title {
  font-size: 36px;
  margin-bottom: 12px;
  font-weight: 600;
  letter-spacing: 2px;
}

.role-subtitle {
  font-size: 16px;
  color: #6b7280;
}

.role-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 0 16px 48px;
}

.role-card {
  max-width: 520px;
  width: 100%;
  background: #ffffff;
  border-radius: 16px;
  padding: 40px 36px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
  text-align: center;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
}

.card-info {
  font-size: 16px;
  margin-bottom: 12px;
  color: #475569;
}

.card-message {
  font-size: 15px;
  line-height: 1.8;
  color: #4b5563;
  margin-bottom: 28px;
}

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.primary-button {
  padding: 12px 16px;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.25);
}

.secondary-link {
  font-size: 15px;
  color: #2563eb;
  text-decoration: none;
}

.secondary-link:hover {
  text-decoration: underline;
}

@media (max-width: 600px) {
  .role-card {
    padding: 32px 24px;
  }

  .card-message {
    text-align: left;
  }
}
</style>
