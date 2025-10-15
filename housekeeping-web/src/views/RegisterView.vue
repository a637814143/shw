<template>
  <div class="auth-page">
    <h1 class="auth-title">注册账号</h1>
    <div class="auth-card">
      <p class="auth-description">
        填写以下信息即可创建家政服务平台账号，发布需求或加入成为家政人员。
      </p>
      <form class="auth-form">
        <div class="form-row">
          <label class="form-label" for="register-account">设置账号</label>
          <input id="register-account" class="form-input" type="text" placeholder="请输入账号" />
        </div>
        <div class="form-row">
          <label class="form-label" for="register-password">设置密码</label>
          <input id="register-password" class="form-input" type="password" placeholder="请输入密码" />
        </div>
        <div class="form-row">
          <label class="form-label" for="register-confirm">确认密码</label>
          <input id="register-confirm" class="form-input" type="password" placeholder="请再次输入密码" />
        </div>
        <button type="submit" class="primary-button">注册</button>
      </form>
      <p class="auth-switch">
        已有账号？
        <RouterLink class="auth-link" to="/login">返回登录</RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.auth-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 32px 16px;
  background: linear-gradient(180deg, #f5f7fb 0%, #ffffff 100%);
}

.auth-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #1f2a44;
  letter-spacing: 2px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  padding: 40px 32px;
  border-radius: 16px;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.12);
}

.auth-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 20px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-label {
  flex: 0 0 110px;
  font-size: 16px;
  color: #3a4660;
}

.form-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #d5d9e2;
  border-radius: 8px;
  background-color: #fafbff;
  font-size: 16px;
  color: #1f2a44;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
}

.primary-button {
  margin-top: 8px;
  width: 100%;
  padding: 12px 16px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(5, 150, 105, 0.25);
}

.primary-button:active {
  transform: translateY(0);
}

.auth-switch {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #6b7280;
}

.auth-link {
  color: #059669;
  text-decoration: none;
  margin-left: 4px;
}

.auth-link:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .auth-card {
    padding: 32px 24px;
  }

  .form-row {
    flex-direction: column;
    align-items: stretch;
  }

  .form-label {
    flex: unset;
    width: 100%;
  }
}
</style>
