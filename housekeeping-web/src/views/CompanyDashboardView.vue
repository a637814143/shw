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
  await Promise.all([loadAccount(), loadServices()])
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fb;
}

.dashboard-header {
  padding: 24px 32px;
  background: #fff;
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #1e3a5f;
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

.wallet {
  font-weight: 600;
  color: #2563eb;
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
  background: #1e3a5f;
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
  background: rgba(255, 255, 255, 0.1);
}

.sidebar-item.active {
  background: rgba(255, 255, 255, 0.2);
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

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.primary-button {
  padding: 10px 18px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  cursor: pointer;
}

.secondary-button {
  padding: 10px 18px;
  border-radius: 10px;
  border: 1px solid #d1d5db;
  background: #fff;
  cursor: pointer;
}

.form-card {
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 20px;
  background: #f9fafb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
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
.form-field textarea {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 12px;
  background: #fff;
}

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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
  vertical-align: top;
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
