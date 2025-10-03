<template>
  <button 
    :class="buttonClasses" 
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-spinner"></span>
    <slot v-if="!loading"></slot>
    <span v-if="loading && loadingText">{{ loadingText }}</span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
  loadingText?: string
  fullWidth?: boolean
  rounded?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  loadingText: 'Loading...',
  fullWidth: false,
  rounded: false
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const buttonClasses = computed(() => {
  const baseClasses = 'btn'
  const variantClasses = `btn-${props.variant}`
  const sizeClasses = `btn-${props.size}`
  const widthClasses = props.fullWidth ? 'btn-full' : ''
  const roundedClasses = props.rounded ? 'btn-rounded' : ''
  const loadingClasses = props.loading ? 'btn-loading' : ''
  
  return [baseClasses, variantClasses, sizeClasses, widthClasses, roundedClasses, loadingClasses].filter(Boolean).join(' ')
})

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  text-decoration: none;
  position: relative;
  overflow: hidden;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Variants */
.btn-primary {
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.3);
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #A0522D 0%, #CD853F 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 69, 19, 0.4);
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #5a6268;
  transform: translateY(-1px);
}

.btn-success {
  background: #28a745;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background: #218838;
  transform: translateY(-1px);
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #c82333;
  transform: translateY(-1px);
}

.btn-warning {
  background: #ffc107;
  color: #212529;
}

.btn-warning:hover:not(:disabled) {
  background: #e0a800;
  transform: translateY(-1px);
}

.btn-info {
  background: #17a2b8;
  color: white;
}

.btn-info:hover:not(:disabled) {
  background: #138496;
  transform: translateY(-1px);
}

/* Sizes */
.btn-sm {
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 4px;
}

.btn-md {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 6px;
}

.btn-lg {
  padding: 16px 32px;
  font-size: 18px;
  border-radius: 8px;
}

/* Full width */
.btn-full {
  width: 100%;
}

/* Rounded */
.btn-rounded {
  border-radius: 50px;
}

/* Loading state */
.btn-loading {
  pointer-events: none;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Active state */
.btn:active:not(:disabled) {
  transform: translateY(0);
}
</style>
