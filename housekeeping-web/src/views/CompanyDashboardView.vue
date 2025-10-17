<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">家政公司工作台</h1>
        <p class="dashboard-subtitle">维护服务项目、安排预约并及时处理用户退款</p>
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
      <article class="stat-card primary">
        <p class="stat-label">待执行预约</p>
        <p class="stat-value">{{ companyStats.upcoming }}</p>
        <p class="stat-helper">提前规划人手与物料</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">待处理退款</p>
        <p class="stat-value">{{ companyStats.pendingRefunds }}</p>
        <p class="stat-helper">及时响应守护用户体验</p>
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
              <p>完善服务信息让用户更了解您的优势。当前平均定价 ¥{{ companyStats.avgPrice.toFixed(2) }}</p>
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

        <section v-else-if="activeSection === 'appointments'" class="panel">
          <header class="panel-header">
            <div>
              <h2>预约排班</h2>
              <p>掌握近期预约并同步上门进度，合理安排师傅日程。</p>
            </div>
            <button type="button" class="secondary-button" @click="loadCompanyOrders">刷新预约</button>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务</th>
                  <th>预约时间</th>
                  <th>用户</th>
                  <th>状态</th>
                  <th>进度备注</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in companyOrders" :key="order.id">
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">价格：¥{{ order.price.toFixed(2) }} / {{ order.unit }}</div>
                    <div class="order-subtext">联系方式：{{ order.contact }}</div>
                    <div v-if="order.specialRequest" class="order-subtext highlight">
                      用户需求：{{ order.specialRequest }}
                    </div>
                  </td>
                  <td>{{ formatDateTime(order.scheduledAt) }}</td>
                  <td>{{ order.username }}</td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">
                      {{ statusText(order.status) }}
                    </span>
                  </td>
                  <td>
                    <input
                      v-model="progressNoteEdits[order.id]"
                      type="text"
                      class="progress-input"
                      placeholder="填写最新进度"
                    />
                  </td>
                  <td class="table-actions actions-inline">
                    <button
                      type="button"
                      class="link-button"
                      :disabled="progressSaving[order.id]"
                      @click="saveOrderProgress(order, 'SCHEDULED')"
                    >
                      重置
                    </button>
                    <button
                      type="button"
                      class="link-button"
                      :disabled="progressSaving[order.id]"
                      @click="saveOrderProgress(order, 'IN_PROGRESS')"
                    >
                      服务中
                    </button>
                    <button
                      type="button"
                      class="link-button"
                      :disabled="progressSaving[order.id]"
                      @click="saveOrderProgress(order, 'COMPLETED')"
                    >
                      完成
                    </button>
                  </td>
                </tr>
                <tr v-if="!companyOrders.length">
                  <td colspan="6" class="empty-row">暂无预约记录，用户预约后会自动出现在此处。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <CompanyReviewsPanel
          v-else-if="activeSection === 'reviews'"
          class="panel immersive-panel"
          :loading="reviewsLoading"
          :reviews="companyReviews"
          @refresh="loadCompanyReviews"
        />

        <CompanyMessagingPanel
          v-else-if="activeSection === 'messages'"
          class="panel immersive-panel"
          :conversations="conversationSummaries"
          :loading-conversations="conversationsLoading"
          :active-conversation-id="activeConversationId"
          :messages="activeMessages"
          :loading-messages="messagesLoading"
          :sending="messageSending"
          @refresh-conversations="handleRefreshConversations"
          @refresh-messages="refreshActiveMessages"
          @select-conversation="selectConversation"
          @send-message="handleSendMessage"
        />

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
                  <td>{{ formatDateTime(order.updatedAt) }}</td>
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
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'
import {
  fetchCurrentAccount,
  createCompanyService,
  deleteCompanyService,
  fetchCompanyOrders,
  fetchCompanyRefunds,
  fetchCompanyServices,
  fetchCompanyReviews,
  fetchCompanyConversations,
  fetchCompanyMessages,
  handleCompanyRefund,
  markCompanyConversationRead,
  sendCompanyMessage,
  updateCompanyOrderProgress,
  updateCompanyService,
  type AccountProfileItem,
  type CompanyServicePayload,
  type CompanyConversationItem,
  type CompanyMessageItem,
  type HousekeepServiceItem,
  type ServiceReviewItem,
  type ServiceOrderItem,
  type UpdateOrderProgressPayload,
} from '../services/dashboard'

import CompanyReviewsPanel from '../pages/company/CompanyReviewsPanel.vue'
import CompanyMessagingPanel from '../pages/company/CompanyMessagingPanel.vue'

type SectionKey = 'services' | 'appointments' | 'reviews' | 'messages' | 'refunds'

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
  { key: 'appointments', icon: '📅', label: '预约排班' },
  { key: 'reviews', icon: '✨', label: '服务口碑' },
  { key: 'messages', icon: '💬', label: '客户沟通' },
  { key: 'refunds', icon: '💸', label: '退款审核' },
]

