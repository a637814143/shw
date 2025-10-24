<template>
  <div class="tourist-page">
    <header class="tourist-nav">
      <div class="nav-left">
        <span class="nav-brand">家政服务平台</span>
      </div>
      <nav class="nav-center" aria-label="游客模式导航">
        <button
          v-for="item in navItems"
          :key="item.value"
          type="button"
          class="nav-link"
          :class="{ active: activeTab === item.value }"
          @click="setActiveTab(item.value)"
        >
          {{ item.label }}
        </button>
      </nav>
      <div class="nav-right">
        <button type="button" class="exit-button" @click="goBack">退出</button>
      </div>
    </header>

    <main class="tourist-content">
      <section v-if="activeTab === 'home'" class="panel home-panel">
        <h1>欢迎体验全流程家政服务平台</h1>
        <p>了解我们的服务、贴士与公告，随时准备好加入平台。</p>
        <figure class="hero-image">
          <img src="https://via.placeholder.com/1200x600?text=%E5%AE%B6%E6%94%BF%E6%9C%8D%E5%8A%A1" alt="家政服务宣传图" />
        </figure>
      </section>

      <section v-else-if="activeTab === 'services'" class="panel">
        <header class="panel-header">
          <div>
            <h2>家政服务</h2>
            <p>以下服务由家政公司实时更新，游客可浏览但暂无法预约。</p>
          </div>
          <div class="service-actions">
            <label class="service-search">
              <span class="sr-only">搜索家政服务</span>
              <input
                v-model.trim="serviceSearch"
                type="search"
                placeholder="搜索服务或公司名称"
                aria-label="搜索家政服务"
              />
            </label>
            <button type="button" class="ghost-button" @click="loadServices" :disabled="servicesLoading">
              刷新列表
            </button>
          </div>
        </header>
        <p v-if="servicesError" class="error-tip">{{ servicesError }}</p>
        <div v-else class="service-grid">
          <article v-for="service in filteredServices" :key="service.id" class="service-card">
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
            <p class="service-note">请登录后预约服务。</p>
          </article>
          <p v-if="!filteredServices.length && !servicesLoading" class="empty-tip">
            {{ serviceSearch ? '未找到匹配的服务，请尝试调整搜索词。' : '暂无家政服务，稍后再来看看。' }}
          </p>
        </div>
      </section>

      <section v-else-if="activeTab === 'tips'" class="panel">
        <header class="panel-header">
          <div>
            <h2>居家贴士</h2>
            <p>管理员发布的居家生活提示，帮助您打造舒适家庭环境。</p>
          </div>
          <button type="button" class="ghost-button" @click="loadTips" :disabled="tipsLoading">刷新内容</button>
        </header>
        <p v-if="tipsError" class="error-tip">{{ tipsError }}</p>
        <ul v-else class="info-list">
          <li v-for="tip in tips" :key="tip.id">
            <h3>{{ tip.title }}</h3>
            <p>{{ tip.content }}</p>
          </li>
          <li v-if="!tips.length && !tipsLoading" class="empty-tip">暂无居家贴士，敬请期待。</li>
        </ul>
      </section>

      <section v-else class="panel">
        <header class="panel-header">
          <div>
            <h2>系统公告</h2>
            <p>及时了解平台动态与重要通知。</p>
          </div>
          <button type="button" class="ghost-button" @click="loadAnnouncements" :disabled="announcementsLoading">
            刷新内容
          </button>
        </header>
        <p v-if="announcementsError" class="error-tip">{{ announcementsError }}</p>
        <ul v-else class="info-list">
          <li v-for="item in announcements" :key="item.id">
            <h3>{{ item.title }}</h3>
            <p>{{ item.content }}</p>
          </li>
          <li v-if="!announcements.length && !announcementsLoading" class="empty-tip">暂无系统公告。</li>
        </ul>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import {
  fetchPublicAnnouncements,
  fetchPublicServices,
  fetchPublicTips,
  type DashboardAnnouncementItem,
  type DashboardTipItem,
  type HousekeepServiceItem,
} from '../services/dashboard'

type TouristTab = 'home' | 'services' | 'tips' | 'announcements'

const router = useRouter()

const activeTab = ref<TouristTab>('home')
const services = ref<HousekeepServiceItem[]>([])
const servicesLoading = ref(false)
const servicesError = ref('')
const serviceSearch = ref('')
const filteredServices = computed(() => {
  if (!serviceSearch.value) {
    return services.value
  }
  const keyword = serviceSearch.value.toLowerCase()
  return services.value.filter((service) => {
    const nameMatch = service.name?.toLowerCase().includes(keyword)
    const companyMatch = service.companyName?.toLowerCase().includes(keyword)
    const descriptionMatch = service.description?.toLowerCase().includes(keyword)
    return nameMatch || companyMatch || descriptionMatch
  })
})

const tips = ref<DashboardTipItem[]>([])
const tipsLoading = ref(false)
const tipsError = ref('')

const announcements = ref<DashboardAnnouncementItem[]>([])
const announcementsLoading = ref(false)
const announcementsError = ref('')

const navItems = computed(() => [
  { label: '首页', value: 'home' as TouristTab },
  { label: '家政服务', value: 'services' as TouristTab },
  { label: '居家贴士', value: 'tips' as TouristTab },
  { label: '系统公告', value: 'announcements' as TouristTab },
])

const setActiveTab = (value: TouristTab) => {
  activeTab.value = value
}

