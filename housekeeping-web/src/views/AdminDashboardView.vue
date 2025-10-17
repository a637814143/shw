<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">平台管理员中心</h1>
        <p class="dashboard-subtitle">一站式掌握资金流、服务履约与内容运营</p>
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
        <p class="stat-helper">普通用户 + 家政公司 + 管理员</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">家政公司</p>
        <p class="stat-value">{{ adminStats.totalCompanies }}</p>
        <p class="stat-helper">优质服务提供方规模</p>
      </article>
      <article class="stat-card glass">
        <p class="stat-label">累计充值</p>
        <p class="stat-value">¥{{ adminStats.totalRecharge.toFixed(2) }}</p>
        <p class="stat-helper">提现 {{ adminStats.totalWithdraw.toFixed(2) }}</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">待审退款</p>
        <p class="stat-value">{{ adminStats.pendingRefunds }}</p>
        <p class="stat-helper">需优先关注的用户诉求</p>
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
        <section v-if="activeSection === 'overview'" class="panel immersive-panel">
          <header class="panel-header">
            <div>
              <h2>数据总览</h2>
              <p>洞察充值趋势、服务履约与账户构成，打造 Apple 式流畅体验。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadOverview" :disabled="overviewLoading">
              {{ overviewLoading ? '刷新中…' : '刷新数据' }}
            </button>
          </header>
          <div v-if="overviewLoading" class="loading-state">正在同步最新统计…</div>
          <div v-else class="overview-grid">
            <article class="insight-card">
              <header class="insight-header">
                <h3>近 7 日充值趋势</h3>
                <span class="insight-helper">金额单位：元</span>
              </header>
              <div class="sparkline" role="img" aria-label="七日充值趋势柱状图">
                <div
                  v-for="point in weeklySeries"
                  :key="point.label"
                  class="spark-bar"
                  :style="sparkStyle(point.amount)"
                >
                  <span class="spark-amount">¥{{ point.amount.toFixed(2) }}</span>
                  <span class="spark-label">{{ shortLabel(point.label) }}</span>
                </div>
              </div>
            </article>
            <article class="insight-card">
              <header class="insight-header">
                <h3>预约履约对比</h3>
                <span class="insight-helper">反映服务调度效率</span>
              </header>
              <ul class="metric-list">
                <li v-for="metric in appointmentMetrics" :key="metric.status">
                  <span>{{ metric.status }}</span>
                  <span class="metric-bar">
                    <span class="metric-fill" :style="metricStyle(metric.count)"></span>
                    <strong>{{ metric.count }}</strong>
                  </span>
                </li>
              </ul>
            </article>
            <article class="insight-card highlight">
              <header class="insight-header">
                <h3>账户构成与风控</h3>
                <span class="insight-helper">确保平台运维有序</span>
              </header>
              <ul class="stat-lines">
                <li><span>管理员数量</span><strong>{{ adminStats.totalAdmins }}</strong></li>
                <li><span>普通用户</span><strong>{{ normalUserCount }}</strong></li>
                <li><span>累计提现</span><strong>¥{{ adminStats.totalWithdraw.toFixed(2) }}</strong></li>
              </ul>
            </article>
          </div>
        </section>

        <section v-else-if="activeSection === 'users'" class="panel">
          <header class="panel-header">
            <div>
              <h2>用户资产与密码管理</h2>
              <p>精细化调节用户余额、积分与密码，保障账户安全。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadUsers" :disabled="usersLoading">
              {{ usersLoading ? '刷新中…' : '刷新列表' }}
            </button>
          </header>
          <div v-if="usersLoading" class="loading-state">正在获取用户信息…</div>
          <div v-else class="table-wrapper">
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
                      <input v-model.number="walletEdits[user.id]" type="number" min="0" step="0.01" />
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
                      <input v-model="passwordEdits[user.id]" type="text" placeholder="请输入新密码" />
                      <button type="button" class="link-button" @click="savePassword(user.id)">重置</button>
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

        <section v-else-if="activeSection === 'orders'" class="panel">
          <header class="panel-header">
            <div>
              <h2>服务预约调度</h2>
              <p>跨公司统一分配家政人员，实时跟进上门进度。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadAdminOrders" :disabled="ordersLoading">
              {{ ordersLoading ? '刷新中…' : '刷新列表' }}
            </button>
          </header>
          <div v-if="ordersLoading" class="loading-state">正在获取预约数据…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务</th>
                  <th>预约时间</th>
                  <th>状态</th>
                  <th>当前指派</th>
                  <th>指派家政人员</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in adminOrders" :key="order.id">
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">用户：{{ order.username }} · {{ order.companyName }}</div>
                  </td>
                  <td>{{ formatDateTime(order.scheduledAt) }}</td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">{{ statusText(order.status) }}</span>
                    <div class="order-subtext">{{ order.progressNote || '待更新' }}</div>
                  </td>
                  <td>
                    <div class="order-subtext">{{ order.assignedWorker || '未指派' }}</div>
                    <div class="order-subtext muted">{{ order.workerContact || '—' }}</div>
                  </td>
                  <td>
                    <div class="assign-grid">
                      <input
                        v-model="ensureAssignEdit(order.id).workerName"
                        type="text"
                        placeholder="人员姓名"
                      />
                      <input
                        v-model="ensureAssignEdit(order.id).workerContact"
                        type="text"
                        placeholder="联系方式"
                      />
                      <button
                        type="button"
                        class="primary-button"
                        :disabled="assignSaving[order.id]"
                        @click="saveAssignment(order)"
                      >
                        {{ assignSaving[order.id] ? '指派中…' : '保存指派' }}
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="!adminOrders.length">
                  <td colspan="5" class="empty-row">暂无预约数据。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'transactions'" class="panel">
          <header class="panel-header">
            <div>
              <h2>充值与调整流水</h2>
              <p>记录用户自助充值、积分兑换及管理员调整。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadTransactions" :disabled="transactionsLoading">
              {{ transactionsLoading ? '刷新中…' : '刷新流水' }}
            </button>
          </header>
          <div v-if="transactionsLoading" class="loading-state">正在同步流水记录…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>时间</th>
                  <th>账号</th>
                  <th>类型</th>
                  <th>金额</th>
                  <th>备注</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in transactions" :key="item.id">
                  <td>{{ formatDateTime(item.createdAt) }}</td>
                  <td>{{ item.username }}</td>
                  <td>{{ transactionText(item.type) }}</td>
                  <td :class="{ positive: item.amount >= 0, negative: item.amount < 0 }">¥{{ item.amount.toFixed(2) }}</td>
                  <td>{{ item.note || '—' }}</td>
                </tr>
                <tr v-if="!transactions.length">
                  <td colspan="5" class="empty-row">暂无充值或调整记录。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'favorites'" class="panel">
          <header class="panel-header">
            <div>
              <h2>收藏洞察</h2>
              <p>了解用户偏好，为内容与服务运营提供数据依据。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadFavorites" :disabled="favoritesLoading">
              {{ favoritesLoading ? '刷新中…' : '刷新数据' }}
            </button>
          </header>
          <div v-if="favoritesLoading" class="loading-state">正在加载收藏列表…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>用户</th>
                  <th>收藏服务</th>
                  <th>所属公司</th>
                  <th>时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in favorites" :key="item.id">
                  <td>{{ item.username }}</td>
                  <td>{{ item.serviceName }}</td>
                  <td>{{ item.companyName }}</td>
                  <td>{{ formatDateTime(item.createdAt) }}</td>
                </tr>
                <tr v-if="!favorites.length">
                  <td colspan="4" class="empty-row">暂未产生收藏数据。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'content'" class="panel immersive-panel">
          <header class="panel-header">
            <div>
              <h2>内容运营中心</h2>
              <p>以苹果式高级质感管理轮播、贴士与公告。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadContent" :disabled="contentLoading">
              {{ contentLoading ? '同步中…' : '全部刷新' }}
            </button>
          </header>
          <div v-if="contentLoading" class="loading-state">正在同步内容配置…</div>
          <div v-else class="content-grid">
            <section class="content-card">
              <header>
                <h3>轮播图</h3>
                <p>推荐焦点服务，图片支持外链。</p>
              </header>
              <form class="content-form" @submit.prevent="submitCarousel">
                <input v-model="carouselForm.title" type="text" placeholder="标题" required />
                <input v-model="carouselForm.imageUrl" type="url" placeholder="图片地址" required />
                <input v-model="carouselForm.serviceLink" type="text" placeholder="关联服务或跳转链接（可选）" />
                <div class="form-actions">
                  <button type="submit" class="primary-button" :disabled="carouselSaving">
                    {{ carouselSaving ? '保存中…' : carouselEditing ? '更新轮播' : '新增轮播' }}
                  </button>
                  <button type="button" class="secondary-button" @click="resetCarousel" :disabled="carouselSaving">
                    重置
                  </button>
                </div>
              </form>
              <ul class="content-list">
                <li v-for="item in carousels" :key="item.id">
                  <div>
                    <strong>{{ item.title }}</strong>
                    <p class="muted">{{ item.imageUrl }}</p>
                    <p class="muted">{{ item.serviceLink || '无跳转' }}</p>
                  </div>
                  <div class="list-actions">
                    <button type="button" class="link-button" @click="editCarousel(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="removeCarousel(item.id)">删除</button>
                  </div>
                </li>
                <li v-if="!carousels.length" class="empty-row">暂无轮播图，立即创建一个吧。</li>
              </ul>
            </section>

            <section class="content-card">
              <header>
                <h3>居家贴士</h3>
                <p>精选生活指南，提升平台温度。</p>
              </header>
              <form class="content-form" @submit.prevent="submitTip">
                <input v-model="tipForm.title" type="text" placeholder="贴士标题" required />
                <textarea v-model="tipForm.content" rows="3" placeholder="贴士内容" required></textarea>
                <div class="form-actions">
                  <button type="submit" class="primary-button" :disabled="tipSaving">
                    {{ tipSaving ? '保存中…' : tipEditing ? '更新贴士' : '新增贴士' }}
                  </button>
                  <button type="button" class="secondary-button" @click="resetTip" :disabled="tipSaving">
                    重置
                  </button>
                </div>
              </form>
              <ul class="content-list">
                <li v-for="item in tips" :key="item.id">
                  <div>
                    <strong>{{ item.title }}</strong>
                    <p class="muted">{{ item.content }}</p>
                  </div>
                  <div class="list-actions">
                    <button type="button" class="link-button" @click="editTip(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="removeTip(item.id)">删除</button>
                  </div>
                </li>
                <li v-if="!tips.length" class="empty-row">还没有贴士内容。</li>
              </ul>
            </section>

            <section class="content-card">
              <header>
                <h3>系统公告</h3>
                <p>向所有用户广播平台政策与活动。</p>
              </header>
              <form class="content-form" @submit.prevent="submitAnnouncement">
                <input v-model="announcementForm.title" type="text" placeholder="公告标题" required />
                <textarea v-model="announcementForm.content" rows="3" placeholder="公告内容" required></textarea>
                <div class="form-actions">
                  <button type="submit" class="primary-button" :disabled="announcementSaving">
                    {{ announcementSaving ? '保存中…' : announcementEditing ? '更新公告' : '新增公告' }}
                  </button>
                  <button type="button" class="secondary-button" @click="resetAnnouncement" :disabled="announcementSaving">
                    重置
                  </button>
                </div>
              </form>
              <ul class="content-list">
                <li v-for="item in announcements" :key="item.id">
                  <div>
                    <strong>{{ item.title }}</strong>
                    <p class="muted">{{ item.content }}</p>
                  </div>
                  <div class="list-actions">
                    <button type="button" class="link-button" @click="editAnnouncement(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="removeAnnouncement(item.id)">删除</button>
                  </div>
                </li>
                <li v-if="!announcements.length" class="empty-row">暂无系统公告。</li>
              </ul>
            </section>
          </div>
        </section>

        <section v-else class="panel">
          <header class="panel-header">
            <div>
              <h2>全站退款处理</h2>
              <p>当公司未及时处理时，由管理员终审保障体验。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadRefunds" :disabled="refundsLoading">
              {{ refundsLoading ? '刷新中…' : '刷新列表' }}
            </button>
          </header>
          <div v-if="refundsLoading" class="loading-state">正在同步退款请求…</div>
          <div v-else class="table-wrapper">
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
                    <button type="button" class="link-button danger" @click="handleRefund(order, false)">拒绝</button>
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
  assignAdminWorker,
  createDashboardAnnouncement,
  createDashboardCarousel,
  createDashboardTip,
  deleteDashboardAnnouncement,
  deleteDashboardCarousel,
  deleteDashboardTip,
  fetchAdminFavorites,
  fetchAdminOrders,
  fetchAdminOverview,
  fetchAdminRefunds,
  fetchAdminTransactions,
  fetchAdminUsers,
  fetchDashboardAnnouncements,
  fetchDashboardCarousels,
  fetchDashboardTips,
  handleAdminRefund,
  updateAdminLoyalty,
  updateAdminPassword,
  updateAdminWallet,
  updateDashboardAnnouncement,
  updateDashboardCarousel,
  updateDashboardTip,
  type AccountTransactionItem,
  type AdminOverviewItem,
  type AssignWorkerPayload,
  type DashboardAnnouncementItem,
  type DashboardCarouselItem,
  type DashboardTipItem,
  type ServiceFavoriteItem,
  type ServiceOrderItem,
  type UpdateLoyaltyPayload,
  type UpdatePasswordPayload,
  type UpdateWalletPayload,
  type UserAccountItem,
} from '../services/dashboard'


