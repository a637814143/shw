<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h2>欢迎回来，{{ displayName }}</h2>
        <p class="muted">当前登录角色：普通用户</p>
      </div>
      <div class="header-actions">
        <button class="text-button" @click="refreshAll" :disabled="loading">刷新数据</button>
        <button class="danger-button" @click="logout">退出登录</button>
      </div>
    </header>

    <main class="dashboard-content">
      <section class="card">
        <header class="card-header">
          <h3>个人资料</h3>
        </header>
        <form class="card-body form-grid" @submit.prevent="updateProfile">
          <label>
            <span>姓名</span>
            <input v-model="profileForm.realName" type="text" placeholder="请输入姓名" />
          </label>
          <label>
            <span>手机号</span>
            <input v-model="profileForm.phone" type="tel" placeholder="请输入手机号" />
          </label>
          <label>
            <span>电子邮箱</span>
            <input v-model="profileForm.email" type="email" placeholder="请输入邮箱" />
          </label>
          <label>
            <span>联系地址</span>
            <input v-model="profileForm.address" type="text" placeholder="请输入联系地址" />
          </label>
          <label class="full-row">
            <span>账户余额</span>
            <div class="readonly-field">¥ {{ profile?.balance ?? 0 }}</div>
          </label>
          <div class="full-row feedback" v-if="profileFeedback.error">{{ profileFeedback.error }}</div>
          <div class="full-row feedback success" v-if="profileFeedback.success">{{ profileFeedback.success }}</div>
          <div class="full-row align-right">
            <button class="primary-button" type="submit" :disabled="savingProfile">
              {{ savingProfile ? '保存中...' : '保存修改' }}
            </button>
          </div>
        </form>
      </section>

      <section class="card">
        <header class="card-header">
          <h3>修改密码</h3>
        </header>
        <form class="card-body form-grid" @submit.prevent="changePassword">
          <label>
            <span>旧密码</span>
            <input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" required />
          </label>
          <label>
            <span>新密码</span>
            <input v-model="passwordForm.newPassword" type="password" placeholder="不少于6位" required />
          </label>
          <label>
            <span>确认新密码</span>
            <input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" required />
          </label>
          <div class="full-row feedback" v-if="passwordFeedback.error">{{ passwordFeedback.error }}</div>
          <div class="full-row feedback success" v-if="passwordFeedback.success">{{ passwordFeedback.success }}</div>
          <div class="full-row align-right">
            <button class="primary-button" type="submit" :disabled="changingPassword">
              {{ changingPassword ? '提交中...' : '更新密码' }}
            </button>
          </div>
        </form>
      </section>

      <section class="card">
        <header class="card-header">
          <h3>热门服务</h3>
          <div class="filters">
            <select v-model="selectedCategory" @change="fetchServices">
              <option value="ALL">全部分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
            <input
              v-model="serviceKeyword"
              type="search"
              placeholder="输入服务关键词"
              @keyup.enter="fetchServices"
            />
            <button class="text-button" type="button" @click="fetchServices">搜索</button>
            <button class="text-button" type="button" @click="resetServiceFilters">重置</button>
          </div>
        </header>
        <div class="card-body">
          <p v-if="loadingServices" class="muted">正在加载服务数据...</p>
          <template v-else>
            <table v-if="services.length" class="data-table">
              <thead>
                <tr>
                  <th>服务名称</th>
                  <th>简介</th>
                  <th>价格</th>
                  <th>单位</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in services" :key="item.id">
                  <td>{{ item.name }}</td>
                  <td>{{ item.description || '—' }}</td>
                  <td>¥ {{ item.price }}</td>
                  <td>{{ item.unit || '次' }}</td>
                  <td>
                    <span :class="['status-tag', item.status === 1 ? 'status-online' : 'status-offline']">
                      {{ item.status === 1 ? '上架' : '下架' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
            <p v-else class="muted">暂无服务数据，请稍后再试。</p>
          </template>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  authAPI,
  userAPI,
  serviceAPI,
  categoryAPI,
  removeToken,
  setSessionProfile,
  getSessionProfile
} from '@/utils/api'

interface ServiceCategory {
  id: number
  name: string
}

interface HousekeepingServiceItem {
  id: number
  name: string
  description?: string
  price: number
  unit?: string
  status: number
}

const router = useRouter()

const loading = ref(false)
const profile = ref<any>(getSessionProfile())
const currentUserId = ref<number | null>(null)
const currentUserType = ref<string>('USER')

const profileForm = reactive({
  realName: '',
  phone: '',
  email: '',
  address: ''
})

const profileFeedback = reactive({ success: '', error: '' })
const savingProfile = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordFeedback = reactive({ success: '', error: '' })
const changingPassword = ref(false)

const categories = ref<ServiceCategory[]>([])
const services = ref<HousekeepingServiceItem[]>([])
const loadingServices = ref(false)
const selectedCategory = ref<'ALL' | number>('ALL')
const serviceKeyword = ref('')

const displayName = computed(() => profile.value?.realName || profile.value?.username || '用户')

const syncProfileForm = () => {
  if (!profile.value) return
  profileForm.realName = profile.value.realName || ''
  profileForm.phone = profile.value.phone || ''
  profileForm.email = profile.value.email || ''
  profileForm.address = profile.value.address || ''
}

const fetchCurrentUser = async () => {
  loading.value = true
  try {
    const response = await authAPI.currentUser()
    if (response?.data) {
      currentUserId.value = response.data.userId
      currentUserType.value = response.data.userType
      profile.value = response.data.profile
      setSessionProfile(response.data.profile)
      syncProfileForm()
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const response = await categoryAPI.listPublic()
    categories.value = response?.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const fetchServices = async () => {
  loadingServices.value = true
  try {
    let response
    const keyword = serviceKeyword.value.trim()
    if (keyword) {
      response = await serviceAPI.searchServices(keyword, 1, 20)
    } else if (selectedCategory.value !== 'ALL') {
      response = await serviceAPI.getServicesByCategory(Number(selectedCategory.value), 1, 20)
    } else {
      response = await serviceAPI.getPopularServices(1, 20)
    }
    services.value = response?.data?.records || []
  } catch (error) {
    console.error('加载服务失败:', error)
  } finally {
    loadingServices.value = false
  }
}

const refreshAll = () => {
  fetchCurrentUser()
  fetchCategories()
  fetchServices()
}

const updateProfile = async () => {
  profileFeedback.error = ''
  profileFeedback.success = ''

  const payload: Record<string, any> = {
    realName: profileForm.realName.trim(),
    phone: profileForm.phone.trim(),
    email: profileForm.email.trim(),
    address: profileForm.address.trim()
  }

  savingProfile.value = true
  try {
    const response = await userAPI.updateUserInfo(payload)
    if (response?.code !== 200) {
      throw new Error(response?.message || '更新失败')
    }
    profile.value = response.data
    setSessionProfile(response.data)
    profileFeedback.success = '资料已更新'
  } catch (error: any) {
    console.error('更新个人资料失败:', error)
    profileFeedback.error = error?.message || '更新失败，请稍后再试'
  } finally {
    savingProfile.value = false
  }
}

const changePassword = async () => {
  passwordFeedback.error = ''
  passwordFeedback.success = ''

  if (passwordForm.newPassword.length < 6) {
    passwordFeedback.error = '新密码长度至少6位'
    return
  }

  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordFeedback.error = '两次输入的新密码不一致'
    return
  }

  if (!currentUserId.value || !currentUserType.value) {
    passwordFeedback.error = '未获取到用户信息'
    return
  }

  changingPassword.value = true
  try {
    const response = await authAPI.changePassword(
      currentUserId.value,
      currentUserType.value,
      passwordForm.oldPassword,
      passwordForm.newPassword
    )
    if (response?.code !== 200) {
      throw new Error(response?.message || '修改密码失败')
    }
    passwordFeedback.success = '密码修改成功'
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error: any) {
    console.error('修改密码失败:', error)
    passwordFeedback.error = error?.message || '修改密码失败'
  } finally {
    changingPassword.value = false
  }
}

const resetServiceFilters = () => {
  selectedCategory.value = 'ALL'
  serviceKeyword.value = ''
  fetchServices()
}

const logout = () => {
  removeToken()
  router.replace('/login')
}

onMounted(() => {
  syncProfileForm()
  refreshAll()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f8fafc;
  padding: 32px 40px 48px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 4px;
}

.muted {
  color: #64748b;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.dashboard-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.18);
  display: flex;
  flex-direction: column;
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.7);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.card-body {
  padding: 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px 20px;
}

.form-grid label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: #0f172a;
}

.form-grid input {
  height: 42px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  background: #f8fafc;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-grid input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
  outline: none;
}

.readonly-field {
  height: 42px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  border-radius: 10px;
  background: rgba(226, 232, 240, 0.6);
  color: #475569;
}

.full-row {
  grid-column: 1 / -1;
}

.align-right {
  display: flex;
  justify-content: flex-end;
}

.feedback {
  font-size: 14px;
  color: #dc2626;
}

.feedback.success {
  color: #16a34a;
}

.primary-button,
.text-button,
.danger-button {
  height: 40px;
  padding: 0 20px;
  border-radius: 999px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #ffffff;
}

.primary-button:hover {
  box-shadow: 0 10px 18px rgba(37, 99, 235, 0.25);
  transform: translateY(-1px);
}

.text-button {
  background: rgba(37, 99, 235, 0.12);
  color: #1d4ed8;
}

.text-button:hover {
  background: rgba(37, 99, 235, 0.2);
}

.danger-button {
  background: rgba(220, 38, 38, 0.12);
  color: #b91c1c;
}

.danger-button:hover {
  background: rgba(220, 38, 38, 0.2);
}

.filters {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filters select,
.filters input {
  height: 38px;
  border-radius: 999px;
  border: 1px solid #cbd5f5;
  padding: 0 14px;
  background: #f8fafc;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.filters select:focus,
.filters input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
  outline: none;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table thead {
  background: rgba(37, 99, 235, 0.08);
}

.data-table th,
.data-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;
  text-align: left;
  color: #1e293b;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status-online {
  background: rgba(34, 197, 94, 0.12);
  color: #15803d;
}

.status-offline {
  background: rgba(148, 163, 184, 0.2);
  color: #475569;
}

@media (max-width: 960px) {
  .dashboard {
    padding: 24px 16px 32px;
  }

  .filters {
    flex-wrap: wrap;
  }
}
</style>