const goBack = () => {
  router.push({ name: 'login' })
}

const loadServices = async () => {
  servicesLoading.value = true
  servicesError.value = ''
  try {
    services.value = await fetchPublicServices()
  } catch (error) {
    servicesError.value =
      error instanceof Error ? error.message : '加载服务列表失败，请稍后再试。'
  } finally {
    servicesLoading.value = false
  }
}

const loadTips = async () => {
  tipsLoading.value = true
  tipsError.value = ''
  try {
    tips.value = await fetchPublicTips()
  } catch (error) {
    tipsError.value =
      error instanceof Error ? error.message : '加载居家贴士失败，请稍后再试。'
  } finally {
    tipsLoading.value = false
  }
}

const loadAnnouncements = async () => {
  announcementsLoading.value = true
  announcementsError.value = ''
  try {
    announcements.value = await fetchPublicAnnouncements()
  } catch (error) {
    announcementsError.value =
      error instanceof Error ? error.message : '加载系统公告失败，请稍后再试。'
  } finally {
    announcementsLoading.value = false
  }
}

onMounted(async () => {
  await Promise.allSettled([loadServices(), loadTips(), loadAnnouncements()])
})
</script>

<style scoped>
.tourist-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

.tourist-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 32px;
  background: #ffffff;
  border-bottom: 1px solid rgba(15, 23, 42, 0.1);
  gap: 16px;
}

.nav-left {
  min-width: 160px;
}

.nav-brand {
  font-size: 24px;
  font-weight: 700;
  color: var(--brand-primary);
}

.nav-center {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-link {
  padding: 10px 18px;
  border-radius: 999px;
  border: none;
  background: transparent;
  color: #475569;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.nav-link:hover,
.nav-link:focus-visible {
  background: rgba(37, 99, 235, 0.12);
  color: var(--brand-primary);
  outline: none;
}

.nav-link.active {
  background: var(--brand-primary);
  color: #ffffff;
}

.nav-right {
  min-width: 120px;
  display: flex;
  justify-content: flex-end;
}

.exit-button {
  padding: 10px 18px;
  border-radius: 8px;
  border: 1px solid var(--brand-border);
  background: #ffffff;
  color: #0f172a;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.exit-button:hover {
  background: rgba(15, 23, 42, 0.04);
  transform: translateY(-1px);
}

.exit-button:focus-visible {
  outline: 3px solid rgba(37, 99, 235, 0.3);
  outline-offset: 2px;
}

.tourist-content {
  flex: 1;
  width: min(1100px, 100%);
  margin: 32px auto;
  padding: 0 20px 40px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.16);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.home-panel h1 {
  font-size: clamp(28px, 3vw + 10px, 40px);
  font-weight: 700;
  color: #0f172a;
}

.home-panel p {
  color: #475569;
  font-size: 16px;
}

.hero-image {
  margin: 0;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.12);
}

.hero-image img {
  width: 100%;
  height: auto;
  display: block;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.service-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.service-search {
  position: relative;
}

.service-search input {
  padding: 10px 16px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.5);
  background: #ffffff;
  min-width: 220px;
  font-size: 14px;
  color: #0f172a;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.service-search input:focus {
  outline: none;
  border-color: var(--brand-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.2);
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.panel-header h2 {
  margin: 0;
  font-size: 24px;
  color: #0f172a;
}

.panel-header p {
  margin: 4px 0 0;
  color: #475569;
  max-width: 520px;
}

.ghost-button {
  padding: 10px 18px;
  border-radius: 999px;
  border: 1px solid var(--brand-border);
  background: transparent;
  color: var(--brand-primary);
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.ghost-button:hover {
  background: rgba(37, 99, 235, 0.12);
}

.ghost-button:focus-visible {
  outline: 3px solid rgba(37, 99, 235, 0.3);
  outline-offset: 2px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.service-card {
  padding: 20px;
  border-radius: 16px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 0 10px 30px rgba(37, 99, 235, 0.08);
}

.service-title {
  margin: 0;
  font-size: 18px;
  color: #0f172a;
}

.service-company {
  margin: 0;
  color: #475569;
}

.service-meta {
  display: grid;
  gap: 12px;
  margin: 0;
  padding: 0;
}

.service-meta div {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #1e293b;
}

.service-meta dt {
  font-weight: 600;
}

.service-meta dd {
  margin: 0;
}

.service-desc {
  margin: 0;
  color: #475569;
  line-height: 1.5;
  font-size: 14px;
}

.service-note {
  margin: 0;
  color: var(--brand-primary);
  font-weight: 600;
  font-size: 14px;
}

.info-list {
  display: grid;
  gap: 16px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.info-list li {
  padding: 20px;
  border-radius: 16px;
  background: #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 10px 30px rgba(37, 99, 235, 0.08);
}

.info-list h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #0f172a;
}

.info-list p {
  margin: 0;
  color: #475569;
  line-height: 1.6;
}

.empty-tip {
  margin: 0;
  padding: 20px;
  text-align: center;
  color: #94a3b8;
}

.error-tip {
  margin: 0;
  color: #dc2626;
  font-weight: 600;
}

@media (max-width: 768px) {
  .tourist-nav {
    flex-direction: column;
    align-items: flex-start;
  }

  .nav-center {
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .nav-right {
    width: 100%;
    justify-content: flex-start;
  }

  .panel {
    padding: 24px;
  }
}
</style>
