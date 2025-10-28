<template>
  <section class="messaging-panel">
    <div class="messaging-shell">
      <aside class="thread-rail">
        <header class="thread-header">
          <div class="thread-title-block">
            <h2>客服沟通中心</h2>
            <label class="thread-search">
              <span aria-hidden="true" class="thread-search-icon">🔍</span>
              <input
                v-model="searchTerm"
                type="search"
                name="conversation-search"
                autocomplete="off"
                placeholder="搜索服务、公司或消息"
                aria-label="搜索服务、公司或消息"
              />
            </label>
          </div>
          <button
            type="button"
            class="icon-button"
            aria-label="刷新会话"
            @click="emit('refreshConversations')"
            :disabled="loadingConversations"
          >
            <span aria-hidden="true">⟳</span>
          </button>
        </header>

        <div v-if="loadingConversations" class="thread-empty" role="status">正在加载沟通列表…</div>

        <ul v-else-if="filteredConversations.length" class="thread-list">
          <li
            v-for="item in filteredConversations"
            :key="item.orderId"
            class="thread-item"
            :class="{ active: item.orderId === activeConversationId }"
            @click="emit('selectConversation', item.orderId)"
          >
            <div class="thread-title">
              <span class="thread-service">{{ item.serviceName }}</span>
              <span class="thread-status" :class="`status-${item.status.toLowerCase()}`">{{ statusText(item.status) }}</span>
            </div>
            <div class="thread-meta">
              <span>家政公司：{{ item.companyName }}</span>
              <time>{{ formatDateTime(item.lastMessageAt) }}</time>
            </div>
            <p class="thread-preview">{{ item.lastMessage || '尚未开始沟通' }}</p>
            <span v-if="item.unreadCount" class="thread-unread">{{ item.unreadCount }}</span>
          </li>
        </ul>

        <div v-else class="thread-empty">
          <template v-if="hasSearchTerm">
            <h3>未找到匹配的会话</h3>
            <p>尝试搜索服务名称、公司或消息关键词。</p>
          </template>
          <template v-else>
            <h3>暂无沟通记录</h3>
            <p>预约成功后，可在此处与家政公司实时对话。</p>
          </template>
        </div>
      </aside>

      <section class="thread-view">
        <header class="thread-view-header">
          <div v-if="activeConversation">
            <h3>{{ activeConversation.serviceName }}</h3>
            <p>
              家政公司：<strong>{{ activeConversation.companyName }}</strong>
              · 最近更新：{{ formatDateTime(activeConversation.lastMessageAt) }}
            </p>
          </div>
          <div v-else>
            <h3>选择一个订单开始沟通</h3>
            <p>点击左侧订单，可查看历史消息并发送新的咨询。</p>
          </div>
          <button
            v-if="activeConversationId !== null"
            type="button"
            class="ghost-button"
            @click="emit('refreshMessages')"
            :disabled="loadingMessages"
          >
            立即同步
          </button>
        </header>

        <div v-if="activeConversationId === null" class="thread-placeholder">
          <p>选择订单后，可与家政公司实时沟通。</p>
        </div>

        <div v-else class="thread-content">
          <div ref="messageContainer" class="message-scroller">
            <div v-if="loadingMessages" class="message-loading" role="status">消息加载中…</div>
            <template v-else>
              <div v-if="!messages.length" class="message-empty">暂无沟通记录，发送首条消息吧。</div>
              <article
                v-for="message in messages"
                :key="message.id"
                class="message-bubble"
                :class="{ inbound: message.incoming, outbound: !message.incoming }"
              >
                <header>
                  <span class="message-author">{{ message.incoming ? message.senderName : '我' }}</span>
                  <time class="message-time">{{ formatDateTime(message.createdAt) }}</time>
                </header>
                <p>{{ message.content }}</p>
              </article>
            </template>
          </div>

          <form class="composer" @submit.prevent="submitMessage">
            <textarea
              v-model="messageDraft"
              rows="2"
              placeholder="请输入要发送的内容…"
              :disabled="sending"
            ></textarea>
            <button type="submit" class="send-button" :disabled="sending || !messageDraft.trim()">
              {{ sending ? '发送中…' : '发送' }}
            </button>
          </form>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'

