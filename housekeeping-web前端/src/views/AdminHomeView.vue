<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h2>系统管理后台</h2>
        <p class="muted">您好，{{ displayName }}，可以在此维护用户、服务者与分类</p>
      </div>
      <div class="header-actions">
        <button class="text-button" @click="refreshAll" :disabled="loading">刷新</button>
        <button class="danger-button" @click="logout">退出登录</button>
      </div>
    </header>

    <section class="stats-grid">
      <div class="stat-card">
        <span class="stat-label">注册用户</span>
        <strong class="stat-value">{{ statistics.user.totalUsers ?? '-' }}</strong>
        <span class="stat-sub">活跃：{{ statistics.user.activeUsers ?? '-' }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">服务者数量</span>
        <strong class="stat-value">{{ statistics.provider.totalProviders ?? '-' }}</strong>
        <span class="stat-sub">已认证：{{ statistics.provider.certifiedProviders ?? '-' }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">服务总数</span>
        <strong class="stat-value">{{ statistics.service.totalServices ?? '-' }}</strong>
        <span class="stat-sub">上架：{{ statistics.service.activeServices ?? '-' }}</span>
      </div>
    </section>

    <main class="dashboard-content">
      <section class="card">
        <header class="card-header">
          <h3>用户列表</h3>
        </header>
        <div class="card-body">
          <p v-if="loadingUsers" class="muted">正在加载用户数据...</p>
          <template v-else>
            <table v-if="userList.length" class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>手机号</th>
                  <th>状态</th>
                  <th class="actions-column">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in userList" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.phone || '—' }}</td>
                  <td>
                    <span :class="['status-tag', user.status === 1 ? 'status-online' : 'status-offline']">
                      {{ user.status === 1 ? '启用' : '禁用' }}
                    </span>
                  </td>
                  <td class="actions-column">
                    <button class="text-button" type="button" @click="toggleUserStatus(user)">
                      {{ user.status === 1 ? '禁用' : '启用' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
            <p v-else class="muted">暂无用户数据。</p>
          </template>
        </div>
      </section>

      <section class="card">
        <header class="card-header">
          <h3>服务者列表</h3>
        </header>
        <div class="card-body">
          <p v-if="loadingProviders" class="muted">正在加载服务者数据...</p>
          <template v-else>
            <table v-if="providerList.length" class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>姓名</th>
                  <th>手机号</th>
                  <th>认证状态</th>
                  <th class="actions-column">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="provider in providerList" :key="provider.id">
                  <td>{{ provider.id }}</td>
                  <td>{{ provider.realName || provider.username }}</td>
                  <td>{{ provider.phone || '—' }}</td>
                  <td>
                    <span :class="['status-tag', provider.certificationStatus === 1 ? 'status-online' : 'status-offline']">
                      {{ certificationLabel(provider.certificationStatus) }}
                    </span>
                  </td>
                  <td class="actions-column">
                    <button class="text-button" type="button" @click="approveProvider(provider, true)" :disabled="provider.certificationStatus === 1">
                      通过
                    </button>
                    <button class="text-button" type="button" @click="approveProvider(provider, false)" :disabled="provider.certificationStatus === 2">
                      拒绝
                    </button>
                    <button class="danger-button" type="button" @click="toggleProviderStatus(provider)">
                      {{ provider.status === 1 ? '禁用' : '启用' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
            <p v-else class="muted">暂无服务者数据。</p>
          </template>
        </div>
      </section>

      <section class="card full-width">
        <header class="card-header">
          <h3>服务分类管理</h3>
        </header>
        <div class="card-body">
          <form class="inline-form" @submit.prevent="createCategory">
            <input v-model.trim="categoryForm.name" type="text" placeholder="分类名称" required />
            <input v-model.trim="categoryForm.description" type="text" placeholder="分类描述" />
            <input v-model.number="categoryForm.sortOrder" type="number" min="0" placeholder="排序" />
            <button class="primary-button" type="submit" :disabled="creatingCategory">
              {{ creatingCategory ? '创建中...' : '新增分类' }}
            </button>
          </form>
          <p v-if="categoryFeedback" class="feedback" :class="{ success: categoryFeedbackType === 'success' }">{{ categoryFeedback }}</p>

          <table v-if="categoryList.length" class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>名称</th>
                <th>描述</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="category in categoryList" :key="category.id">
                <td>{{ category.id }}</td>
                <td>{{ category.name }}</td>
                <td>{{ category.description || '—' }}</td>
                <td>
                  <span :class="['status-tag', category.status === 1 ? 'status-online' : 'status-offline']">
                    {{ category.status === 1 ? '启用' : '禁用' }}
                  </span>
                </td>
                <td class="actions-column">
                  <button class="text-button" type="button" @click="toggleCategoryStatus(category)">
                    {{ category.status === 1 ? '禁用' : '启用' }}
                  </button>
                  <button class="danger-button" type="button" @click="removeCategory(category.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="muted">尚未创建分类。</p>
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
  providerAPI,
  categoryAPI,
  serviceAPI,
  removeToken,
  setSessionProfile,
  getSessionProfile
} from '@/utils/api'

const router = useRouter()

const loading = ref(false)
const profile = ref<any>(getSessionProfile())
const displayName = computed(() => profile.value?.realName || profile.value?.username || '管理员')

const statistics = reactive({
  user: {} as Record<string, number>,
  provider: {} as Record<string, number>,
  service: {} as Record<string, number>
})

const userList = ref<any[]>([])
const loadingUsers = ref(false)

const providerList = ref<any[]>([])
const loadingProviders = ref(false)

const categoryList = ref<any[]>([])
const categoryForm = reactive({
  name: '',
  description: '',
  sortOrder: 0
})
const creatingCategory = ref(false)
const categoryFeedback = ref('')
const categoryFeedbackType = ref<'success' | 'error'>('success')

const certificationLabel = (status: number) => {
  switch (status) {
    case 1:
      return '已认证'
    case 2:
      return '认证失败'
    default:
      return '待审核'
  }
}

const fetchProfile = async () => {
  loading.value = true
  try {
    const response = await authAPI.currentUser()
    if (response?.data) {
      profile.value = response.data.profile
      setSessionProfile(response.data.profile)
    }
  } catch (error) {
    console.error('获取管理员信息失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    const [userStats, providerStats, serviceStats] = await Promise.all([
      userAPI.statistics(),
      providerAPI.statistics(),
      serviceAPI.getAdminStatistics()
    ])
    statistics.user = userStats?.data || {}
    statistics.provider = providerStats?.data || {}
    statistics.service = serviceStats?.data || {}
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

const fetchUsers = async () => {
  loadingUsers.value = true
  try {
    const response = await userAPI.listUsers(1, 10)
    userList.value = response?.data?.records || []
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loadingUsers.value = false
  }
}

const fetchProviders = async () => {
  loadingProviders.value = true
  try {
    const response = await providerAPI.listProviders(1, 10)
    providerList.value = response?.data?.records || []
  } catch (error) {
    console.error('加载服务者列表失败:', error)
  } finally {
    loadingProviders.value = false
  }
}

const fetchCategories = async () => {
  try {
    const response = await categoryAPI.listAll()
    categoryList.value = response?.data || []
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

const toggleUserStatus = async (user: any) => {
  try {
    const nextStatus = user.status === 1 ? 0 : 1
    const response = await userAPI.updateStatus(user.id, nextStatus)
    if (response?.code !== 200) {
      throw new Error(response?.message || '操作失败')
    }
    user.status = nextStatus
  } catch (error) {
    console.error('更新用户状态失败:', error)
  }
}

const toggleProviderStatus = async (provider: any) => {
  try {
    const nextStatus = provider.status === 1 ? 0 : 1
    const response = await providerAPI.updateStatus(provider.id, nextStatus)
    if (response?.code !== 200) {
      throw new Error(response?.message || '操作失败')
    }
    provider.status = nextStatus
  } catch (error) {
    console.error('更新服务者状态失败:', error)
  }
}

const approveProvider = async (provider: any, approve: boolean) => {
  try {
    const nextStatus = approve ? 1 : 2
    const response = await providerAPI.updateCertification(provider.id, nextStatus)
    if (response?.code !== 200) {
      throw new Error(response?.message || '操作失败')
    }
    provider.certificationStatus = nextStatus
  } catch (error) {
    console.error('更新认证状态失败:', error)
  }
}

const createCategory = async () => {
  categoryFeedback.value = ''
  creatingCategory.value = true
  try {
    const payload = {
      name: categoryForm.name,
      description: categoryForm.description,
      sortOrder: categoryForm.sortOrder
    }
    const response = await categoryAPI.create(payload)
    if (response?.code !== 200) {
      throw new Error(response?.message || '创建失败')
    }
    categoryFeedback.value = '分类创建成功'
    categoryFeedbackType.value = 'success'
    categoryForm.name = ''
    categoryForm.description = ''
    categoryForm.sortOrder = 0
    fetchCategories()
  } catch (error: any) {
    console.error('创建分类失败:', error)
    categoryFeedback.value = error?.message || '创建失败'
    categoryFeedbackType.value = 'error'
  } finally {
    creatingCategory.value = false
  }
}

const toggleCategoryStatus = async (category: any) => {
  try {
    const nextStatus = category.status === 1 ? 0 : 1
    const response = await categoryAPI.updateStatus(category.id, nextStatus)
    if (response?.code !== 200) {
      throw new Error(response?.message || '操作失败')
    }
    category.status = nextStatus
  } catch (error) {
    console.error('更新分类状态失败:', error)
  }
}

const removeCategory = async (id: number) => {
  if (!confirm('确认删除该分类？')) return
  try {
    const response = await categoryAPI.remove(id)
    if (response?.code !== 200) {
      throw new Error(response?.message || '删除失败')
    }
    categoryList.value = categoryList.value.filter((item) => item.id !== id)
  } catch (error) {
    console.error('删除分类失败:', error)
  }
}

const refreshAll = () => {
  fetchProfile()
  fetchStatistics()
  fetchUsers()
  fetchProviders()
  fetchCategories()
}

const logout = () => {
  removeToken()
  router.replace('/login')
}

onMounted(() => {
  refreshAll()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #eef2ff;
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
  color: #1e1b4b;
  margin-bottom: 4px;
}

.muted {
  color: #6366f1;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #312e81, #4338ca);
  border-radius: 18px;
  padding: 20px 24px;
  color: #f8fafc;
  box-shadow: 0 12px 30px rgba(49, 46, 129, 0.3);
}

.stat-label {
  font-size: 13px;
  text-transform: uppercase;
  opacity: 0.8;
  letter-spacing: 1px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  display: block;
  margin: 8px 0 4px;
}

.stat-sub {
  font-size: 13px;
  opacity: 0.85;
}

.dashboard-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
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

.card.full-width {
  grid-column: 1 / -1;
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.7);
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.card-body {
  padding: 24px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table thead {
  background: rgba(59, 130, 246, 0.1);
}

.data-table th,
.data-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;
  text-align: left;
  color: #1e293b;
}

.actions-column {
  display: flex;
  gap: 8px;
}

.inline-form {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.inline-form input {
  height: 40px;
  border-radius: 10px;
  border: 1px solid #c7d2fe;
  padding: 0 12px;
  background: #f8fafc;
}

.feedback {
  color: #dc2626;
  margin-bottom: 16px;
}

.feedback.success {
  color: #16a34a;
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
  background: rgba(59, 130, 246, 0.16);
  color: #1d4ed8;
}

.status-offline {
  background: rgba(148, 163, 184, 0.2);
  color: #475569;
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
  background: linear-gradient(135deg, #6366f1, #4338ca);
  color: #ffffff;
}

.primary-button:hover {
  box-shadow: 0 10px 18px rgba(99, 102, 241, 0.25);
  transform: translateY(-1px);
}

.text-button {
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
}

.text-button:hover {
  background: rgba(59, 130, 246, 0.2);
}

.danger-button {
  background: rgba(220, 38, 38, 0.12);
  color: #b91c1c;
}

.danger-button:hover {
  background: rgba(220, 38, 38, 0.2);
}

@media (max-width: 960px) {
  .dashboard {
    padding: 24px 16px 32px;
  }

  .dashboard-content {
    grid-template-columns: 1fr;
  }

  .actions-column {
    flex-direction: column;
  }
}
</style>
