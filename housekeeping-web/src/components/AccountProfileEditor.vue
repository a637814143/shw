<template>
  <div class="profile-editor">
    <div class="profile-visual">
      <img :src="previewAvatar" alt="头像预览" class="profile-avatar" />
      <div class="visual-actions">
        <button type="button" class="primary-button" @click="triggerFileSelect">上传头像</button>
        <button
          v-if="profileForm.avatarBase64 && profileForm.avatarBase64 !== defaultAvatar"
          type="button"
          class="ghost-button"
          @click="resetAvatar"
        >
          恢复默认
        </button>
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          class="sr-only"
          @change="handleFileChange"
        />
      </div>
    </div>

    <form class="profile-form" @submit.prevent="submitProfile">
      <label class="form-field">
        <span>展示名称</span>
        <input v-model.trim="profileForm.displayName" type="text" maxlength="100" required />
      </label>

      <label class="form-field">
        <span>头像数据（Base64）</span>
        <textarea
          v-model.trim="profileForm.avatarBase64"
          rows="4"
          placeholder="可直接粘贴 data:image/png;base64,..."
        ></textarea>
      </label>

      <p class="form-hint">支持上传 PNG/JPEG/GIF，建议控制在 512KB 以内。</p>

      <div class="form-actions">
        <button type="submit" class="primary-button" :disabled="saving">
          {{ saving ? '保存中…' : '保存个人资料' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'

import {
  type AccountProfileItem,
  type UpdateAccountProfilePayload,
  updateCurrentAccount,
} from '../services/dashboard'

const FALLBACK_AVATAR =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=='

const props = defineProps<{
  account: AccountProfileItem | null
}>()

const emit = defineEmits<{ (e: 'updated', payload: AccountProfileItem): void }>()

const profileForm = reactive<UpdateAccountProfilePayload>({
  displayName: '',
  avatarBase64: '',
})

const fileInput = ref<HTMLInputElement | null>(null)
const saving = ref(false)

const defaultAvatar = computed(() => props.account?.avatarBase64 || FALLBACK_AVATAR)

const previewAvatar = computed(() => profileForm.avatarBase64 || defaultAvatar.value)

watch(
  () => props.account,
  (account) => {
    if (!account) {
      profileForm.displayName = ''
      profileForm.avatarBase64 = ''
      return
    }
    profileForm.displayName = account.displayName
    profileForm.avatarBase64 = account.avatarBase64
  },
  { immediate: true },
)

const triggerFileSelect = () => {
  fileInput.value?.click()
}

const resetAvatar = () => {
  profileForm.avatarBase64 = defaultAvatar.value
}

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement | null
  if (!target || !target.files || !target.files.length) {
    return
  }

  const file = target.files.item(0)
  if (!file) {
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    profileForm.avatarBase64 = typeof reader.result === 'string' ? reader.result : ''
  }
  reader.onerror = () => {
    alert('读取头像文件失败，请重试')
  }
  reader.readAsDataURL(file)
  target.value = ''
}

const submitProfile = async () => {
  if (!profileForm.displayName.trim()) {
    alert('展示名称不能为空')
    return
  }
  saving.value = true
  try {
    const payload: UpdateAccountProfilePayload = {
      displayName: profileForm.displayName.trim(),
      avatarBase64: profileForm.avatarBase64?.trim() || defaultAvatar.value,
    }
    const updated = await updateCurrentAccount(payload)
    profileForm.displayName = updated.displayName
    profileForm.avatarBase64 = updated.avatarBase64
    emit('updated', updated)
    alert('个人资料已更新')
  } catch (error) {
    console.error(error)
    alert('保存失败，请稍后再试或检查头像数据是否有效')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.profile-editor {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  align-items: flex-start;
}

.profile-visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.visual-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.profile-form {
  flex: 1;
  min-width: 260px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-field input,
.form-field textarea {
  padding: 0.75rem 1rem;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.9);
  font-size: 0.95rem;
}

.form-field textarea {
  min-height: 120px;
  resize: vertical;
}

.form-hint {
  margin: 0;
  font-size: 0.85rem;
  color: rgba(0, 0, 0, 0.6);
}

.form-actions {
  display: flex;
  gap: 1rem;
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

@media (max-width: 720px) {
  .profile-editor {
    flex-direction: column;
    align-items: stretch;
  }

  .profile-visual {
    flex-direction: row;
    justify-content: flex-start;
  }

  .visual-actions {
    flex-direction: row;
  }
}
</style>
