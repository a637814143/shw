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
                  <th>设置新余额</th>
                  <th>重置密码</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.username }}</td>
                  <td>{{ roleText(user.role) }}</td>
                  <td>{{ user.balance.toFixed(2) }}</td>
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
                  <td colspan="5" class="empty-row">暂无用户数据。</td>
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
  updateAdminWallet,
  type ServiceOrderItem,
  type UpdatePasswordPayload,
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

const loadUsers = async () => {
  try {
    const data = await fetchAdminUsers()
    users.value = data
    data.forEach((user) => {
      ensureWalletDraft(user)
      ensurePasswordDraft(user)
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
  await loadUsers()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f4f6fb;
}

.dashboard-header {
  padding: 24px 32px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.dashboard-subtitle {
  margin: 6px 0 0;
  color: #6b7280;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome {
  font-weight: 600;
  color: #1f2937;
}

.logout-button {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #ffffff;
  cursor: pointer;
}

.logout-button:hover {
  background: #f3f4f6;
}

.dashboard-main {
  flex: 1;
  display: flex;
  min-height: 0;
}

.sidebar {
  width: 220px;
  background: #111827;
  color: #fff;
  padding: 32px 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  background: transparent;
  color: inherit;
  border: none;
  text-align: left;
  font-size: 16px;
  cursor: pointer;
}

.sidebar-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.sidebar-item.active {
  background: rgba(255, 255, 255, 0.15);
  font-weight: 600;
}

.content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
}

.panel {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  vertical-align: middle;
}

.inline-form {
  display: flex;
  align-items: center;
  gap: 8px;
}

.inline-form input {
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 8px 10px;
  width: 120px;
}

.table-actions {
  width: 160px;
}

.actions-inline {
  display: flex;
  gap: 12px;
}

.link-button {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
}

.link-button.danger {
  color: #ef4444;
}

.link-button:hover {
  text-decoration: underline;
}

.order-subtext {
  font-size: 13px;
  color: #6b7280;
}

.empty-row {
  text-align: center;
  color: #6b7280;
}

@media (max-width: 960px) {
  .dashboard-main {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    flex-direction: row;
    overflow-x: auto;
  }

  .content {
    padding: 20px;
  }
}
</style>
