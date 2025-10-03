<template>
  <div class="auth-layout">
    <section class="auth-panel">
      <h1 class="auth-title">创建新账号</h1>
      <p class="auth-subtitle">注册后即可登录平台并使用对应角色的功能</p>

      <form class="auth-form" @submit.prevent="handleRegister">
        <label class="form-field">
          <span>账号</span>
          <input v-model.trim="form.username" type="text" placeholder="请输入账号" required />
        </label>

        <label class="form-field">
          <span>密码</span>
          <input v-model="form.password" type="password" placeholder="至少6位密码" required />
        </label>

        <label class="form-field">
          <span>确认密码</span>
          <input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" required />
        </label>

        <label class="form-field">
          <span>手机号</span>
          <input v-model.trim="form.phone" type="tel" placeholder="请输入常用手机号" required />
        </label>

        <label class="form-field">
          <span>角色</span>
          <select v-model="form.role">
            <option value="user">普通用户</option>
            <option value="provider">家政服务人员</option>
          </select>
        </label>

        <p v-if="errorMessage" class="form-error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="form-success">{{ successMessage }}</p>

        <button class="auth-button" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? '注册中...' : '注册账号' }}
        </button>
      </form>

      <p class="auth-extra">
        已有账号？
        <router-link to="/login">返回登录</router-link>
      </p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '@/utils/api'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  role: 'user'
})

const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const phoneReg = /^1[3-9]\d{9}$/

const handleRegister = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  if (!form.username || !form.password || !form.confirmPassword || !form.phone) {
    errorMessage.value = '请完整填写注册信息'
    return
  }

  if (form.password.length < 6) {
    errorMessage.value = '密码长度至少为6位'
    return
  }

  if (form.password !== form.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  if (!phoneReg.test(form.phone)) {
    errorMessage.value = '请输入正确的手机号'
    return
  }

  isSubmitting.value = true
  try {
    let response
    if (form.role === 'provider') {
      response = await authAPI.providerRegister(form.username, form.password, form.phone)
    } else {
      response = await authAPI.userRegister(form.username, form.password, form.phone)
    }

    if (!response || response.code !== 200) {
      throw new Error(response?.message || '注册失败')
    }

    successMessage.value = '注册成功，请使用账号登录'
    setTimeout(() => {
      router.replace('/login')
    }, 800)
  } catch (error: any) {
    console.error('注册失败:', error)
    errorMessage.value = error?.message || '注册失败，请稍后再试'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ecf2ff 0%, #e0e7ff 100%);
  padding: 24px;
}

.auth-panel {
  width: 100%;
  max-width: 440px;
  background: #ffffff;
  padding: 44px 40px;
  border-radius: 18px;
  box-shadow: 0 22px 50px rgba(30, 64, 175, 0.15);
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.auth-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e3a8a;
  margin-bottom: 8px;
  text-align: center;
}

.auth-subtitle {
  color: #475569;
  text-align: center;
  margin-bottom: 32px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field span {
  font-size: 14px;
  color: #1f2937;
}

.form-field input,
.form-field select {
  height: 44px;
  padding: 0 12px;
  border: 1px solid #c7d2fe;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-field input:focus,
.form-field select:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

.auth-button {
  height: 46px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #4f46e5, #4338ca);
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.auth-button:hover {
  box-shadow: 0 12px 20px rgba(79, 70, 229, 0.25);
  transform: translateY(-1px);
}

.auth-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.form-error {
  color: #dc2626;
  font-size: 14px;
  margin-top: -8px;
}

.form-success {
  color: #16a34a;
  font-size: 14px;
  margin-top: -8px;
}

.auth-extra {
  margin-top: 24px;
  text-align: center;
  color: #475569;
}

a {
  color: #4338ca;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>