type SectionKey = 'overview' | 'users' | 'orders' | 'transactions' | 'favorites' | 'content' | 'refunds'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

const router = useRouter()
const username = computed(() => sessionStorage.getItem(AUTH_ACCOUNT_KEY) ?? 'admin')

const sections: SectionMeta[] = [
  { key: 'overview', icon: '✨', label: '数据总览' },
  { key: 'users', icon: '🧾', label: '用户管理' },
  { key: 'orders', icon: '📋', label: '预约调度' },
  { key: 'transactions', icon: '💳', label: '充值流水' },
  { key: 'favorites', icon: '❤️', label: '收藏洞察' },
  { key: 'content', icon: '🖼️', label: '内容运营' },
  { key: 'refunds', icon: '💰', label: '退款审批' },
]

const activeSection = ref<SectionKey>('overview')

const overview = ref<AdminOverviewItem | null>(null)
const overviewLoading = ref(false)

const users = ref<UserAccountItem[]>([])
const usersLoading = ref(false)

const adminOrders = ref<ServiceOrderItem[]>([])
const ordersLoading = ref(false)

const transactions = ref<AccountTransactionItem[]>([])
const transactionsLoading = ref(false)

const favorites = ref<ServiceFavoriteItem[]>([])
const favoritesLoading = ref(false)

