<template>
  <section class="profile-card">
    <header class="panel-header">
      <div>
        <h3>基础资料</h3>
        <p>完善联系方式与地址信息，方便服务沟通与上门安排。</p>
      </div>
    </header>
    <form class="profile-form" @submit.prevent="submitProfile">
      <div class="profile-layout">
        <div class="profile-visual">
          <img :src="previewAvatarUrl" alt="头像预览" class="profile-avatar" />
          <div class="visual-actions">
            <button type="button" class="primary-button" @click="triggerFileSelect">上传头像</button>
            <button
              v-if="profileForm.avatarBase64 && profileForm.avatarBase64 !== defaultAvatarDataUrl"
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
          <p class="avatar-hint">支持 PNG/JPEG/GIF，建议 512KB 以内。</p>
        </div>

        <div class="form-grid">
          <label class="form-field">
            <span>展示名称</span>
            <input v-model.trim="profileForm.displayName" type="text" maxlength="100" required />
          </label>
          <label class="form-field">
            <span>常用联系方式</span>
            <input v-model.trim="profileForm.contactNumber" type="text" maxlength="100" placeholder="手机号或微信号" />
          </label>
          <label class="form-field form-field-full">
            <span>常用地址</span>
            <input v-model.trim="profileForm.address" type="text" maxlength="255" placeholder="示例：上海市浦东新区世纪大道 100 号" />
          </label>
          <template v-if="isCompany">
            <label class="form-field">
              <span>公司联系电话</span>
              <input v-model.trim="profileForm.companyPhone" type="text" maxlength="100" placeholder="用于客户联系" />
            </label>
            <label class="form-field">
              <span>公司地址</span>
              <input v-model.trim="profileForm.companyAddress" type="text" maxlength="255" placeholder="用于展示给客户" />
            </label>
            <label class="form-field form-field-full">
              <span>公司简介</span>
              <textarea v-model.trim="profileForm.companyDescription" rows="3" maxlength="1000" placeholder="突出公司优势、服务范围或团队特色"></textarea>
            </label>
          </template>
        </div>
      </div>
      <div class="form-actions">
        <button type="submit" class="primary-button" :disabled="saving">
          {{ saving ? '保存中…' : '保存个人资料' }}
        </button>
      </div>
    </form>
  </section>

  <section class="password-card">
    <header>
      <h3>修改密码</h3>
      <p>请输入当前密码与新密码，保障账户安全。</p>
    </header>
    <form class="password-form" @submit.prevent="submitPassword">
      <label class="form-field">
        <span>当前密码</span>
        <input v-model.trim="passwordForm.currentPassword" type="password" autocomplete="current-password" />
      </label>
      <label class="form-field">
        <span>新密码</span>
        <input v-model.trim="passwordForm.newPassword" type="password" autocomplete="new-password" />
      </label>
      <label class="form-field">
        <span>确认新密码</span>
        <input v-model.trim="passwordForm.confirmPassword" type="password" autocomplete="new-password" />
      </label>
      <div class="form-actions">
        <button type="submit" class="primary-button" :disabled="passwordSaving">
          {{ passwordSaving ? '提交中…' : '更新密码' }}
        </button>
      </div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, reactive, ref, watch } from 'vue'

import {
  type AccountProfileItem,
  type UpdateAccountProfilePayload,
  type UpdateAccountPasswordPayload,
  updateCurrentAccount,
  updateCurrentPassword,
} from '../services/dashboard'
import { createObjectUrlFromDataUrl, revokeObjectUrl } from '../utils/image'

const FALLBACK_AVATAR =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=='

const props = defineProps<{
  account: AccountProfileItem | null
}>()

const emit = defineEmits<{ (e: 'updated', payload: AccountProfileItem): void }>()

type ProfileFormState = {
  displayName: string
  avatarBase64: string
  contactNumber: string
  address: string
  companyPhone: string
  companyAddress: string
  companyDescription: string
}

const profileForm = reactive<ProfileFormState>({
  displayName: '',
  avatarBase64: '',
  contactNumber: '',
  address: '',
  companyPhone: '',
  companyAddress: '',
  companyDescription: '',
})

