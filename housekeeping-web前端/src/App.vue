<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="branding">
        <h1>轻松到家</h1>
        <p>一站式家政服务预约系统</p>
      </div>
    </header>

    <main class="app-content">
      <RouterView />
    </main>

    <footer class="app-footer">
      <p>© {{ new Date().getFullYear() }} 轻松到家. 为您提供安全放心的家政服务。</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #f5f9ff 0%, #ffffff 40%);
  color: #0f172a;
}

.app-header {
  background: #1d4ed8;
  color: #ffffff;
  padding: 2.5rem 1.5rem 2rem;
  box-shadow: 0 10px 30px rgba(29, 78, 216, 0.25);
}

.branding h1 {
  font-size: 2.1rem;
  margin-bottom: 0.35rem;
  font-weight: 700;
}

.branding p {
  font-size: 1rem;
  opacity: 0.9;
}

.app-content {
  flex: 1;
  width: min(1100px, 94vw);
  margin: -4rem auto 0;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.12);
  padding: 3rem 3.5rem;
}

.app-footer {
  text-align: center;
  padding: 2rem 1rem 3rem;
  color: #475569;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .app-content {
    margin-top: -3rem;
    padding: 2rem 1.5rem;
    width: calc(100vw - 2rem);
  }

  .branding h1 {
    font-size: 1.6rem;
  }

  .branding p {
    font-size: 0.95rem;
  }
}
</style>
