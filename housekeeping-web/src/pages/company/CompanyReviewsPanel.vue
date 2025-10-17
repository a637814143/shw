<template>
  <section class="reviews-panel">
    <header class="reviews-header">
      <div>
        <h2 class="reviews-title">服务口碑雷达</h2>
        <p class="reviews-subtitle">
          平均评分
          <strong class="metric-highlight">{{ averageRating }}</strong>
          ，好评率
          <strong class="metric-highlight">{{ positiveRate }}</strong>
          ，精选评价 {{ pinnedCount }} 条
        </p>
      </div>
      <button type="button" class="ghost-button" @click="emit('refresh')">
        <span class="button-icon">⟳</span>
        刷新评价
      </button>
    </header>

    <div v-if="loading" class="reviews-loading" role="status">正在更新最新口碑…</div>

    <ul v-else-if="reviews.length" class="reviews-list">
      <li v-for="item in reviews" :key="item.id" class="review-card" :class="{ pinned: item.pinned }">
        <div class="review-card-header">
          <div class="service-name">{{ item.serviceName }}</div>
          <div class="rating-display" :aria-label="`评分 ${item.rating} 分`">
            <span v-for="index in 5" :key="index" class="rating-star" :class="{ active: index <= item.rating }">★</span>
            <span class="rating-number">{{ item.rating }}</span>
          </div>
        </div>

        <p class="review-content">{{ item.content || '用户未留下文字评价' }}</p>

        <div class="review-meta">
          <span class="meta-author">{{ item.username }}</span>
          <span class="meta-time">{{ formatDate(item.createdAt) }}</span>
        </div>

        <div v-if="item.companyReply" class="review-reply" role="note">
          <div class="reply-badge">商家回复</div>
          <p class="reply-content">{{ item.companyReply }}</p>
          <span class="reply-time">{{ formatDate(item.replyAt || item.updatedAt) }}</span>
        </div>
      </li>
    </ul>

    <div v-else class="reviews-empty">
      <h3>还没有评价</h3>
      <p>完成更多订单以收集客户反馈，口碑展示将自动呈现。</p>
      <button type="button" class="ghost-button" @click="emit('refresh')">立即同步</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import type { ServiceReviewItem } from '../../services/dashboard'

const props = defineProps<{
  reviews: ServiceReviewItem[]
  loading: boolean
}>()

const emit = defineEmits<{
  (event: 'refresh'): void
}>()

const averageRating = computed(() => {
  if (!props.reviews.length) return '0.0'
  const total = props.reviews.reduce((sum, item) => sum + item.rating, 0)
  return (total / props.reviews.length).toFixed(1)
})

const positiveRate = computed(() => {
  if (!props.reviews.length) return '0%'
  const positive = props.reviews.filter((item) => item.rating >= 4).length
  return `${Math.round((positive / props.reviews.length) * 100)}%`
})

const pinnedCount = computed(() => props.reviews.filter((item) => item.pinned).length)

const formatDate = (value?: string | null) => {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}
</script>

<style scoped>
.reviews-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: relative;
}

.reviews-panel::before {
  content: '';
  position: absolute;
  inset: 12px;
  border-radius: calc(var(--brand-radius) + 12px);
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.18), rgba(16, 185, 129, 0.12));
  opacity: 0.6;
  filter: blur(32px);
  z-index: -1;
}

.reviews-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.reviews-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.reviews-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--brand-text-muted);
}

.metric-highlight {
  font-size: 18px;
  font-weight: 700;
  color: var(--brand-primary);
  margin: 0 6px;
}

.ghost-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 999px;
  border: 1px solid rgba(59, 130, 246, 0.4);
  background: rgba(255, 255, 255, 0.2);
  color: var(--brand-primary);
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
  backdrop-filter: blur(10px);
}

.ghost-button:hover {
  background: rgba(59, 130, 246, 0.28);
  transform: translateY(-1px);
}

.button-icon {
  font-size: 14px;
}

.reviews-loading,
.reviews-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 48px 16px;
  border-radius: calc(var(--brand-radius) + 6px);
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(148, 163, 184, 0.18);
  text-align: center;
  color: var(--brand-text-muted);
}

.reviews-empty h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--brand-text);
}

.reviews-list {
  list-style: none;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
  padding: 0;
  margin: 0;
}

.review-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  border-radius: calc(var(--brand-radius) + 4px);
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 35px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(12px);
  position: relative;
  overflow: hidden;
}

.review-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(320px circle at 80% 0%, rgba(59, 130, 246, 0.18), transparent 55%);
  pointer-events: none;
}

.review-card.pinned {
  border-color: rgba(250, 204, 21, 0.6);
  box-shadow: 0 22px 45px rgba(250, 204, 21, 0.25);
}

.review-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.service-name {
  font-weight: 700;
  font-size: 16px;
}

.rating-display {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 700;
  color: var(--brand-warning);
}

.rating-star {
  opacity: 0.25;
  transition: opacity 0.2s ease;
}

.rating-star.active {
  opacity: 1;
}

.rating-number {
  margin-left: 4px;
  font-size: 14px;
  color: var(--brand-text);
}

.review-content {
  margin: 0;
  font-size: 14px;
  color: var(--brand-text);
  line-height: 1.6;
}

.review-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: var(--brand-text-muted);
}

.review-reply {
  margin-top: 8px;
  padding: 14px;
  border-radius: calc(var(--brand-radius) + 2px);
  background: rgba(59, 130, 246, 0.08);
  border: 1px solid rgba(59, 130, 246, 0.18);
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: var(--brand-text);
}

.reply-badge {
  align-self: flex-start;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.16);
  color: var(--brand-primary);
  font-size: 12px;
  font-weight: 600;
}

.reply-content {
  margin: 0;
  font-size: 14px;
}

.reply-time {
  font-size: 12px;
  color: var(--brand-text-muted);
  align-self: flex-end;
}

@media (max-width: 1024px) {
  .reviews-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .reviews-list {
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  }
}
</style>
