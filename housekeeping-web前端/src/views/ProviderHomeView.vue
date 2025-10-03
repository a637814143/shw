<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h2>服务者控制台</h2>
        <p class="muted">欢迎，{{ displayName }}，在这里管理您的资料与服务</p>
      </div>
      <div class="header-actions">
        <button class="text-button" @click="refreshAll" :disabled="loading">刷新</button>
        <button class="danger-button" @click="logout">退出登录</button>
      </div>
    </header>

    <main class="dashboard-content">
      <section class="card">
        <header class="card-header">
          <h3>服务者资料</h3>
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
            <span>邮箱</span>
            <input v-model="profileForm.email" type="email" placeholder="请输入邮箱" />
          </label>
          <label>
            <span>联系地址</span>
            <input v-model="profileForm.address" type="text" placeholder="请输入联系地址" />
          </label>
          <label class="full-row">
            <span>技能特长</span>
            <textarea v-model="profileForm.skills" rows="3" placeholder="描述您的技能"></textarea>
          </label>
          <label class="full-row">
            <span>工作经验</span>
            <textarea v-model="profileForm.workExperience" rows="3" placeholder="填写工作经验"></textarea>
          </label>
          <div class="full-row feedback" v-if="profileFeedback.error">{{ profileFeedback.error }}</div>
          <div class="full-row feedback success" v-if="profileFeedback.success">{{ profileFeedback.success }}</div>
          <div class="full-row align-right">
            <button class="primary-button" type="submit" :disabled="savingProfile">
              {{ savingProfile ? '保存中...' : '保存信息' }}
            </button>
          </div>
        </form>
      </section>

      <section class="card">
        <header class="card-header">
          <h3>发布新服务</h3>
        </header>
        <form class="card-body form-grid" @submit.prevent="createService">
          <label>
            <span>服务名称</span>
            <input v-model.trim="serviceForm.name" type="text" placeholder="请输入服务名称" required />
          </label>
          <label>
            <span>服务分类</span>
            <select v-model="serviceForm.categoryId" required>
              <option disabled value="">请选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </label>
          <label>
            <span>服务价格</span>
            <input v-model="serviceForm.price" type="number" min="0" step="0.01" placeholder="价格" required />
          </label>
          <label>
            <span>计价单位</span>
            <input v-model="serviceForm.unit" type="text" placeholder="例如：次/小时" />
          </label>
          <label class="full-row">
            <span>服务简介</span>
            <textarea v-model="serviceForm.description" rows="3" placeholder="简要介绍服务内容"></textarea>
          </label>
          <div class="full-row feedback" v-if="serviceFeedback.error">{{ serviceFeedback.error }}</div>
          <div class="full-row feedback success" v-if="serviceFeedback.success">{{ serviceFeedback.success }}</div>
          <div class="full-row align-right">
            <button class="primary-button" type="submit" :disabled="creatingService">
              {{ creatingService ? '发布中...' : '发布服务' }}
            </button>
          </div>
        </form>
      </section>

      <section class="card full-width">
        <header class="card-header">
          <h3>我的服务</h3>
          <div class="header-actions">
            <button class="text-button" type="button" @click="fetchProviderServices">刷新列表</button>
          </div>
        </header>
        <div class="card-body">
          <p v-if="loadingServices" class="muted">正在加载服务数据...</p>
          <template v-else>
            <table v-if="providerServices.length" class="data-table">
              <thead>
                <tr>
                  <th>服务名称</th>
                  <th>分类</th>
                  <th>价格</th>
                  <th>状态</th>
                  <th class="actions-column">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="service in providerServices" :key="service.id">
                  <td>{{ service.name }}</td>
                  <td>{{ service.category?.name || findCategoryName(service.categoryId) }}</td>
                  <td>¥ {{ service.price }}</td>
                  <td>
                    <span :class="['status-tag', service.status === 1 ? 'status-online' : 'status-offline']">
                      {{ service.status === 1 ? '上架' : '下架' }}
                    </span>
                  </td>
                  <td class="actions-column">
                    <button class="text-button" type="button" @click="toggleServiceStatus(service)">
                      {{ service.status === 1 ? '下架' : '上架' }}
                    </button>
                    <button class="danger-button" type="button" @click="deleteService(service.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
            <p v-else class="muted">暂无服务，请先发布新服务。</p>
          </template>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { providerAPI, serviceAPI, categoryAPI, removeToken, setSessionProfile, getSessionProfile } from '@/utils/api'

interface ServiceCategory {
  id: number
  name: string
}

interface ProviderServiceItem {
  id: number
  name: string
  price: number
  status: number
  categoryId: number
  category?: ServiceCategory
}

const router = useRouter()

const loading = ref(false)
const profile = ref<any>(getSessionProfile())
const displayName = computed(() => profile.value?.realName || profile.value?.username || '服务者')

const profileForm = reactive({
  realName: '',
  phone: '',
  email: '',
  address: '',
  skills: '',
  workExperience: ''
})
const profileFeedback = reactive({ success: '', error: '' })
const savingProfile = ref(false)

const categories = ref<ServiceCategory[]>([])
const providerServices = ref<ProviderServiceItem[]>([])
const loadingServices = ref(false)

const serviceForm = reactive({
  name: '',
  categoryId: '' as number | '' ,
  price: '',
  unit: '次',
  description: ''
})
const serviceFeedback = reactive({ success: '', error: '' })
const creatingService = ref(false)

