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
          <label class="form-label" for="role">请选择角色</label>
          <select id="role" class="form-select">
            <option value="admin">管理员</option>
            <option value="staff">家政人员</option>
            <option value="user">用户</option>
          </select>
        </div>
        <button type="submit" class="login-button">登录</button>
      </form>
      <section class="register-section">
        <h2 class="register-title">没有账号？立即注册</h2>
        <p class="register-description">
          注册成为家政服务平台的用户，即可快速发布需求或加入平台成为家政人员。
        </p>
        <form class="register-form">
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
          <button type="button" class="register-button">注册账号</button>
        </form>
      </section>
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

.form-select {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #d5d9e2;
  border-radius: 8px;
  background-color: #fafbff;
  color: #1f2a44;
  font-size: 16px;
  appearance: none;
  background-image: linear-gradient(45deg, transparent 50%, #3b82f6 50%),
    linear-gradient(135deg, #3b82f6 50%, transparent 50%),
    linear-gradient(to right, #d5d9e2, #d5d9e2);
  background-position: calc(100% - 20px) calc(50% - 3px),
    calc(100% - 15px) calc(50% - 3px),
    calc(100% - 2.5rem) 50%;
  background-size: 5px 5px, 5px 5px, 1px 50%;
  background-repeat: no-repeat;
  cursor: pointer;
}

.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
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

.register-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.register-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2a44;
  margin-bottom: 12px;
}

.register-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 20px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.register-button {
  margin-top: 8px;
  align-self: flex-end;
  padding: 10px 24px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.register-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(5, 150, 105, 0.25);
}

.register-button:active {
  transform: translateY(0);
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