const refundOrders = ref<ServiceOrderItem[]>([])
const refundsLoading = ref(false)

const carousels = ref<DashboardCarouselItem[]>([])
const tips = ref<DashboardTipItem[]>([])
const announcements = ref<DashboardAnnouncementItem[]>([])
const contentLoading = ref(false)

const walletEdits = reactive<Record<number, number>>({})
const loyaltyEdits = reactive<Record<number, number>>({})
const passwordEdits = reactive<Record<number, string>>({})

const assignEdits = reactive<Record<number, AssignWorkerPayload>>({})
const assignSaving = reactive<Record<number, boolean>>({})

const ensureAssignEdit = (orderId: number): AssignWorkerPayload => {
  if (!assignEdits[orderId]) {
    assignEdits[orderId] = {
      workerName: '',
      workerContact: '',
    }
  }
  return assignEdits[orderId]
}

const carouselForm = reactive<{ title: string; imageUrl: string; serviceLink: string }>({
  title: '',
  imageUrl: '',
  serviceLink: '',
})
const tipForm = reactive<{ title: string; content: string }>({ title: '', content: '' })
const announcementForm = reactive<{ title: string; content: string }>({ title: '', content: '' })

const carouselEditing = ref<number | null>(null)
const tipEditing = ref<number | null>(null)
const announcementEditing = ref<number | null>(null)
const carouselSaving = ref(false)
const tipSaving = ref(false)
const announcementSaving = ref(false)

