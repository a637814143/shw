<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="platform-title">家政服务平台</h1>
        <p class="platform-subtitle">贴心服务，让家更温暖</p>
      </div>
      <div class="user-greeting">您好，{{ username }}！</div>
    </header>
    <div class="dashboard-main">
      <aside class="sidebar">
        <nav class="menu" aria-label="主导航">
          <button
            v-for="item in menuItems"
            :key="item.key"
            type="button"
            class="menu-item"
            :class="{ active: activeSection === item.key, highlight: item.highlight }"
            @click="switchSection(item.key)"
          >
            <span class="menu-icon" aria-hidden="true">{{ item.icon }}</span>
            {{ item.label }}
          </button>
        </nav>
      </aside>
      <section class="content">
        <div v-if="activeSection === 'home'" class="home-section">
          <div class="hero">
            <div>
              <h2 class="hero-title">欢迎回来</h2>
              <p class="hero-subtitle">在这里管理家政服务、贴士、评价与特惠内容</p>
            </div>
            <div class="hero-summary">
              <div class="summary-card">
                <span class="summary-value">{{ serviceItems.length }}</span>
                <span class="summary-label">家政服务</span>
              </div>
              <div class="summary-card">
                <span class="summary-value">{{ tipItems.length }}</span>
                <span class="summary-label">居家贴士</span>
              </div>
              <div class="summary-card">
                <span class="summary-value">{{ reviewItems.length }}</span>
                <span class="summary-label">服务评价</span>
              </div>
              <div class="summary-card">
                <span class="summary-value">{{ offerItems.length }}</span>
                <span class="summary-label">特惠活动</span>
              </div>
            </div>
          </div>
          <div class="quick-glance">
            <div class="glance-card">
              <h3>热门服务</h3>
              <ul>
                <li v-for="service in serviceItems.slice(0, 4)" :key="service.id">
                  <span class="glance-icon">{{ service.icon }}</span>
                  <div>
                    <p class="glance-title">{{ service.name }}</p>
                    <p class="glance-desc">{{ service.description }}</p>
                  </div>
                </li>
                <li v-if="!serviceItems.length" class="empty-tip">暂无服务，请先新增服务项目。</li>
              </ul>
            </div>
            <div class="glance-card">
              <h3>精选贴士</h3>
              <ul>
                <li v-for="tip in tipItems.slice(0, 3)" :key="tip.id">
                  <p class="glance-title">{{ tip.title }}</p>
                  <p class="glance-desc">{{ tip.content }}</p>
                </li>
                <li v-if="!tipItems.length" class="empty-tip">暂无贴士内容。</li>
              </ul>
            </div>
          </div>
        </div>

        <div v-else-if="activeSection === 'services'" class="section-panel">
          <div class="section-header">
            <div>
              <h2>家政服务管理</h2>
              <p>新增或维护家政服务项目，丰富首页展示。</p>
            </div>
            <button class="primary-button" type="button" @click="openServiceForm()">新增服务</button>
          </div>

          <div v-if="serviceFormVisible" class="form-card">
            <form class="form-grid" @submit.prevent="submitServiceForm">
              <div class="form-field">
                <label for="service-name">服务名称</label>
                <input id="service-name" v-model="serviceForm.name" type="text" placeholder="如：日常保洁" />
              </div>
              <div class="form-field">
                <label for="service-icon">图标</label>
                <input id="service-icon" v-model="serviceForm.icon" type="text" placeholder="输入emoji或短标签" />
              </div>
              <div class="form-field form-field-full">
                <label for="service-description">服务描述</label>
                <textarea
                  id="service-description"
                  v-model="serviceForm.description"
                  rows="2"
                  placeholder="为用户介绍该服务的亮点"
                ></textarea>
              </div>
              <div class="form-actions">
                <button class="secondary-button" type="button" @click="closeServiceForm">取消</button>
                <button class="primary-button" type="submit" :disabled="serviceFormLoading">
                  {{ serviceFormLoading ? '保存中…' : serviceFormSubmitText }}
                </button>
              </div>
            </form>
          </div>

          <div class="data-card">
            <table class="data-table">
              <thead>
                <tr>
                  <th>图标</th>
                  <th>服务名称</th>
                  <th>描述</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="service in serviceItems" :key="service.id">
                  <td class="icon-cell">{{ service.icon }}</td>
                  <td>{{ service.name }}</td>
                  <td>{{ service.description }}</td>
                  <td class="table-actions">
                    <button class="link-button" type="button" @click="openServiceForm(service)">编辑</button>
                    <button class="link-button danger" type="button" @click="handleDeleteService(service.id)">
                      删除
                    </button>
                  </td>
                </tr>
                <tr v-if="!serviceItems.length">
                  <td colspan="4" class="empty-row">暂无服务数据，请先新增服务。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-else-if="activeSection === 'tips'" class="section-panel">
          <div class="section-header">
            <div>
              <h2>居家小贴士</h2>
              <p>分享生活妙招，为用户提供实用建议。</p>
            </div>
            <button class="primary-button" type="button" @click="openTipForm()">新增贴士</button>
          </div>

          <div v-if="tipFormVisible" class="form-card">
            <form class="form-grid" @submit.prevent="submitTipForm">
              <div class="form-field form-field-full">
                <label for="tip-title">贴士标题</label>
                <input id="tip-title" v-model="tipForm.title" type="text" placeholder="如：厨房清洁小妙招" />
              </div>
              <div class="form-field form-field-full">
                <label for="tip-content">贴士内容</label>
                <textarea
                  id="tip-content"
                  v-model="tipForm.content"
                  rows="3"
                  placeholder="详细描述贴士内容"
                ></textarea>
              </div>
              <div class="form-actions">
                <button class="secondary-button" type="button" @click="closeTipForm">取消</button>
                <button class="primary-button" type="submit" :disabled="tipFormLoading">
                  {{ tipFormLoading ? '保存中…' : tipFormSubmitText }}
                </button>
              </div>
            </form>
          </div>

          <div class="data-card">
            <table class="data-table">
              <thead>
                <tr>
                  <th>标题</th>
                  <th>内容</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tip in tipItems" :key="tip.id">
                  <td>{{ tip.title }}</td>
                  <td>{{ tip.content }}</td>
                  <td class="table-actions">
                    <button class="link-button" type="button" @click="openTipForm(tip)">编辑</button>
                    <button class="link-button danger" type="button" @click="handleDeleteTip(tip.id)">删除</button>
                  </td>
                </tr>
                <tr v-if="!tipItems.length">
                  <td colspan="3" class="empty-row">暂无贴士内容。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-else-if="activeSection === 'reviews'" class="section-panel">
          <div class="section-header">
            <div>
              <h2>服务评价</h2>
              <p>记录真实用户反馈，提升服务品质。</p>
            </div>
            <button class="primary-button" type="button" @click="openReviewForm()">新增评价</button>
          </div>

          <div v-if="reviewFormVisible" class="form-card">
            <form class="form-grid" @submit.prevent="submitReviewForm">
              <div class="form-field">
                <label for="review-author">评价人</label>
                <input id="review-author" v-model="reviewForm.author" type="text" placeholder="如：李女士" />
              </div>
              <div class="form-field">
                <label for="review-service">服务项目</label>
                <input id="review-service" v-model="reviewForm.serviceName" type="text" placeholder="如：深度保洁" />
              </div>
              <div class="form-field">
                <label for="review-rating">评分</label>
                <input
                  id="review-rating"
                  v-model.number="reviewForm.rating"
                  type="number"
                  min="1"
                  max="5"
                  placeholder="1-5"
                />
              </div>
              <div class="form-field form-field-full">
                <label for="review-content">评价内容</label>
                <textarea
                  id="review-content"
                  v-model="reviewForm.content"
                  rows="3"
                  placeholder="填写详细评价"
                ></textarea>
              </div>
              <div class="form-actions">
                <button class="secondary-button" type="button" @click="closeReviewForm">取消</button>
                <button class="primary-button" type="submit" :disabled="reviewFormLoading">
                  {{ reviewFormLoading ? '保存中…' : reviewFormSubmitText }}
                </button>
              </div>
            </form>
          </div>

          <div class="data-card">
            <table class="data-table">
              <thead>
                <tr>
                  <th>评价人</th>
                  <th>服务项目</th>
                  <th>评分</th>
                  <th>内容</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="review in reviewItems" :key="review.id">
                  <td>{{ review.author }}</td>
                  <td>{{ review.serviceName }}</td>
                  <td>
                    <span class="rating">{{ review.rating }} ⭐</span>
                  </td>
                  <td>{{ review.content }}</td>
                  <td class="table-actions">
                    <button class="link-button" type="button" @click="openReviewForm(review)">编辑</button>
                    <button class="link-button danger" type="button" @click="handleDeleteReview(review.id)">
                      删除
                    </button>
                  </td>
                </tr>
                <tr v-if="!reviewItems.length">
                  <td colspan="5" class="empty-row">暂无评价记录。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-else-if="activeSection === 'offers'" class="section-panel">
          <div class="section-header">
            <div>
              <h2>特惠活动</h2>
              <p>设置限时优惠，吸引更多用户关注。</p>
            </div>
            <button class="primary-button" type="button" @click="openOfferForm()">新增特惠</button>
          </div>

          <div v-if="offerFormVisible" class="form-card">
            <form class="form-grid" @submit.prevent="submitOfferForm">
              <div class="form-field form-field-full">
                <label for="offer-title">特惠标题</label>
                <input id="offer-title" v-model="offerForm.title" type="text" placeholder="如：新客8折" />
              </div>
              <div class="form-field">
                <label for="offer-tag">标签</label>
                <input id="offer-tag" v-model="offerForm.tag" type="text" placeholder="如：限时" />
              </div>
              <div class="form-field form-field-full">
                <label for="offer-content">特惠内容</label>
                <textarea
                  id="offer-content"
                  v-model="offerForm.content"
                  rows="3"
                  placeholder="描述优惠详情"
                ></textarea>
              </div>
              <div class="form-actions">
                <button class="secondary-button" type="button" @click="closeOfferForm">取消</button>
                <button class="primary-button" type="submit" :disabled="offerFormLoading">
                  {{ offerFormLoading ? '保存中…' : offerFormSubmitText }}
                </button>
              </div>
            </form>
          </div>

          <div class="data-card">
            <table class="data-table">
              <thead>
                <tr>
                  <th>标题</th>
                  <th>标签</th>
                  <th>内容</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="offer in offerItems" :key="offer.id">
                  <td>{{ offer.title }}</td>
                  <td>{{ offer.tag || '—' }}</td>
                  <td>{{ offer.content }}</td>
                  <td class="table-actions">
                    <button class="link-button" type="button" @click="openOfferForm(offer)">编辑</button>
                    <button class="link-button danger" type="button" @click="handleDeleteOffer(offer.id)">删除</button>
                  </td>
                </tr>
                <tr v-if="!offerItems.length">
                  <td colspan="4" class="empty-row">暂无特惠活动。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'

