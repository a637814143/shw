<template>
  <div class="panel-container">
    <div class="panel-card" v-if="userInfo">
      <h1 class="panel-title">欢迎您{{ userInfo.usertype }}:{{ userInfo.username }}</h1>
      <p v-if="userInfo.usermoney !== undefined && userInfo.usermoney !== null" class="panel-balance">
        账户余额：{{ formatMoney(userInfo.usermoney) }}
      </p>
    </div>
    <div v-else class="panel-card">
      <h1 class="panel-title">欢迎使用家政服务平台</h1>
      <p class="panel-balance">请重新登录。</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userInfo = computed(() => {
  const raw = localStorage.getItem('userInfo')
  if (!raw) {
    router.replace('/login')
    return null
  }

  try {
    return JSON.parse(raw)
  } catch (error) {
    localStorage.removeItem('userInfo')
    router.replace('/login')
    return null
  }
})

const formatMoney = (value: number) => {
  return Number(value).toFixed(2)
}
</script>

<style scoped>
.panel-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5, #e0e0e0);
  padding: 20px;
}

.panel-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 40px 60px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.panel-title {
  font-size: 28px;
  color: #333333;
  margin-bottom: 16px;
}

.panel-balance {
  font-size: 18px;
  color: #555555;
}
</style>
