<template>
  <div class="dashboard">
    <header class="panel-header" v-if="wallet">
      <div>
        <h2>{{ greeting }}</h2>
        <p class="muted">当前角色：{{ translateRole(wallet.types) }}</p>
      </div>
      <div class="wallet" v-if="wallet.types !== 'ADMIN'">
        <span>钱包余额</span>
        <strong>￥{{ wallet.money.toFixed(2) }}</strong>
      </div>
    </header>

    <section v-if="role === 'USER'" class="card-grid">
      <article class="card wide">
        <h3>{{ editing ? '更新预约' : '创建新的预约' }}</h3>
        <form class="form-grid" @submit.prevent="submitBooking">
          <label>
            服务项目
            <select v-model.number="form.serviceItemId" required>
              <option value="0" disabled>请选择服务</option>
              <option v-for="item in services" :key="item.id" :value="item.id">
                {{ item.name }}（￥{{ item.price }}）
              </option>
            </select>
          </label>
          <label>
            客户姓名
            <input v-model.trim="form.customerName" required />
          </label>
          <label>
            联系电话
            <input v-model.trim="form.phone" required />
          </label>
          <label>
            服务地址
            <input v-model.trim="form.address" required />
          </label>
          <label>
            服务日期
            <input v-model="form.serviceDate" type="date" required />
          </label>
          <label class="full">
            备注
            <textarea v-model="form.notes" rows="3" placeholder="可填写门禁信息或其他需求"></textarea>
          </label>
          <div class="actions">
            <button type="submit" :disabled="pending">
              {{ pending ? '提交中…' : editing ? '保存修改' : '提交预约' }}
            </button>
            <button v-if="editing" type="button" class="secondary" @click="resetForm">取消编辑</button>
          </div>
        </form>
        <p v-if="feedback" :class="['feedback', feedbackType]">{{ feedback }}</p>
      </article>

      <article class="card">
        <header class="card-header">
          <h3>我的预约</h3>
          <span>{{ bookings.length }} 条记录</span>
        </header>
        <ul class="booking-list">
          <li v-for="booking in bookings" :key="booking.id">
            <div>
              <strong>{{ booking.serviceItem.name }}</strong>
              <p>{{ booking.customerName }} · {{ booking.phone }}</p>
              <small>服务日期：{{ booking.serviceDate }} | 金额：￥{{ booking.price.toFixed(2) }}</small>
              <small>状态：{{ statusLabel(booking) }}</small>
            </div>
            <div class="row-actions">
              <button type="button" @click="startEdit(booking)">编辑</button>
              <button type="button" class="danger" @click="removeBooking(booking)">删除</button>
            </div>
          </li>
        </ul>
        <p v-if="!bookings.length" class="empty">暂无预约记录，立即创建一条吧。</p>
      </article>
    </section>

    <section v-else-if="role === 'ADMIN'" class="card-grid">
      <article class="card wide">
        <h3>{{ editing ? '更新预约' : '代用户创建预约' }}</h3>
        <form class="form-grid" @submit.prevent="submitBooking">
          <label>
            用户账号
            <input v-model.trim="form.createdBy" placeholder="请输入用户账号" required />
          </label>
          <label>
            服务项目
            <select v-model.number="form.serviceItemId" required>
              <option value="0" disabled>请选择服务</option>
              <option v-for="item in services" :key="item.id" :value="item.id">
                {{ item.name }}（￥{{ item.price }}）
              </option>
            </select>
          </label>
          <label>
            客户姓名
            <input v-model.trim="form.customerName" required />
          </label>
          <label>
            联系电话
            <input v-model.trim="form.phone" required />
          </label>
          <label>
            服务地址
            <input v-model.trim="form.address" required />
          </label>
          <label>
            服务日期
            <input v-model="form.serviceDate" type="date" required />
          </label>
          <label class="full">
            备注
            <textarea v-model="form.notes" rows="3" placeholder="可填写备注信息"></textarea>
          </label>
          <div class="actions">
            <button type="submit" :disabled="pending">
              {{ pending ? '提交中…' : editing ? '保存修改' : '创建预约' }}
            </button>
            <button v-if="editing" type="button" class="secondary" @click="resetForm">取消编辑</button>
          </div>
        </form>
        <p v-if="feedback" :class="['feedback', feedbackType]">{{ feedback }}</p>
      </article>

      <article class="card">
        <header class="card-header">
          <h3>预约列表</h3>
          <span>{{ bookings.length }} 条记录</span>
        </header>
        <ul class="booking-list">
          <li v-for="booking in bookings" :key="booking.id">
            <div>
              <strong>{{ booking.serviceItem.name }}</strong>
              <p>{{ booking.customerName }} · {{ booking.phone }}</p>
              <small>服务日期：{{ booking.serviceDate }} | 金额：￥{{ booking.price.toFixed(2) }}</small>
              <small>提交账号：{{ booking.createdBy }} | 状态：{{ statusLabel(booking) }}</small>
              <small v-if="booking.assignedProvider">已接受：{{ booking.assignedProvider }}</small>
            </div>
            <div class="row-actions">
              <button type="button" @click="startEdit(booking)">编辑</button>
              <button type="button" class="danger" @click="removeBooking(booking)">删除</button>
            </div>
          </li>
        </ul>
        <p v-if="!bookings.length" class="empty">暂无预约数据。</p>
      </article>
    </section>

    <section v-else-if="role === 'PROVIDER'" class="card-grid">
      <article class="card wide">
        <header class="card-header">
          <h3>待接单预约</h3>
          <span>{{ availableBookings.length }} 条可接任务</span>
        </header>
        <ul class="booking-list">
          <li v-for="booking in availableBookings" :key="booking.id">
            <div>
              <strong>{{ booking.serviceItem.name }}</strong>
              <p>{{ booking.customerName }} · {{ booking.phone }}</p>
              <small>服务日期：{{ booking.serviceDate }} | 金额：￥{{ booking.price.toFixed(2) }}</small>
              <small>提交账号：{{ booking.createdBy }}</small>
            </div>
            <div class="row-actions">
              <button type="button" @click="accept(booking)" :disabled="pending">
                接受任务
              </button>
            </div>
          </li>
        </ul>
        <p v-if="!availableBookings.length" class="empty">暂时没有可接受的预约任务。</p>
      </article>

      <article class="card">
        <header class="card-header">
          <h3>我已接受的预约</h3>
          <span>{{ acceptedBookings.length }} 条记录</span>
        </header>
        <ul class="booking-list">
          <li v-for="booking in acceptedBookings" :key="booking.id">
            <div>
              <strong>{{ booking.serviceItem.name }}</strong>
              <p>{{ booking.customerName }} · {{ booking.phone }}</p>
              <small>服务日期：{{ booking.serviceDate }} | 金额：￥{{ booking.price.toFixed(2) }}</small>
            </div>
          </li>
        </ul>
        <p v-if="!acceptedBookings.length" class="empty">还没有接受任何任务。</p>
      </article>
    </section>

    <p v-else class="empty">正在加载用户信息...</p>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watchEffect } from 'vue'
