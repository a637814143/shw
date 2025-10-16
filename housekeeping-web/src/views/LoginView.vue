<template>
  <div class="auth-page">
    <div class="auth-container">
      <section class="auth-info">
        <p class="info-badge">全流程家政服务平台</p>
        <h1 class="info-title">欢迎回来，开启专业级家政服务运营</h1>
        <p class="info-description">
          统一管理个人预约、家政公司项目以及平台级审批，让每一次服务都更高效、更可信赖。
        </p>
        <ul class="info-highlights">
          <li>实时查看钱包余额与订单状态</li>
          <li>家政公司一站式上架、维护服务</li>
          <li>管理员全局监控，守护交易安全</li>
        </ul>
      </section>

      <div class="auth-card" aria-labelledby="login-title">
        <div class="card-header">
          <h2 id="login-title">账号登录</h2>
          <p>使用平台账号登录并选择对应角色</p>
        </div>
        <form class="auth-form" @submit.prevent="handleLogin">
          <label class="form-field" for="account">
            <span class="form-label">账号</span>
            <input
              id="account"
              v-model="account"
              class="form-input"
              type="text"
              autocomplete="username"
              placeholder="请输入账号"
            />
          </label>
          <label class="form-field" for="password">
            <span class="form-label">密码</span>
            <input
              id="password"
              v-model="password"
              class="form-input"
              type="password"
              autocomplete="current-password"
              placeholder="请输入密码"
            />
          </label>
          <label class="form-field" for="role">
            <span class="form-label">角色</span>
            <select id="role" v-model="role" class="form-select">
              <option v-for="roleOption in roleOptions" :key="roleOption.value" :value="roleOption.value">
                {{ roleOption.label }}
              </option>
            </select>
          </label>
          <button type="submit" class="primary-button" :disabled="isSubmitting">
            {{ isSubmitting ? '登录中…' : '立即登录' }}
          </button>
        </form>
        <p class="auth-switch">
          没有账号？
          <RouterLink class="auth-link" to="/register">立即注册</RouterLink>
        </p>
      </div>
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

    sessionStorage.setItem(AUTH_TOKEN_KEY, result.token)
    sessionStorage.setItem(AUTH_ACCOUNT_KEY, result.account)
    sessionStorage.setItem(AUTH_ROLE_KEY, result.role)

    if (result.role === 'user') {
      router.push({ name: 'user-dashboard' })
    } else if (result.role === 'company') {
      router.push({ name: 'company-dashboard' })
    } else if (result.role === 'admin') {
      router.push({ name: 'admin-dashboard' })
    } else {
      router.push({
        name: 'role-landing',
        query: { role: result.role },
      })
    }
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '登录失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
}

.auth-container {
  width: min(1100px, 100%);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 48px;
  align-items: center;
}

.auth-info {
  color: #0f172a;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: var(--brand-primary);
  font-weight: 600;
  font-size: 14px;
  width: fit-content;
}

.info-title {
  font-size: clamp(32px, 3vw + 12px, 46px);
  font-weight: 700;
  line-height: 1.25;
}

.info-description {
  font-size: 16px;
  color: var(--brand-text-muted);
  max-width: 520px;
}

.info-highlights {
  display: grid;
  gap: 12px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.info-highlights li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: var(--brand-radius);
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 12px 30px rgba(37, 99, 235, 0.08);
  backdrop-filter: blur(10px);
}

.info-highlights li::before {
  content: '✔';
  color: var(--brand-success);
  font-weight: 700;
}

.auth-card {
  width: min(420px, 100%);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92) 0%, rgba(255, 255, 255, 0.82) 100%);
  border-radius: calc(var(--brand-radius) + 4px);
  box-shadow: var(--brand-shadow);
  border: 1px solid var(--brand-border);
  padding: 40px 36px;
  backdrop-filter: blur(16px);
}

.card-header h2 {
  font-size: 26px;
  font-weight: 700;
  margin: 0;
  color: var(--brand-text);
}

.card-header p {
  margin-top: 6px;
  font-size: 14px;
  color: var(--brand-text-muted);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
  margin-top: 24px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 15px;
  color: var(--brand-text-muted);
}

.form-label {
  font-weight: 600;
  color: var(--brand-text);
}

.form-input,
.form-select {
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 12px;
  padding: 12px 14px;
  background: rgba(248, 250, 255, 0.9);
  font-size: 15px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: var(--brand-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.2);
}

.form-select {
  appearance: none;
  background-image: linear-gradient(45deg, transparent 50%, var(--brand-primary) 50%),
    linear-gradient(135deg, var(--brand-primary) 50%, transparent 50%),
    linear-gradient(to right, rgba(148, 163, 184, 0.35), rgba(148, 163, 184, 0.35));
  background-position:
    calc(100% - 22px) calc(50% - 4px),
    calc(100% - 16px) calc(50% - 4px),
    calc(100% - 2.5rem) 50%;
  background-size: 6px 6px, 6px 6px, 1px 55%;
  background-repeat: no-repeat;
}

.primary-button {
  margin-top: 8px;
  width: 100%;
  padding: 14px 16px;
  background: linear-gradient(135deg, var(--brand-primary) 0%, var(--brand-primary-dark) 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 17px;
  font-weight: 700;
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
  box-shadow: var(--brand-shadow-soft);
}

.auth-switch {
  margin-top: 24px;
  text-align: center;
  color: var(--brand-text-muted);
  font-size: 14px;
}

.auth-link {
  color: var(--brand-primary);
  text-decoration: none;
  font-weight: 600;
}

.auth-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .auth-container {
    gap: 36px;
  }

  .auth-card {
    padding: 32px 24px;
  }
}
</style>
