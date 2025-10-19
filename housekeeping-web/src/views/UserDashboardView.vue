<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">家政服务体验中心</h1>
        <p class="dashboard-subtitle">精选内容、智能预约、实时沟通，沉浸式高级体验。</p>
      </div>
      <div class="header-actions">
        <template v-if="avatarSrc">
          <img :src="avatarSrc" alt="账号头像" class="account-avatar" />
        </template>
        <span v-else class="account-badge" aria-hidden="true">{{ displayInitials }}</span>
        <span class="welcome">您好，{{ displayName }}！</span>
        <span class="wallet">钱包余额：¥{{ balanceText }}</span>
        <span class="loyalty">积分：{{ loyaltyText }}</span>
        <button type="button" class="logout-button" @click="logout">退出登录</button>
      </div>
    </header>

    <section class="stats-grid" aria-label="账户概览">
      <article class="stat-card accent">
        <p class="stat-label">积分余额</p>
        <p class="stat-value">{{ loyaltyText }}</p>
        <p class="stat-helper">可在钱包中兑换余额（5:1）</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">待服务订单</p>
        <p class="stat-value">{{ orderStats.awaiting }}</p>
        <p class="stat-helper">等待家政人员上门</p>
      </article>
      <article class="stat-card glass">
        <p class="stat-label">收藏服务</p>
        <p class="stat-value">{{ favoritesCount }}</p>
        <p class="stat-helper">点击服务卡片右上角可收藏</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">系统公告</p>
        <p class="stat-value">{{ announcements.length }}</p>
        <p class="stat-helper">关注最新服务与活动</p>
      </article>
    </section>

    <transition name="fade">
      <div v-if="bookingDialogVisible" class="dialog-backdrop" @click.self="closeBooking">
        <form class="dialog-card" @submit.prevent="submitBooking">
          <header class="dialog-header">
            <h2>预约 {{ bookingForm.service?.name }}</h2>
            <p>请选择上门时间并填写特殊需求，平台会将信息同步给 {{ bookingForm.service?.companyName }}。</p>
          </header>
          <div class="dialog-body">
            <label class="dialog-field">
              <span>预约时间</span>
              <input v-model="bookingForm.scheduledAt" type="datetime-local" required />
            </label>
            <label class="dialog-field">
              <span>上门地址</span>
              <input
                v-model="bookingForm.serviceAddress"
                type="text"
                maxlength="255"
                required
                placeholder="请填写具体的上门地点"
              />
            </label>
            <label class="dialog-field">
              <span>到访联系方式（选填）</span>
              <input
                v-model="bookingForm.contactPhone"
                type="text"
                maxlength="100"
                placeholder="手机或其他即时联系方式"
              />
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