import { AUTH_ACCOUNT_KEY } from '../constants/auth'
import {
  createOffer,
  createReview,
  createService,
  createTip,
  deleteOffer,
  deleteReview,
  deleteService,
  deleteTip,
  fetchOffers,
  fetchReviews,
  fetchServices,
  fetchTips,
  type DashboardOfferItem,
  type DashboardReviewItem,
  type DashboardServiceItem,
  type DashboardTipItem,
  type OfferPayload,
  type ReviewPayload,
  type ServicePayload,
  type TipPayload,
  updateOffer,
  updateReview,
  updateService,
  updateTip,
} from '../services/dashboard'

type SectionKey = 'home' | 'services' | 'tips' | 'reviews' | 'offers'

type MenuItem = {
  key: SectionKey
  label: string
  icon: string
  highlight?: boolean
}

const username = computed(() => sessionStorage.getItem(AUTH_ACCOUNT_KEY) ?? '用户')

const menuItems: MenuItem[] = [
  { key: 'home', label: '首页', icon: '🏠' },
  { key: 'services', label: '家政服务', icon: '🧽' },
  { key: 'tips', label: '居家小贴士', icon: '💡' },
  { key: 'reviews', label: '服务评价', icon: '⭐' },
  { key: 'offers', label: '特惠', icon: '🎁', highlight: true },
]

