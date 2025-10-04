<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="branding" @click="goHome">
        <h1>轻松到家</h1>
        <p>家政服务一体化平台</p>
      </div>
      <nav class="session" v-if="session">
        <span class="session-item">角色：{{ translateRole(session.user.types) }}</span>
        <span class="session-item">账号：{{ session.user.userName }}</span>
        <span class="session-item" v-if="session.user.types !== 'ADMIN'">
          钱包余额：￥{{ session.user.money.toFixed(2) }}
        </span>
        <button class="logout" type="button" @click="logout">退出登录</button>
      </nav>
    </header>

    <main class="app-content">
      <RouterView @wallet-updated="updateBalance" />
    </main>

    <footer class="app-footer">
      <p>© {{ currentYear }} 轻松到家 · 守护美好居家生活</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import { AUTH_EVENT, clearSession, getSession, type AuthSession } from './utils/api'

const router = useRouter()
const session = ref<AuthSession | null>(getSession())
const currentYear = new Date().getFullYear()

const handleAuthChange = () => {
  session.value = getSession()
}

onMounted(() => {
  window.addEventListener(AUTH_EVENT, handleAuthChange as EventListener)
})

onUnmounted(() => {
  window.removeEventListener(AUTH_EVENT, handleAuthChange as EventListener)
})

const goHome = () => {
  if (session.value) {
    router.push({ name: 'dashboard' })
  } else {
    router.push({ name: 'login' })
  }
}

const logout = () => {
  clearSession()
  handleAuthChange()
  router.replace({ name: 'login' })
}

const translateRole = (role: string) => {
  switch (role) {
    case 'ADMIN':
      return '管理员'
    case 'PROVIDER':
      return '家政服务人员'
    default:
      return '普通用户'
  }
}

const updateBalance = (amount: number) => {
  if (!session.value) return
  session.value = {
    ...session.value,
    user: {
      ...session.value.user,
      money: amount,
    },
  }
  localStorage.setItem('hk.auth.session', JSON.stringify(session.value))
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #f5f9ff 0%, #ffffff 40%);
  color: #0f172a;
}

.app-header {
  background: #1d4ed8;
  color: #ffffff;
  padding: 1.5rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 10px 30px rgba(29, 78, 216, 0.25);
}

.branding {
  cursor: pointer;
}

.branding h1 {
  font-size: 1.9rem;
  margin: 0;
  font-weight: 700;
}

.branding p {
  font-size: 0.95rem;
  opacity: 0.9;
  margin: 0.25rem 0 0;
}

.session {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.95rem;
}

.session-item {
  background: rgba(255, 255, 255, 0.15);
  padding: 0.45rem 0.8rem;
  border-radius: 999px;
}

.logout {
  background: #f97316;
  color: #fff;
  border: none;
  border-radius: 999px;
  padding: 0.45rem 1.2rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.logout:hover {
  background: #ea580c;
}

.app-content {
  flex: 1;
  width: min(1100px, 94vw);
  margin: 2.5rem auto 0;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.12);
  padding: 3rem 3.5rem;
}

.app-footer {
  text-align: center;
  padding: 2rem 1rem 3rem;
  color: #475569;
  font-size: 0.9rem;
}

@media (max-width: 900px) {
  .app-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .app-content {
    width: calc(100vw - 2rem);
    padding: 2rem 1.5rem;
  }

  .session {
    flex-wrap: wrap;
    gap: 0.6rem;
  }
}
</style>