<transition name="fade">
  <div v-if="paymentDialogVisible" class="dialog-backdrop" @click.self="closePaymentDialog">
    <div class="dialog-card payment-card">
      <header class="dialog-header">
        <h2>扫描二维码完成支付</h2>
        <p>请使用手机扫描下方二维码，在支付页面确认后系统将自动创建订单。</p>
      </header>
      <div class="payment-body">
        <img v-if="paymentQrSrc" :src="paymentQrSrc" alt="支付二维码" class="payment-qr" />
        <div v-else class="payment-qr placeholder">二维码生成中…</div>
        <p class="payment-summary">
          服务：{{ paymentServiceName || '—' }}
          <span v-if="paymentCompanyName"> · 提供方：{{ paymentCompanyName }}</span>
        </p>
        <p v-if="paymentAmount !== null" class="payment-summary">金额：¥{{ paymentAmount.toFixed(2) }}</p>
        <p v-if="paymentSession?.expiresAt" class="payment-tip">
          二维码有效期至：{{ formatDateTime(paymentSession.expiresAt) }}
        </p>
        <p class="payment-tip">
          二维码链接：
          <template v-if="paymentQrLink">
            <a :href="paymentQrLink" target="_blank" rel="noopener">{{ paymentQrLink }}</a>
          </template>
          <template v-else>—</template>
        </p>
        <p v-if="paymentStatus === 'checking'" class="payment-status checking">正在获取支付结果，请稍候…</p>
        <p v-else-if="paymentStatus === 'success'" class="payment-status success">{{ paymentMessage }}</p>
        <p v-else-if="paymentStatus === 'failed'" class="payment-status error">{{ paymentError }}</p>
        <p v-else class="payment-status">请扫码并在手机上完成支付确认。</p>
      </div>
      <footer class="dialog-footer">
        <button type="button" class="secondary-button" :disabled="paymentChecking" @click="closePaymentDialog">
          {{ paymentStatus === 'success' ? '关闭' : '取消' }}
        </button>
        <button
          v-if="paymentStatus !== 'success'"
          type="button"
          class="primary-button"
          :disabled="paymentChecking"
          @click="checkPaymentResult"
        >
          {{ paymentChecking ? '查询中…' : '已完成支付，查询结果' }}
        </button>
        <button v-else type="button" class="primary-button" @click="closePaymentDialog">返回平台</button>
      </footer>
    </div>
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
        <section v-if="activeSection === 'profile'" class="panel profile-panel">
          <header class="panel-header">
            <div>
              <h2>个人资料</h2>
              <p>维护联系方式与地址信息，预约时即可一键填写。</p>
            </div>
          </header>
          <AccountProfileEditor :account="account" @updated="handleProfileUpdated" />
        </section>

        <section v-else-if="activeSection === 'discover'" class="panel immersive-panel">
          <header class="panel-header">
            <div>
              <h2>精选推荐</h2>
              <p>浏览轮播专题、热门贴士与系统公告，打造 Apple 级视觉体验。</p>
            </div>
            <button type="button" class="ghost-button" @click="refreshDiscover" :disabled="discoverLoading">
              {{ discoverLoading ? '刷新中…' : '刷新内容' }}
            </button>
          </header>
          <div v-if="discoverLoading" class="loading-state">正在同步精选内容…</div>
          <div v-else class="discover-grid">
            <section class="carousel">
              <header class="section-title">
                <h3>主题轮播</h3>
                <p>点击图片可查看详情或跳转服务。</p>
              </header>
              <div class="carousel-track" role="region" aria-label="精选轮播">
                <article v-for="item in carousels" :key="item.id" class="carousel-card">
                  <div class="carousel-media" :style="{ backgroundImage: `url(${item.imageUrl})` }"></div>
                  <div class="carousel-body">
                    <h4>{{ item.title }}</h4>
                    <p>{{ item.serviceLink ? item.serviceLink : '精选家政专题' }}</p>
                  </div>
                </article>
                <p v-if="!carousels.length" class="empty-tip">暂无轮播内容，稍后再来看看。</p>
              </div>
            </section>

            <section class="tips">
              <header class="section-title">
                <h3>居家贴士</h3>
                <p>精选生活小窍门，守护家庭温度。</p>
              </header>
              <ul class="tip-list">
                <li v-for="item in tips" :key="item.id">
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.content }}</p>
                </li>
                <li v-if="!tips.length" class="empty-tip">暂无贴士内容。</li>
              </ul>
            </section>

            <section class="announcements">
              <header class="section-title">
                <h3>系统公告</h3>
                <p>及时获取平台政策与活动提醒。</p>
              </header>
              <ul class="announcement-list">
                <li v-for="item in announcements" :key="item.id">
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.content }}</p>
                </li>
                <li v-if="!announcements.length" class="empty-tip">暂无公告。</li>
              </ul>
            </section>
          </div>
        </section>

        <section v-else-if="activeSection === 'services'" class="panel">
          <header class="panel-header">
            <div>
              <h2>可选家政服务</h2>
              <p>当前可预约 {{ services.length }} 项，点击服务卡片即可填写预约时间与需求。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadServices">刷新列表</button>
          </header>
          <div class="service-grid">
            <article v-for="service in services" :key="service.id" class="service-card">
              <button
                type="button"
                class="favorite-toggle"
                :class="{ active: favoriteIdSet.has(service.id) }"
                @click="toggleFavorite(service)"
              >
                {{ favoriteIdSet.has(service.id) ? '♥' : '♡' }}
              </button>
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
              <button type="button" class="primary-button" @click="handleSelectService(service)">预约服务</button>
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
                    <div class="order-subtext">上门地址：{{ order.serviceAddress || '—' }}</div>
                    <div class="order-subtext">到访联系电话：{{ order.contactPhone || '—' }}</div>
                  </td>
                  <td>
                    <div class="order-subtext">{{ formatDateTime(order.scheduledAt) }}</div>
                  </td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">{{ statusText(order.status) }}</span>
                    <div v-if="order.assignedWorker" class="order-subtext">
                      人员：{{ order.assignedWorker }}<span v-if="order.workerContact">（{{ order.workerContact }}）</span>
                    </div>
                  </td>
                  <td>
                    <div class="order-subtext">{{ order.progressNote || '等待家政公司更新' }}</div>
                    <div v-if="order.specialRequest" class="order-subtext highlight">需求：{{ order.specialRequest }}</div>
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
                      type="button"
                      class="link-button"
                      @click="jumpToMessages(order.id)"
                    >
                      去沟通
                    </button>
                    <button
                      v-if="canRequestRefund(order)"
                      type="button"
                      class="link-button"
                      @click="handleRequestRefund(order)"
                    >
                      申请退款
                    </button>
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

        <section v-else-if="activeSection === 'wallet'" class="panel">
          <header class="panel-header">
            <div>
              <h2>钱包充值与积分兑换</h2>
              <p>支持余额充值与积分兑换，兑换比例 5:1。</p>
            </div>
          </header>
          <div class="wallet-grid">
            <form class="wallet-card" @submit.prevent="submitRecharge">
              <h3>快捷充值</h3>
              <p>充值金额将实时到账，用于预约服务。</p>
              <input v-model.number="walletForm.amount" type="number" min="0.01" step="0.01" placeholder="充值金额" />
              <button type="submit" class="primary-button" :disabled="walletSaving">
                {{ walletSaving ? '充值中…' : '确认充值' }}
              </button>
            </form>
            <form class="wallet-card" @submit.prevent="submitExchange">
              <h3>积分兑换</h3>
              <p>每 5 积分可兑换 1 元余额，积分需为 5 的倍数。</p>
              <input v-model.number="exchangeForm.points" type="number" min="5" step="5" placeholder="兑换积分" />
              <button type="submit" class="primary-button" :disabled="exchangeSaving">
                {{ exchangeSaving ? '兑换中…' : '确认兑换' }}
              </button>
            </form>
          </div>
        </section>

        <UserMessagingPanel
          v-else-if="activeSection === 'messages'"
          class="panel immersive-panel"
          :conversations="conversations"
          :active-conversation-id="activeConversationId"
          :messages="messages"
          :loading-conversations="conversationsLoading"
          :loading-messages="messagesLoading"
          :sending="messageSending"
          @refresh-conversations="loadConversations"
          @refresh-messages="refreshMessages"
          @select-conversation="selectConversation"
          @send-message="handleSendMessage"
        />

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
              <input id="review-rating" v-model.number="reviewForm.rating" type="number" min="1" max="5" />
            </div>
            <div class="form-field form-field-full">
              <label for="review-content">评价内容</label>
              <textarea id="review-content" v-model="reviewForm.content" rows="4" placeholder="描述您的服务体验"></textarea>
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
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'
import {
  addUserFavorite,
  checkQrPaymentStatus,
  createQrPaymentSession,
  fetchCurrentAccount,
  createUserOrder,
  exchangeUserPoints,
  fetchDashboardAnnouncements,
  fetchDashboardCarousels,
  fetchDashboardTips,
  fetchServiceReviews,
  fetchUserConversations,
  fetchUserFavorites,
  fetchUserMessages,
  fetchUserOrders,
  fetchUserServices,
  removeUserFavorite,
  markUserConversationRead,
  rechargeUserWallet,
  requestUserRefund,
  sendUserMessage,
  submitUserReview,
  type AccountProfileItem,
  type CreateOrderPayload,
  type CreatePaymentSessionPayload,
  type CompanyMessageItem,
  type CompanyMessagePayload,
  type DashboardAnnouncementItem,
  type DashboardCarouselItem,
  type PaymentGatewayCheckResult,
  type PaymentSessionInfo,
  type DashboardTipItem,
  type HousekeepServiceItem,
  type ServiceFavoriteItem,
  type ServiceOrderItem,
  type ServiceReviewItem,
  type UserConversationItem,
} from '../services/dashboard'

