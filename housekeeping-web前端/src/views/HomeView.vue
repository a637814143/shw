<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createBooking, fetchBookings, fetchServices, type Booking, type BookingPayload, type ServiceItem } from '../utils/api'

const services = ref<ServiceItem[]>([])
const bookings = ref<Booking[]>([])
const isSubmitting = ref(false)
const feedback = reactive<{ type: 'success' | 'error' | null; message: string }>({
  type: null,
  message: '',
})

const bookingForm = reactive<BookingPayload>({
  customerName: '',
  phone: '',
  address: '',
  serviceDate: '',
  notes: '',
  serviceItemId: 0,
})

const resetForm = () => {
  bookingForm.customerName = ''
  bookingForm.phone = ''
  bookingForm.address = ''
  bookingForm.serviceDate = ''
  bookingForm.notes = ''
  bookingForm.serviceItemId = 0
}

const loadData = async () => {
  try {
    services.value = await fetchServices()
    bookings.value = await fetchBookings()
  } catch (error) {
    feedback.type = 'error'
    feedback.message = '无法加载服务数据，请确认后台服务已启动。'
    const fallbackServices: ServiceItem[] = [
      { id: 1, name: '家庭保洁（示例）', description: '2小时基础保洁服务，包含地面与台面清洁。', price: 128 },
      { id: 2, name: '深度保洁（示例）', description: '针对厨房与卫生间的深度清洁体验数据。', price: 258 },
      { id: 3, name: '家电清洗（示例）', description: '油烟机、空调拆洗演示数据。', price: 198 },
    ]
    services.value = fallbackServices
    bookings.value = [
      {
        id: 1,
        customerName: '张女士',
        phone: '138****0001',
        address: '上海市浦东新区花木路88号',
        serviceDate: new Date().toISOString().slice(0, 10),
        notes: '上午9点前到达，门禁8888。',
        status: 'PENDING',
        serviceItem: fallbackServices[0],
      },
    ]
  }
}

onMounted(() => {
  loadData()
})