const passwordForm = reactive<{ currentPassword: string; newPassword: string; confirmPassword: string }>({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const fileInput = ref<HTMLInputElement | null>(null)
const saving = ref(false)
const passwordSaving = ref(false)

const isCompany = computed(() => props.account?.role === 'COMPANY')

const defaultAvatarDataUrl = computed(() => props.account?.avatarBase64 || FALLBACK_AVATAR)

const previewAvatarUrl = ref<string>('')
let lastPreviewObjectUrl: string | null = null

const updatePreviewAvatar = (dataUrl: string) => {
  const nextUrl = createObjectUrlFromDataUrl(dataUrl || FALLBACK_AVATAR)
  if (lastPreviewObjectUrl && lastPreviewObjectUrl !== nextUrl) {
    revokeObjectUrl(lastPreviewObjectUrl)
  }
  previewAvatarUrl.value = nextUrl
  lastPreviewObjectUrl = nextUrl.startsWith('blob:') ? nextUrl : null
}

watch(
  () => [profileForm.avatarBase64, defaultAvatarDataUrl.value],
  () => {
    updatePreviewAvatar(profileForm.avatarBase64 || defaultAvatarDataUrl.value)
  },
  { immediate: true },
)

onBeforeUnmount(() => {
  if (lastPreviewObjectUrl) {
    revokeObjectUrl(lastPreviewObjectUrl)
    lastPreviewObjectUrl = null
  }
})

watch(
  () => props.account,
  (account) => {
    if (!account) {
      profileForm.displayName = ''
      profileForm.avatarBase64 = ''
      profileForm.contactNumber = ''
      profileForm.address = ''
      profileForm.companyPhone = ''
      profileForm.companyAddress = ''
      profileForm.companyDescription = ''
      return
    }
    profileForm.displayName = account.displayName
    profileForm.avatarBase64 = account.avatarBase64
    profileForm.contactNumber = account.contactNumber || ''
    profileForm.address = account.address || ''
    profileForm.companyPhone = account.companyPhone || ''
    profileForm.companyAddress = account.companyAddress || ''
    profileForm.companyDescription = account.companyDescription || ''
  },
  { immediate: true },
)

const triggerFileSelect = () => {
  fileInput.value?.click()
}

const resetAvatar = () => {
  profileForm.avatarBase64 = ''
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
      avatarBase64: profileForm.avatarBase64?.trim() || defaultAvatarDataUrl.value,
      contactNumber: profileForm.contactNumber?.trim() || undefined,
      address: profileForm.address?.trim() || undefined,
    }
    if (isCompany.value) {
      payload.companyPhone = profileForm.companyPhone?.trim() || undefined
      payload.companyAddress = profileForm.companyAddress?.trim() || undefined
      payload.companyDescription = profileForm.companyDescription?.trim() || undefined
    }
    const updated = await updateCurrentAccount(payload)
    profileForm.avatarBase64 = updated.avatarBase64
    emit('updated', updated)
    alert('个人资料已更新')
  } catch (error) {
    console.error(error)
    alert(error instanceof Error ? error.message : '保存失败，请稍后再试')
  } finally {
    saving.value = false
  }
}

const submitPassword = async () => {
  if (!passwordForm.currentPassword || !passwordForm.newPassword) {
    alert('请填写完整的密码信息')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    alert('两次输入的新密码不一致')
    return
  }
  passwordSaving.value = true
  try {
    const payload: UpdateAccountPasswordPayload = {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    }
    await updateCurrentPassword(payload)
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    alert('密码已更新，请使用新密码重新登录')
  } catch (error) {
    console.error(error)
    alert('修改密码失败，请确认原密码是否正确')
  } finally {
    passwordSaving.value = false
  }
}
</script>

<style scoped>
.profile-card {
  background: var(--surface-color, #fff);
  border-radius: 1.5rem;
  padding: 2rem;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
  margin-bottom: 2rem;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.profile-layout {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  align-items: flex-start;
}

.profile-visual {
  flex: 0 0 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.profile-avatar {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
}

.visual-actions {
  display: flex;
  gap: 0.75rem;
}

.avatar-hint {
  font-size: 0.85rem;
  color: rgba(15, 23, 42, 0.6);
  text-align: center;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-size: 0.95rem;
}

.form-field input,
.form-field textarea {
  width: 100%;
  border: 1px solid rgba(15, 23, 42, 0.12);
  border-radius: 0.75rem;
  padding: 0.75rem 1rem;
  background: rgba(15, 23, 42, 0.02);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-field textarea {
  resize: vertical;
}

.form-field input:focus,
.form-field textarea:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
  background: #fff;
}

.form-field-full {
  grid-column: 1 / -1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.password-card {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.08), rgba(56, 189, 248, 0.08));
  border-radius: 1.5rem;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.1);
}

.password-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
}

@media (max-width: 640px) {
  .profile-card,
  .password-card {
    padding: 1.5rem;
  }

  .profile-layout {
    flex-direction: column;
    align-items: center;
  }

  .profile-visual {
    align-self: center;
  }
}
</style>