const activeSection = ref<SectionKey>('home')

const serviceItems = ref<DashboardServiceItem[]>([])
const tipItems = ref<DashboardTipItem[]>([])
const reviewItems = ref<DashboardReviewItem[]>([])
const offerItems = ref<DashboardOfferItem[]>([])

const serviceFormVisible = ref(false)
const serviceFormLoading = ref(false)
const editingServiceId = ref<number | null>(null)
const serviceForm = reactive<ServicePayload>({ name: '', description: '', icon: '' })

const tipFormVisible = ref(false)
const tipFormLoading = ref(false)
const editingTipId = ref<number | null>(null)
const tipForm = reactive<TipPayload>({ title: '', content: '' })

const reviewFormVisible = ref(false)
const reviewFormLoading = ref(false)
const editingReviewId = ref<number | null>(null)
const reviewForm = reactive<ReviewPayload>({ author: '', serviceName: '', rating: 5, content: '' })

const offerFormVisible = ref(false)
const offerFormLoading = ref(false)
const editingOfferId = ref<number | null>(null)
const offerForm = reactive<OfferPayload>({ title: '', content: '', tag: '' })

const serviceFormSubmitText = computed(() => (editingServiceId.value ? '保存修改' : '保存服务'))
const tipFormSubmitText = computed(() => (editingTipId.value ? '保存修改' : '保存贴士'))
const reviewFormSubmitText = computed(() => (editingReviewId.value ? '保存修改' : '保存评价'))
const offerFormSubmitText = computed(() => (editingOfferId.value ? '保存修改' : '保存特惠'))