const activeSection = ref<SectionKey>('services')
const services = ref<HousekeepServiceItem[]>([])
const refundOrders = ref<ServiceOrderItem[]>([])
const companyOrders = ref<ServiceOrderItem[]>([])
const serviceFormVisible = ref(false)
const serviceSaving = ref(false)
const editingServiceId = ref<number | null>(null)
const progressNoteEdits = reactive<Record<number, string>>({})
const progressSaving = reactive<Record<number, boolean>>({})
const serviceForm = reactive<CompanyServicePayload>({
  name: '',
  unit: '',
  price: 0,
  contact: '',
  description: '',
})

const serviceSubmitText = computed(() => (editingServiceId.value ? '保存修改' : '新增服务'))

const companyReviews = ref<ServiceReviewItem[]>([])
const reviewsLoading = ref(false)
const conversationSummaries = ref<CompanyConversationItem[]>([])
const conversationsLoading = ref(false)
const messagesByOrder = reactive<Record<number, CompanyMessageItem[]>>({})
const lastMessageTimestamps = reactive<Record<number, string>>({})
const activeConversationId = ref<number | null>(null)
const messagesLoading = ref(false)
const messageSending = ref(false)
const messagePollHandle = ref<number | null>(null)

const activeMessages = computed(() =>
  activeConversationId.value != null ? messagesByOrder[activeConversationId.value] ?? [] : [],
)

const companyStats = computed(() => {
  const totalServices = services.value.length
  const pendingRefunds = refundOrders.value.length
  const avgPrice = totalServices
    ? services.value.reduce((sum, item) => sum + item.price, 0) / totalServices
    : 0
  const balance = account.value?.balance ?? 0
  const upcoming = companyOrders.value.length
  return {
    totalServices,
    pendingRefunds,
    avgPrice,
    balance,
    upcoming,
  }
})

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

const saveOrderProgress = async (order: ServiceOrderItem, status: ServiceOrderItem['status']) => {
  progressSaving[order.id] = true
  try {
    const payload: UpdateOrderProgressPayload = {
      status,
      progressNote: progressNoteEdits[order.id]?.trim() || undefined,
    }
    const updated = await updateCompanyOrderProgress(order.id, payload)
    const index = companyOrders.value.findIndex((item) => item.id === updated.id)
    if (index >= 0) {
      companyOrders.value.splice(index, 1, updated)
    }
    progressNoteEdits[updated.id] = updated.progressNote || ''
    window.alert('预约进度已更新')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '更新失败，请稍后再试')
  } finally {
    progressSaving[order.id] = false
  }
}

const updateConversationSummary = (
  orderId: number,
  transform: (item: CompanyConversationItem) => CompanyConversationItem,
) => {
  const current = conversationSummaries.value
  const index = current.findIndex((item) => item.orderId === orderId)
  if (index === -1) {
    return
  }
  const next = [...current]
  const target = next[index]
  if (!target) {
    return
  }
  next.splice(index, 1, transform({ ...target }))
  conversationSummaries.value = next
}

const loadCompanyReviews = async () => {
  reviewsLoading.value = true
  try {
    companyReviews.value = await fetchCompanyReviews()
  } catch (error) {
    console.error(error)
  } finally {
    reviewsLoading.value = false
  }
}

const loadConversationSummaries = async (): Promise<CompanyConversationItem[]> => {
  conversationsLoading.value = true
  try {
    const items = await fetchCompanyConversations()
    conversationSummaries.value = items
    if (!items.length) {
      activeConversationId.value = null
      stopMessagePolling()
    } else if (
      activeConversationId.value == null ||
      !items.some((item) => item.orderId === activeConversationId.value)
    ) {
      const first = items[0]
      if (first) {
        activeConversationId.value = first.orderId
      }
    }
    return items
  } catch (error) {
    console.error(error)
    return []
  } finally {
    conversationsLoading.value = false
  }
}

const loadMessagesForOrder = async (
  orderId: number,
  options: { since?: string | null; silent?: boolean } = {},
) => {
  if (!options.silent) {
    messagesLoading.value = true
  }
  try {
    const params = options.since ? { since: options.since } : undefined
    const fetched = await fetchCompanyMessages(orderId, params)
    if (fetched.length) {
      const existing = [...(messagesByOrder[orderId] ?? [])]
      const existingIds = new Set(existing.map((item) => item.id))
      const combined = options.since
        ? [...existing, ...fetched.filter((item) => !existingIds.has(item.id))]
        : fetched
      combined.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
      if (combined.length) {
        const lastMessage = combined[combined.length - 1]
        if (lastMessage) {
          messagesByOrder[orderId] = combined
          lastMessageTimestamps[orderId] = lastMessage.createdAt
          updateConversationSummary(orderId, (item) => ({
            ...item,
            lastMessage: lastMessage.content,
            lastMessageAt: lastMessage.createdAt,
            unreadCount: 0,
          }))
        }
      }
    } else if (!messagesByOrder[orderId]) {
      messagesByOrder[orderId] = []
    }
    try {
      await markCompanyConversationRead(orderId)
      updateConversationSummary(orderId, (item) => ({ ...item, unreadCount: 0 }))
    } catch (error) {
      console.error(error)
    }
  } catch (error) {
    console.error(error)
  } finally {
    if (!options.silent) {
      messagesLoading.value = false
    }
  }
}

