<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">家政服务大厅</h1>
        <p class="dashboard-subtitle">挑选心仪服务、管理订单与提交评价</p>
      </div>
      <div class="header-actions">
        <span class="welcome">您好，{{ username }}！</span>
        <span class="wallet">钱包余额：¥{{ balanceText }}</span>
        <span class="loyalty">积分：{{ loyaltyText }}</span>
        <button type="button" class="logout-button" @click="logout">退出登录</button>
      </div>
    </header>

    <section class="stats-grid" aria-label="关键数据概览">
      <article class="stat-card accent">
        <p class="stat-label">账户积分</p>
        <p class="stat-value">{{ loyaltyText }}</p>
        <p class="stat-helper">完成服务会自动累积积分</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">待上门服务</p>
        <p class="stat-value">{{ orderStats.awaiting }}</p>
        <p class="stat-helper">包括刚预约与等待师傅上门</p>
      </article>
      <article class="stat-card success">
        <p class="stat-label">服务进行中</p>
        <p class="stat-value">{{ orderStats.inProgress }}</p>
        <p class="stat-helper">实时同步家政公司进度</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">退款处理中</p>
        <p class="stat-value">{{ orderStats.refunding }}</p>
        <p class="stat-helper">等待家政公司或管理员处理</p>
      </article>
    </section>

    <transition name="fade">
      <div v-if="bookingDialogVisible" class="dialog-backdrop" @click.self="closeBooking">
        <form class="dialog-card" @submit.prevent="submitBooking">
          <header class="dialog-header">
            <h2>预约 {{ bookingForm.service?.name }}</h2>
            <p>
              请选择上门时间并填写特殊需求，平台会将信息同步给 {{ bookingForm.service?.companyName }}。
            </p>
          </header>
          <div class="dialog-body">
            <label class="dialog-field">
              <span>预约时间</span>
              <input v-model="bookingForm.scheduledAt" type="datetime-local" required />
            </label>
            <label class="dialog-field">
              <span>特殊需求（选填）</span>
              <textarea
                v-model="bookingForm.specialRequest"
                rows="3"
                maxlength="500"
                placeholder="例如：提前电话联系、携带清洁工具等"
              ></textarea>
            </label>
          </div>
          <footer class="dialog-footer">
            <button type="button" class="secondary-button" @click="closeBooking">取消</button>
            <button type="submit" class="primary-button">确认预约</button>
          </footer>
        </form>
      </div>
    </transition>

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
          <span class="sidebar-icon" aria-hidden="true">{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </aside>

      <main class="content">
        <section v-if="activeSection === 'services'" class="panel">
          <header class="panel-header">
            <h2>可选家政服务</h2>
            <p>当前可预约 {{ services.length }} 项，点击服务卡片即可填写预约时间与需求。</p>
          </header>
          <div class="service-grid">
            <article v-for="service in services" :key="service.id" class="service-card">
              <h3 class="service-title">{{ service.name }}</h3>
              <p class="service-company">提供方：{{ service.companyName }}</p>
              <dl class="service-meta">
                <div>
                  <dt>计价单位</dt>
                  <dd>{{ service.unit }}</dd>
                </div>
                <div>
                  <dt>服务价格</dt>
                  <dd>¥{{ service.price.toFixed(2) }}</dd>
                </div>
                <div>
                  <dt>联系方式</dt>
                  <dd>{{ service.contact }}</dd>
                </div>
              </dl>
              <p v-if="service.description" class="service-desc">{{ service.description }}</p>
              <button type="button" class="primary-button" @click="handleSelectService(service)">
                选择该服务
              </button>
            </article>
            <p v-if="!services.length" class="empty-tip">暂无家政公司发布服务，稍后再来看看吧。</p>
          </div>
        </section>

        <section v-else-if="activeSection === 'orders'" class="panel">
          <header class="panel-header">
            <h2>我的服务订单</h2>
            <p>共 {{ orderStats.total }} 单 · 已完成 {{ orderStats.completed }} 单。</p>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务</th>
                  <th>预约时间</th>
                  <th>状态</th>
                  <th>进度与需求</th>
                  <th>退款申请</th>
                  <th>处理信息</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in orders" :key="order.id">
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">{{ order.companyName }} · {{ order.contact }}</div>
                    <div class="order-meta">
                      <span>¥{{ order.price.toFixed(2) }} / {{ order.unit }}</span>
                      <span>积分 +{{ order.loyaltyPoints }}</span>
                    </div>
                  </td>
                  <td>
                    <div class="order-subtext">{{ formatDateTime(order.scheduledAt) }}</div>
                  </td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">
                      {{ statusText(order.status) }}
                    </span>
                  </td>
                  <td>
                    <div class="order-subtext">{{ order.progressNote || '等待家政公司更新' }}</div>
                    <div v-if="order.specialRequest" class="order-subtext highlight">
                      用户需求：{{ order.specialRequest }}
                    </div>
                  </td>
                  <td>
                    <div v-if="order.refundReason" class="order-subtext">{{ order.refundReason }}</div>
                    <div v-else class="order-subtext muted">—</div>
                  </td>
                  <td>
                    <div v-if="order.refundResponse" class="order-subtext">
                      {{ order.refundResponse }}
                      <template v-if="order.handledBy">（{{ order.handledBy }}）</template>
                    </div>
                    <div v-else class="order-subtext muted">—</div>
                  </td>
                  <td class="table-actions">
                    <button
                      v-if="canRequestRefund(order)"
                      type="button"
                      class="link-button"
                      @click="handleRequestRefund(order)"
                    >
                      申请退款
                    </button>
                    <span v-else class="order-subtext muted">无可用操作</span>
                  </td>
                </tr>
                <tr v-if="!orders.length">
                  <td colspan="7" class="empty-row">您还没有订单，先去选择一个服务吧。</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="upcomingOrders.length" class="schedule-timeline">
            <h3>我的预约日程</h3>
            <ul>
              <li v-for="item in upcomingOrders" :key="item.id">
                <div>
                  <strong>{{ formatDateTime(item.scheduledAt) }}</strong>
                  <span> · {{ item.serviceName }}（{{ item.companyName }}）</span>
                </div>
                <p>{{ item.progressNote || '等待服务开始' }}</p>
              </li>
            </ul>
          </div>
        </section>

        <section v-else class="panel">
          <header class="panel-header">
            <h2>提交服务评价</h2>
            <p>感谢分享真实感受，平台会及时反馈给家政公司。</p>
          </header>
          <form class="form-grid" @submit.prevent="handleSubmitReview">
            <div class="form-field">
              <label for="review-service">评价的服务</label>
              <select id="review-service" v-model="reviewForm.serviceId">
                <option disabled value="">请选择服务</option>
                <option v-for="service in reviewableServices" :key="service.id" :value="service.id">
                  {{ service.name }}（{{ service.companyName }}）
                </option>
              </select>
            </div>
            <div class="form-field">
              <label for="review-rating">评分（1-5分）</label>
              <input
                id="review-rating"
                v-model.number="reviewForm.rating"
                type="number"
                min="1"
                max="5"
              />
            </div>
            <div class="form-field form-field-full">
              <label for="review-content">评价内容</label>
              <textarea
                id="review-content"
                v-model="reviewForm.content"
                rows="4"
                placeholder="描述您的服务体验"
              ></textarea>
            </div>
            <div class="form-actions">
              <button type="submit" class="primary-button" :disabled="reviewSubmitting">
                {{ reviewSubmitting ? '提交中…' : '提交评价' }}
              </button>
            </div>
          </form>

          <div v-if="serviceReviews.length" class="review-list">
            <h3 class="review-title">近期评价</h3>
            <ul>
              <li v-for="item in serviceReviews" :key="item.id" class="review-item">
                <div class="review-header">
                  <strong>{{ item.username }}</strong>
                  <span class="review-rating">{{ item.rating }} 分</span>
                  <time>{{ formatDateTime(item.createdAt) }}</time>
                </div>
                <p class="review-content">{{ item.content }}</p>
              </li>
            </ul>
          </div>
          <p v-else class="empty-tip">选择服务后可查看已发布的评价。</p>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'
import {
  fetchCurrentAccount,
  createUserOrder,
  fetchServiceReviews,
  fetchUserOrders,
  fetchUserServices,
  requestUserRefund,
  submitUserReview,
  type AccountProfileItem,
  type HousekeepServiceItem,
  type ServiceOrderItem,
  type ServiceReviewItem,
} from '../services/dashboard'

type SectionKey = 'services' | 'orders' | 'reviews'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

const router = useRouter()
const account = ref<AccountProfileItem | null>(null)
const username = computed(
  () => account.value?.username ?? sessionStorage.getItem(AUTH_ACCOUNT_KEY) ?? '用户',
)
const balanceText = computed(() => (account.value ? account.value.balance.toFixed(2) : '0.00'))
const loyaltyText = computed(() => (account.value ? account.value.loyaltyPoints.toString() : '0'))

const sections: SectionMeta[] = [
  { key: 'services', icon: '🧹', label: '选择服务' },
  { key: 'orders', icon: '📋', label: '我的订单' },
  { key: 'reviews', icon: '⭐', label: '评价服务' },
]

const activeSection = ref<SectionKey>('services')
const services = ref<HousekeepServiceItem[]>([])
const orders = ref<ServiceOrderItem[]>([])
const reviewForm = reactive<{ serviceId: number | ''; rating: number; content: string }>({
  serviceId: '',
  rating: 5,
  content: '',
})
const reviewSubmitting = ref(false)
const serviceReviews = ref<ServiceReviewItem[]>([])

const orderStats = computed(() => {
  const total = orders.value.length
  const awaiting = orders.value.filter((order) => order.status === 'SCHEDULED' || order.status === 'PENDING').length
  const inProgress = orders.value.filter((order) => order.status === 'IN_PROGRESS').length
  const refunding = orders.value.filter((order) => order.status === 'REFUND_REQUESTED').length
  const completed = orders.value.filter((order) => order.status === 'COMPLETED').length
  return {
    total,
    awaiting,
    inProgress,
    refunding,
    completed,
  }
})

const upcomingOrders = computed(() => {
  return orders.value
    .filter((order) => ['SCHEDULED', 'PENDING', 'IN_PROGRESS'].includes(order.status))
    .slice()
    .sort((a, b) => new Date(a.scheduledAt).getTime() - new Date(b.scheduledAt).getTime())
    .slice(0, 5)
})

const reviewableServices = computed(() => {
  const uniqueMap = new Map<number, { id: number; name: string; companyName: string }>()
  orders.value.forEach((order) => {
    if (!uniqueMap.has(order.serviceId)) {
      uniqueMap.set(order.serviceId, {
        id: order.serviceId,
        name: order.serviceName,
        companyName: order.companyName,
      })
    }
  })
  return Array.from(uniqueMap.values())
})

const switchSection = (key: SectionKey) => {
  activeSection.value = key
  if (key === 'orders') {
    loadOrders()
    loadAccount()
  }
}

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const loadServices = async () => {
  try {
    services.value = await fetchUserServices()
  } catch (error) {
    console.error(error)
  }
}

const loadOrders = async () => {
  try {
    orders.value = await fetchUserOrders()
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

const loadReviews = async (serviceId: number) => {
  try {
    serviceReviews.value = await fetchServiceReviews(serviceId)
  } catch (error) {
    console.error(error)
    serviceReviews.value = []
  }
}

const bookingDialogVisible = ref(false)
const bookingForm = reactive<{ service: HousekeepServiceItem | null; scheduledAt: string; specialRequest: string }>(
  {
    service: null,
    scheduledAt: '',
    specialRequest: '',
  },
)

const toLocalInputValue = (date: Date) => {
  const offset = date.getTimezoneOffset()
  const local = new Date(date.getTime() - offset * 60_000)
  return local.toISOString().slice(0, 16)
}

const handleSelectService = (service: HousekeepServiceItem) => {
  bookingForm.service = service
  bookingForm.scheduledAt = toLocalInputValue(new Date(Date.now() + 2 * 60 * 60 * 1000))
  bookingForm.specialRequest = ''
  bookingDialogVisible.value = true
}

const closeBooking = () => {
  bookingDialogVisible.value = false
}

const submitBooking = async () => {
  if (!bookingForm.service) {
    return
  }
  const scheduledDate = new Date(bookingForm.scheduledAt)
  if (Number.isNaN(scheduledDate.getTime())) {
    window.alert('请选择有效的预约时间')
    return
  }
  try {
    await createUserOrder({
      serviceId: bookingForm.service.id,
      scheduledAt: scheduledDate.toISOString(),
      specialRequest: bookingForm.specialRequest.trim() || undefined,
    })
    window.alert('已成功预约服务，您可以在“我的订单”中查看进度。')
    bookingDialogVisible.value = false
    await Promise.all([loadOrders(), loadAccount()])
    activeSection.value = 'orders'
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '预约失败，请稍后再试')
  }
}

const canRequestRefund = (order: ServiceOrderItem) => {
  return (
    order.status === 'PENDING' ||
    order.status === 'SCHEDULED' ||
    order.status === 'IN_PROGRESS' ||
    order.status === 'REFUND_REJECTED'
  )
}

const handleRequestRefund = async (order: ServiceOrderItem) => {
  const reason = window.prompt('请输入退款原因：', order.refundReason ?? '')
  if (!reason) return
  try {
    await requestUserRefund(order.id, { reason })
    window.alert('退款申请已提交，等待家政公司或管理员处理。')
    await loadOrders()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '提交退款申请失败')
  }
}

const handleSubmitReview = async () => {
  const serviceIdNumber = Number(reviewForm.serviceId)
  if (!Number.isFinite(serviceIdNumber) || serviceIdNumber <= 0) {
    window.alert('请选择需要评价的服务')
    return
  }
  if (!reviewForm.content.trim()) {
    window.alert('请填写评价内容')
    return
  }
  if (reviewForm.rating < 1 || reviewForm.rating > 5) {
    window.alert('评分需在1-5之间')
    return
  }
  reviewSubmitting.value = true
  try {
    await submitUserReview({
      serviceId: serviceIdNumber,
      rating: reviewForm.rating,
      content: reviewForm.content.trim(),
    })
    window.alert('感谢您的评价！')
    reviewForm.content = ''
    await loadReviews(serviceIdNumber)
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '提交评价失败')
  } finally {
    reviewSubmitting.value = false
  }
}