const switchSection = (section: SectionKey) => {
  activeSection.value = section
  if (section === 'services' && !serviceItems.value.length) loadServices()
  if (section === 'tips' && !tipItems.value.length) loadTips()
  if (section === 'reviews' && !reviewItems.value.length) loadReviews()
  if (section === 'offers' && !offerItems.value.length) loadOffers()
}

const resetServiceForm = () => {
  serviceForm.name = ''
  serviceForm.description = ''
  serviceForm.icon = ''
  editingServiceId.value = null
}

const openServiceForm = (item?: DashboardServiceItem) => {
  if (item) {
    serviceForm.name = item.name
    serviceForm.description = item.description
    serviceForm.icon = item.icon
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
  if (!serviceForm.name.trim() || !serviceForm.description.trim() || !serviceForm.icon.trim()) {
    window.alert('请完整填写服务信息')
    return
  }
  serviceFormLoading.value = true
  try {
    const payload: ServicePayload = {
      name: serviceForm.name.trim(),
      description: serviceForm.description.trim(),
      icon: serviceForm.icon.trim(),
    }
    if (editingServiceId.value) {
      await updateService(editingServiceId.value, payload)
    } else {
      await createService(payload)
    }
    await loadServices()
    closeServiceForm()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '保存服务失败')
  } finally {
    serviceFormLoading.value = false
  }
}

const handleDeleteService = async (id: number) => {
  if (!window.confirm('确定要删除该服务吗？')) return
  try {
    await deleteService(id)
    await loadServices()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除服务失败')
  }
}

const resetTipForm = () => {
  tipForm.title = ''
  tipForm.content = ''
  editingTipId.value = null
}

const openTipForm = (item?: DashboardTipItem) => {
  if (item) {
    tipForm.title = item.title
    tipForm.content = item.content
    editingTipId.value = item.id
  } else {
    resetTipForm()
  }
  tipFormVisible.value = true
}

const closeTipForm = () => {
  tipFormVisible.value = false
  resetTipForm()
}

const submitTipForm = async () => {
  if (!tipForm.title.trim() || !tipForm.content.trim()) {
    window.alert('请填写贴士标题和内容')
    return
  }
  tipFormLoading.value = true
  try {
    const payload: TipPayload = {
      title: tipForm.title.trim(),
      content: tipForm.content.trim(),
    }
    if (editingTipId.value) {
      await updateTip(editingTipId.value, payload)
    } else {
      await createTip(payload)
    }
    await loadTips()
    closeTipForm()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '保存贴士失败')
  } finally {
    tipFormLoading.value = false
  }
}