const adminStats = computed(() => {
  const base = overview.value
  return {
    totalUsers: base?.totalUsers ?? 0,
    totalCompanies: base?.totalCompanies ?? 0,
    totalAdmins: base?.totalAdmins ?? 0,
    totalRecharge: Number(base?.totalRecharge ?? 0),
    totalWithdraw: Number(base?.totalWithdraw ?? 0),
    pendingRefunds: refundOrders.value.length,
  }
})

const normalUserCount = computed(
  () => Math.max(0, adminStats.value.totalUsers - adminStats.value.totalCompanies - adminStats.value.totalAdmins),
)

const weeklySeries = computed(() => overview.value?.weeklyRecharge ?? [])
const maxWeeklyAmount = computed(() => {
  const max = weeklySeries.value.reduce((acc, item) => Math.max(acc, item.amount), 0)
  return max <= 0 ? 1 : max
})

const appointmentMetrics = computed(() => overview.value?.appointmentMetrics ?? [])
const maxAppointment = computed(() => {
  const max = appointmentMetrics.value.reduce((acc, item) => Math.max(acc, item.count), 0)
  return max <= 0 ? 1 : max
})

const sparkStyle = (amount: number) => ({
  height: `${Math.max(12, Math.round((amount / maxWeeklyAmount.value) * 100))}%`,
})

