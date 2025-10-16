<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">家政服务大厅</h1>
        <p class="dashboard-subtitle">挑选心仪服务、管理订单与提交评价</p>
      </div>
      <div class="header-actions">
        <span class="welcome">您好，{{ username }}！</span>
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
          <span class="sidebar-icon" aria-hidden="true">{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </aside>

      <main class="content">
        <section v-if="activeSection === 'services'" class="panel">
          <header class="panel-header">
            <h2>可选家政服务</h2>
            <p>点击即可预约服务，费用由家政公司在平台上设置。</p>
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
            <p>查看已预约的服务并处理退款申请。</p>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务</th>
                  <th>价格</th>
                  <th>状态</th>
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
                  </td>
                  <td>¥{{ order.price.toFixed(2) }} / {{ order.unit }}</td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">
                      {{ statusText(order.status) }}
                    </span>
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
                  <td colspan="6" class="empty-row">您还没有订单，先去选择一个服务吧。</td>
                </tr>
              </tbody>
            </table>
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
                  <time>{{ formatDate(item.createdAt) }}</time>
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
  createUserOrder,
  fetchServiceReviews,
  fetchUserOrders,
  fetchUserServices,
  requestUserRefund,
  submitUserReview,
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
const username = computed(() => sessionStorage.getItem(AUTH_ACCOUNT_KEY) ?? '用户')

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

const loadReviews = async (serviceId: number) => {
  try {
    serviceReviews.value = await fetchServiceReviews(serviceId)
  } catch (error) {
    console.error(error)
    serviceReviews.value = []
  }
}

const handleSelectService = async (service: HousekeepServiceItem) => {
  if (!window.confirm(`确认选择服务“${service.name}”？`)) {
    return
  }
  try {
    await createUserOrder({ serviceId: service.id })
    window.alert('已成功预约服务，您可以在“我的订单”中查看进度。')
    await loadOrders()
    activeSection.value = 'orders'
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '预约失败，请稍后再试')
  }
}

const canRequestRefund = (order: ServiceOrderItem) => {
  return order.status === 'PENDING' || order.status === 'REFUND_REJECTED'
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

const formatDate = (value: string) => {
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
  await Promise.all([loadServices(), loadOrders()])
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
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
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
  font-size: 14px;
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
  transition: background-color 0.2s ease;
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
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.sidebar-item:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateX(4px);
}

.sidebar-item.active {
  background: rgba(255, 255, 255, 0.2);
  font-weight: 600;
}

.sidebar-icon {
  font-size: 20px;
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

.panel-header h2 {
  margin: 0;
  font-size: 22px;
  color: #1f2937;
}

.panel-header p {
  margin: 8px 0 0;
  color: #6b7280;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.service-card {
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 20px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.service-title {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.service-company {
  margin: 0;
  font-size: 14px;
  color: #4b5563;
}

.service-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 0;
}

.service-meta dt {
  font-size: 12px;
  color: #6b7280;
}

.service-meta dd {
  margin: 2px 0 0;
  font-size: 14px;
  color: #1f2937;
}

.service-desc {
  margin: 0;
  color: #4b5563;
  font-size: 14px;
  line-height: 1.6;
}

.primary-button {
  align-self: flex-start;
  padding: 10px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.25);
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
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
  vertical-align: top;
}

.table-actions {
  width: 140px;
}

.order-subtext {
  font-size: 13px;
  color: #4b5563;
}

.order-subtext.muted {
  color: #9ca3af;
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
  background: #3b82f6;
}

.status-completed {
  background: #10b981;
}

.status-refund_requested {
  background: #f59e0b;
}

.status-refund_approved {
  background: #059669;
}

.status-refund_rejected {
  background: #ef4444;
}

.link-button {
  background: none;
  border: none;
  padding: 0;
  color: #2563eb;
  cursor: pointer;
}

.link-button:hover {
  text-decoration: underline;
}

.empty-row,
.empty-tip {
  text-align: center;
  color: #6b7280;
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
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  background: #f9fafb;
}

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
}

.review-list {
  border-top: 1px solid #e5e7eb;
  padding-top: 16px;
}

.review-title {
  margin: 0 0 12px;
  font-size: 16px;
  color: #1f2937;
}

.review-item {
  padding: 12px 0;
  border-bottom: 1px dashed #e5e7eb;
}

.review-header {
  display: flex;
  gap: 12px;
  align-items: baseline;
  font-size: 14px;
  color: #1f2937;
}

.review-rating {
  color: #f97316;
  font-weight: 600;
}

.review-content {
  margin: 8px 0 0;
  color: #4b5563;
  line-height: 1.6;
}

@media (max-width: 960px) {
  .dashboard-main {
    flex-direction: column;
  }

  .sidebar {
    flex-direction: row;
    width: 100%;
    overflow-x: auto;
  }

  .content {
    padding: 20px;
  }
}
</style>