import { createObjectUrlFromDataUrl, revokeObjectUrl } from '../utils/image'

import UserMessagingPanel from '../pages/user/UserMessagingPanel.vue'
import AccountProfileEditor from '../components/AccountProfileEditor.vue'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

type SectionKey = 'profile' | 'discover' | 'services' | 'orders' | 'wallet' | 'messages' | 'reviews'

type PaymentStatus = 'idle' | 'checking' | 'success' | 'failed'

const router = useRouter()
const account = ref<AccountProfileItem | null>(null)

const FALLBACK_AVATAR =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=='

const services = ref<HousekeepServiceItem[]>([])
const orders = ref<ServiceOrderItem[]>([])
const serviceReviews = ref<ServiceReviewItem[]>([])
const carousels = ref<DashboardCarouselItem[]>([])
const tips = ref<DashboardTipItem[]>([])
const announcements = ref<DashboardAnnouncementItem[]>([])
const favorites = ref<ServiceFavoriteItem[]>([])
const conversations = ref<UserConversationItem[]>([])
const messages = ref<CompanyMessageItem[]>([])
const conversationsLoading = ref(false)
const messagesLoading = ref(false)
const messageSending = ref(false)

const activeConversationId = ref<number | null>(null)

const bookingDialogVisible = ref(false)
const bookingForm = reactive<{
  service: HousekeepServiceItem | null
  scheduledAt: string
  serviceAddress: string
  contactPhone: string
  specialRequest: string
}>({
  service: null,
  scheduledAt: '',
  serviceAddress: '',
  contactPhone: '',
  specialRequest: '',
})

