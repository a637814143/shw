<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="candle candle-1"></div>
      <div class="candle candle-2"></div>
      <div class="plate">
        <div class="pancakes"></div>
        <div class="fork"></div>
      </div>
      <div class="banana-dish">
        <div class="banana"></div>
        <div class="string"></div>
      </div>
      <div class="mug"></div>
      <div class="tablecloth"></div>
    </div>

    <!-- 注册表单 -->
    <div class="register-form">
      <h1 class="form-title">欢迎注册</h1>
      
      <form @submit.prevent="handleRegister" class="form">
        <!-- 账号输入 -->
        <div class="input-group">
          <div class="input-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <input 
            type="text" 
            v-model="formData.username" 
            placeholder="请输入账号" 
            class="form-input"
            required
          />
        </div>

        <!-- 密码输入 -->
        <div class="input-group">
          <div class="input-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="16" r="1" stroke="currentColor" stroke-width="2"/>
              <path d="M7 11V7C7 5.67392 7.52678 4.40215 8.46447 3.46447C9.40215 2.52678 10.6739 2 12 2C13.3261 2 14.5979 2.52678 15.5355 3.46447C16.4732 4.40215 17 5.67392 17 7V11" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <input 
            type="password" 
            v-model="formData.password" 
            placeholder="请输入密码" 
            class="form-input"
            required
          />
        </div>

        <!-- 确认密码输入 -->
        <div class="input-group">
          <div class="input-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="16" r="1" stroke="currentColor" stroke-width="2"/>
              <path d="M7 11V7C7 5.67392 7.52678 4.40215 8.46447 3.46447C9.40215 2.52678 10.6739 2 12 2C13.3261 2 14.5979 2.52678 15.5355 3.46447C16.4732 4.40215 17 5.67392 17 7V11" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <input 
            type="password" 
            v-model="formData.confirmPassword" 
            placeholder="请确认密码" 
            class="form-input"
            required
          />
        </div>

        <!-- 手机号输入 -->
        <div class="input-group">
          <div class="input-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 16.92V19.92C22.0011 20.1985 21.9441 20.4742 21.8325 20.7293C21.7209 20.9845 21.5573 21.2136 21.3521 21.4019C21.1468 21.5901 20.9046 21.7335 20.6407 21.8227C20.3769 21.9119 20.0974 21.9451 19.82 21.92C16.7428 21.5856 13.787 20.5341 11.19 18.85C8.77382 17.3147 6.72533 15.2662 5.18999 12.85C3.49997 10.2412 2.44824 7.27099 2.11999 4.18C2.095 3.90347 2.12787 3.62476 2.21649 3.36162C2.30512 3.09849 2.44756 2.85669 2.63476 2.65162C2.82196 2.44655 3.0498 2.28271 3.30379 2.17052C3.55777 2.05833 3.83233 2.00026 4.10999 2H7.10999C7.59531 1.99522 8.06679 2.16708 8.43376 2.48353C8.80073 2.79999 9.03996 3.23945 9.10999 3.72C9.23662 4.68007 9.47144 5.62273 9.80999 6.53C9.94454 6.88792 9.97366 7.27691 9.89391 7.65088C9.81415 8.02485 9.62886 8.36811 9.35999 8.64L8.08999 9.91C9.51355 12.4135 11.5865 14.4864 14.09 15.91L15.36 14.64C15.6319 14.3711 15.9751 14.1858 16.3491 14.1061C16.7231 14.0263 17.1121 14.0555 17.47 14.19C18.3773 14.5286 19.3199 14.7634 20.28 14.89C20.7658 14.9588 21.2094 15.2032 21.5265 15.5775C21.8437 15.9518 22.0122 16.4296 21.9999 16.92H22Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <input 
            type="tel" 
            v-model="formData.phone" 
            placeholder="请输入手机号" 
            class="form-input"
            required
          />
        </div>

        <!-- 角色选择 -->
        <div class="input-group">
          <select v-model="formData.role" class="form-select">
            <option value="user">用户</option>
            <option value="provider">家政人员</option>
          </select>
          <div class="select-arrow">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <polyline points="6,9 12,15 18,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <!-- 成功提示 -->
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>

        <!-- 注册按钮 -->
        <button type="submit" class="register-btn" :disabled="isLoading">
          <span v-if="isLoading">注册中...</span>
          <span v-else>注册</span>
        </button>

        <!-- 登录链接 -->
        <div class="login-link">
          <span>已有账号?</span>
          <a href="#" @click.prevent="goToLogin" class="login-text">请登录</a>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI, setToken } from '@/utils/api'

const router = useRouter()

// 表单数据
const formData = ref({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  role: 'user'
})

// 注册状态
const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