const syncProfileForm = () => {
  if (!profile.value) return
  profileForm.realName = profile.value.realName || ''
  profileForm.phone = profile.value.phone || ''
  profileForm.email = profile.value.email || ''
  profileForm.address = profile.value.address || ''
  profileForm.skills = profile.value.skills || ''
  profileForm.workExperience = profile.value.workExperience || ''
}

const fetchProfile = async () => {
  loading.value = true
  try {
    const response = await providerAPI.getProviderInfo()
    if (response?.data) {
      profile.value = response.data
      setSessionProfile(response.data)
      syncProfileForm()
    }
  } catch (error) {
    console.error('获取服务者信息失败:', error)
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

const fetchProviderServices = async () => {
  loadingServices.value = true
  try {
    const response = await serviceAPI.getProviderServices(1, 20)
    providerServices.value = response?.data?.records || []
  } catch (error) {
    console.error('加载服务列表失败:', error)
  } finally {
    loadingServices.value = false
  }
}

const refreshAll = () => {
  fetchProfile()
  fetchCategories()
  fetchProviderServices()
}

const updateProfile = async () => {
  profileFeedback.error = ''
  profileFeedback.success = ''
  savingProfile.value = true
  try {
    const payload = {
      realName: profileForm.realName.trim(),
      phone: profileForm.phone.trim(),
      email: profileForm.email.trim(),
      address: profileForm.address.trim(),
      skills: profileForm.skills.trim(),
      workExperience: profileForm.workExperience.trim()
    }
    const response = await providerAPI.updateProviderInfo(payload)
    if (response?.code !== 200) {
      throw new Error(response?.message || '更新失败')
    }
    profile.value = response.data
    setSessionProfile(response.data)
    profileFeedback.success = '资料已更新'
  } catch (error: any) {
    console.error('更新服务者资料失败:', error)
    profileFeedback.error = error?.message || '更新失败，请稍后重试'
  } finally {
    savingProfile.value = false
  }
}

const createService = async () => {
  serviceFeedback.error = ''
  serviceFeedback.success = ''

  if (!serviceForm.name || !serviceForm.categoryId || !serviceForm.price) {
    serviceFeedback.error = '请完整填写服务信息'
    return
  }

  const priceValue = Number(serviceForm.price)
  if (Number.isNaN(priceValue) || priceValue < 0) {
    serviceFeedback.error = '请输入合法的价格'
    return
  }

  creatingService.value = true
  try {
    const payload = {
      name: serviceForm.name.trim(),
      categoryId: Number(serviceForm.categoryId),
      price: priceValue,
      unit: serviceForm.unit.trim() || '次',
      description: serviceForm.description.trim()
    }
    const response = await serviceAPI.createService(payload)
    if (response?.code !== 200) {
      throw new Error(response?.message || '发布失败')
    }
    serviceFeedback.success = '服务发布成功'
    serviceForm.name = ''
    serviceForm.categoryId = ''
    serviceForm.price = ''
    serviceForm.unit = '次'
    serviceForm.description = ''
    fetchProviderServices()
  } catch (error: any) {
    console.error('发布服务失败:', error)
    serviceFeedback.error = error?.message || '发布失败，请稍后再试'
  } finally {
    creatingService.value = false
  }
}

const toggleServiceStatus = async (service: ProviderServiceItem) => {
  try {
    const nextStatus = service.status === 1 ? 0 : 1
    const response = await serviceAPI.updateServiceStatus(service.id, nextStatus)
    if (response?.code !== 200) {
      throw new Error(response?.message || '操作失败')
    }
    service.status = nextStatus
  } catch (error) {
    console.error('更新服务状态失败:', error)
  }
}

const deleteService = async (id: number) => {
  if (!confirm('确认删除该服务？')) return
  try {
    const response = await serviceAPI.deleteService(id)
    if (response?.code !== 200) {
      throw new Error(response?.message || '删除失败')
    }
    providerServices.value = providerServices.value.filter((item) => item.id !== id)
  } catch (error) {
    console.error('删除服务失败:', error)
  }
}

const findCategoryName = (id: number) => {
  return categories.value.find((item) => item.id === id)?.name || '—'
}

const logout = () => {
  removeToken()
  router.replace('/login')
}

onMounted(async () => {
  syncProfileForm()
  await fetchProfile()
  await fetchCategories()
  await fetchProviderServices()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f1f5f9;
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

.card.full-width {
  grid-column: 1 / -1;
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

.form-grid input,
.form-grid textarea,
.form-grid select {
  border-radius: 12px;
  border: 1px solid #cbd5f5;
  background: #f8fafc;
  padding: 10px 12px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-grid input:focus,
.form-grid textarea:focus,
.form-grid select:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
  outline: none;
}

.form-grid textarea {
  resize: vertical;
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
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  color: #ffffff;
}

.primary-button:hover {
  box-shadow: 0 10px 18px rgba(14, 165, 233, 0.25);
  transform: translateY(-1px);
}

.text-button {
  background: rgba(14, 165, 233, 0.12);
  color: #0284c7;
}

.text-button:hover {
  background: rgba(14, 165, 233, 0.2);
}

.danger-button {
  background: rgba(220, 38, 38, 0.12);
  color: #b91c1c;
}

.danger-button:hover {
  background: rgba(220, 38, 38, 0.2);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table thead {
  background: rgba(14, 165, 233, 0.08);
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

  .dashboard-content {
    grid-template-columns: 1fr;
  }

  .actions-column {
    flex-direction: column;
  }
}
</style>