const statusText = (status: ServiceOrderItem['status']) => {
  switch (status) {
    case 'SCHEDULED':
      return '待上门'
    case 'IN_PROGRESS':
      return '服务中'
    case 'PENDING':
      return '等待服务'
    case 'COMPLETED':
      return '已完成'
    case 'REFUND_REQUESTED':
      return '退款审核中'
    case 'REFUND_APPROVED':
      return '退款成功'
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

watch(
  () => reviewForm.serviceId,
  async (serviceId) => {
    const id = Number(serviceId)
    if (!Number.isNaN(id) && id > 0) {
      await loadReviews(id)
    } else {
      serviceReviews.value = []
    }
  },
)

onMounted(async () => {
  await Promise.all([loadAccount(), loadServices(), loadOrders()])
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
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08) 0%, rgba(15, 118, 110, 0.04) 52%, rgba(124, 58, 237, 0.06) 100%);
  border-radius: 40px 40px 0 0;
  z-index: -1;
}

.dashboard-header {
  position: relative;
  border-radius: calc(var(--brand-radius) + 12px);
  padding: 28px clamp(20px, 4vw, 36px);
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.95) 0%, rgba(124, 58, 237, 0.85) 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: 0 28px 48px rgba(37, 99, 235, 0.25);
  overflow: hidden;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(600px circle at 90% 10%, rgba(255, 255, 255, 0.18), transparent 60%);
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
  color: #facc15;
}