const handleDeleteTip = async (id: number) => {
  if (!window.confirm('确定要删除该贴士吗？')) return
  try {
    await deleteTip(id)
    await loadTips()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除贴士失败')
  }
}

const resetReviewForm = () => {
  reviewForm.author = ''
  reviewForm.serviceName = ''
  reviewForm.rating = 5
  reviewForm.content = ''
  editingReviewId.value = null
}

const openReviewForm = (item?: DashboardReviewItem) => {
  if (item) {
    reviewForm.author = item.author
    reviewForm.serviceName = item.serviceName
    reviewForm.rating = item.rating
    reviewForm.content = item.content
    editingReviewId.value = item.id
  } else {
    resetReviewForm()
  }
  reviewFormVisible.value = true
}

const closeReviewForm = () => {
  reviewFormVisible.value = false
  resetReviewForm()
}

const submitReviewForm = async () => {
  if (!reviewForm.author.trim() || !reviewForm.serviceName.trim() || !reviewForm.content.trim()) {
    window.alert('请完整填写评价信息')
    return
  }
  if (reviewForm.rating < 1 || reviewForm.rating > 5) {
    window.alert('评分范围为1-5分')
    return
  }
  reviewFormLoading.value = true
  try {
    const payload: ReviewPayload = {
      author: reviewForm.author.trim(),
      serviceName: reviewForm.serviceName.trim(),
      rating: reviewForm.rating,
      content: reviewForm.content.trim(),
    }
    if (editingReviewId.value) {
      await updateReview(editingReviewId.value, payload)
    } else {
      await createReview(payload)
    }
    await loadReviews()
    closeReviewForm()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '保存评价失败')
  } finally {
    reviewFormLoading.value = false
  }
}

const handleDeleteReview = async (id: number) => {
  if (!window.confirm('确定要删除该评价吗？')) return
  try {
    await deleteReview(id)
    await loadReviews()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除评价失败')
  }
}

const resetOfferForm = () => {
  offerForm.title = ''
  offerForm.content = ''
  offerForm.tag = ''
  editingOfferId.value = null
}

const openOfferForm = (item?: DashboardOfferItem) => {
  if (item) {
    offerForm.title = item.title
    offerForm.content = item.content
    offerForm.tag = item.tag ?? ''
    editingOfferId.value = item.id
  } else {
    resetOfferForm()
  }
  offerFormVisible.value = true
}

const closeOfferForm = () => {
  offerFormVisible.value = false
  resetOfferForm()
}

const submitOfferForm = async () => {
  if (!offerForm.title.trim() || !offerForm.content.trim()) {
    window.alert('请填写特惠标题和内容')
    return
  }
  offerFormLoading.value = true
  try {
    const payload: OfferPayload = {
      title: offerForm.title.trim(),
      content: offerForm.content.trim(),
      tag: offerForm.tag?.trim() ? offerForm.tag.trim() : undefined,
    }
    if (editingOfferId.value) {
      await updateOffer(editingOfferId.value, payload)
    } else {
      await createOffer(payload)
    }
    await loadOffers()
    closeOfferForm()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '保存特惠失败')
  } finally {
    offerFormLoading.value = false
  }
}

const handleDeleteOffer = async (id: number) => {
  if (!window.confirm('确定要删除该特惠吗？')) return
  try {
    await deleteOffer(id)
    await loadOffers()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除特惠失败')
  }
}

const loadServices = async () => {
  try {
    serviceItems.value = await fetchServices()
  } catch (error) {
    console.error(error)
  }
}

const loadTips = async () => {
  try {
    tipItems.value = await fetchTips()
  } catch (error) {
    console.error(error)
  }
}

const loadReviews = async () => {
  try {
    reviewItems.value = await fetchReviews()
  } catch (error) {
    console.error(error)
  }
}

const loadOffers = async () => {
  try {
    offerItems.value = await fetchOffers()
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await Promise.all([loadServices(), loadTips(), loadReviews(), loadOffers()])
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: 'Source Han Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #eef2f6;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8f9fa;
  padding: 24px 40px;
  box-shadow: 0 2px 10px rgba(30, 58, 95, 0.06);
}

.platform-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #1e3a5f;
  letter-spacing: 2px;
}

