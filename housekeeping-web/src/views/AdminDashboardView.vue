<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">平台管理员中心</h1>
        <p class="dashboard-subtitle">掌控用户资产、密码及全站退款流程</p>
      </div>
      <div class="header-actions">
        <span class="welcome">管理员：{{ username }}</span>
        <button type="button" class="logout-button" @click="logout">退出登录</button>
      </div>
  </header>

    <section class="stats-grid" aria-label="平台概览">
      <article class="stat-card accent">
        <p class="stat-label">平台注册用户</p>
        <p class="stat-value">{{ adminStats.totalUsers }}</p>
        <p class="stat-helper">覆盖普通用户、家政公司与管理员</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">待审退款</p>
        <p class="stat-value">{{ adminStats.pendingRefunds }}</p>
        <p class="stat-helper">需及时处理以维护服务体验</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">平台资产</p>
        <p class="stat-value">¥{{ adminStats.totalWallet.toFixed(2) }}</p>
        <p class="stat-helper">人均余额 ¥{{ adminStats.avgWallet.toFixed(2) }}</p>
      </article>
      <article class="stat-card success">
        <p class="stat-label">平台积分</p>
        <p class="stat-value">{{ adminStats.totalPoints }}</p>
        <p class="stat-helper">鼓励用户复购与口碑传播</p>
      </article>
    </section>

    <div class="dashboard-main">
      <aside class="sidebar">
        <button
          v-for="item in sections"
          :key="item.key"
          type="button"
          class="sidebar-item"
          :class="{ active: activeSection === item.key }"
          @click="switchSection(item.key)"
        >
          <span class="sidebar-icon">{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </aside>

      <main class="content">
        <section v-if="activeSection === 'users'" class="panel">
          <header class="panel-header">
            <div>
              <h2>用户资产与密码管理</h2>
              <p>可直接调整任意用户的余额或重置密码。</p>
            </div>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>账号</th>
                  <th>角色</th>
                  <th>余额（¥）</th>
                  <th>积分</th>
                  <th>设置新余额</th>
                  <th>调整积分</th>
                  <th>重置密码</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.username }}</td>
                  <td>{{ roleText(user.role) }}</td>
                  <td>{{ user.balance.toFixed(2) }}</td>
                  <td>{{ user.loyaltyPoints }}</td>
                  <td>
                    <div class="inline-form">
                      <input
                        v-model.number="walletEdits[user.id]"
                        type="number"
                        min="0"
                        step="0.01"
                      />
                      <button type="button" class="link-button" @click="saveWallet(user.id)">保存</button>
                    </div>
                  </td>
                  <td>
                    <div class="inline-form">
                      <input v-model.number="loyaltyEdits[user.id]" type="number" min="0" step="1" />
                      <button type="button" class="link-button" @click="saveLoyalty(user.id)">更新</button>
                    </div>
                  </td>
                  <td>
                    <div class="inline-form">
                      <input
                        v-model="passwordEdits[user.id]"
                        type="text"
                        placeholder="请输入新密码"
                      />
                      <button type="button" class="link-button" @click="savePassword(user.id)">
                        重置
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="!users.length">
                  <td colspan="7" class="empty-row">暂无用户数据。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else class="panel">
          <header class="panel-header">
            <div>
              <h2>全站退款处理</h2>
              <p>当公司未及时处理时，可由管理员进行终审。</p>
            </div>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务</th>
                  <th>申请人</th>
                  <th>家政公司</th>
                  <th>退款原因</th>
                  <th>申请时间</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in refundOrders" :key="order.id">
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">¥{{ order.price.toFixed(2) }} / {{ order.unit }}</div>
                  </td>
                  <td>{{ order.username }}</td>
                  <td>{{ order.companyName }}</td>
                  <td>{{ order.refundReason }}</td>
                  <td>{{ formatDate(order.updatedAt) }}</td>
                  <td class="table-actions actions-inline">
                    <button type="button" class="link-button" @click="handleRefund(order, true)">同意</button>
                    <button type="button" class="link-button danger" @click="handleRefund(order, false)">
                      拒绝
                    </button>
                  </td>
                </tr>
                <tr v-if="!refundOrders.length">
                  <td colspan="6" class="empty-row">暂无待处理的退款申请。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY, ROLE_LABELS } from '../constants/auth'
import {
  fetchAdminRefunds,
  fetchAdminUsers,
  handleAdminRefund,
  updateAdminPassword,
  updateAdminLoyalty,
  updateAdminWallet,
  type ServiceOrderItem,
  type UpdatePasswordPayload,
  type UpdateLoyaltyPayload,
  type UpdateWalletPayload,
  type UserAccountItem,
} from '../services/dashboard'

type SectionKey = 'users' | 'refunds'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

const router = useRouter()
const username = computed(() => sessionStorage.getItem(AUTH_ACCOUNT_KEY) ?? 'admin')