const submitBooking = async () => {
  if (!bookingForm.serviceItemId) {
    feedback.type = 'error'
    feedback.message = '请先选择需要预约的服务项目。'
    return
  }

  isSubmitting.value = true
  feedback.type = null
  feedback.message = ''

  try {
    const newBooking = await createBooking(bookingForm)
    bookings.value = [newBooking, ...bookings.value]
    feedback.type = 'success'
    feedback.message = '预约提交成功，我们将尽快与您联系确认详情。'
    resetForm()
  } catch (error: any) {
    feedback.type = 'error'
    feedback.message = error?.message || '预约提交失败，请稍后再试。'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="home-view">
    <section class="hero">
      <div class="hero-text">
        <h2>专业家政服务，让生活更有品质</h2>
        <p>覆盖家庭保洁、深度清洁、家电清洗等热门服务，支持在线预约与实时跟进。</p>
        <ul>
          <li>严选服务团队 · 双重背景审查</li>
          <li>支持在线预约 · 24小时内快速响应</li>
          <li>标准化流程 · 服务全程可追踪</li>
        </ul>
      </div>
      <div class="hero-card">
        <h3>在线预约</h3>
        <form class="booking-form" @submit.prevent="submitBooking">
          <label>
            姓名
            <input v-model="bookingForm.customerName" placeholder="请输入您的姓名" required />
          </label>
          <label>
            联系电话
            <input v-model="bookingForm.phone" placeholder="请输入手机号" required />
          </label>
          <label>
            服务地址
            <input v-model="bookingForm.address" placeholder="例如：上海市浦东新区世纪大道100号" required />
          </label>
          <label>
            服务项目
            <select v-model.number="bookingForm.serviceItemId" required>
              <option disabled value="0">请选择</option>
              <option v-for="service in services" :key="service.id" :value="service.id">
                {{ service.name }}（￥{{ service.price }}）
              </option>
            </select>
          </label>
          <label>
            预约日期
            <input v-model="bookingForm.serviceDate" type="date" required />
          </label>
          <label>
            备注
            <textarea v-model="bookingForm.notes" rows="3" placeholder="可填写门禁信息或特殊需求"></textarea>
          </label>
          <button class="submit" :disabled="isSubmitting">
            {{ isSubmitting ? '提交中…' : '提交预约' }}
          </button>
        </form>
        <p v-if="feedback.type" :class="['feedback', feedback.type]">
          {{ feedback.message }}
        </p>
      </div>
    </section>

    <section class="services">
      <header>
        <h3>热门服务推荐</h3>
        <p>所有服务均经过专业培训与严格质检，为您提供舒心体验。</p>
      </header>
      <div class="service-grid">
        <article v-for="service in services" :key="service.id" class="service-card">
          <h4>{{ service.name }}</h4>
          <p>{{ service.description }}</p>
          <strong>￥{{ service.price.toFixed(0) }}</strong>
        </article>
      </div>
    </section>

    <section v-if="bookings.length" class="bookings">
      <header>
        <h3>最新预约</h3>
        <p>以下为最近提交的预约，便于前台人员统一调度。</p>
      </header>
      <ul>
        <li v-for="booking in bookings" :key="booking.id">
          <div class="booking-title">
            <span class="name">{{ booking.customerName }}</span>
            <span class="service">{{ booking.serviceItem.name }}</span>
          </div>
          <div class="booking-meta">
            <span>{{ booking.serviceDate }}</span>
            <span>{{ booking.phone }}</span>
            <span>{{ booking.address }}</span>
          </div>
        </li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
.home-view {
  display: flex;
  flex-direction: column;
  gap: 3.5rem;
}

.hero {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2.5rem;
  align-items: start;
}

.hero-text h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.hero-text p {
  color: #475569;
  margin-bottom: 1.2rem;
  line-height: 1.7;
}

.hero-text ul {
  padding-left: 1.1rem;
  color: #1d4ed8;
  display: grid;
  gap: 0.35rem;
}

.hero-card {
  background: linear-gradient(160deg, #1d4ed8 0%, #2563eb 60%, #60a5fa 100%);
  color: #ffffff;
  border-radius: 20px;
  padding: 2.25rem;
  box-shadow: 0 20px 40px rgba(37, 99, 235, 0.35);
}

.hero-card h3 {
  font-size: 1.4rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.booking-form {
  display: grid;
  gap: 1rem;
}

.booking-form label {
  font-size: 0.95rem;
  display: grid;
  gap: 0.4rem;
}

.booking-form input,
.booking-form textarea,
.booking-form select {
  border: none;
  border-radius: 12px;
  padding: 0.75rem 0.9rem;
  font-size: 0.95rem;
  font-family: inherit;
  color: #0f172a;
}

.booking-form textarea {
  resize: vertical;
  min-height: 90px;
}

.booking-form input::placeholder,
.booking-form textarea::placeholder {
  color: rgba(15, 23, 42, 0.55);
}

.booking-form select {
  appearance: none;
}

.booking-form .submit {
  margin-top: 0.5rem;
  padding: 0.85rem;
  border-radius: 999px;
  border: none;
  background: #fef3c7;
  color: #1d4ed8;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.booking-form .submit:disabled {
  opacity: 0.7;
  cursor: progress;
}

.booking-form .submit:not(:disabled):hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.15);
}

.feedback {
  margin-top: 1rem;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  font-size: 0.95rem;
  background: rgba(255, 255, 255, 0.18);
}

.feedback.success {
  border: 1px solid rgba(187, 247, 208, 0.8);
  color: #dcfce7;
}

.feedback.error {
  border: 1px solid rgba(254, 202, 202, 0.8);
  color: #fee2e2;
}

.services header,
.bookings header {
  margin-bottom: 1.5rem;
}

.services h3,
.bookings h3 {
  font-size: 1.45rem;
  margin-bottom: 0.4rem;
}

.services p,
.bookings p {
  color: #475569;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.75rem;
}

.service-card {
  padding: 1.75rem;
  border-radius: 18px;
  background: #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.15);
  box-shadow: 0 18px 24px rgba(15, 23, 42, 0.06);
  display: grid;
  gap: 0.8rem;
}

.service-card h4 {
  font-size: 1.2rem;
  color: #1d4ed8;
}

.service-card p {
  color: #475569;
  line-height: 1.6;
}

.service-card strong {
  color: #0f172a;
  font-size: 1.15rem;
}

.bookings ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 1rem;
}

.bookings li {
  padding: 1.25rem 1.5rem;
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid rgba(148, 163, 184, 0.12);
  box-shadow: 0 12px 20px rgba(15, 23, 42, 0.05);
}

.booking-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.6rem;
}

.booking-title .name {
  font-weight: 600;
  font-size: 1.05rem;
}

.booking-title .service {
  color: #1d4ed8;
}

.booking-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  font-size: 0.9rem;
  color: #475569;
}

@media (max-width: 768px) {
  .home-view {
    gap: 2.5rem;
  }

  .hero {
    grid-template-columns: 1fr;
  }

  .app-content {
    padding: 2rem 1.5rem;
  }
}
</style>
