// UI Components Export
export { default as Button } from './Button.vue'
export { default as Card } from './Card.vue'
export { default as Input } from './Input.vue'
export { default as Modal } from './Modal.vue'
export { default as Notification } from './Notification.vue'
export { default as NotificationManager } from './NotificationManager.vue'
export { default as Loading } from './Loading.vue'

// Types
export interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
  loadingText?: string
  fullWidth?: boolean
  rounded?: boolean
}

export interface CardProps {
  variant?: 'default' | 'elevated' | 'outlined' | 'filled'
  padding?: 'none' | 'sm' | 'md' | 'lg'
  rounded?: boolean
  shadow?: boolean
}

export interface InputProps {
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

export interface ModalProps {
  isOpen: boolean
  title?: string
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full'
  closable?: boolean
  closeOnOverlay?: boolean
  closeOnEscape?: boolean
  persistent?: boolean
}

export interface NotificationProps {
  type?: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message?: string
  duration?: number
  closable?: boolean
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center'
}

export interface LoadingProps {
  visible?: boolean
  text?: string
  size?: 'sm' | 'md' | 'lg'
  type?: 'spinner' | 'dots' | 'pulse' | 'bars'
  overlay?: boolean
  color?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info'
}