const sections: SectionMeta[] = [
  { key: 'users', icon: '🧾', label: '用户管理' },
  { key: 'refunds', icon: '💰', label: '退款审批' },
]

const activeSection = ref<SectionKey>('users')
const users = ref<UserAccountItem[]>([])
const refundOrders = ref<ServiceOrderItem[]>([])
const walletEdits = reactive<Record<number, number>>({})
const passwordEdits = reactive<Record<number, string>>({})
const loyaltyEdits = reactive<Record<number, number>>({})

const adminStats = computed(() => {
  const totalUsers = users.value.length
  const totalWallet = users.value.reduce((sum, item) => sum + item.balance, 0)
  const avgWallet = totalUsers > 0 ? totalWallet / totalUsers : 0
  const pendingRefunds = refundOrders.value.length
  const totalPoints = users.value.reduce((sum, item) => sum + (item.loyaltyPoints ?? 0), 0)
  return {
    totalUsers,
    totalWallet,
    avgWallet,
    pendingRefunds,
    totalPoints,
  }
})

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const switchSection = (key: SectionKey) => {
  activeSection.value = key
  if (key === 'refunds') {
    loadRefunds()
  }
}

const roleText = (role: string) => ROLE_LABELS[role as keyof typeof ROLE_LABELS] ?? role

const ensureWalletDraft = (user: UserAccountItem) => {
  if (walletEdits[user.id] === undefined) {
    walletEdits[user.id] = user.balance
  }
}

const ensurePasswordDraft = (user: UserAccountItem) => {
  if (passwordEdits[user.id] === undefined) {
    passwordEdits[user.id] = ''
  }
}

const ensureLoyaltyDraft = (user: UserAccountItem) => {
  if (loyaltyEdits[user.id] === undefined) {
    loyaltyEdits[user.id] = user.loyaltyPoints
  }
}

const loadUsers = async () => {
  try {
    const data = await fetchAdminUsers()
    users.value = data
    data.forEach((user) => {
      ensureWalletDraft(user)
      ensurePasswordDraft(user)
      ensureLoyaltyDraft(user)
    })
  } catch (error) {
    console.error(error)
  }
}

const loadRefunds = async () => {
  try {
    refundOrders.value = await fetchAdminRefunds()
  } catch (error) {
    console.error(error)
  }
}

const saveWallet = async (userId: number) => {
  const money = Number(walletEdits[userId])
  if (Number.isNaN(money) || money < 0) {
    window.alert('请输入正确的金额')
    return
  }
  try {
    await updateAdminWallet(userId, { money } as UpdateWalletPayload)
    await loadUsers()
    window.alert('余额已更新')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '更新余额失败')
  }
}

const savePassword = async (userId: number) => {
  const password = passwordEdits[userId]?.trim()
  if (!password) {
    window.alert('请输入新密码')
    return
  }
  try {
    await updateAdminPassword(userId, { password } as UpdatePasswordPayload)
    passwordEdits[userId] = ''
    window.alert('密码已重置')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '重置密码失败')
  }
}

const saveLoyalty = async (userId: number) => {
  const points = Number(loyaltyEdits[userId])
  if (!Number.isFinite(points) || points < 0) {
    window.alert('请输入正确的积分')
    return
  }
  try {
    await updateAdminLoyalty(userId, { loyaltyPoints: Math.floor(points) } as UpdateLoyaltyPayload)
    await loadUsers()
    window.alert('积分已更新')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '更新积分失败')
  }
}

const handleRefund = async (order: ServiceOrderItem, approve: boolean) => {
  const message = window.prompt('请输入处理说明（可选）：', order.refundResponse || '') || undefined
  try {
    await handleAdminRefund(order.id, { approve, message })
    await loadRefunds()
    window.alert('处理完成')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '处理失败')
  }
}

const formatDate = (value: string) => {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}

onMounted(async () => {
  await Promise.all([loadUsers(), loadRefunds()])
})
</script>


<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 32px clamp(16px, 5vw, 48px) 48px;
  position: relative;
  z-index: 0;
}

.dashboard::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.08) 0%, rgba(37, 99, 235, 0.07) 45%, rgba(14, 116, 144, 0.05) 100%);
  border-radius: 40px 40px 0 0;
  z-index: -1;
}

.dashboard-header {
  position: relative;
  border-radius: calc(var(--brand-radius) + 12px);
  padding: 28px clamp(20px, 4vw, 36px);
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.95) 0%, rgba(37, 99, 235, 0.9) 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: 0 28px 48px rgba(124, 58, 237, 0.25);
  overflow: hidden;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(620px circle at 85% 5%, rgba(255, 255, 255, 0.22), transparent 65%);
  z-index: 0;
}

.dashboard-header > * {
  position: relative;
  z-index: 1;
}

.dashboard-title {
  margin: 0;
  font-size: clamp(28px, 2.5vw + 12px, 36px);
  font-weight: 700;
}

