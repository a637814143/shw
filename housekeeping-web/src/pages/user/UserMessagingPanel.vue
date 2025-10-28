<template>
  <section class="messaging-panel">
    <div class="messaging-shell">
      <aside class="thread-rail">
        <header class="thread-header">
          <div class="thread-title-block">
            <h2>客服沟通中心</h2>
            <label class="thread-search">
              <span aria-hidden="true" class="thread-search-icon">🔍</span>
              <span class="sr-only">搜索会话</span>
              <input
                v-model="searchTerm"
                type="search"
                name="conversation-search"
                autocomplete="off"
                placeholder="搜索服务、公司或消息"
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
  min-height: 520px;
}

.messaging-shell {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 1.25rem;
}

.thread-rail {
  background: rgba(15, 23, 42, 0.55);
  border-radius: 1.1rem;
  border: 1px solid rgba(148, 163, 184, 0.18);
  padding: 1.1rem;
  display: flex;
  flex-direction: column;
}

.thread-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1rem;
}

.thread-title-block {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  flex: 1;
}

.thread-header h2 {
  margin: 0;
  font-size: 1.2rem;
  color: #e2e8f0;
}

.thread-search {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  padding: 0 0.9rem;
  height: 2.35rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(15, 23, 42, 0.35);
  color: #e2e8f0;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.thread-search:focus-within {
  border-color: rgba(56, 189, 248, 0.6);
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.2);
}

.thread-search input {
  flex: 1;
  border: none;
  background: transparent;
  color: inherit;
  font-size: 0.9rem;
  outline: none;
}

.thread-search input::placeholder {
  color: rgba(226, 232, 240, 0.55);
}

.thread-search-icon {
  font-size: 1rem;
}

.icon-button {
  width: 2.2rem;
  height: 2.2rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.25);
  background: rgba(15, 23, 42, 0.4);
  color: #94a3b8;
}

.thread-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  overflow-y: auto;
}

.thread-item {
  padding: 0.75rem 0.85rem;
  border-radius: 0.95rem;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid transparent;
  cursor: pointer;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.thread-item.active {
  border-color: rgba(56, 189, 248, 0.45);
  background: rgba(56, 189, 248, 0.12);
}

.thread-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.thread-service {
  color: #f8fafc;
}

.thread-status {
  font-size: 0.75rem;
  padding: 0.15rem 0.6rem;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.15);
  color: #93c5fd;
}

.thread-status.status-in_progress {
  background: rgba(45, 212, 191, 0.18);
  color: #5eead4;
}

.thread-status.status-completed {
  background: rgba(34, 197, 94, 0.18);
  color: #86efac;
}

.thread-meta {
  margin-top: 0.35rem;
  font-size: 0.85rem;
  color: rgba(226, 232, 240, 0.6);
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}

.thread-preview {
  margin: 0.45rem 0 0;
  color: rgba(226, 232, 240, 0.7);
  font-size: 0.85rem;
}

.thread-unread {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #f87171;
  color: #0f172a;
  font-size: 0.75rem;
  border-radius: 999px;
  padding: 0.1rem 0.5rem;
  margin-top: 0.4rem;
}

.thread-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: rgba(226, 232, 240, 0.6);
  padding: 2rem 1rem;
}

.thread-view {
  background: rgba(15, 23, 42, 0.55);
  border-radius: 1.1rem;
  border: 1px solid rgba(148, 163, 184, 0.18);
  display: flex;
  flex-direction: column;
}

.thread-view-header {
  padding: 1.25rem;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
}

.thread-view-header h3 {
  margin: 0;
  color: #e2e8f0;
}

.thread-view-header p {
  margin: 0.45rem 0 0;
  color: rgba(226, 232, 240, 0.6);
  font-size: 0.9rem;
}

.thread-placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(226, 232, 240, 0.6);
}

.thread-content {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.message-scroller {
  flex: 1;
  padding: 1.25rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.message-loading,
.message-empty {
  text-align: center;
  color: rgba(226, 232, 240, 0.6);
}

.message-bubble {
  max-width: 70%;
  padding: 0.75rem 1rem;
  border-radius: 1rem 1rem 1rem 0.35rem;
  background: rgba(59, 130, 246, 0.18);
  color: #e2e8f0;
  align-self: flex-start;
}

.message-bubble.outbound {
  align-self: flex-end;
  border-radius: 1rem 1rem 0.35rem 1rem;
  background: linear-gradient(120deg, #6366f1, #38bdf8);
  color: #0f172a;
}

.message-bubble header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 0.35rem;
}

.message-author {
  font-weight: 600;
  font-size: 0.85rem;
}

.message-time {
  font-size: 0.75rem;
  color: rgba(15, 23, 42, 0.6);
}

.message-bubble.inbound .message-time {
  color: rgba(226, 232, 240, 0.65);
}

.composer {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.25rem 1.25rem;
  border-top: 1px solid rgba(148, 163, 184, 0.12);
}

.composer textarea {
  flex: 1;
  border-radius: 0.85rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  background: rgba(15, 23, 42, 0.6);
  color: #f8fafc;
  resize: none;
  padding: 0.65rem 0.75rem;
}

.send-button {
  padding: 0.55rem 1.2rem;
  border-radius: 999px;
  background: linear-gradient(120deg, #38bdf8, #6366f1);
  border: none;
  color: #0f172a;
  font-weight: 600;
  cursor: pointer;
}

@media (max-width: 1024px) {
  .messaging-shell {
    grid-template-columns: 1fr;
  }

  .thread-rail {
    max-height: 240px;
    overflow-y: auto;
  }
}
</style>