import type {
  CompanyMessageItem,
  CompanyMessagePayload,
  UserConversationItem,
  ServiceOrderStatus,
} from '../../services/dashboard'

const props = defineProps<{
  conversations: UserConversationItem[]
  activeConversationId: number | null
  messages: CompanyMessageItem[]
  loadingConversations: boolean
  loadingMessages: boolean
  sending: boolean
}>()

const emit = defineEmits<{
  (event: 'refreshConversations'): void
  (event: 'refreshMessages'): void
  (event: 'selectConversation', orderId: number): void
  (event: 'sendMessage', payload: CompanyMessagePayload): void
}>()

const messageDraft = ref('')
const messageContainer = ref<HTMLDivElement | null>(null)
const searchTerm = ref('')

const activeConversation = computed(() =>
  props.conversations.find((item) => item.orderId === props.activeConversationId) ?? null,
)

const hasSearchTerm = computed(() => searchTerm.value.trim().length > 0)

const filteredConversations = computed(() => {
  const keyword = searchTerm.value.trim().toLowerCase()
  if (!keyword) {
    return props.conversations
  }
  return props.conversations.filter((item) => {
    const candidates = [item.serviceName, item.companyName, item.lastMessage]
    return candidates.some((text) => {
      if (!text) return false
      return text.toLowerCase().includes(keyword)
    })
  })
})