import {
  acceptBooking,
  createBooking,
  deleteBooking,
  fetchAllBookings,
  fetchMyBookings,
  fetchServices,
  fetchWallet,
  getSession,
  saveSession,
  updateBooking,
  type Booking,
  type BookingPayload,
  type ServiceItem,
  type UserRole,
} from '../utils/api'

const emit = defineEmits<{ (e: 'wallet-updated', value: number): void }>()

const wallet = ref<{ userName: string; types: UserRole; money: number } | null>(null)
const services = ref<ServiceItem[]>([])
const bookings = ref<Booking[]>([])
const pending = ref(false)
const feedback = ref('')
const feedbackType = ref<'success' | 'error'>('success')
const editing = ref<Booking | null>(null)

const form = reactive<BookingPayload>({
  customerName: '',
  phone: '',
  address: '',
  serviceDate: '',
  notes: '',
  serviceItemId: 0,
  createdBy: '',
})

const session = ref(getSession())
const role = computed<UserRole | undefined>(() => wallet.value?.types ?? session.value?.user.types)
const greeting = computed(() => {
  const name = wallet.value?.userName ?? '访客'
  switch (role.value) {
    case 'ADMIN':
      return `欢迎回来，${name} 管理员`
    case 'PROVIDER':
      return `您好，${name} （家政人员）`
    case 'USER':
      return `您好，${name}`
    default:
      return '欢迎使用家政服务系统'
  }
})

