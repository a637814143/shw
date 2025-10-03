<template>
  <div class="notification-manager">
    <Notification
      v-for="notification in notifications"
      :key="notification.id"
      :type="notification.type"
      :title="notification.title"
      :message="notification.message"
      :duration="notification.duration"
      :closable="notification.closable"
      :position="notification.position"
      @close="removeNotification(notification.id)"
      @click="handleNotificationClick(notification)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import Notification from './Notification.vue'

interface NotificationItem {
  id: string
  type: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message: string
  duration?: number
  closable?: boolean
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center'
  onClick?: () => void
}

const notifications = ref<NotificationItem[]>([])

const addNotification = (notification: Omit<NotificationItem, 'id'>) => {
  const id = Math.random().toString(36).substr(2, 9)
  const newNotification: NotificationItem = {
    id,
    duration: 5000,
    closable: true,
    position: 'top-right',
    ...notification
  }
  
  notifications.value.push(newNotification)
  
  return id
}

const removeNotification = (id: string) => {
  const index = notifications.value.findIndex(n => n.id === id)
  if (index > -1) {
    notifications.value.splice(index, 1)
  }
}

const clearAll = () => {
  notifications.value = []
}

const handleNotificationClick = (notification: NotificationItem) => {
  if (notification.onClick) {
    notification.onClick()
  }
}

// Convenience methods
const success = (message: string, options?: Partial<NotificationItem>) => {
  return addNotification({ type: 'success', message, ...options })
}

const error = (message: string, options?: Partial<NotificationItem>) => {
  return addNotification({ type: 'error', message, ...options })
}

const warning = (message: string, options?: Partial<NotificationItem>) => {
  return addNotification({ type: 'warning', message, ...options })
}

const info = (message: string, options?: Partial<NotificationItem>) => {
  return addNotification({ type: 'info', message, ...options })
}

// Expose methods for global use
defineExpose({
  addNotification,
  removeNotification,
  clearAll,
  success,
  error,
  warning,
  info
})
</script>

<style scoped>
.notification-manager {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1000;
}

.notification-manager :deep(.notification) {
  pointer-events: auto;
}
</style>