const metricStyle = (count: number) => ({
  width: `${Math.max(6, Math.round((count / maxAppointment.value) * 100))}%`,
})

const shortLabel = (label: string) => {
  if (!label) return ''
  const parts = label.split('-')
  return parts.length >= 2 ? `${parts[1]}/${parts[2] ?? ''}` : label
}

const formatDateTime = (value: string) => {
  if (!value) return '—'
  return new Date(value).toLocaleString('zh-CN', {
    hour12: false,
  })
}

const formatDate = (value: string) => {
  if (!value) return '—'
  return new Date(value).toLocaleDateString('zh-CN')
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

const roleText = (role: string) => ROLE_LABELS[role as keyof typeof ROLE_LABELS] ?? role

const transactionText = (type: string) => {
  switch (type) {
    case 'RECHARGE':
      return '用户充值'
    case 'WITHDRAW':
      return '提现'
    case 'ADJUST':
      return '管理员调整'
    default:
      return type
  }
}

const switchSection = (key: SectionKey) => {
  activeSection.value = key
  if (key === 'users') {
    loadUsers()
  } else if (key === 'orders') {
    loadAdminOrders()
  } else if (key === 'transactions') {
    loadTransactions()
  } else if (key === 'favorites') {
    loadFavorites()
  } else if (key === 'content') {
    loadContent()
  } else if (key === 'refunds') {
    loadRefunds()
  }
}

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const loadOverview = async () => {
  overviewLoading.value = true
  try {
    overview.value = await fetchAdminOverview()
  } catch (error) {
    console.error(error)
  } finally {
    overviewLoading.value = false
  }
}

const loadUsers = async () => {
  usersLoading.value = true
  try {
    users.value = await fetchAdminUsers()
    users.value.forEach((user) => {
      if (walletEdits[user.id] === undefined) walletEdits[user.id] = user.balance
      if (loyaltyEdits[user.id] === undefined) loyaltyEdits[user.id] = user.loyaltyPoints
      if (passwordEdits[user.id] === undefined) passwordEdits[user.id] = ''
    })
  } catch (error) {
    console.error(error)
  } finally {
    usersLoading.value = false
  }
}

const loadAdminOrders = async () => {
  ordersLoading.value = true
  try {
    adminOrders.value = await fetchAdminOrders()
    adminOrders.value.forEach((order) => {
      const edit = ensureAssignEdit(order.id)
      edit.workerName = order.assignedWorker ?? ''
      edit.workerContact = order.workerContact ?? ''
    })
  } catch (error) {
    console.error(error)
  } finally {
    ordersLoading.value = false
  }
}

const loadTransactions = async () => {
  transactionsLoading.value = true
  try {
    transactions.value = await fetchAdminTransactions()
  } catch (error) {
    console.error(error)
  } finally {
    transactionsLoading.value = false
  }
}

const loadFavorites = async () => {
  favoritesLoading.value = true
  try {
    favorites.value = await fetchAdminFavorites()
  } catch (error) {
    console.error(error)
  } finally {
    favoritesLoading.value = false
  }
}

const loadRefunds = async () => {
  refundsLoading.value = true
  try {
    refundOrders.value = await fetchAdminRefunds()
  } catch (error) {
    console.error(error)
  } finally {
    refundsLoading.value = false
  }
}

const loadContent = async () => {
  contentLoading.value = true
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
    contentLoading.value = false
  }
}