const availableBookings = computed(() => bookings.value.filter((b) => !b.assignedProvider))
const acceptedBookings = computed(() =>
  bookings.value.filter((b) => b.assignedProvider && b.assignedProvider === wallet.value?.userName)
)

watchEffect(() => {
  if (wallet.value) {
    emit('wallet-updated', wallet.value.money)
  }
})

const translateRole = (value: string) => {
  switch (value) {
    case 'ADMIN':
      return '管理员'
    case 'PROVIDER':
      return '家政服务人员'
    default:
      return '普通用户'
  }
}

const statusLabel = (booking: Booking) => {
  if (booking.assignedProvider) {
    return `已被 ${booking.assignedProvider} 接受`
  }
  return booking.status === 'PENDING' ? '待处理' : booking.status
}

const resetForm = () => {
  form.customerName = ''
  form.phone = ''
  form.address = ''
  form.serviceDate = ''
  form.notes = ''
  form.serviceItemId = 0
  form.createdBy = ''
  editing.value = null
  feedback.value = ''
}

const populateForm = (booking: Booking) => {
  form.customerName = booking.customerName
  form.phone = booking.phone
  form.address = booking.address
  form.serviceDate = booking.serviceDate
  form.notes = booking.notes ?? ''
  form.serviceItemId = booking.serviceItem.id
  form.createdBy = booking.createdBy
}

const loadWallet = async () => {
  const data = await fetchWallet()
  wallet.value = { userName: data.userName, types: data.types, money: data.money }
  const current = getSession()
  if (current) {
    saveSession({
      ...current,
      user: { ...current.user, money: data.money, types: data.types },
    })
    session.value = getSession()
  }
}

const loadBookings = async () => {
  if (role.value === 'ADMIN' || role.value === 'PROVIDER') {
    bookings.value = await fetchAllBookings()
  } else {
    bookings.value = await fetchMyBookings()
  }
}

const loadServices = async () => {
  services.value = await fetchServices()
}

const loadAll = async () => {
  pending.value = true
  try {
    await loadWallet()
    await Promise.all([loadServices(), loadBookings()])
  } catch (error: any) {
    feedback.value = error?.message ?? '数据加载失败，请检查后台服务'
    feedbackType.value = 'error'
  } finally {
    pending.value = false
  }
}

const submitBooking = async () => {
  if (!form.serviceItemId) {
    feedback.value = '请选择服务项目'
    feedbackType.value = 'error'
    return
  }
  if (role.value === 'ADMIN' && !form.createdBy) {
    feedback.value = '请填写预约所属的用户账号'
    feedbackType.value = 'error'
    return
  }

  pending.value = true
  feedback.value = ''
  try {
    let result: Booking
    if (editing.value) {
      result = await updateBookingRequest(editing.value.id)
      const index = bookings.value.findIndex((b) => b.id === editing.value?.id)
      if (index >= 0) {
        bookings.value.splice(index, 1, result)
      }
      feedbackType.value = 'success'
      feedback.value = '预约已更新'
    } else {
      result = await createBooking(form)
      if (role.value === 'USER') {
        bookings.value = [result, ...bookings.value]
      } else {
        await loadBookings()
      }
      feedbackType.value = 'success'
      feedback.value = '预约提交成功'
    }
    await loadWallet()
    resetForm()
  } catch (error: any) {
    feedbackType.value = 'error'
    feedback.value = error?.message ?? '提交失败，请稍后再试'
  } finally {
    pending.value = false
  }
}

const updateBookingRequest = async (id: number) => {
  const payload: BookingPayload = {
    customerName: form.customerName,
    phone: form.phone,
    address: form.address,
    serviceDate: form.serviceDate,
    notes: form.notes,
    serviceItemId: form.serviceItemId,
  }
  if (role.value === 'ADMIN') {
    payload.createdBy = form.createdBy
  }
  return await updateBooking(id, payload)
}