.dashboard-subtitle {
  margin-top: 8px;
  font-size: 15px;
  opacity: 0.85;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: rgba(15, 23, 42, 0.25);
  border-radius: 999px;
  backdrop-filter: blur(12px);
}

.logout-button {
  padding: 8px 18px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(15, 23, 42, 0.28);
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.logout-button:hover {
  transform: translateY(-1px);
  background: rgba(15, 23, 42, 0.42);
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.25);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
  gap: 18px;
}

.stat-card {
  padding: 20px;
  border-radius: calc(var(--brand-radius) + 4px);
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.16);
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(12px);
}

.stat-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(240px circle at 85% 15%, rgba(255, 255, 255, 0.25), transparent 60%);
  z-index: 0;
}

.stat-card > * {
  position: relative;
  z-index: 1;
}

.stat-label {
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--brand-text-muted);
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: var(--brand-text);
}

.stat-helper {
  font-size: 13px;
  color: var(--brand-text-muted);
}

.stat-card.accent {
  background: linear-gradient(150deg, rgba(124, 58, 237, 0.14), rgba(124, 58, 237, 0.05));
  border-color: rgba(124, 58, 237, 0.22);
}

.stat-card.primary {
  background: linear-gradient(150deg, rgba(37, 99, 235, 0.12), rgba(37, 99, 235, 0.04));
  border-color: rgba(37, 99, 235, 0.2);
}

.stat-card.success {
  background: linear-gradient(150deg, rgba(16, 185, 129, 0.12), rgba(16, 185, 129, 0.04));
  border-color: rgba(16, 185, 129, 0.2);
}

.stat-card.warning {
  background: linear-gradient(150deg, rgba(245, 158, 11, 0.12), rgba(245, 158, 11, 0.04));
  border-color: rgba(245, 158, 11, 0.2);
}

.dashboard-main {
  display: grid;
  grid-template-columns: minmax(220px, 260px) 1fr;
  gap: 28px;
  align-items: flex-start;
}

.sidebar {
  background: rgba(255, 255, 255, 0.8);
  border-radius: calc(var(--brand-radius) + 2px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 24px 50px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(16px);
  padding: 28px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sidebar-item {
  border: none;
  background: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-text-muted);
  cursor: pointer;
  transition: color 0.2s ease, background-color 0.2s ease, transform 0.2s ease;
}

.sidebar-item .sidebar-icon {
  font-size: 20px;
}

.sidebar-item:hover {
  color: var(--brand-secondary);
  background: rgba(124, 58, 237, 0.08);
  transform: translateX(4px);
}

.sidebar-item.active {
  color: var(--brand-secondary);
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.18), rgba(37, 99, 235, 0.08));
  box-shadow: inset 0 0 0 1px rgba(124, 58, 237, 0.2);
}

.content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel {
  background: rgba(255, 255, 255, 0.9);
  border-radius: calc(var(--brand-radius) + 4px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
  padding: 28px 32px;
  backdrop-filter: blur(14px);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.panel-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}

.panel-header p {
  margin: 8px 0 0;
  color: var(--brand-text-muted);
  font-size: 14px;
}

.table-wrapper {
  overflow-x: auto;
  border-radius: calc(var(--brand-radius) + 2px);
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.data-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: rgba(255, 255, 255, 0.95);
  table-layout: fixed;
}

.data-table thead th {
  background: rgba(124, 58, 237, 0.1);
  color: var(--brand-text);
  font-weight: 600;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  text-align: left;
}

.data-table tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
  vertical-align: top;
  color: var(--brand-text);
  text-align: left;
}

.data-table tbody tr:last-child td {
  border-bottom: none;
}

.data-table tbody tr:hover td {
  background: rgba(124, 58, 237, 0.06);
}

.table-actions {
  width: 200px;
}

.actions-inline {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.inline-form {
  display: flex;
  align-items: center;
  gap: 10px;
}

.inline-form input {
  width: 120px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 10px;
  padding: 8px 10px;
  background: rgba(248, 250, 255, 0.9);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.inline-form input:focus {
  outline: none;
  border-color: var(--brand-secondary);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.2);
}

.link-button {
  background: none;
  border: none;
  padding: 0;
  color: var(--brand-primary);
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s ease;
}

.link-button:hover {
  color: var(--brand-primary-dark);
  text-decoration: underline;
}

.link-button.danger {
  color: var(--brand-danger);
}

.empty-row {
  text-align: center;
  color: var(--brand-text-muted);
  padding: 16px 0;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.status-refund_requested {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.status-refund_approved {
  background: linear-gradient(135deg, #14b8a6, #0f766e);
}

.status-refund_rejected {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

@media (max-width: 1024px) {
  .dashboard {
    padding: 24px 24px 40px;
  }

  .dashboard-main {
    grid-template-columns: 1fr;
  }

  .sidebar {
    flex-direction: row;
    overflow-x: auto;
  }
}

@media (max-width: 720px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    align-self: stretch;
    justify-content: space-between;
  }

  .panel {
    padding: 24px;
  }
}
</style>

