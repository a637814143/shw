<template>
  <Transition name="notification">
    <div v-if="visible" :class="notificationClasses" @click="handleClick">
      <div class="notification-icon">
        <slot name="icon">
          <i :class="iconClass"></i>
        </slot>
      </div>
      <div class="notification-content">
        <div v-if="title" class="notification-title">{{ title }}</div>
        <div class="notification-message">
          <slot>{{ message }}</slot>
        </div>
      </div>
      <button v-if="closable" class="notification-close" @click="close">
        <i class="fas fa-times"></i>
      </button>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'

interface Props {
  type?: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message?: string
  duration?: number
  closable?: boolean
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  duration: 5000,
  closable: true,
  position: 'top-right'
})

const emit = defineEmits<{
  close: []
  click: []
}>()

const visible = ref(false)
let timer: number | null = null

const notificationClasses = computed(() => {
  const classes = ['notification']
  classes.push(`notification-${props.type}`)
  classes.push(`notification-${props.position}`)
  return classes
})

const iconClass = computed(() => {
  const icons = {
    success: 'fas fa-check-circle',
    error: 'fas fa-exclamation-circle',
    warning: 'fas fa-exclamation-triangle',
    info: 'fas fa-info-circle'
  }
  return icons[props.type]
})

const show = () => {
  visible.value = true
  
  if (props.duration > 0) {
    timer = setTimeout(() => {
      close()
    }, props.duration)
  }
}

const close = () => {
  visible.value = false
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  emit('close')
}

const handleClick = () => {
  emit('click')
}

onMounted(() => {
  show()
})

onUnmounted(() => {
  if (timer) {
    clearTimeout(timer)
  }
})

defineExpose({
  show,
  close
})
</script>

<style scoped>
.notification {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 1rem;
  border-radius: 0.5rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  max-width: 24rem;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  position: fixed;
  z-index: 1000;
}

.notification:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.notification-success {
  background-color: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #166534;
}

.notification-error {
  background-color: #fef2f2;
  border: 1px solid #fecaca;
  color: #991b1b;
}

.notification-warning {
  background-color: #fffbeb;
  border: 1px solid #fed7aa;
  color: #92400e;
}

.notification-info {
  background-color: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #1e40af;
}

.notification-icon {
  flex-shrink: 0;
  font-size: 1.25rem;
  margin-top: 0.125rem;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-weight: 600;
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}

.notification-message {
  font-size: 0.875rem;
  line-height: 1.4;
}

.notification-close {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  transition: all 0.2s ease-in-out;
  flex-shrink: 0;
  opacity: 0.7;
}

.notification-close:hover {
  opacity: 1;
  background-color: rgba(0, 0, 0, 0.1);
}

/* Position classes */
.notification-top-right {
  top: 1rem;
  right: 1rem;
}

.notification-top-left {
  top: 1rem;
  left: 1rem;
}

.notification-bottom-right {
  bottom: 1rem;
  right: 1rem;
}

.notification-bottom-left {
  bottom: 1rem;
  left: 1rem;
}

.notification-top-center {
  top: 1rem;
  left: 50%;
  transform: translateX(-50%);
}

.notification-bottom-center {
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
}

/* Transitions */
.notification-enter-active,
.notification-leave-active {
  transition: all 0.3s ease-in-out;
}

.notification-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.notification-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.notification-top-left.notification-enter-from,
.notification-top-left.notification-leave-to {
  transform: translateX(-100%);
}

.notification-top-center.notification-enter-from,
.notification-top-center.notification-leave-to,
.notification-bottom-center.notification-enter-from,
.notification-bottom-center.notification-leave-to {
  transform: translateX(-50%) translateY(-100%);
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .notification-success {
    background-color: #064e3b;
    border-color: #065f46;
    color: #6ee7b7;
  }
  
  .notification-error {
    background-color: #7f1d1d;
    border-color: #991b1b;
    color: #fca5a5;
  }
  
  .notification-warning {
    background-color: #78350f;
    border-color: #92400e;
    color: #fcd34d;
  }
  
  .notification-info {
    background-color: #1e3a8a;
    border-color: #1e40af;
    color: #93c5fd;
  }
  
  .notification-close:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }
}
</style>
