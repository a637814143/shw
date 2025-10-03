<template>
  <div class="login-container">
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

    <!-- 登录表单 -->
    <div class="login-form">
      <h1 class="form-title">家政服务平台</h1>
      
      <form @submit.prevent="handleLogin" class="form">
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

        <!-- 角色选择 -->
        <div class="input-group">
          <select v-model="formData.role" class="form-select">
            <option value="admin">管理员</option>
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

        <!-- 登录按钮 -->
        <button type="submit" class="login-btn" :disabled="isLoading">
          <span v-if="isLoading">登录中...</span>
          <span v-else>登录</span>
        </button>

        <!-- 注册链接 -->
        <div class="register-link">
          <span>还没有账号?</span>
          <a href="#" @click.prevent="goToRegister" class="register-text">请注册</a>
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
  role: 'user'
})

// 登录状态
const isLoading = ref(false)
const errorMessage = ref('')


// 处理登录
const handleLogin = async () => {
  // 重置错误信息
  errorMessage.value = ''
  
  // 验证输入
  if (!formData.value.username.trim()) {
    errorMessage.value = '请输入账号'
    return
  }
  
  if (!formData.value.password.trim()) {
    errorMessage.value = '请输入密码'
    return
  }
  
  isLoading.value = true
  
  try {
    let response
    // 根据角色调用不同的登录接口
    if (formData.value.role === 'admin') {
      response = await authAPI.adminLogin(formData.value.username, formData.value.password)
    } else if (formData.value.role === 'provider') {
      response = await authAPI.providerLogin(formData.value.username, formData.value.password)
    } else {
      response = await authAPI.userLogin(formData.value.username, formData.value.password)
    }
    
    // 保存token和用户信息
    if (response.data && response.data.token) {
      setToken(response.data.token)
      
      // 保存用户信息到localStorage
      localStorage.setItem('userInfo', JSON.stringify({
        username: formData.value.username,
        role: formData.value.role,
        loginTime: new Date().toISOString(),
        userId: response.data.user?.id,
        userData: response.data.user,
        userType: response.data.userType
      }))
      
      console.log('登录成功:', response)
      
      // 根据用户角色跳转到不同的主界面
      if (formData.value.role === 'admin') {
        router.replace('/admin-home')
      } else if (formData.value.role === 'provider') {
        router.replace('/provider-home')
      } else {
        router.replace('/user-home')
      }
    } else {
      errorMessage.value = '登录响应格式错误'
      console.error('登录响应数据:', response)
    }
  } catch (error: any) {
    errorMessage.value = error.message || '登录失败，请稍后重试'
    console.error('登录错误:', error)
  } finally {
    isLoading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
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
  right: 20%;
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

/* 登录表单样式 */
.login-form {
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

.login-btn {
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

.login-btn:hover:not(:disabled) {
  background: #A0522D;
}

.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.register-text {
  color: #8B4513;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
}

.register-text:hover {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form {
    margin: 20px;
    min-width: auto;
    width: calc(100% - 40px);
  }
  
  .background-decoration {
    opacity: 0.5;
  }
}
</style>