.logout-button {
  padding: 8px 18px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(15, 23, 42, 0.25);
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.logout-button:hover {
  transform: translateY(-1px);
  background: rgba(15, 23, 42, 0.4);
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
  background: linear-gradient(150deg, rgba(59, 130, 246, 0.12), rgba(59, 130, 246, 0.04));
  border-color: rgba(59, 130, 246, 0.2);
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
  color: var(--brand-primary);
  background: rgba(37, 99, 235, 0.08);
  transform: translateX(4px);
}

.sidebar-item.active {
  color: var(--brand-primary);
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.18), rgba(37, 99, 235, 0.08));
  box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.2);
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

.primary-button {
  align-self: flex-start;
  padding: 10px 18px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, var(--brand-primary) 0%, var(--brand-primary-dark) 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: var(--brand-shadow-soft);
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.service-card {
  border-radius: calc(var(--brand-radius) + 2px);
  padding: 20px;
  background: rgba(248, 250, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 20px 36px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.service-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 28px 50px rgba(37, 99, 235, 0.18);
}

.service-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}

.service-company {
  margin: 0;
  font-size: 14px;
  color: var(--brand-text-muted);
}

.service-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 0;
}

.service-meta dt {
  font-size: 12px;
  color: var(--brand-text-muted);
}