.platform-subtitle {
  margin: 4px 0 0;
  color: #4b5563;
  font-size: 14px;
}

.user-greeting {
  font-size: 16px;
  color: #1e3a5f;
  font-weight: 600;
}

.dashboard-main {
  flex: 1;
  display: flex;
  min-height: 0;
}

.sidebar {
  width: 260px;
  background-color: #1e3a5f;
  padding: 32px 20px;
  color: #ffffff;
}

.menu {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  color: inherit;
  background: transparent;
  border: none;
  text-align: left;
  font-size: 16px;
  font-weight: 500;
  transition: background-color 0.2s ease, transform 0.2s ease;
  cursor: pointer;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.12);
  transform: translateX(4px);
}

.menu-item.active {
  background-color: rgba(255, 255, 255, 0.18);
}

.menu-item.highlight {
  background-color: rgba(255, 107, 53, 0.16);
  color: #ff6b35;
  font-weight: 600;
}

.menu-item.highlight.active {
  background-color: rgba(255, 107, 53, 0.24);
  color: #ff6b35;
}

.menu-item.highlight:hover {
  background-color: rgba(255, 107, 53, 0.24);
}

.menu-icon {
  font-size: 20px;
}

.content {
  flex: 1;
  padding: 40px 48px;
  background-color: #ffffff;
  overflow-y: auto;
}

.hero {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fb 0%, #ffffff 100%);
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  margin-bottom: 32px;
}

.hero-title {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  color: #1e3a5f;
}

.hero-subtitle {
  margin-top: 8px;
  font-size: 16px;
  color: #4b5563;
}

.hero-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
}

.summary-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 12px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(30, 58, 95, 0.08);
  min-width: 120px;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e3a5f;
}

.summary-label {
  margin-top: 4px;
  font-size: 14px;
  color: #6b7280;
}

.quick-glance {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.glance-card {
  padding: 24px;
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  background-color: #fafbff;
  box-shadow: 0 10px 28px rgba(30, 58, 95, 0.08);
}

.glance-card h3 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #1e3a5f;
}

.glance-card ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.glance-card li {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.glance-icon {
  font-size: 20px;
}

.glance-title {
  margin: 0;
  font-weight: 600;
  color: #1f2937;
}

.glance-desc {
  margin: 4px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.empty-tip {
  color: #9ca3af;
}

.section-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #1e3a5f;
}

.section-header p {
  margin: 6px 0 0;
  color: #4b5563;
}

.primary-button {
  padding: 10px 18px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.25);
}

.primary-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.secondary-button {
  padding: 10px 18px;
  border-radius: 999px;
  border: 1px solid #cbd5f5;
  background: #ffffff;
  color: #1e3a5f;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}

.form-card {
  padding: 24px;
  border-radius: 20px;
  border: 1px solid #e5e7eb;
  background-color: #ffffff;
  box-shadow: 0 12px 32px rgba(30, 58, 95, 0.12);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field-full {
  grid-column: 1 / -1;
}

.form-field label {
  font-weight: 600;
  color: #1e3a5f;
}

.form-field input,
.form-field textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #cdd5e5;
  border-radius: 10px;
  font-size: 14px;
  color: #1f2937;
  background-color: #f8fafc;
}

.form-field textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  grid-column: 1 / -1;
}

.data-card {
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  box-shadow: 0 10px 28px rgba(30, 58, 95, 0.1);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table thead {
  background-color: #f1f5f9;
  color: #1e3a5f;
}

.data-table th,
.data-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  vertical-align: top;
}

.data-table tbody tr:last-child td {
  border-bottom: none;
}

.icon-cell {
  font-size: 20px;
}

.table-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: flex-start;
}

.link-button {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
  font-size: 14px;
  padding: 0;
}

.link-button.danger {
  color: #ef4444;
}

.empty-row {
  text-align: center;
  color: #9ca3af;
  padding: 24px 16px;
}

.rating {
  font-weight: 600;
  color: #f59e0b;
}

@media (max-width: 960px) {
  .dashboard-main {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    display: flex;
    justify-content: center;
  }

  .menu {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .menu-item {
    flex: 0 0 auto;
  }
}
</style>