const paymentDialogVisible = ref(false)
const paymentChecking = ref(false)
const paymentStatus = ref<PaymentStatus>('idle')
const paymentMessage = ref('')
const paymentError = ref('')
const pendingOrderPayload = ref<CreateOrderPayload | null>(null)
const paymentServiceName = ref('')
const paymentCompanyName = ref('')
const paymentAmount = ref<number | null>(null)
const paymentSession = ref<PaymentSessionInfo | null>(null)

const reviewForm = reactive<{ serviceId: number | ''; rating: number; content: string }>({
  serviceId: '',
  rating: 5,
  content: '',
})
const reviewSubmitting = ref(false)

const walletForm = reactive<{ amount: number | null }>({ amount: null })
const walletSaving = ref(false)
const exchangeForm = reactive<{ points: number | null }>({ points: null })
const exchangeSaving = ref(false)

const discoverLoading = ref(false)

const sections: SectionMeta[] = [
  { key: 'profile', icon: '👤', label: '个人资料' },
  { key: 'discover', icon: '🌟', label: '精选推荐' },
  { key: 'services', icon: '🧹', label: '选择服务' },
  { key: 'orders', icon: '📋', label: '我的订单' },
  { key: 'wallet', icon: '💳', label: '充值/兑换' },
  { key: 'messages', icon: '💬', label: '在线沟通' },
  { key: 'reviews', icon: '⭐', label: '评价服务' },
]

const activeSection = ref<SectionKey>('discover')

const displayName = computed(
  () =>
    account.value?.displayName ||
    account.value?.username ||
    sessionStorage.getItem(AUTH_ACCOUNT_KEY) ||
    '用户',
)
const balanceText = computed(() => (account.value ? account.value.balance.toFixed(2) : '0.00'))
const loyaltyText = computed(() => (account.value ? account.value.loyaltyPoints.toString() : '0'))

const avatarSrc = ref<string>('')
let lastAvatarObjectUrl: string | null = null

const updateAvatarSrc = (dataUrl: string) => {
  const nextUrl = createObjectUrlFromDataUrl(dataUrl || FALLBACK_AVATAR)
  if (lastAvatarObjectUrl && lastAvatarObjectUrl !== nextUrl) {
    revokeObjectUrl(lastAvatarObjectUrl)
  }
  avatarSrc.value = nextUrl
  lastAvatarObjectUrl = nextUrl.startsWith('blob:') ? nextUrl : null
}

watch(
  () => account.value?.avatarBase64,
  (dataUrl) => {
    updateAvatarSrc(dataUrl || FALLBACK_AVATAR)
  },
  { immediate: true },
)

onBeforeUnmount(() => {
  if (lastAvatarObjectUrl) {
    revokeObjectUrl(lastAvatarObjectUrl)
    lastAvatarObjectUrl = null
  }
})

const displayInitials = computed(() => {
  const source = displayName.value?.trim()
  if (!source) {
    return '用'
  }
  return source.charAt(0).toUpperCase()
})

const favoriteIdSet = computed(() => new Set(favorites.value.map((item) => item.serviceId)))
const favoritesCount = computed(() => favorites.value.length)

const paymentQrLink = computed(() => paymentSession.value?.qrUrl ?? '')

const paymentQrSrc = computed(() => {
  const link = paymentQrLink.value
  if (!link) {
    return ''
  }
  const base = 'https://api.qrserver.com/v1/create-qr-code/'
  return `${base}?size=240x240&data=${encodeURIComponent(link)}`
})

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

const loadAccount = async () => {
  try {
    account.value = await fetchCurrentAccount()
  } catch (error) {
    console.error(error)
  }
}

