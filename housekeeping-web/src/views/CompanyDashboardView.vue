<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">家政公司工作台</h1>
        <p class="dashboard-subtitle">维护服务项目并及时处理用户退款</p>
      </div>
      <div class="header-actions">
        <span class="welcome">您好，{{ username }}！</span>
        <span class="wallet">钱包余额：¥{{ balanceText }}</span>
        <button type="button" class="logout-button" @click="logout">退出登录</button>
      </div>
  </header>

    <section class="stats-grid" aria-label="运营数据概览">
      <article class="stat-card accent">
        <p class="stat-label">上架服务</p>
        <p class="stat-value">{{ companyStats.totalServices }}</p>
        <p class="stat-helper">向平台展示您的服务能力</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">待处理退款</p>
        <p class="stat-value">{{ companyStats.pendingRefunds }}</p>
        <p class="stat-helper">及时响应守护用户体验</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">平均定价</p>
        <p class="stat-value">¥{{ companyStats.avgPrice.toFixed(2) }}</p>
        <p class="stat-helper">以数据优化您的定价策略</p>
      </article>
      <article class="stat-card success">
        <p class="stat-label">账户余额</p>
        <p class="stat-value">¥{{ companyStats.balance.toFixed(2) }}</p>
        <p class="stat-helper">平台托管，资金安全透明</p>
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
        <section v-if="activeSection === 'services'" class="panel">
          <header class="panel-header">
            <div>
              <h2>服务项目管理</h2>
              <p>完善服务信息让用户更了解您的优势。</p>
            </div>
            <button type="button" class="primary-button" @click="openServiceForm()">新增服务</button>
          </header>

          <div v-if="serviceFormVisible" class="form-card">
            <form class="form-grid" @submit.prevent="submitServiceForm">
              <div class="form-field">
                <label for="service-name">服务名称</label>
                <input id="service-name" v-model="serviceForm.name" type="text" />
              </div>
              <div class="form-field">
                <label for="service-unit">计价单位</label>
                <input id="service-unit" v-model="serviceForm.unit" type="text" placeholder="如：次/小时" />
              </div>
              <div class="form-field">
                <label for="service-price">价格</label>
                <input id="service-price" v-model.number="serviceForm.price" type="number" min="0" step="0.01" />
              </div>
              <div class="form-field">
                <label for="service-contact">联系方式</label>
                <input id="service-contact" v-model="serviceForm.contact" type="text" />
              </div>
              <div class="form-field form-field-full">
                <label for="service-description">服务简介</label>
                <textarea id="service-description" v-model="serviceForm.description" rows="3"></textarea>
              </div>
              <div class="form-actions">
                <button type="button" class="secondary-button" @click="closeServiceForm">取消</button>
                <button type="submit" class="primary-button" :disabled="serviceSaving">
                  {{ serviceSaving ? '保存中…' : serviceSubmitText }}
                </button>
              </div>
            </form>
          </div>

          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务名称</th>
                  <th>价格</th>
                  <th>联系方式</th>
                  <th>描述</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in services" :key="item.id">
                  <td>
                    <strong>{{ item.name }}</strong>
                    <div class="order-subtext">单位：{{ item.unit }}</div>
                  </td>
                  <td>¥{{ item.price.toFixed(2) }}</td>
                  <td>{{ item.contact }}</td>
                  <td>{{ item.description || '—' }}</td>
                  <td class="table-actions">
                    <button type="button" class="link-button" @click="openServiceForm(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="handleDeleteService(item)">删除</button>
                  </td>
                </tr>
                <tr v-if="!services.length">
                  <td colspan="5" class="empty-row">还没有服务内容，请点击上方按钮进行新增。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else class="panel">
          <header class="panel-header">
            <div>
              <h2>退款申请处理</h2>
              <p>审核用户的退款申请，保障服务体验。</p>
            </div>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>订单信息</th>
                  <th>用户</th>
                  <th>退款原因</th>
                  <th>提交时间</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in refundOrders" :key="order.id">
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">价格：¥{{ order.price.toFixed(2) }} / {{ order.unit }}</div>
                  </td>
                  <td>{{ order.username }}</td>
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
                  <td colspan="5" class="empty-row">暂无待处理的退款申请。</td>
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

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'
import {
  fetchCurrentAccount,
  createCompanyService,
  deleteCompanyService,
  fetchCompanyRefunds,
  fetchCompanyServices,
  handleCompanyRefund,
  updateCompanyService,
  type AccountProfileItem,
  type CompanyServicePayload,
  type HousekeepServiceItem,
  type ServiceOrderItem,
} from '../services/dashboard'

