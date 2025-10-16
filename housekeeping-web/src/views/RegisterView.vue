<template>
  <div class="auth-page">
    <div class="auth-container">
      <section class="auth-info">
        <p class="info-badge">欢迎加入</p>
        <h1 class="info-title">几分钟内搭建属于您的家政业务</h1>
        <p class="info-description">
          注册后即可在平台上发布家政服务、预约优质项目，并实时掌握资金流水。我们为个人用户、家政公司提供全面的数字化工具。
        </p>
        <ul class="info-highlights">
          <li>多角色账户体系满足不同身份需求</li>
          <li>安全托管钱包，交易清晰可追溯</li>
          <li>评价与退款机制保障体验</li>
        </ul>
      </section>

      <div class="auth-card" aria-labelledby="register-title">
        <div class="card-header">
          <h2 id="register-title">创建账号</h2>
          <p>填写下方信息，即刻加入家政服务平台</p>
        </div>
        <form class="auth-form" @submit.prevent="handleRegister">
          <label class="form-field" for="register-account">
            <span class="form-label">账号</span>
            <input
              id="register-account"
              v-model="account"
              class="form-input"
              type="text"
              placeholder="请输入账号"
              autocomplete="username"
            />
          </label>
          <label class="form-field" for="register-password">
            <span class="form-label">密码</span>
            <input
              id="register-password"
              v-model="password"
              class="form-input"
              type="password"
              placeholder="请输入密码"
              autocomplete="new-password"
            />
          </label>
          <label class="form-field" for="register-confirm">
            <span class="form-label">确认密码</span>
            <input
              id="register-confirm"
              v-model="confirmPassword"
              class="form-input"
              type="password"
              placeholder="请再次输入密码"
              autocomplete="new-password"
            />
          </label>
          <label class="form-field" for="register-role">
            <span class="form-label">角色</span>
            <select id="register-role" v-model="role" class="form-select">
              <option v-for="roleOption in roleOptions" :key="roleOption.value" :value="roleOption.value">
                {{ roleOption.label }}
              </option>
            </select>
          </label>
          <button type="submit" class="primary-button" :disabled="isSubmitting">
            {{ isSubmitting ? '注册中…' : '立即注册' }}
          </button>
        </form>
        <p class="auth-switch">
          已有账号？
          <RouterLink class="auth-link" to="/login">直接登录</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

import { AUTH_ROLE_KEY, ROLE_LABELS, type UserRole } from '../constants/auth'
import { registerAccount, type RegisterPayload } from '../services/auth'

const router = useRouter()

const account = ref('')
const password = ref('')
const confirmPassword = ref('')
const role = ref<UserRole>('user')
const roleOptions = (['user', 'company'] as UserRole[]).map((value) => ({
  value,
  label: ROLE_LABELS[value],
}))
const isSubmitting = ref(false)

const handleRegister = async () => {
  if (!account.value.trim() || !password.value || !confirmPassword.value) {
    window.alert('请完整填写账号和密码信息')
    return
  }

  if (password.value !== confirmPassword.value) {
    window.alert('两次输入的密码不一致')
    return
  }

  isSubmitting.value = true
  try {
    const payload: RegisterPayload = {
      account: account.value.trim(),
      password: password.value,
      role: role.value,
    }

    await registerAccount(payload)

    sessionStorage.removeItem(AUTH_ROLE_KEY)
    window.alert('注册成功，请使用账号登录')
    router.push({ name: 'login' })
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '注册失败，请稍后再试')
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
  background: rgba(124, 58, 237, 0.1);
  color: var(--brand-secondary);
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
  box-shadow: 0 12px 30px rgba(124, 58, 237, 0.08);
  backdrop-filter: blur(10px);
}

.info-highlights li::before {
  content: '★';
  color: var(--brand-secondary);
  font-weight: 700;
}

.auth-card {
  width: min(420px, 100%);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94) 0%, rgba(255, 255, 255, 0.84) 100%);
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
  border-color: var(--brand-secondary);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.2);
}

.form-select {
  appearance: none;
  background-image: linear-gradient(45deg, transparent 50%, var(--brand-secondary) 50%),
    linear-gradient(135deg, var(--brand-secondary) 50%, transparent 50%),
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
  background: linear-gradient(135deg, var(--brand-secondary) 0%, #5b21b6 100%);
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
  color: var(--brand-secondary);
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
