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
        <div class="hero">
          <div class="hero-copy">
            <p class="hero-eyebrow">游客专享体验</p>
            <h1>欢迎来到全流程家政服务平台</h1>
            <p>
              在这里快速了解平台提供的优质家政服务、居家生活贴士与最新系统公告，为您的智慧家庭生活提前做足准备。
            </p>
            <div class="hero-actions">
              <button type="button" class="cta-button" @click="setActiveTab('services')">浏览服务</button>
              <button type="button" class="ghost-link" @click="setActiveTab('tips')">查看居家贴士</button>
            </div>
            <dl class="hero-stats" aria-label="平台亮点数据">
              <div>
                <dt>200+</dt>
                <dd>认证家政公司</dd>
              </div>
              <div>
                <dt>1,500+</dt>
                <dd>专业服务项目</dd>
              </div>
              <div>
                <dt>97%</dt>
                <dd>用户满意度</dd>
              </div>
            </dl>
          </div>
          <figure class="hero-visual" aria-labelledby="tourist-hero-caption">
            <img src="https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=1200&q=80" alt="家政人员正在整理客厅" />
            <figcaption id="tourist-hero-caption">真实家政服务场景展示</figcaption>
          </figure>
        </div>

        <div class="home-highlights">
          <article class="highlight-card">
            <h2>精选家政服务</h2>
            <p>根据家庭需求提供保洁、育儿、养老等多场景服务，并支持公司端实时更新。</p>
          </article>
          <article class="highlight-card">
            <h2>贴心生活指南</h2>
            <p>管理员持续发布居家贴士，让每一次家政选择都更从容。</p>
          </article>
          <article class="highlight-card">
            <h2>及时系统公告</h2>
            <p>第一时间掌握平台动态与保障政策，放心体验各项功能。</p>
          </article>
        </div>

        <div class="home-steps" aria-label="平台体验流程">
          <h2>如何开始体验</h2>
          <ol>
            <li>
              <span class="step-index">01</span>
              <div>
                <h3>浏览游客专区</h3>
                <p>通过游客模式了解平台服务版块、最新贴士和公告。</p>
              </div>
            </li>
            <li>
              <span class="step-index">02</span>
              <div>
                <h3>注册或登录</h3>
                <p>完成账号注册后即可预约服务、收藏常用项目。</p>
              </div>
            </li>
            <li>
              <span class="step-index">03</span>
              <div>
                <h3>预约优质服务</h3>
                <p>选择合适的家政公司和服务时长，享受一站式服务体验。</p>
              </div>
            </li>
          </ol>
        </div>
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
              <div>
                <dt>服务时长</dt>
                <dd>{{ service.serviceTime }}</dd>
              </div>
              <div>
                <dt>空闲人员</dt>
                <dd>{{ service.availableStaffCount }} 人</dd>
              </div>
            </dl>
            <p v-if="service.description" class="service-desc">{{ service.description }}</p>
            <footer class="service-card-footer">
              <span v-if="service.categoryName" class="service-category-chip">{{ service.categoryName }}</span>
              <p class="service-note">请登录后预约服务。</p>
            </footer>
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
    const timeMatch = service.serviceTime?.toLowerCase().includes(keyword)
    return nameMatch || companyMatch || descriptionMatch || timeMatch
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

.home-panel {
  gap: 32px;
}

.hero {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 32px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(14, 165, 233, 0.1));
  border-radius: 20px;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(37, 99, 235, 0.25), transparent 55%);
  pointer-events: none;
}

.hero-copy {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  color: #0f172a;
}

.hero-eyebrow {
  margin: 0;
  font-size: 14px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  font-weight: 700;
  color: var(--brand-primary);
}

.hero-copy h1 {
  margin: 0;
  font-size: clamp(32px, 3.6vw + 6px, 46px);
  font-weight: 700;
  line-height: 1.15;
}

.hero-copy p {
  margin: 0;
  color: #1e293b;
  font-size: 16px;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.cta-button {
  padding: 12px 24px;
  border-radius: 999px;
  border: none;
  background: var(--brand-primary);
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 12px 30px rgba(37, 99, 235, 0.24);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.cta-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 16px 34px rgba(37, 99, 235, 0.28);
}

.cta-button:focus-visible {
  outline: 3px solid rgba(59, 130, 246, 0.5);
  outline-offset: 3px;
}

.ghost-link {
  padding: 12px 22px;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.16);
  background: rgba(255, 255, 255, 0.85);
  color: #0f172a;
  font-weight: 600;
  cursor: pointer;
  backdrop-filter: blur(4px);
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.ghost-link:hover {
  border-color: rgba(15, 23, 42, 0.3);
  background: #ffffff;
}

.ghost-link:focus-visible {
  outline: 3px solid rgba(37, 99, 235, 0.3);
  outline-offset: 3px;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin: 8px 0 0;
  padding: 0;
}

.hero-stats div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hero-stats dt {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #0f172a;
}

.hero-stats dd {
  margin: 0;
  color: #475569;
  font-size: 14px;
}

.hero-visual {
  position: relative;
  margin: 0;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.18);
  background: #0f172a;
}

.hero-visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.hero-visual figcaption {
  position: absolute;
  inset: auto 16px 16px 16px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.7);
  color: #f1f5f9;
  font-size: 12px;
  letter-spacing: 0.04em;
}

.home-highlights {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.highlight-card {
  padding: 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(226, 232, 240, 0.6));
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 14px 34px rgba(37, 99, 235, 0.12);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.highlight-card h2 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.highlight-card p {
  margin: 0;
  color: #475569;
  line-height: 1.6;
}

.home-steps {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.home-steps h2 {
  margin: 0;
  font-size: 22px;
  color: #0f172a;
}

.home-steps ol {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 18px;
}

.home-steps li {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  background: #f8fafc;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  box-shadow: 0 12px 30px rgba(37, 99, 235, 0.08);
}

.home-steps h3 {
  margin: 0 0 6px;
  font-size: 18px;
  color: #0f172a;
}

.home-steps p {
  margin: 0;
  color: #475569;
  line-height: 1.6;
}

.step-index {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: var(--brand-primary);
  color: #ffffff;
  font-weight: 700;
  font-size: 16px;
  box-shadow: 0 10px 26px rgba(37, 99, 235, 0.2);
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

.service-card-footer {
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.service-category-chip {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: var(--brand-primary);
  font-size: 13px;
  font-weight: 600;
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

  .hero {
    grid-template-columns: 1fr;
    padding: 28px;
  }

  .hero-visual {
    height: 220px;
  }

  .hero-visual figcaption {
    inset: auto 12px 12px 12px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .home-steps li {
    flex-direction: column;
    align-items: stretch;
  }

  .step-index {
    width: 36px;
    height: 36px;
    font-size: 15px;
  }
}
</style>
