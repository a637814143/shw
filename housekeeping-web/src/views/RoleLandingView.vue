<template>
  <div class="role-page">
    <div class="role-container">
      <header class="role-hero">
        <p class="role-badge">账户信息确认</p>
        <h1 class="role-title">{{ roleLabel }} 登录成功</h1>
        <p class="role-subtitle">
          当前账号：<strong v-if="account" class="role-account">{{ account }}</strong>
          <span v-else class="role-account muted">未读取到账户信息</span>
        </p>
      </header>

      <section class="role-card" aria-labelledby="role-help-title">
        <div class="card-body">
          <h2 id="role-help-title">下一步可以这样做</h2>
          <p>
            您已成功登录，但当前角色没有专属面板。请返回登录页重新选择，或直接进入对应角色的工作台。
          </p>
          <div class="quick-actions">
            <RouterLink class="quick-link" to="/user">普通用户中心</RouterLink>
            <RouterLink class="quick-link" to="/company">家政公司中心</RouterLink>
            <RouterLink class="quick-link" to="/admin">管理员中心</RouterLink>
          </div>
        </div>
        <footer class="card-footer">
          <button type="button" class="primary-button" @click="goToLogin">返回登录页面</button>
        </footer>
      </section>
    </div>
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
  align-items: center;
  justify-content: center;
  padding: 64px 20px;
}

.role-container {
  width: min(880px, 100%);
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.role-hero {
  display: flex;
  flex-direction: column;
  gap: 16px;
  color: var(--brand-text);
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: var(--brand-primary);
  font-weight: 600;
  font-size: 14px;
  width: fit-content;
}

.role-title {
  font-size: clamp(34px, 4vw + 12px, 48px);
  font-weight: 700;
  line-height: 1.25;
  margin: 0;
}

.role-subtitle {
  font-size: 16px;
  color: var(--brand-text-muted);
}

.role-account {
  color: var(--brand-text);
  font-weight: 700;
}

.role-account.muted {
  color: var(--brand-text-muted);
}

.role-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94) 0%, rgba(255, 255, 255, 0.86) 100%);
  border-radius: calc(var(--brand-radius) + 4px);
  border: 1px solid var(--brand-border);
  box-shadow: var(--brand-shadow);
  backdrop-filter: blur(16px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.card-body {
  padding: 36px 40px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-body h2 {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
}

.card-body p {
  color: var(--brand-text-muted);
  line-height: 1.7;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-link {
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--brand-primary);
  font-weight: 600;
  text-decoration: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.quick-link:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.12);
}

.card-footer {
  padding: 20px 40px 36px;
  display: flex;
  justify-content: flex-end;
  background: rgba(15, 23, 42, 0.02);
}

.primary-button {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--brand-primary) 0%, var(--brand-primary-dark) 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: var(--brand-shadow-soft);
}

@media (max-width: 640px) {
  .role-container {
    gap: 24px;
  }

  .card-body,
  .card-footer {
    padding: 28px 24px;
  }
}
</style>
