<template>
  <section class="messaging-panel">
    <div class="messaging-shell">
      <aside class="thread-rail">
        <header class="thread-header">
          <div>
            <h2>客户沟通中心</h2>
            <p>实时追踪用户问询并快速回复，维系高端服务体验</p>
          </div>
          <button type="button" class="icon-button" @click="emit('refreshConversations')" :disabled="loadingConversations">
            <span aria-hidden="true">⟳</span>
            <span class="sr-only">刷新会话列表</span>
          </button>
        </header>

        <div v-if="loadingConversations" class="thread-empty" role="status">正在获取最新会话…</div>

        <ul v-else-if="conversations.length" class="thread-list">
          <li
            v-for="item in conversations"
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
              <span>{{ item.customerName }}</span>
              <time>{{ formatDateTime(item.lastMessageAt) }}</time>
            </div>
            <p class="thread-preview">{{ item.lastMessage || '暂无消息' }}</p>
            <span v-if="item.unreadCount" class="thread-unread">{{ item.unreadCount }}</span>
          </li>
        </ul>

        <div v-else class="thread-empty">
          <h3>暂无沟通消息</h3>
          <p>当用户通过订单发起咨询时，会在此处显示。</p>
        </div>
      </aside>

      <section class="thread-view">
        <header class="thread-view-header">
          <div v-if="activeConversation">
            <h3>{{ activeConversation.serviceName }}</h3>
            <p>
              客户：<strong>{{ activeConversation.customerName }}</strong>
              · 最近更新：{{ formatDateTime(activeConversation.lastMessageAt) }}
            </p>
          </div>
          <div v-else>
            <h3>选择一个会话开始交流</h3>
            <p>点击左侧会话即可查看历史记录并发送消息。</p>
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
          <p>从左侧选择一位客户，我们将呈现完整的沟通历史。</p>
        </div>

        <div v-else class="thread-content">
          <div ref="messageContainer" class="message-scroller">
            <div v-if="loadingMessages" class="message-loading" role="status">消息加载中…</div>
            <template v-else>
              <div v-if="!messages.length" class="message-empty">尚无沟通记录，发送首条欢迎语吧。</div>
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
              placeholder="输入要发送的内容…"
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

import type { CompanyConversationItem, CompanyMessageItem, ServiceOrderStatus } from '../../services/dashboard'

const props = defineProps<{
  conversations: CompanyConversationItem[]
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
  (event: 'sendMessage', payload: { orderId: number; content: string }): void
}>()

const messageDraft = ref('')
const messageContainer = ref<HTMLDivElement | null>(null)

const activeConversation = computed(() =>
  props.conversations.find((item) => item.orderId === props.activeConversationId) ?? null,
)

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
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}

const submitMessage = () => {
  if (props.activeConversationId === null) {
    return
  }
  const content = messageDraft.value.trim()
  if (!content) {
    return
  }
  emit('sendMessage', { orderId: props.activeConversationId, content })
  messageDraft.value = ''
}

watch(
  () => props.activeConversationId,
  () => {
    messageDraft.value = ''
  },
)

watch(
  () => props.messages.length,
  () => {
    nextTick(() => {
      if (messageContainer.value) {
        messageContainer.value.scrollTop = messageContainer.value.scrollHeight
      }
    })
  },
)
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

.thread-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.thread-header p {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--brand-text-muted);
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

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
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