.service-meta dd {
  margin: 2px 0 0;
  font-size: 14px;
  color: var(--brand-text);
}

.service-desc {
  margin: 0;
  color: var(--brand-text-muted);
  line-height: 1.6;
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
  background: rgba(37, 99, 235, 0.08);
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
  background: rgba(37, 99, 235, 0.05);
}

.table-actions {
  width: 150px;
}

.order-subtext {
  font-size: 13px;
  color: var(--brand-text-muted);
}

.order-subtext.muted {
  color: rgba(148, 163, 184, 0.9);
}

.order-subtext.highlight {
  color: var(--brand-primary);
  font-weight: 600;
}

.order-meta {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  font-size: 13px;
  color: var(--brand-primary);
  flex-wrap: wrap;
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

.status-pending {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.status-scheduled {
  background: linear-gradient(135deg, #6366f1, #4338ca);
}

.status-in_progress {
  background: linear-gradient(135deg, #14b8a6, #0f766e);
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

.empty-tip,
.empty-row {
  text-align: center;
  color: var(--brand-text-muted);
  padding: 16px 0;
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
  border-color: var(--brand-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.18);
}

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  z-index: 20;
}

.dialog-card {
  width: min(520px, 100%);
  background: rgba(255, 255, 255, 0.96);
  border-radius: calc(var(--brand-radius) + 4px);
  box-shadow: 0 32px 60px rgba(15, 23, 42, 0.35);
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 28px;
}

.dialog-header h2 {
  margin: 0 0 4px;
}

.dialog-header p {
  margin: 0;
  color: var(--brand-text-muted);
  line-height: 1.6;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
  color: var(--brand-text);
}

.dialog-field input,
.dialog-field textarea {
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: var(--brand-radius);
  padding: 10px 12px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.9);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.schedule-timeline {
  margin-top: 24px;
  padding: 20px;
  border-radius: calc(var(--brand-radius) + 4px);
  background: rgba(37, 99, 235, 0.05);
}

.schedule-timeline h3 {
  margin: 0 0 12px;
}

.schedule-timeline ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 12px;
}

.schedule-timeline li {
  padding: 12px 16px;
  border-radius: var(--brand-radius);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.12);
}

.schedule-timeline li p {
  margin: 8px 0 0;
  color: var(--brand-text-muted);
}

.header-actions .loyalty {
  color: var(--brand-primary);
  font-weight: 600;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.review-list {
  border-top: 1px solid rgba(148, 163, 184, 0.2);
  padding-top: 16px;
  margin-top: 24px;
}

.review-title {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 700;
}

.review-item {
  padding: 12px 0;
  border-bottom: 1px dashed rgba(148, 163, 184, 0.2);
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  gap: 12px;
  align-items: baseline;
  font-size: 14px;
}

.review-rating {
  color: var(--brand-warning);
  font-weight: 700;
}

.review-content {
  margin: 8px 0 0;
  color: var(--brand-text-muted);
  line-height: 1.6;
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

