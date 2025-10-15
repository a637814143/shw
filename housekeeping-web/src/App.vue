<template>
  <div class="login-page">
    <h1 class="login-title">家政服务平台</h1>
    <div class="login-card">
      <form class="login-form">
        <div class="form-row">
          <label class="form-label" for="account">请输入账号</label>
          <input id="account" class="form-input" type="text" />
        </div>
        <div class="form-row">
          <label class="form-label" for="password">请输入密码</label>
          <input id="password" class="form-input" type="password" />
        </div>
        <div class="form-row role-row">
          <span class="form-label">角色</span>
          <span class="role-option">管理员</span>
        </div>
        <button type="submit" class="login-button">登录</button>
      </form>
      <p class="register-hint">没有账号？请注册</p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 32px 16px;
  background: linear-gradient(180deg, #f5f7fb 0%, #ffffff 100%);
}

.login-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #1f2a44;
  letter-spacing: 2px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  padding: 40px 32px;
  border-radius: 16px;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.12);
}

.login-form {
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
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.role-row {
  justify-content: space-between;
}

.role-option {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #d5d9e2;
  border-radius: 8px;
  background-color: #f0f4ff;
  color: #1f2a44;
  font-size: 16px;
}

.login-button {
  margin-top: 8px;
  width: 100%;
  padding: 12px 16px;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.login-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.25);
}

.login-button:active {
  transform: translateY(0);
}

.register-hint {
  margin-top: 24px;
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}

@media (max-width: 480px) {
  .login-card {
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