const saveWallet = async (userId: number) => {
  try {
    await updateAdminWallet(userId, { money: walletEdits[userId] } as UpdateWalletPayload)
    await loadUsers()
  } catch (error) {
    console.error(error)
  }
}

const saveLoyalty = async (userId: number) => {
  try {
    await updateAdminLoyalty(userId, { loyaltyPoints: loyaltyEdits[userId] } as UpdateLoyaltyPayload)
    await loadUsers()
  } catch (error) {
    console.error(error)
  }
}

const savePassword = async (userId: number) => {
  if (!passwordEdits[userId]) {
    window.alert('请先输入新密码')
    return
  }
  try {
    await updateAdminPassword(userId, { password: passwordEdits[userId] } as UpdatePasswordPayload)
    passwordEdits[userId] = ''
    window.alert('密码已更新')
  } catch (error) {
    console.error(error)
  }
}

const saveAssignment = async (order: ServiceOrderItem) => {
  const payload = ensureAssignEdit(order.id)
  if (!payload.workerName || !payload.workerContact) {
    window.alert('请填写人员姓名与联系方式')
    return
  }
  assignSaving[order.id] = true
  try {
    await assignAdminWorker(order.id, payload)
    await loadAdminOrders()
  } catch (error) {
    console.error(error)
  } finally {
    assignSaving[order.id] = false
  }
}

const handleRefund = async (order: ServiceOrderItem, approve: boolean) => {
  const message = approve ? '确认同意该退款？' : '确认拒绝该退款？'
  if (!window.confirm(message)) return
  try {
    await handleAdminRefund(order.id, { approve })
    await loadRefunds()
    await loadOverview()
  } catch (error) {
    console.error(error)
  }
}

const editCarousel = (item: DashboardCarouselItem) => {
  carouselEditing.value = item.id
  carouselForm.title = item.title
  carouselForm.imageUrl = item.imageUrl
  carouselForm.serviceLink = item.serviceLink ?? ''
}

const resetCarousel = () => {
  carouselEditing.value = null
  carouselForm.title = ''
  carouselForm.imageUrl = ''
  carouselForm.serviceLink = ''
}

const submitCarousel = async () => {
  carouselSaving.value = true
  try {
    if (carouselEditing.value) {
      await updateDashboardCarousel(carouselEditing.value, { ...carouselForm, id: carouselEditing.value })
    } else {
      await createDashboardCarousel({ ...carouselForm, id: 0 })
    }
    resetCarousel()
    await loadContent()
  } catch (error) {
    console.error(error)
  } finally {
    carouselSaving.value = false
  }
}