const selectConversation = async (orderId: number) => {
  activeConversationId.value = orderId
  await loadMessagesForOrder(orderId)
  startMessagePolling(orderId)
}

const refreshActiveMessages = async () => {
  if (activeConversationId.value == null) {
    return
  }
  await loadMessagesForOrder(activeConversationId.value)
}

const handleSendMessage = async (payload: { orderId: number; content: string }) => {
  const trimmed = payload.content.trim()
  if (!trimmed) {
    window.alert('请输入消息内容')
    return
  }
  messageSending.value = true
  try {
    const message = await sendCompanyMessage(payload.orderId, { content: trimmed })
    const existing = [...(messagesByOrder[payload.orderId] ?? [])]
    existing.push(message)
    existing.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
    messagesByOrder[payload.orderId] = existing
    lastMessageTimestamps[payload.orderId] = message.createdAt
    updateConversationSummary(payload.orderId, (item) => ({
      ...item,
      lastMessage: message.content,
      lastMessageAt: message.createdAt,
      unreadCount: 0,
    }))
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '发送失败，请稍后再试')
  } finally {
    messageSending.value = false
  }
}

const stopMessagePolling = () => {
  if (messagePollHandle.value != null) {
    window.clearInterval(messagePollHandle.value)
    messagePollHandle.value = null
  }
}

const startMessagePolling = (orderId: number) => {
  stopMessagePolling()
  messagePollHandle.value = window.setInterval(() => {
    const since = lastMessageTimestamps[orderId]
    if (since) {
      loadMessagesForOrder(orderId, { since, silent: true })
    } else {
      loadMessagesForOrder(orderId, { silent: true })
    }
  }, 10000)
}

const switchSection = async (key: SectionKey) => {
  if (activeSection.value === 'messages' && key !== 'messages') {
    stopMessagePolling()
  }
  activeSection.value = key
  if (key === 'refunds') {
    await loadRefunds()
  }
  if (key === 'appointments') {
    await loadCompanyOrders()
  }
  if (key === 'reviews') {
    await loadCompanyReviews()
  }
  if (key === 'messages') {
    const items = await loadConversationSummaries()
    const target = activeConversationId.value ?? items[0]?.orderId ?? null
    if (target != null) {
      await selectConversation(target)
    } else {
      stopMessagePolling()
    }
  }
}

const handleRefreshConversations = async () => {
  const items = await loadConversationSummaries()
  if (activeSection.value === 'messages') {
    const target = activeConversationId.value ?? items[0]?.orderId ?? null
    if (target != null) {
      await selectConversation(target)
    }
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

const loadCompanyOrders = async () => {
  try {
    const result = await fetchCompanyOrders()
    companyOrders.value = result
    result.forEach((item) => {
      progressNoteEdits[item.id] = item.progressNote || ''
    })
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

const statusText = (status: ServiceOrderItem['status']) => {
  switch (status) {
    case 'SCHEDULED':
      return '待上门'
    case 'IN_PROGRESS':
      return '服务中'
    case 'PENDING':
      return '等待用户安排'
    case 'COMPLETED':
      return '已完成'
    case 'REFUND_REQUESTED':
      return '退款审核中'
    case 'REFUND_APPROVED':
      return '已退款'
    case 'REFUND_REJECTED':
      return '退款被拒'
    default:
      return status
  }
}

const formatDateTime = (value: string) => {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}

onMounted(async () => {
  await Promise.all([loadAccount(), loadServices(), loadRefunds(), loadCompanyOrders()])
})

onUnmounted(() => {
  stopMessagePolling()
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

.panel.immersive-panel {
  padding: clamp(24px, 3vw, 36px);
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

.order-subtext.highlight {
  color: var(--brand-primary);
  font-weight: 600;
}

.progress-input {
  width: 100%;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: var(--brand-radius);
  padding: 8px 10px;
  font-size: 13px;
  background: rgba(248, 250, 255, 0.92);
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

.status-scheduled {
  background: linear-gradient(135deg, #6366f1, #4338ca);
}

.status-in_progress {
  background: linear-gradient(135deg, #14b8a6, #0f766e);
}

.status-pending {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.status-completed {
  background: linear-gradient(135deg, #10b981, #059669);
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
  background: rgba(16, 185, 129, 0.1);
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
  background: rgba(16, 185, 129, 0.06);
}

.table-actions {
  width: 220px;
}

.actions-inline {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
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