const startEdit = (booking: Booking) => {
  editing.value = booking
  populateForm(booking)
  if (role.value !== 'ADMIN') {
    form.createdBy = ''
  }
}

const removeBooking = async (booking: Booking) => {
  if (!confirm('确定要删除该预约吗？')) {
    return
  }
  pending.value = true
  try {
    await deleteBooking(booking.id)
    bookings.value = bookings.value.filter((item) => item.id !== booking.id)
    await loadWallet()
    feedbackType.value = 'success'
    feedback.value = '预约已删除，金额已退回钱包'
  } catch (error: any) {
    feedbackType.value = 'error'
    feedback.value = error?.message ?? '删除失败'
  } finally {
    pending.value = false
  }
}

const accept = async (booking: Booking) => {
  pending.value = true
  feedback.value = ''
  try {
    const updated = await acceptBooking(booking.id)
    const index = bookings.value.findIndex((item) => item.id === booking.id)
    if (index >= 0) {
      bookings.value.splice(index, 1, updated)
    }
    await loadWallet()
    feedbackType.value = 'success'
    feedback.value = '任务接受成功，收益已入账'
  } catch (error: any) {
    feedbackType.value = 'error'
    feedback.value = error?.message ?? '接受任务失败'
  } finally {
    pending.value = false
  }
}

loadAll()
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 1rem;
}

.panel-header h2 {
  font-size: 1.8rem;
  margin: 0;
}

.muted {
  color: #64748b;
  margin-top: 0.4rem;
}

.wallet {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  padding: 1rem 1.5rem;
  border-radius: 18px;
  min-width: 200px;
}

.wallet span {
  font-size: 0.9rem;
  opacity: 0.8;
}

.wallet strong {
  font-size: 1.4rem;
  margin-top: 0.3rem;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem;
  align-items: start;
}

.card {
  background: #f8fafc;
  border-radius: 20px;
  padding: 1.8rem;
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.card.wide {
  grid-column: span 2;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.card-header span {
  color: #475569;
  font-size: 0.9rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.2rem 1.5rem;
}

.form-grid label {
  display: flex;
  flex-direction: column;
  font-weight: 600;
  color: #1e293b;
}

.form-grid input,
.form-grid select,
.form-grid textarea {
  margin-top: 0.5rem;
  border: 1px solid #dbeafe;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
}

.form-grid textarea {
  resize: vertical;
}

.form-grid input:focus,
.form-grid select:focus,
.form-grid textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.12);
}

.form-grid .full {
  grid-column: 1 / -1;
}

.actions {
  grid-column: 1 / -1;
  display: flex;
  gap: 1rem;
}

.actions button {
  padding: 0.8rem 1.5rem;
  border-radius: 12px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s ease;
}

.actions button:first-child {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
}

.actions .secondary {
  background: #e2e8f0;
  color: #1e293b;
}

.feedback {
  margin-top: 1rem;
  padding: 0.8rem 1rem;
  border-radius: 12px;
  font-size: 0.95rem;
}

.feedback.success {
  background: #dcfce7;
  color: #166534;
}

.feedback.error {
  background: #fee2e2;
  color: #991b1b;
}

.booking-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.booking-list li {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  background: #fff;
  border-radius: 14px;
  padding: 1rem 1.2rem;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
}

.booking-list strong {
  font-size: 1rem;
}

.booking-list small {
  display: block;
  color: #64748b;
  margin-top: 0.25rem;
}

.row-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 90px;
}

.row-actions button {
  border: none;
  border-radius: 10px;
  padding: 0.45rem 0.8rem;
  font-weight: 600;
  cursor: pointer;
  background: #e0e7ff;
  color: #312e81;
}

.row-actions button.danger {
  background: #fee2e2;
  color: #b91c1c;
}

.empty {
  text-align: center;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .card.wide {
    grid-column: span 1;
  }

  .booking-list li {
    flex-direction: column;
  }

  .row-actions {
    flex-direction: row;
    justify-content: flex-end;
  }
}
</style>