const statusText = (status: ServiceOrderStatus) => {
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

const formatDateTime = (value?: string | null) => {
  if (!value) return '—'
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

const scrollToBottom = () => {
  nextTick(() => {
    const container = messageContainer.value
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}

watch(
  () => props.messages,
  (messages) => {
    if (messages.length) {
      scrollToBottom()
    }
  },
  { deep: true },
)

const submitMessage = () => {
  const content = messageDraft.value.trim()
  if (!content) return
  emit('sendMessage', { content })
  messageDraft.value = ''
}
</script>

<style scoped>
.messaging-panel {
  display: flex;
  flex-direction: column;
}

.messaging-shell {
  display: grid;
  grid-template-columns: minmax(240px, 320px) 1fr;
  gap: 24px;
}

.thread-rail {
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: rgba(255, 255, 255, 0.78);
  border-radius: calc(var(--brand-radius) + 4px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  padding: 20px;
  box-shadow: 0 22px 45px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(12px);
}

.thread-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.thread-title-block {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.thread-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.thread-search {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  height: 38px;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.32);
  background: rgba(248, 250, 255, 0.85);
  box-shadow: inset 0 1px 2px rgba(15, 23, 42, 0.08);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.thread-search:focus-within {
  border-color: rgba(59, 130, 246, 0.6);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.thread-search input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 13px;
  color: var(--brand-text);
  outline: none;
}

.thread-search input::placeholder {
  color: var(--brand-text-muted);
}

.thread-search-icon {
  font-size: 16px;
}

.icon-button {
  border: none;
  background: rgba(59, 130, 246, 0.12);
  color: var(--brand-primary);
  border-radius: 12px;
  width: 38px;
  height: 38px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.icon-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.icon-button:not(:disabled):hover {
  background: rgba(59, 130, 246, 0.2);
  transform: translateY(-1px);
}

.thread-empty {
  text-align: center;
  color: var(--brand-text-muted);
  font-size: 14px;
  padding: 40px 16px;
  border-radius: calc(var(--brand-radius) + 4px);
  background: rgba(255, 255, 255, 0.7);
  border: 1px dashed rgba(148, 163, 184, 0.25);
}

.thread-empty h3 {
  margin: 0 0 6px;
  font-size: 17px;
  font-weight: 700;
  color: var(--brand-text);
}

.thread-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 520px;
  overflow-y: auto;
  scrollbar-width: thin;
}

.thread-item {
  position: relative;
  padding: 14px;
  border-radius: calc(var(--brand-radius) + 2px);
  background: rgba(248, 250, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.18);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.thread-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 30px rgba(59, 130, 246, 0.18);
}

.thread-item.active {
  border-color: rgba(59, 130, 246, 0.35);
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.25);
}

.thread-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  font-weight: 600;
}

.thread-service {
  color: var(--brand-text);
}

.thread-status {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  text-transform: uppercase;
}

.thread-status.status-scheduled,
.thread-status.status-pending {
  background: linear-gradient(135deg, #6366f1, #4338ca);
}

.thread-status.status-in_progress {
  background: linear-gradient(135deg, #14b8a6, #0f766e);
}

.thread-status.status-completed {
  background: linear-gradient(135deg, #10b981, #059669);
}

.thread-status.status-refund_requested {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.thread-status.status-refund_approved {
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
}

.thread-status.status-refund_rejected {
  background: linear-gradient(135deg, #f43f5e, #e11d48);
}

.thread-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: var(--brand-text-muted);
  margin-top: 6px;
  gap: 8px;
}

.thread-meta span {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.thread-meta time {
  flex-shrink: 0;
}

.thread-preview {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--brand-text);
  line-height: 1.5;
  max-height: 40px;
  overflow: hidden;
}

.thread-unread {
  position: absolute;
  top: 12px;
  right: 12px;
  min-width: 22px;
  height: 22px;
  border-radius: 999px;
  background: linear-gradient(135deg, #f97316, #fb923c);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
}

.thread-view {
  background: rgba(255, 255, 255, 0.9);
  border-radius: calc(var(--brand-radius) + 6px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 22px 45px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(14px);
  display: flex;
  flex-direction: column;
  padding: 24px;
  gap: 20px;
}

.thread-view-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.thread-view-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.thread-view-header p {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--brand-text-muted);
}

.thread-placeholder {
  padding: 48px;
  text-align: center;
  color: var(--brand-text-muted);
  font-size: 14px;
  border: 1px dashed rgba(148, 163, 184, 0.24);
  border-radius: calc(var(--brand-radius) + 4px);
}

.thread-content {
  display: flex;
  flex-direction: column;
  gap: 18px;
  min-height: 420px;
}

.message-scroller {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  border-radius: calc(var(--brand-radius) + 2px);
  background: rgba(248, 250, 255, 0.8);
  border: 1px solid rgba(148, 163, 184, 0.18);
  display: flex;
  flex-direction: column;
  gap: 12px;
  scrollbar-width: thin;
}

.message-loading,
.message-empty {
  text-align: center;
  color: var(--brand-text-muted);
  font-size: 14px;
  padding: 32px 16px;
}

.message-bubble {
  max-width: 70%;
  padding: 14px 16px;
  border-radius: 20px;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
  display: inline-flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
}

.message-bubble header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);
}

.message-bubble p {
  margin: 0;
  font-size: 14px;
  color: #fff;
  line-height: 1.6;
}

.message-bubble.inbound {
  align-self: flex-start;
  background: linear-gradient(135deg, #818cf8, #6366f1);
  border-bottom-left-radius: 4px;
}

.message-bubble.outbound {
  align-self: flex-end;
  background: linear-gradient(135deg, #34d399, #10b981);
  border-bottom-right-radius: 4px;
}

.message-author {
  font-weight: 600;
}

.message-time {
  font-size: 11px;
}

.composer {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  align-items: center;
}

.composer textarea {
  width: 100%;
  border-radius: calc(var(--brand-radius) + 2px);
  border: 1px solid rgba(148, 163, 184, 0.28);
  padding: 12px 16px;
  resize: vertical;
  min-height: 80px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.85);
}

.send-button {
  padding: 12px 24px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.send-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-button:not(:disabled):hover {
  transform: translateY(-1px);
  box-shadow: 0 16px 30px rgba(124, 58, 237, 0.35);
}

.ghost-button {
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.28);
  background: rgba(248, 250, 255, 0.8);
  color: var(--brand-primary);
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.ghost-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ghost-button:not(:disabled):hover {
  transform: translateY(-1px);
  background: rgba(59, 130, 246, 0.14);
}

@media (max-width: 1200px) {
  .messaging-shell {
    grid-template-columns: 1fr;
  }

  .thread-rail {
    flex-direction: row;
    align-items: stretch;
    overflow-x: auto;
  }

  .thread-list {
    flex-direction: row;
    max-height: none;
    overflow-y: hidden;
    gap: 16px;
  }

  .thread-item {
    min-width: 240px;
  }
}

@media (max-width: 768px) {
  .thread-view {
    padding: 18px;
  }

  .composer {
    grid-template-columns: 1fr;
  }

  .send-button {
    width: 100%;
  }
}
</style>