const handleProfileUpdated = (payload: AccountProfileItem) => {
  account.value = payload
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

const loadFavorites = async () => {
  try {
    favorites.value = await fetchUserFavorites()
  } catch (error) {
    console.error(error)
  }
}

const loadDiscover = async () => {
  discoverLoading.value = true
  try {
    const [carouselData, tipData, announcementData] = await Promise.all([
      fetchDashboardCarousels(),
      fetchDashboardTips(),
      fetchDashboardAnnouncements(),
    ])
    carousels.value = carouselData
    tips.value = tipData
    announcements.value = announcementData
  } catch (error) {
    console.error(error)
  } finally {
    discoverLoading.value = false
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

const refreshDiscover = () => {
  loadDiscover()
}

const switchSection = (key: SectionKey) => {
  activeSection.value = key
  if (key === 'orders') {
    loadOrders()
    loadAccount()
  } else if (key === 'profile') {
    loadAccount()
  } else if (key === 'messages') {
    loadConversations()
  } else if (key === 'wallet') {
    loadAccount()
  } else if (key === 'reviews') {
    if (reviewForm.serviceId) {
      loadReviews(Number(reviewForm.serviceId))
    }
  }
}

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const handleSelectService = (service: HousekeepServiceItem) => {
  bookingForm.service = service
  bookingForm.scheduledAt = ''
  bookingForm.serviceAddress = account.value?.address || ''
  bookingForm.contactPhone = account.value?.contactNumber || ''
  bookingForm.specialRequest = ''
  bookingDialogVisible.value = true
}

const closeBooking = () => {
  bookingDialogVisible.value = false
}

const resetPaymentState = () => {
  paymentStatus.value = 'idle'
  paymentMessage.value = ''
  paymentError.value = ''
  paymentChecking.value = false
  pendingOrderPayload.value = null
  paymentServiceName.value = ''
  paymentCompanyName.value = ''
  paymentAmount.value = null
  paymentSession.value = null
}

const submitBooking = async () => {
  if (!bookingForm.service || !bookingForm.scheduledAt) {
    window.alert('请填写完整的预约信息')
    return
  }

  const normalizedAddress = bookingForm.serviceAddress.trim()
  if (!normalizedAddress) {
    window.alert('请填写上门地址')
    return
  }
  const normalizedContact = bookingForm.contactPhone.trim()

  resetPaymentState()
  pendingOrderPayload.value = {
    serviceId: bookingForm.service.id,
    scheduledAt: new Date(bookingForm.scheduledAt).toISOString(),
    specialRequest: bookingForm.specialRequest,
    serviceAddress: normalizedAddress,
    contactPhone: normalizedContact || undefined,
  }
  paymentServiceName.value = bookingForm.service.name
  paymentCompanyName.value = bookingForm.service.companyName
  paymentAmount.value = bookingForm.service.price

  const payload: CreatePaymentSessionPayload = {
    serviceName: bookingForm.service.name,
    companyName: bookingForm.service.companyName,
    amount: bookingForm.service.price,
  }

  try {
    const sessionInfo = await createQrPaymentSession(payload)
    paymentSession.value = sessionInfo
    bookingDialogVisible.value = false
    paymentDialogVisible.value = true
  } catch (error) {
    console.error(error)
    window.alert('生成支付二维码失败，请稍后再试。')
    resetPaymentState()
  }
}

const closePaymentDialog = () => {
  if (paymentChecking.value) {
    return
  }
  paymentDialogVisible.value = false
  resetPaymentState()
}

const checkPaymentResult = async () => {
  if (paymentChecking.value) {
    return
  }
  if (!pendingOrderPayload.value) {
    if (paymentStatus.value === 'success') {
      closePaymentDialog()
      return
    }
    paymentStatus.value = 'failed'
    paymentError.value = '当前没有待支付的订单，请重新选择服务。'
    return
  }

  const token = paymentSession.value?.token
  if (!token) {
    paymentStatus.value = 'failed'
    paymentError.value = '支付会话不存在或已过期，请重新生成二维码。'
    return
  }

  paymentChecking.value = true
  paymentStatus.value = 'checking'
  paymentError.value = ''

  try {
    const gatewayResult: PaymentGatewayCheckResult = await checkQrPaymentStatus(token)
    if (gatewayResult.rawPayload) {
      console.debug('支付网关返回原始数据：', gatewayResult.rawPayload)
    }

    if (gatewayResult.token) {
      const current = paymentSession.value
      paymentSession.value = {
        token: gatewayResult.token,
        qrPath: current?.qrPath ?? '',
        qrUrl: current?.qrUrl ?? '',
        expiresAt: gatewayResult.expiresAt || current?.expiresAt || '',
      }
    }

    if (gatewayResult.status === 'CONFIRMED') {
      const payload = pendingOrderPayload.value
      try {
        await createUserOrder(payload)
        await Promise.all([loadOrders(), loadAccount()])
        pendingOrderPayload.value = null
        paymentStatus.value = 'success'
        paymentMessage.value = '支付成功，订单已创建。'
        bookingForm.service = null
        bookingForm.scheduledAt = ''
        bookingForm.serviceAddress = ''
        bookingForm.contactPhone = ''
        bookingForm.specialRequest = ''
      } catch (orderError) {
        console.error(orderError)
        paymentStatus.value = 'failed'
        paymentError.value =
          orderError instanceof Error ? orderError.message : '下单失败，请稍后再试。'
      }
    } else {
      paymentStatus.value = 'failed'
      paymentError.value =
        gatewayResult.message || '未能获取支付结果，请确认后再试。'
      if (gatewayResult.status === 'DECLINED') {
        pendingOrderPayload.value = null
      }
    }
  } catch (error) {
    console.error(error)
    paymentStatus.value = 'failed'
    paymentError.value =
      error instanceof Error ? error.message : '获取支付结果失败，请检查网络后重试。'
  } finally {
    paymentChecking.value = false
  }
}

const canRequestRefund = (order: ServiceOrderItem) => {
  return (
    order.status === 'IN_PROGRESS' ||
    order.status === 'SCHEDULED' ||
    order.status === 'COMPLETED'
  )
}

const handleRequestRefund = async (order: ServiceOrderItem) => {
  const reason = window.prompt('请输入退款原因：')
  if (!reason) return
  try {
    await requestUserRefund(order.id, { reason })
    await loadOrders()
    window.alert('退款申请已提交，等待家政公司或管理员处理。')
  } catch (error) {
    console.error(error)
  }
}

const toggleFavorite = async (service: HousekeepServiceItem) => {
  try {
    if (favoriteIdSet.value.has(service.id)) {
      const target = favorites.value.find((item) => item.serviceId === service.id)
      if (target) {
        await removeUserFavorite(service.id)
      }
    } else {
      await addUserFavorite(service.id)
    }
    await loadFavorites()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmitReview = async () => {
  if (!reviewForm.serviceId) {
    window.alert('请选择要评价的服务')
    return
  }
  reviewSubmitting.value = true
  try {
    await submitUserReview({
      serviceId: Number(reviewForm.serviceId),
      rating: reviewForm.rating,
      content: reviewForm.content,
    })
    reviewForm.content = ''
    await loadReviews(Number(reviewForm.serviceId))
    window.alert('评价提交成功')
  } catch (error) {
    console.error(error)
  } finally {
    reviewSubmitting.value = false
  }
}

const submitRecharge = async () => {
  if (!walletForm.amount || walletForm.amount <= 0) {
    window.alert('请输入正确的充值金额')
    return
  }
  walletSaving.value = true
  try {
    account.value = await rechargeUserWallet({ amount: walletForm.amount })
    walletForm.amount = null
    window.alert('充值成功')
  } catch (error) {
    console.error(error)
  } finally {
    walletSaving.value = false
  }
}

const submitExchange = async () => {
  if (!exchangeForm.points || exchangeForm.points <= 0 || exchangeForm.points % 5 !== 0) {
    window.alert('积分需为 5 的倍数')
    return
  }
  exchangeSaving.value = true
  try {
    account.value = await exchangeUserPoints({ points: exchangeForm.points })
    exchangeForm.points = null
    window.alert('积分兑换成功')
  } catch (error) {
    console.error(error)
  } finally {
    exchangeSaving.value = false
  }
}

const loadConversations = async () => {
  conversationsLoading.value = true
  try {
    conversations.value = await fetchUserConversations()
    const firstConversation = conversations.value[0]
    if (firstConversation && activeConversationId.value === null) {
      activeConversationId.value = firstConversation.orderId
      await refreshMessages()
    }
  } catch (error) {
    console.error(error)
  } finally {
    conversationsLoading.value = false
  }
}

const selectConversation = async (orderId: number) => {
  activeConversationId.value = orderId
  await Promise.all([refreshMessages(), markUserConversationRead(orderId)])
}

const refreshMessages = async () => {
  if (activeConversationId.value === null) return
  messagesLoading.value = true
  try {
    messages.value = await fetchUserMessages(activeConversationId.value)
  } catch (error) {
    console.error(error)
  } finally {
    messagesLoading.value = false
  }
}

const handleSendMessage = async (payload: CompanyMessagePayload) => {
  if (activeConversationId.value === null) return
  messageSending.value = true
  try {
    await sendUserMessage(activeConversationId.value, payload)
    await refreshMessages()
  } catch (error) {
    console.error(error)
  } finally {
    messageSending.value = false
  }
}

const jumpToMessages = (orderId: number) => {
  activeSection.value = 'messages'
  activeConversationId.value = orderId
  loadConversations().then(() => {
    selectConversation(orderId)
  })
}

const formatDateTime = (value: string) => {
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

const statusText = (status: ServiceOrderItem['status']) => {
  switch (status) {
    case 'SCHEDULED':
      return '待上门'
    case 'IN_PROGRESS':
      return '服务中'
    case 'COMPLETED':
      return '已完成'
    case 'PENDING':
      return '待确认'
    case 'REFUND_REQUESTED':
      return '退款审核中'
    case 'REFUND_APPROVED':
      return '已退款'
    case 'REFUND_REJECTED':
      return '退款驳回'
    default:
      return status
  }
}

onMounted(async () => {
  await Promise.all([loadAccount(), loadServices(), loadOrders(), loadDiscover(), loadFavorites()])
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  padding: 2.5rem 2rem 3rem;
  background: radial-gradient(circle at top left, rgba(56, 189, 248, 0.18), transparent 55%),
    radial-gradient(circle at bottom right, rgba(148, 163, 184, 0.18), transparent 50%),
    linear-gradient(135deg, #0f172a, #1f2937 45%, #0b1120 100%);
  color: #f8fafc;
}

.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
}

.dashboard-title {
  font-size: 2.4rem;
  font-weight: 700;
  margin: 0;
}

.dashboard-subtitle {
  margin-top: 0.75rem;
  color: rgba(226, 232, 240, 0.75);
  font-size: 1.05rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
  font-size: 0.95rem;
  color: rgba(226, 232, 240, 0.8);
}

.account-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.35);
  border: 2px solid rgba(148, 163, 184, 0.35);
}

.account-badge {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1.25rem;
  letter-spacing: 0.05em;
  background: rgba(226, 232, 240, 0.2);
  color: #f8fafc;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.35);
  border: 2px solid rgba(148, 163, 184, 0.35);
  text-transform: uppercase;
}

.logout-button {
  padding: 0.5rem 1.25rem;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.2);
  border: 1px solid rgba(148, 163, 184, 0.35);
  color: #f8fafc;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2.5rem;
}

.stat-card {
  padding: 1.75rem;
  border-radius: 1.25rem;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(148, 163, 184, 0.15);
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.35);
  position: relative;
}

.stat-card.glass {
  border-color: rgba(125, 211, 252, 0.25);
  background: rgba(8, 47, 73, 0.6);
}

.stat-label {
  font-size: 0.95rem;
  color: rgba(226, 232, 240, 0.7);
  margin: 0 0 0.5rem;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  margin: 0;
}

.stat-helper {
  margin-top: 0.75rem;
  font-size: 0.9rem;
  color: rgba(226, 232, 240, 0.6);
}

.dashboard-main {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 2rem;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.85rem 1.1rem;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.35);
  border: 1px solid transparent;
  color: rgba(226, 232, 240, 0.75);
}

.sidebar-item.active {
  color: #0f172a;
  background: linear-gradient(120deg, #38bdf8, #6366f1);
  box-shadow: 0 15px 30px rgba(14, 165, 233, 0.25);
}

.sidebar-icon {
  font-size: 1.2rem;
}

.panel {
  padding: 2rem;
  border-radius: 1.5rem;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.18);
  backdrop-filter: blur(18px);
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.35);
}

.profile-panel {
  background: linear-gradient(160deg, rgba(45, 212, 191, 0.15), rgba(56, 189, 248, 0.12));
  border-color: rgba(20, 184, 166, 0.25);
}

.immersive-panel {
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.18), rgba(99, 102, 241, 0.15));
  border-color: rgba(148, 163, 184, 0.25);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.panel-header h2 {
  margin: 0;
  font-size: 1.6rem;
  font-weight: 600;
}