// 处理注册
const handleRegister = async () => {
  // 重置消息
  errorMessage.value = ''
  successMessage.value = ''
  
  // 验证输入
  if (!formData.value.username.trim()) {
    errorMessage.value = '请输入账号'
    return
  }
  
  if (!formData.value.password.trim()) {
    errorMessage.value = '请输入密码'
    return
  }
  
  if (!formData.value.phone.trim()) {
    errorMessage.value = '请输入手机号'
    return
  }
  
  if (formData.value.password !== formData.value.confirmPassword) {
    errorMessage.value = '密码确认不匹配'
    return
  }
  
  if (formData.value.password.length < 6) {
    errorMessage.value = '密码长度至少6位'
    return
  }
  
  // 验证手机号格式
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(formData.value.phone)) {
    errorMessage.value = '请输入正确的手机号'
    return
  }
  
  isLoading.value = true
  
  try {
    let response
    if (formData.value.role === 'user') {
      response = await authAPI.userRegister(
        formData.value.username,
        formData.value.password,
        formData.value.phone
      )
    } else {
      response = await authAPI.providerRegister(
        formData.value.username,
        formData.value.password,
        formData.value.phone
      )
    }
    
    console.log('注册成功:', response)
    successMessage.value = '注册成功！请使用新账号登录'
    
    // 延迟跳转到登录页面
    setTimeout(() => {
      router.replace('/login')
    }, 1500)
    
  } catch (error: any) {
    errorMessage.value = error.message || '注册失败，请稍后重试'
    console.error('注册错误:', error)
  } finally {
    isLoading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 0;
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 50%, #CD853F 100%);
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 背景装饰样式 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.tablecloth {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 60%;
  background: 
    repeating-linear-gradient(
      0deg,
      #F5F5DC 0px,
      #F5F5DC 20px,
      #DEB887 20px,
      #DEB887 40px
    ),
    repeating-linear-gradient(
      90deg,
      #F5F5DC 0px,
      #F5F5DC 20px,
      #DEB887 20px,
      #DEB887 40px
    );
  opacity: 0.3;
}

.candle {
  position: absolute;
  width: 8px;
  height: 40px;
  background: #8B4513;
  border-radius: 4px;
}

.candle-1 {
  bottom: 20%;
  left: 15%;
  transform: rotate(-5deg);
}

.candle-2 {
  bottom: 18%;
  left: 20%;
  transform: rotate(3deg);
}

.candle::after {
  content: '';
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 8px;
  background: #FFD700;
  border-radius: 2px;
  box-shadow: 0 0 10px #FFD700;
}

.plate {
  position: absolute;
  bottom: 15%;
  left: 10%;
  width: 60px;
  height: 60px;
  background: #FFFFFF;
  border: 3px solid #333;
  border-radius: 50%;
}

.pancakes {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 40px;
  height: 30px;
  background: #8B4513;
  border-radius: 20px;
}

.fork {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 2px;
  height: 20px;
  background: #C0C0C0;
  border-radius: 1px;
}

.banana-dish {
  position: absolute;
  bottom: 25%;
  left: 25%;
  width: 30px;
  height: 20px;
  background: #DEB887;
  border-radius: 15px;
}

.banana {
  position: absolute;
  top: 2px;
  left: 5px;
  width: 20px;
  height: 15px;
  background: #FFD700;
  border-radius: 10px;
}

.string {
  position: absolute;
  top: -5px;
  left: 10px;
  width: 10px;
  height: 8px;
  background: #8B4513;
  border-radius: 2px;
}

.mug {
  position: absolute;
  bottom: 10%;
  right: 30%;
  width: 40px;
  height: 50px;
  background: #8B4513;
  border-radius: 0 8px 8px 0;
}

.mug::after {
  content: '';
  position: absolute;
  top: 10px;
  right: -8px;
  width: 8px;
  height: 20px;
  background: #8B4513;
  border-radius: 0 4px 4px 0;
}


/* 注册表单样式 */
.register-form {
  background: rgba(200, 200, 200, 0.8);
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 2;
  position: relative;
  min-width: 350px;
}

.form-title {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  color: #666;
  z-index: 3;
}

.form-input {
  width: 100%;
  padding: 12px 12px 12px 45px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #8B4513;
}

.form-select {
  width: 100%;
  padding: 12px 40px 12px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
  appearance: none;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.form-select:focus {
  outline: none;
  border-color: #8B4513;
}

.select-arrow {
  position: absolute;
  right: 12px;
  color: #666;
  pointer-events: none;
}

.register-btn {
  background: #8B4513;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.register-btn:hover:not(:disabled) {
  background: #A0522D;
}

.register-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.login-text {
  color: #8B4513;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
}

.login-text:hover {
  text-decoration: underline;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 10px 12px;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
  border: 1px solid #f5c6cb;
}

.success-message {
  background: #d4edda;
  color: #155724;
  padding: 10px 12px;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
  border: 1px solid #c3e6cb;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    justify-content: center;
    padding-right: 0;
  }
  
  .register-form {
    margin: 20px;
    min-width: auto;
    width: calc(100% - 40px);
  }
  
  .background-decoration {
    opacity: 0.5;
  }
}
</style>