const removeCarousel = async (id: number) => {
  if (!window.confirm('确定删除该轮播图？')) return
  try {
    await deleteDashboardCarousel(id)
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

const editTip = (item: DashboardTipItem) => {
  tipEditing.value = item.id
  tipForm.title = item.title
  tipForm.content = item.content
}

const resetTip = () => {
  tipEditing.value = null
  tipForm.title = ''
  tipForm.content = ''
}

const submitTip = async () => {
  tipSaving.value = true
  try {
    if (tipEditing.value) {
      await updateDashboardTip(tipEditing.value, { ...tipForm, id: tipEditing.value })
    } else {
      await createDashboardTip({ ...tipForm, id: 0 })
    }
    resetTip()
    await loadContent()
  } catch (error) {
    console.error(error)
  } finally {
    tipSaving.value = false
  }
}

const removeTip = async (id: number) => {
  if (!window.confirm('确定删除该贴士？')) return
  try {
    await deleteDashboardTip(id)
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

const editAnnouncement = (item: DashboardAnnouncementItem) => {
  announcementEditing.value = item.id
  announcementForm.title = item.title
  announcementForm.content = item.content
}

const resetAnnouncement = () => {
  announcementEditing.value = null
  announcementForm.title = ''
  announcementForm.content = ''
}

const submitAnnouncement = async () => {
  announcementSaving.value = true
  try {
    if (announcementEditing.value) {
      await updateDashboardAnnouncement(announcementEditing.value, {
        ...announcementForm,
        id: announcementEditing.value,
      })
    } else {
      await createDashboardAnnouncement({ ...announcementForm, id: 0 })
    }
    resetAnnouncement()
    await loadContent()
  } catch (error) {
    console.error(error)
  } finally {
    announcementSaving.value = false
  }
}

const removeAnnouncement = async (id: number) => {
  if (!window.confirm('确定删除该公告？')) return
  try {
    await deleteDashboardAnnouncement(id)
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await Promise.all([loadOverview(), loadUsers(), loadRefunds()])
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  padding: 2.5rem 2rem 3rem;
  background: radial-gradient(circle at top left, rgba(90, 119, 255, 0.18), transparent 55%),
    radial-gradient(circle at bottom right, rgba(255, 255, 255, 0.18), transparent 50%),
    linear-gradient(135deg, #0f172a, #1f2937 45%, #0b1120 100%);
  color: #f8fafc;
}

.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  position: relative;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  inset: auto 0 -0.75rem;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(148, 163, 184, 0.4), transparent);
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  margin: 0;
}

.dashboard-subtitle {
  margin-top: 0.75rem;
  color: rgba(226, 232, 240, 0.75);
  font-size: 1.05rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.95rem;
  color: rgba(226, 232, 240, 0.9);
}

.logout-button {
  padding: 0.5rem 1.25rem;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.2);
  border: 1px solid rgba(148, 163, 184, 0.35);
  color: #f8fafc;
  transition: all 0.2s ease;
}

.logout-button:hover {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(255, 255, 255, 0.4);
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
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.35), transparent 55%);
  opacity: 0.35;
  pointer-events: none;
}

.stat-card.accent::after {
  background: radial-gradient(circle at top right, rgba(16, 185, 129, 0.45), transparent 60%);
}

.stat-card.warning::after {
  background: radial-gradient(circle at top right, rgba(248, 113, 113, 0.5), transparent 55%);
}

.stat-card.glass {
  border-color: rgba(125, 211, 252, 0.3);
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
  color: #f8fafc;
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
  transition: all 0.2s ease;
}

.sidebar-item:hover {
  color: #f8fafc;
  border-color: rgba(148, 163, 184, 0.35);
}

.sidebar-item.active {
  color: #0f172a;
  background: linear-gradient(120deg, #38bdf8, #6366f1);
  border-color: transparent;
  box-shadow: 0 15px 30px rgba(14, 165, 233, 0.25);
}

.sidebar-icon {
  font-size: 1.2rem;
}

.content {
  display: block;
}

.panel {
  padding: 2rem;
  border-radius: 1.5rem;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.18);
  backdrop-filter: blur(18px);
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.35);
}

.immersive-panel {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.18), rgba(14, 165, 233, 0.1));
  border: 1px solid rgba(148, 163, 184, 0.25);
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
  color: rgba(226, 232, 240, 0.65);
}

.ghost-button {
  padding: 0.55rem 1.3rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(15, 23, 42, 0.3);
  color: #e2e8f0;
  transition: all 0.2s ease;
}