.panel-header p {
  margin: 0.35rem 0 0;
  color: rgba(226, 232, 240, 0.6);
}

.ghost-button {
  padding: 0.55rem 1.3rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(15, 23, 42, 0.3);
  color: #e2e8f0;
}

.loading-state {
  padding: 2.5rem;
  text-align: center;
  color: rgba(226, 232, 240, 0.7);
}

.discover-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1.5rem;
}

.carousel {
  grid-column: span 2;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.carousel-track {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(220px, 1fr);
  gap: 1rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
}

.carousel-card {
  border-radius: 1.1rem;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.18);
  overflow: hidden;
  min-height: 220px;
  display: flex;
  flex-direction: column;
}

.carousel-media {
  flex: 1;
  background-size: cover;
  background-position: center;
  min-height: 140px;
}

.carousel-body {
  padding: 0.85rem 1rem;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.section-title h3 {
  margin: 0;
}

.section-title p {
  margin: 0;
  color: rgba(226, 232, 240, 0.6);
  font-size: 0.9rem;
}

.tip-list,
.announcement-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.tip-list li,
.announcement-list li {
  padding: 0.85rem 1rem;
  border-radius: 1rem;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.15);
}

.tip-list strong,
.announcement-list strong {
  display: block;
  margin-bottom: 0.35rem;
}

