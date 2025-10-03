<template>
  <div v-if="visible" :class="loadingClasses">
    <div class="loading-overlay" v-if="overlay">
      <div class="loading-content">
        <div class="loading-spinner">
          <div class="spinner" :class="spinnerClass"></div>
        </div>
        <div v-if="text" class="loading-text">{{ text }}</div>
      </div>
    </div>
    <div v-else class="loading-inline">
      <div class="spinner" :class="spinnerClass"></div>
      <span v-if="text" class="loading-text">{{ text }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  visible?: boolean
  text?: string
  size?: 'sm' | 'md' | 'lg'
  type?: 'spinner' | 'dots' | 'pulse' | 'bars'
  overlay?: boolean
  color?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info'
}

const props = withDefaults(defineProps<Props>(), {
  visible: true,
  size: 'md',
  type: 'spinner',
  overlay: false,
  color: 'primary'
})

const loadingClasses = computed(() => {
  const classes = ['loading']
  if (props.overlay) {
    classes.push('loading-overlay-container')
  } else {
    classes.push('loading-inline-container')
  }
  return classes
})

const spinnerClass = computed(() => {
  const classes = ['spinner']
  classes.push(`spinner-${props.type}`)
  classes.push(`spinner-${props.size}`)
  classes.push(`spinner-${props.color}`)
  return classes
})
</script>

<style scoped>
.loading-overlay-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-inline-container {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.loading-inline {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-text {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

/* Spinner animations */
.spinner {
  border-radius: 50%;
  border: 2px solid transparent;
  animation: spin 1s linear infinite;
}

.spinner-spinner {
  border-top-color: #3b82f6;
}

.spinner-dots {
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 50%;
  background-color: #3b82f6;
  animation: dots 1.4s ease-in-out infinite both;
}

.spinner-pulse {
  width: 1rem;
  height: 1rem;
  border-radius: 50%;
  background-color: #3b82f6;
  animation: pulse 1.5s ease-in-out infinite;
}

.spinner-bars {
  width: 0.25rem;
  height: 1rem;
  background-color: #3b82f6;
  border-radius: 0.125rem;
  animation: bars 1.2s ease-in-out infinite;
}

/* Sizes */
.spinner-sm {
  width: 1rem;
  height: 1rem;
}

.spinner-md {
  width: 1.5rem;
  height: 1.5rem;
}

.spinner-lg {
  width: 2rem;
  height: 2rem;
}

/* Colors */
.spinner-primary {
  border-top-color: #3b82f6;
}

.spinner-secondary {
  border-top-color: #6b7280;
}

.spinner-success {
  border-top-color: #10b981;
}

.spinner-danger {
  border-top-color: #ef4444;
}

.spinner-warning {
  border-top-color: #f59e0b;
}

.spinner-info {
  border-top-color: #06b6d4;
}

/* Animations */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes dots {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes bars {
  0%, 40%, 100% {
    transform: scaleY(0.4);
  }
  20% {
    transform: scaleY(1);
  }
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .loading-overlay-container {
    background-color: rgba(0, 0, 0, 0.8);
  }
  
  .loading-text {
    color: #9ca3af;
  }
  
  .spinner-primary {
    border-top-color: #60a5fa;
  }
  
  .spinner-secondary {
    border-top-color: #9ca3af;
  }
  
  .spinner-success {
    border-top-color: #34d399;
  }
  
  .spinner-danger {
    border-top-color: #f87171;
  }
  
  .spinner-warning {
    border-top-color: #fbbf24;
  }
  
  .spinner-info {
    border-top-color: #22d3ee;
  }
}
</style>