type SectionKey = 'services' | 'refunds'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

const router = useRouter()
const account = ref<AccountProfileItem | null>(null)
const username = computed(
  () => account.value?.username ?? sessionStorage.getItem(AUTH_ACCOUNT_KEY) ?? '公司用户',
)
const balanceText = computed(() => (account.value ? account.value.balance.toFixed(2) : '0.00'))

const sections: SectionMeta[] = [
  { key: 'services', icon: '🧹', label: '服务管理' },
  { key: 'refunds', icon: '💸', label: '退款审核' },
]

const activeSection = ref<SectionKey>('services')
const services = ref<HousekeepServiceItem[]>([])
const refundOrders = ref<ServiceOrderItem[]>([])
const serviceFormVisible = ref(false)
const serviceSaving = ref(false)
const editingServiceId = ref<number | null>(null)
const serviceForm = reactive<CompanyServicePayload>({
  name: '',
  unit: '',
  price: 0,
  contact: '',
  description: '',
})

const serviceSubmitText = computed(() => (editingServiceId.value ? '保存修改' : '新增服务'))

const companyStats = computed(() => {
  const totalServices = services.value.length
  const pendingRefunds = refundOrders.value.length
  const avgPrice = totalServices
    ? services.value.reduce((sum, item) => sum + item.price, 0) / totalServices
    : 0
  const balance = account.value?.balance ?? 0
  return {
    totalServices,
    pendingRefunds,
    avgPrice,
    balance,
  }
})

const switchSection = (key: SectionKey) => {
  activeSection.value = key
  if (key === 'refunds') {
    loadRefunds()
  }
}

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const resetServiceForm = () => {
  serviceForm.name = ''
  serviceForm.unit = ''
  serviceForm.price = 0
  serviceForm.contact = ''
  serviceForm.description = ''
  editingServiceId.value = null
}

const openServiceForm = (item?: HousekeepServiceItem) => {
  if (item) {
    serviceForm.name = item.name
    serviceForm.unit = item.unit
    serviceForm.price = item.price
    serviceForm.contact = item.contact
    serviceForm.description = item.description || ''
    editingServiceId.value = item.id
  } else {
    resetServiceForm()
  }
  serviceFormVisible.value = true
}

const closeServiceForm = () => {
  serviceFormVisible.value = false
  resetServiceForm()
}

const submitServiceForm = async () => {
  if (!serviceForm.name.trim() || !serviceForm.unit.trim() || !serviceForm.contact.trim()) {
    window.alert('请完整填写服务信息')
    return
  }
  if (serviceForm.price <= 0) {
    window.alert('请填写有效的价格')
    return
  }
  serviceSaving.value = true
  try {
    const payload: CompanyServicePayload = {
      name: serviceForm.name.trim(),
      unit: serviceForm.unit.trim(),
      price: Number(serviceForm.price),
      contact: serviceForm.contact.trim(),
      description: serviceForm.description?.trim() || '',
    }
    if (editingServiceId.value) {
      await updateCompanyService(editingServiceId.value, payload)
    } else {
      await createCompanyService(payload)
    }
    await loadServices()
    await loadAccount()
    closeServiceForm()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '保存服务失败')
  } finally {
    serviceSaving.value = false
  }
}

const handleDeleteService = async (item: HousekeepServiceItem) => {
  if (!window.confirm(`确认删除服务“${item.name}”？`)) return
  try {
    await deleteCompanyService(item.id)
    await loadServices()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleRefund = async (order: ServiceOrderItem, approve: boolean) => {
  const message = window.prompt('请输入处理说明（可选）：', order.refundResponse || '') || undefined
  try {
    await handleCompanyRefund(order.id, { approve, message })
    await loadRefunds()
    await loadAccount()
    window.alert('已提交处理结果')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '处理失败')
  }
}

const loadServices = async () => {
  try {
    services.value = await fetchCompanyServices()
  } catch (error) {
    console.error(error)
  }
}

const loadRefunds = async () => {
  try {
    refundOrders.value = await fetchCompanyRefunds()
  } catch (error) {
    console.error(error)
  }
}

const loadAccount = async () => {
  try {
    account.value = await fetchCurrentAccount()
  } catch (error) {
    console.error(error)
  }
}

const formatDate = (value: string) => {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}

onMounted(async () => {
  await Promise.all([loadAccount(), loadServices(), loadRefunds()])
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
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08) 0%, rgba(16, 185, 129, 0.05) 45%, rgba(59, 130, 246, 0.08) 100%);
  border-radius: 40px 40px 0 0;
  z-index: -1;
}