.service-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.service-card {
  position: relative;
  padding: 1.5rem;
  border-radius: 1.2rem;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.18);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.favorite-toggle {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(15, 23, 42, 0.55);
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.3);
  padding: 0.35rem 0.7rem;
  color: rgba(248, 250, 252, 0.75);
}

.favorite-toggle.active {
  background: rgba(248, 113, 113, 0.2);
  border-color: rgba(248, 113, 113, 0.45);
  color: #fda4af;
}

.service-title {
  margin: 0;
  font-size: 1.25rem;
}

.service-company {
  margin: 0;
  color: rgba(226, 232, 240, 0.6);
}

.service-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
  margin: 0;
}

.service-meta dt {
  font-size: 0.85rem;
  color: rgba(148, 163, 184, 0.7);
}

.service-desc {
  margin: 0;
  color: rgba(226, 232, 240, 0.75);
}

.primary-button {
  align-self: flex-start;
  padding: 0.55rem 1.15rem;
  border-radius: 999px;
  border: none;
  background: linear-gradient(120deg, #6366f1, #38bdf8);
  color: #0f172a;
  font-weight: 600;
}

.table-wrapper {
  overflow: auto;
  border-radius: 1.1rem;
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 1rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.data-table tbody tr:hover {
  background: rgba(148, 163, 184, 0.08);
}

.order-subtext {
  color: rgba(226, 232, 240, 0.65);
  font-size: 0.9rem;
}

.order-subtext.muted {
  color: rgba(148, 163, 184, 0.6);
}

.order-meta {
  display: flex;
  gap: 0.75rem;
  font-size: 0.85rem;
  color: rgba(148, 163, 184, 0.7);
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.85rem;
  margin-bottom: 0.35rem;
}

.status-scheduled {
  background: rgba(96, 165, 250, 0.15);
  color: #93c5fd;
}

.status-in_progress {
  background: rgba(45, 212, 191, 0.2);
  color: #5eead4;
}

.status-completed {
  background: rgba(34, 197, 94, 0.2);
  color: #86efac;
}

.status-refund_requested {
  background: rgba(248, 113, 113, 0.2);
  color: #fca5a5;
}

.table-actions {
  white-space: nowrap;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.link-button {
  background: none;
  border: none;
  color: #38bdf8;
  cursor: pointer;
  padding: 0;
  font-size: 0.9rem;
}

.schedule-timeline {
  margin-top: 2rem;
}

.schedule-timeline ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.schedule-timeline li {
  padding: 0.85rem 1rem;
  border-radius: 1rem;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.wallet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
}

.wallet-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1.5rem;
  border-radius: 1.2rem;
  background: rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.wallet-card input,
.form-field select,
.form-field textarea,
.form-field input {
  width: 100%;
  border-radius: 0.85rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  background: rgba(15, 23, 42, 0.6);
  color: #f8fafc;
  padding: 0.55rem 0.75rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

.form-field-full {
  grid-column: 1 / -1;
}

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
}

.review-list {
  margin-top: 2rem;
}

.review-list ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.review-item {
  padding: 1rem;
  border-radius: 1rem;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.15);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.review-rating {
  color: #facc15;
}

.empty-tip {
  text-align: center;
  color: rgba(226, 232, 240, 0.6);
  padding: 1.75rem 0;
}

.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(6px);
  z-index: 30;
}

.dialog-card {
  width: min(560px, 90%);
  background: rgba(15, 23, 42, 0.9);
  border-radius: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  padding: 1.75rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.payment-card {
  max-width: 520px;
}

.payment-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  text-align: center;
}

.payment-qr {
  width: 220px;
  height: 220px;
  border-radius: 1rem;
  background: #fff;
  padding: 0.75rem;
}

.payment-qr.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.95rem;
  color: rgba(15, 23, 42, 0.6);
  background: rgba(255, 255, 255, 0.65);
  font-weight: 500;
}

.payment-tip {
  font-size: 0.85rem;
  color: rgba(226, 232, 240, 0.8);
}

.payment-tip a {
  color: #38bdf8;
}

.payment-summary {
  margin: 0;
  font-size: 0.95rem;
}

.payment-status {
  margin: 0.25rem 0 0;
  font-size: 0.95rem;
  color: rgba(226, 232, 240, 0.85);
}

.payment-status.success {
  color: #4ade80;
}

.payment-status.error {
  color: #fca5a5;
}

.payment-status.checking {
  color: #facc15;
}

.dialog-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.dialog-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1080px) {
  .dashboard-main {
    grid-template-columns: 1fr;
  }

  .header-actions {
    flex-wrap: wrap;
    justify-content: flex-end;
  }

  .account-badge {
    width: 44px;
    height: 44px;
    font-size: 1.1rem;
  }

  .sidebar {
    flex-direction: row;
    flex-wrap: wrap;
    margin-bottom: 1.5rem;
  }

  .sidebar-item {
    flex: 1 1 150px;
    justify-content: center;
  }

  .discover-grid {
    grid-template-columns: 1fr;
  }
}
</style>