.ghost-button:hover {
  background: rgba(148, 163, 184, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
}

.loading-state {
  padding: 2.5rem;
  text-align: center;
  color: rgba(226, 232, 240, 0.7);
}

.table-wrapper {
  overflow: auto;
  border-radius: 1.1rem;
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
}

.data-table th,
.data-table td {
  padding: 1rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.data-table thead {
  background: rgba(15, 23, 42, 0.4);
  color: rgba(226, 232, 240, 0.8);
}

.data-table tbody tr:hover {
  background: rgba(148, 163, 184, 0.08);
}

.inline-form {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.inline-form input,
.assign-grid input,
.content-form input,
.content-form textarea {
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.25);
  border-radius: 0.75rem;
  padding: 0.55rem 0.75rem;
  color: #f8fafc;
}

.inline-form input:focus,
.assign-grid input:focus,
.content-form input:focus,
.content-form textarea:focus {
  outline: none;
  border-color: rgba(99, 102, 241, 0.55);
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

.link-button {
  background: none;
  border: none;
  color: #38bdf8;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  font-size: 0.9rem;
}

.link-button.danger {
  color: #f87171;
}

.primary-button {
  padding: 0.55rem 1.15rem;
  border-radius: 999px;
  border: none;
  background: linear-gradient(120deg, #6366f1, #38bdf8);
  color: #0f172a;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
}

.secondary-button {
  padding: 0.55rem 1.15rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: transparent;
  color: rgba(226, 232, 240, 0.8);
}

.assign-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem;
  align-items: center;
}

.assign-grid .primary-button {
  grid-column: span 2;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.03em;
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

.order-subtext {
  color: rgba(226, 232, 240, 0.65);
  font-size: 0.85rem;
}

.order-subtext.muted {
  color: rgba(148, 163, 184, 0.6);
}

.table-actions {
  white-space: nowrap;
}

.actions-inline {
  display: flex;
  gap: 0.75rem;
}

.empty-row {
  text-align: center;
  padding: 1.75rem 0;
  color: rgba(226, 232, 240, 0.6);
}

.overview-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.insight-card {
  padding: 1.6rem;
  border-radius: 1.25rem;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.18);
  min-height: 240px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.insight-card.highlight {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.2), rgba(14, 165, 233, 0.18));
  border-color: rgba(148, 163, 184, 0.28);
}

.insight-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 1rem;
}

.insight-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.insight-helper {
  color: rgba(226, 232, 240, 0.55);
  font-size: 0.85rem;
}

.sparkline {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 0.75rem;
  align-items: end;
  height: 160px;
}

.spark-bar {
  position: relative;
  background: linear-gradient(180deg, rgba(56, 189, 248, 0.85), rgba(99, 102, 241, 0.65));
  border-radius: 0.75rem 0.75rem 0.35rem 0.35rem;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 0.75rem 0.35rem 0.6rem;
  transition: transform 0.2s ease;
}

.spark-bar:hover {
  transform: translateY(-4px);
}

.spark-amount {
  font-size: 0.8rem;
  font-weight: 600;
  color: rgba(15, 23, 42, 0.9);
  margin-bottom: 0.35rem;
}

.spark-label {
  font-size: 0.75rem;
  color: rgba(15, 23, 42, 0.65);
}

.metric-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.metric-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.metric-bar {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 65%;
}

.metric-fill {
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.85), rgba(14, 165, 233, 0.85));
  box-shadow: 0 6px 12px rgba(14, 165, 233, 0.25);
}

.stat-lines {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.stat-lines li {
  display: flex;
  justify-content: space-between;
  color: rgba(226, 232, 240, 0.85);
}

.positive {
  color: #4ade80;
}

.negative {
  color: #fca5a5;
}

.content-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.content-card {
  background: rgba(15, 23, 42, 0.55);
  border-radius: 1.2rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-height: 100%;
}

.content-card header h3 {
  margin: 0;
  font-size: 1.15rem;
}

.content-card header p {
  margin: 0.4rem 0 0;
  color: rgba(226, 232, 240, 0.6);
  font-size: 0.9rem;
}

.content-form {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
}

.content-form textarea {
  resize: vertical;
  min-height: 90px;
}

.content-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.content-list li {
  padding: 0.85rem;
  border-radius: 0.9rem;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.15);
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.content-list li > div:first-child {
  flex: 1 1 auto;
  min-width: 0;
  overflow-wrap: anywhere;
}

.content-list li strong {
  display: block;
  overflow-wrap: anywhere;
}

.list-actions {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  align-items: flex-end;
}

.muted {
  color: rgba(148, 163, 184, 0.7);
  font-size: 0.85rem;
  margin: 0.15rem 0 0;
  overflow-wrap: anywhere;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

@media (max-width: 1080px) {
  .dashboard-main {
    grid-template-columns: 1fr;
  }

  .sidebar {
    flex-direction: row;
    flex-wrap: wrap;
    margin-bottom: 1.5rem;
  }

  .sidebar-item {
    flex: 1 1 140px;
    justify-content: center;
  }
}
</style>