.dashboard-header {
  position: relative;
  border-radius: calc(var(--brand-radius) + 12px);
  padding: 28px clamp(20px, 4vw, 36px);
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.94) 0%, rgba(16, 185, 129, 0.9) 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: 0 28px 48px rgba(37, 99, 235, 0.24);
  overflow: hidden;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(600px circle at 90% 10%, rgba(255, 255, 255, 0.2), transparent 60%);
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

.welcome {
  font-weight: 600;
}

.wallet {
  font-weight: 700;
  color: #fef08a;
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
  background: linear-gradient(150deg, rgba(37, 99, 235, 0.12), rgba(37, 99, 235, 0.04));
  border-color: rgba(37, 99, 235, 0.2);
}

.stat-card.primary {
  background: linear-gradient(150deg, rgba(16, 185, 129, 0.12), rgba(16, 185, 129, 0.04));
  border-color: rgba(16, 185, 129, 0.2);
}

.stat-card.success {
  background: linear-gradient(150deg, rgba(37, 99, 235, 0.12), rgba(16, 185, 129, 0.04));
  border-color: rgba(37, 99, 235, 0.2);
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
  color: var(--brand-success);
  background: rgba(16, 185, 129, 0.08);
  transform: translateX(4px);
}

.sidebar-item.active {
  color: var(--brand-success);
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.18), rgba(37, 99, 235, 0.08));
  box-shadow: inset 0 0 0 1px rgba(16, 185, 129, 0.2);
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

.form-card {
  margin-bottom: 24px;
  padding: 24px 28px;
  background: rgba(248, 250, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: calc(var(--brand-radius) + 2px);
  box-shadow: 0 20px 38px rgba(37, 99, 235, 0.12);
}

.primary-button {
  align-self: flex-start;
  padding: 10px 18px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, var(--brand-success) 0%, var(--brand-primary) 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: var(--brand-shadow-soft);
}

.secondary-button {
  padding: 10px 18px;
  border-radius: 12px;
  border: 1px solid rgba(37, 99, 235, 0.35);
  background: rgba(37, 99, 235, 0.08);
  color: var(--brand-primary);
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.secondary-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.18);
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.service-card {
  border-radius: calc(var(--brand-radius) + 2px);
  padding: 20px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 20px 36px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.service-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 28px 50px rgba(16, 185, 129, 0.18);
}

.service-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}

.service-company,
.order-subtext {
  margin: 0;
  font-size: 14px;
  color: var(--brand-text-muted);
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
}

.data-table thead th {
  background: rgba(16, 185, 129, 0.1);
  color: var(--brand-text);
  font-weight: 600;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.data-table tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
  vertical-align: top;
  color: var(--brand-text);
}

.data-table tbody tr:last-child td {
  border-bottom: none;
}

.data-table tbody tr:hover td {
  background: rgba(16, 185, 129, 0.06);
}

.table-actions {
  width: 150px;
}

.actions-inline {
  display: flex;
  gap: 12px;
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

.link-button.danger {
  color: var(--brand-danger);
}

.link-button:hover {
  color: var(--brand-primary-dark);
  text-decoration: underline;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field-full {
  grid-column: 1 / -1;
}

.form-field input,
.form-field select,
.form-field textarea {
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 12px;
  padding: 10px 12px;
  background: rgba(248, 250, 255, 0.9);
  font-size: 14px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-field input:focus,
.form-field select:focus,
.form-field textarea:focus {
  outline: none;
  border-color: var(--brand-success);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.18);
}

.empty-row {
  text-align: center;
  color: var(--brand-text-muted);
  padding: 16px 0;
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

  .panel,
  .form-card {
    padding: 24px;
  }
}
</style>

