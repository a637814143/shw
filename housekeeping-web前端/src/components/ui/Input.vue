<template>
  <div class="input-wrapper">
    <label v-if="label" :for="inputId" class="input-label">
      {{ label }}
      <span v-if="required" class="required-mark">*</span>
    </label>
    <div class="input-container">
      <input
        :id="inputId"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :class="inputClasses"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
        @keydown="handleKeydown"
      />
      <div v-if="icon" class="input-icon">
        <slot name="icon">
          <i :class="icon"></i>
        </slot>
      </div>
      <div v-if="clearable && modelValue && !disabled" class="clear-button" @click="clearInput">
        <i class="fas fa-times"></i>
      </div>
    </div>
    <div v-if="error || hint" class="input-message">
      <span v-if="error" class="error-message">{{ error }}</span>
      <span v-else-if="hint" class="hint-message">{{ hint }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Props {
  modelValue?: string | number
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search'
  label?: string
  placeholder?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  error?: string
  hint?: string
  size?: 'sm' | 'md' | 'lg'
  variant?: 'default' | 'filled' | 'outlined'
  icon?: string
  clearable?: boolean
  maxlength?: number
  minlength?: number
  pattern?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  readonly: false,
  required: false,
  size: 'md',
  variant: 'default',
  clearable: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  keydown: [event: KeyboardEvent]
  clear: []
}>()

const inputId = ref(`input-${Math.random().toString(36).substr(2, 9)}`)
const isFocused = ref(false)

const inputClasses = computed(() => {
  const classes = ['input']
  
  // Size
  classes.push(`input-${props.size}`)
  
  // Variant
  classes.push(`input-${props.variant}`)
  
  // States
  if (props.disabled) classes.push('input-disabled')
  if (props.readonly) classes.push('input-readonly')
  if (props.error) classes.push('input-error')
  if (isFocused.value) classes.push('input-focused')
  if (props.icon) classes.push('input-with-icon')
  if (props.clearable) classes.push('input-clearable')
  
  return classes
})

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

const handleFocus = (event: FocusEvent) => {
  isFocused.value = true
  emit('focus', event)
}

const handleBlur = (event: FocusEvent) => {
  isFocused.value = false
  emit('blur', event)
}

const handleKeydown = (event: KeyboardEvent) => {
  emit('keydown', event)
}

const clearInput = () => {
  emit('update:modelValue', '')
  emit('clear')
}
</script>

<style scoped>
.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.required-mark {
  color: #ef4444;
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.input {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  background-color: #ffffff;
  color: #111827;
  transition: all 0.2s ease-in-out;
  outline: none;
}

.input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-default {
  padding: 0.5rem 0.75rem;
}

.input-filled {
  background-color: #f9fafb;
  border-color: transparent;
}

.input-outlined {
  background-color: transparent;
  border-width: 2px;
}

.input-sm {
  padding: 0.375rem 0.5rem;
  font-size: 0.875rem;
}

.input-md {
  padding: 0.5rem 0.75rem;
  font-size: 1rem;
}

.input-lg {
  padding: 0.75rem 1rem;
  font-size: 1.125rem;
}

.input-disabled {
  background-color: #f3f4f6;
  color: #9ca3af;
  cursor: not-allowed;
}

.input-readonly {
  background-color: #f9fafb;
  cursor: default;
}

.input-error {
  border-color: #ef4444;
}

.input-error:focus {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.input-with-icon {
  padding-left: 2.5rem;
}

.input-clearable {
  padding-right: 2.5rem;
}

.input-icon {
  position: absolute;
  left: 0.75rem;
  color: #6b7280;
  pointer-events: none;
}

.clear-button {
  position: absolute;
  right: 0.75rem;
  color: #6b7280;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  transition: all 0.2s ease-in-out;
}

.clear-button:hover {
  color: #374151;
  background-color: #f3f4f6;
}

.input-message {
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.error-message {
  color: #ef4444;
}

.hint-message {
  color: #6b7280;
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .input-label {
    color: #f3f4f6;
  }
  
  .input {
    background-color: #1f2937;
    border-color: #374151;
    color: #f9fafb;
  }
  
  .input-filled {
    background-color: #374151;
  }
  
  .input-disabled {
    background-color: #374151;
    color: #6b7280;
  }
  
  .input-readonly {
    background-color: #374151;
  }
  
  .input-icon {
    color: #9ca3af;
  }
  
  .clear-button {
    color: #9ca3af;
  }
  
  .clear-button:hover {
    color: #f3f4f6;
    background-color: #4b5563;
  }
  
  .hint-message {
    color: #9ca3af;
  }
}
</style>
