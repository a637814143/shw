<template>
  <div :class="cardClasses">
    <div v-if="$slots.header" class="card-header">
      <slot name="header"></slot>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'default' | 'elevated' | 'outlined' | 'filled'
  padding?: 'none' | 'sm' | 'md' | 'lg'
  rounded?: boolean
  shadow?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  padding: 'md',
  rounded: true,
  shadow: true
})

const cardClasses = computed(() => {
  const classes = ['card']
  
  // Variant styles
  switch (props.variant) {
    case 'elevated':
      classes.push('card-elevated')
      break
    case 'outlined':
      classes.push('card-outlined')
      break
    case 'filled':
      classes.push('card-filled')
      break
    default:
      classes.push('card-default')
  }
  
  // Padding
  if (props.padding !== 'md') {
    classes.push(`card-padding-${props.padding}`)
  }
  
  // Rounded corners
  if (props.rounded) {
    classes.push('card-rounded')
  }
  
  // Shadow
  if (props.shadow) {
    classes.push('card-shadow')
  }
  
  return classes.join(' ')
})
</script>

<style scoped>
.card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.card-default {
  border: 1px solid #e5e7eb;
}

.card-elevated {
  border: none;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.card-outlined {
  border: 2px solid #e5e7eb;
  background: transparent;
}

.card-filled {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.card-rounded {
  border-radius: 12px;
}

.card-shadow {
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.card-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.card-body {
  padding: 1.5rem;
}

.card-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
}

.card-padding-none .card-body {
  padding: 0;
}

.card-padding-sm .card-body {
  padding: 0.75rem;
}

.card-padding-lg .card-body {
  padding: 2rem;
}

@media (max-width: 768px) {
  .card-body {
    padding: 1rem;
  }
  
  .card-header,
  .card-footer {
    padding: 0.75rem 1rem;
  }
}
</style>
