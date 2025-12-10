import { AUTH_TOKEN_KEY } from '../constants/auth'

const API_BASE_URL =
  typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE_URL
    ? String(import.meta.env.VITE_API_BASE_URL)
    : 'http://localhost:8080'

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

const buildUrl = (path: string) => `${API_BASE_URL.replace(/\/$/, '')}${path}`

const withAuthHeaders = (options: RequestInit = {}): RequestInit => {
  const token = sessionStorage.getItem(AUTH_TOKEN_KEY)
  const headers = new Headers(options.headers || {})
  headers.set('Content-Type', 'application/json')
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }
  return { ...options, headers }
}

const handleResponse = async <T>(response: Response): Promise<T> => {
  const rawText = await response.text()
  if (!rawText) {
    if (response.ok) {
      return null as T
    }
    throw new Error(response.statusText || '请求失败')
  }

  let parsed: Partial<ApiResponse<T>> & Record<string, any>
  try {
    parsed = JSON.parse(rawText)
  } catch (error) {
    const fallbackMessage = response.statusText || '服务器响应异常'
    throw new Error(fallbackMessage)
  }

  if (typeof parsed.code !== 'number') {
    const fallbackMessage = parsed.message || parsed.error || response.statusText || '请求失败'
    throw new Error(fallbackMessage)
  }

  if (!response.ok || parsed.code !== 200) {
    throw new Error(parsed.message || '请求失败')
  }

  return parsed.data as T
}

const withFixedServiceTime = <T extends HousekeepServiceItem | ServiceOrderItem>(
  items: (T & { serviceTime?: string | null })[],
): T[] => {
  return items.map((item) => ({
    ...item,
    serviceTime: '2小时',
  }))
}

export type ServiceOrderStatus =
  | 'PENDING'
  | 'SCHEDULED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'REFUND_REQUESTED'
  | 'REFUND_APPROVED'
  | 'REFUND_REJECTED'

export type HousekeepServiceStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export interface HousekeepServiceItem {
  id: number
  name: string
  unit: string
  price: number
  contact: string
  serviceTime: string
  description?: string | null
  companyId: number
  companyName: string
  categoryId?: number | null
  categoryName?: string | null
  availableStaffCount: number
  status?: HousekeepServiceStatus
  reviewNote?: string | null
}

export interface CompanyServicePage {
  items: HousekeepServiceItem[]
  total: number
  page: number
  size: number
  averagePrice: number
}

export interface ServiceOrderItem {
  id: number
  serviceId: number
  serviceName: string
  unit: string
  serviceTime?: string | null
  price: number
  contact: string
  companyName: string
  username: string
  status: ServiceOrderStatus
  scheduledAt: string
  specialRequest?: string | null
  serviceAddress?: string | null
  progressNote?: string | null
  loyaltyPoints: number
  refundReason?: string | null
  refundResponse?: string | null
  handledBy?: string | null
  assignedWorker?: string | null
  workerContact?: string | null
  customerContactPhone?: string | null
  customerAddress?: string | null
  createdAt: string
  updatedAt: string
  settlementReleased: boolean
  settlementReleasedAt?: string | null
  userConfirmed: boolean
  categoryId?: number | null
  categoryName?: string | null
  assignedStaffId?: number | null
}

export interface ServiceReviewItem {
  id: number
  serviceId: number
  serviceName: string
  username: string
  rating: number
  content: string
  createdAt: string
  updatedAt: string
  companyReply?: string | null
  replyAt?: string | null
  pinned?: boolean | null
}

export interface UserAccountItem {
  id: number
  username: string
  role: string
  balance: number
  loyaltyPoints: number
}

export interface AccountProfileItem {
  id: number
  username: string
  displayName: string
  role: string
  balance: number
  loyaltyPoints: number
  avatarBase64: string
  contactPhone?: string | null
  contactAddress?: string | null
  companyDescription?: string | null
}

export interface UpdateAccountProfilePayload {
  displayName: string
  avatarBase64?: string
  contactPhone?: string
  contactAddress?: string
  companyDescription?: string
}

export interface CreateOrderPayload {
  serviceId: number
  scheduledAt: string
  specialRequest?: string
  serviceAddress?: string
}

export interface RefundPayload {
  reason: string
}

export interface ReviewPayload {
  serviceId: number
  rating: number
  content: string
}

export interface TimeSlotAvailabilityItem {
  slotKey: string
  label: string
  availableStaff: number
  totalStaff: number
}

export interface CompanyServicePayload {
  name: string
  unit: string
  price: number
  contact: string
  serviceTime: string
  description?: string
  categoryId: number
}

export interface RefundDecisionPayload {
  approve: boolean
  message?: string
}

export interface ServiceApprovalPayload {
  approve: boolean
  reason?: string
}

export interface UpdateWalletPayload {
  money: number
}

export interface UpdatePasswordPayload {
  password: string
}

export interface UpdateLoyaltyPayload {
  loyaltyPoints: number
}

export interface UpdateAccountPasswordPayload {
  currentPassword: string
  newPassword: string
}

export interface UpdateOrderProgressPayload {
  status: ServiceOrderStatus
  progressNote?: string
}

export type PaymentGatewayStatus = 'PENDING' | 'CONFIRMED' | 'DECLINED' | 'ERROR'

export interface PaymentGatewayCheckResult {
  status: PaymentGatewayStatus
  message: string
  rawPayload?: string | null
  token?: string | null
  expiresAt?: string | null
}

export interface CreatePaymentSessionPayload {
  serviceName: string
  companyName?: string | null
  amount: number
}

export interface PaymentSessionInfo {
  token: string
  qrPath: string
  qrUrl: string
  expiresAt: string
}

export interface CompanyConversationItem {
  orderId: number
  serviceName: string
  customerName: string
  status: ServiceOrderStatus
  lastMessage: string
  lastMessageAt: string | null
  unreadCount: number
}

export interface CompanyMessageItem {
  id: number
  orderId: number
  serviceName: string
  senderName: string
  recipientName: string
  content: string
  createdAt: string
  readAt?: string | null
  incoming: boolean
}

export interface CompanyMessagePayload {
  content: string
}

export type AccountTransactionType = 'RECHARGE' | 'WITHDRAW' | 'ADJUST'

export interface DashboardCarouselItem {
  id: number
  title: string
  imageUrl: string
  serviceLink?: string | null
}

export interface DashboardAnnouncementItem {
  id: number
  title: string
  content: string
}

export interface DashboardTipItem {
  id: number
  title: string
  content: string
}

export interface AdminOverviewMetric {
  status: string
  count: number
}

export interface AdminTrendPoint {
  label: string
  amount: number
}

export interface AdminOverviewItem {
  totalRecharge: number
  totalWithdraw: number
  totalUsers: number
  totalCompanies: number
  totalAdmins: number
  weeklyRecharge: AdminTrendPoint[]
  appointmentMetrics: AdminOverviewMetric[]
}

export interface AccountTransactionItem {
  id: number
  username: string
  type: AccountTransactionType
  amount: number
  note?: string | null
  createdAt: string
}

export interface ServiceFavoriteItem {
  id: number
  username: string
  serviceId: number
  serviceName: string
  companyName: string
  createdAt: string
}

export interface UserConversationItem {
  orderId: number
  serviceName: string
  companyName: string
  status: ServiceOrderStatus
  lastMessage: string
  lastMessageAt: string | null
  unreadCount: number
}

export interface WalletRechargePayload {
  amount: number
}

export interface PointsExchangePayload {
  points: number
}

export interface ServiceCategoryItem {
  id: number
  name: string
  description?: string | null
  serviceCount: number
  totalStaffCount: number
  availableStaffCount: number
}

export interface ServiceCategoryPayload {
  name: string
  description?: string
}

export interface CompanyStaffItem {
  id: number
  name: string
  contact: string
  notes?: string | null
  serviceTimeSlots: string[]
  createdAt: string
  updatedAt: string
  categoryId?: number | null
  categoryName?: string | null
  assigned: boolean
}

export interface CompanyStaffPayload {
  name: string
  contact: string
  categoryId: number
  serviceTimeSlots: string[]
  notes?: string
}

// 公共接口
export const fetchPublicServices = async (params?: { keyword?: string; categoryId?: number }): Promise<HousekeepServiceItem[]> => {
  const url = new URL(buildUrl('/api/public/services'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  if (typeof params?.categoryId === 'number') {
    url.searchParams.set('categoryId', String(params.categoryId))
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  const result = await handleResponse<HousekeepServiceItem[]>(response)
  return withFixedServiceTime(result)
}

export const fetchPublicTips = async (params?: { keyword?: string }): Promise<DashboardTipItem[]> => {
  const url = new URL(buildUrl('/api/public/dashboard/tips'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString())
  return handleResponse<DashboardTipItem[]>(response)
}

export const fetchPublicAnnouncements = async (
  params?: { keyword?: string },
): Promise<DashboardAnnouncementItem[]> => {
  const url = new URL(buildUrl('/api/public/dashboard/announcements'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString())
  return handleResponse<DashboardAnnouncementItem[]>(response)
}

export const fetchServiceReviews = async (serviceId: number): Promise<ServiceReviewItem[]> => {
  const response = await fetch(buildUrl(`/api/public/services/${serviceId}/reviews`))
  return handleResponse<ServiceReviewItem[]>(response)
}

export const fetchCompanyReviews = async (params?: { keyword?: string }): Promise<ServiceReviewItem[]> => {
  const url = new URL(buildUrl('/api/company/services/reviews'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<ServiceReviewItem[]>(response)
}

export const deleteCompanyReview = async (reviewId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/company/services/reviews/${reviewId}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteCompanyReviews = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/company/services/reviews/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

// 普通用户接口
export const fetchUserServices = async (params?: { keyword?: string; categoryId?: number }): Promise<HousekeepServiceItem[]> => {
  const url = new URL(buildUrl('/api/user/services'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  if (typeof params?.categoryId === 'number') {
    url.searchParams.set('categoryId', String(params.categoryId))
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  const result = await handleResponse<HousekeepServiceItem[]>(response)
  return withFixedServiceTime(result)
}

export const fetchUserOrders = async (params?: { keyword?: string }): Promise<ServiceOrderItem[]> => {
  const url = new URL(buildUrl('/api/user/services/orders'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
}

export const fetchServiceSlotAvailability = async (
  serviceId: number,
  date: string,
): Promise<TimeSlotAvailabilityItem[]> => {
  const url = new URL(buildUrl(`/api/user/services/${serviceId}/slots`))
  url.searchParams.set('date', date)
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<TimeSlotAvailabilityItem[]>(response)
}

export const deleteUserOrder = async (orderId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/services/orders/${orderId}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteUserOrders = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/user/services/orders/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const createUserOrder = async (payload: CreateOrderPayload): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl('/api/user/services/orders'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const requestUserRefund = async (
  orderId: number,
  payload: RefundPayload,
): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/user/services/orders/${orderId}/refund`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const submitUserReview = async (payload: ReviewPayload): Promise<ServiceReviewItem> => {
  const response = await fetch(buildUrl('/api/user/services/reviews'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceReviewItem>(response)
}

export const fetchUserReviews = async (params?: { keyword?: string }): Promise<ServiceReviewItem[]> => {
  const url = new URL(buildUrl('/api/user/services/reviews'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<ServiceReviewItem[]>(response)
}

export const deleteUserReview = async (reviewId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/services/reviews/${reviewId}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteUserReviews = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/user/services/reviews/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const fetchDashboardCarousels = async (params?: { keyword?: string }): Promise<DashboardCarouselItem[]> => {
  const url = new URL(buildUrl('/api/user/dashboard/carousels'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<DashboardCarouselItem[]>(response)
}

export const fetchDashboardTips = async (params?: { keyword?: string }): Promise<DashboardTipItem[]> => {
  const url = new URL(buildUrl('/api/user/dashboard/tips'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<DashboardTipItem[]>(response)
}

export const fetchDashboardAnnouncements = async (params?: { keyword?: string }): Promise<DashboardAnnouncementItem[]> => {
  const url = new URL(buildUrl('/api/user/dashboard/announcements'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<DashboardAnnouncementItem[]>(response)
}

export const createQrPaymentSession = async (
  payload: CreatePaymentSessionPayload,
): Promise<PaymentSessionInfo> => {
  const response = await fetch(buildUrl('/api/user/payments/qr/session'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<PaymentSessionInfo>(response)
}

export const checkQrPaymentStatus = async (token: string): Promise<PaymentGatewayCheckResult> => {
  const url = new URL(buildUrl('/api/user/payments/qr/status'))
  url.searchParams.set('token', token)
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<PaymentGatewayCheckResult>(response)
}

export const fetchUserFavorites = async (): Promise<ServiceFavoriteItem[]> => {
  const response = await fetch(buildUrl('/api/user/services/favorites'), withAuthHeaders())
  return handleResponse<ServiceFavoriteItem[]>(response)
}

export const addUserFavorite = async (serviceId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/services/${serviceId}/favorite`), {
    ...withAuthHeaders({ method: 'POST' }),
  })
  await handleResponse<null>(response)
}

export const removeUserFavorite = async (serviceId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/services/${serviceId}/favorite`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const confirmUserOrder = async (orderId: number): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/user/services/orders/${orderId}/confirm`), {
    ...withAuthHeaders({ method: 'POST' }),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const fetchUserConversations = async (): Promise<UserConversationItem[]> => {
  const response = await fetch(buildUrl('/api/user/messages/threads'), withAuthHeaders())
  return handleResponse<UserConversationItem[]>(response)
}

export const fetchUserMessages = async (
  orderId: number,
  params?: { since?: string },
): Promise<CompanyMessageItem[]> => {
  const url = new URL(buildUrl(`/api/user/messages/orders/${orderId}`))
  if (params?.since) {
    url.searchParams.set('since', params.since)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<CompanyMessageItem[]>(response)
}

export const sendUserMessage = async (
  orderId: number,
  payload: CompanyMessagePayload,
): Promise<CompanyMessageItem> => {
  const response = await fetch(buildUrl(`/api/user/messages/orders/${orderId}`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<CompanyMessageItem>(response)
}

export const markUserConversationRead = async (orderId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/messages/orders/${orderId}/read`), {
    ...withAuthHeaders({ method: 'POST' }),
  })
  await handleResponse<null>(response)
}

export const rechargeUserWallet = async (
  payload: WalletRechargePayload,
): Promise<AccountProfileItem> => {
  const response = await fetch(buildUrl('/api/user/wallet/recharge'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<AccountProfileItem>(response)
}

export const exchangeUserPoints = async (
  payload: PointsExchangePayload,
): Promise<AccountProfileItem> => {
  const response = await fetch(buildUrl('/api/user/wallet/exchange'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<AccountProfileItem>(response)
}

// 家政公司接口
export const fetchCompanyServices = async (params?: {
  keyword?: string
  categoryId?: number
  page?: number
  size?: number
}): Promise<CompanyServicePage> => {
  const url = new URL(buildUrl('/api/company/services'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  if (typeof params?.categoryId === 'number') {
    url.searchParams.set('categoryId', String(params.categoryId))
  }
  if (params?.page) {
    url.searchParams.set('page', String(params.page))
  }
  if (params?.size) {
    url.searchParams.set('size', String(params.size))
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  const result = await handleResponse<CompanyServicePage>(response)
  return { ...result, items: withFixedServiceTime(result.items) }
}

export const createCompanyService = async (
  payload: CompanyServicePayload,
): Promise<HousekeepServiceItem> => {
  const response = await fetch(buildUrl('/api/company/services'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<HousekeepServiceItem>(response)
}

export const updateCompanyService = async (
  id: number,
  payload: CompanyServicePayload,
): Promise<HousekeepServiceItem> => {
  const response = await fetch(buildUrl(`/api/company/services/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<HousekeepServiceItem>(response)
}

export const deleteCompanyService = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/company/services/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteCompanyServices = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/company/services/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const fetchCompanyStaff = async (
  params?: { keyword?: string; categoryId?: number; scheduledAt?: string },
): Promise<CompanyStaffItem[]> => {
  const url = new URL(buildUrl('/api/company/staff'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  if (typeof params?.categoryId === 'number') {
    url.searchParams.set('categoryId', String(params.categoryId))
  }
  if (params?.scheduledAt) {
    url.searchParams.set('scheduledAt', params.scheduledAt)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<CompanyStaffItem[]>(response)
}

export const createCompanyStaff = async (
  payload: CompanyStaffPayload,
): Promise<CompanyStaffItem> => {
  const response = await fetch(buildUrl('/api/company/staff'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<CompanyStaffItem>(response)
}

export const updateCompanyStaff = async (
  id: number,
  payload: CompanyStaffPayload,
): Promise<CompanyStaffItem> => {
  const response = await fetch(buildUrl(`/api/company/staff/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<CompanyStaffItem>(response)
}

export const deleteCompanyStaff = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/company/staff/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteCompanyStaffBatch = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/company/staff/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const assignCompanyStaff = async (
  staffId: number,
  orderId: number,
): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/company/staff/${staffId}/assign`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify({ orderId }),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const fetchServiceCategories = async (): Promise<ServiceCategoryItem[]> => {
  const response = await fetch(buildUrl('/api/public/service-categories'), withAuthHeaders())
  return handleResponse<ServiceCategoryItem[]>(response)
}

export const fetchAdminServiceCategories = async (): Promise<ServiceCategoryItem[]> => {
  const response = await fetch(buildUrl('/api/admin/service-categories'), withAuthHeaders())
  return handleResponse<ServiceCategoryItem[]>(response)
}

export const createAdminServiceCategory = async (
  payload: ServiceCategoryPayload,
): Promise<ServiceCategoryItem> => {
  const response = await fetch(buildUrl('/api/admin/service-categories'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceCategoryItem>(response)
}

export const updateAdminServiceCategory = async (
  id: number,
  payload: ServiceCategoryPayload,
): Promise<ServiceCategoryItem> => {
  const response = await fetch(buildUrl(`/api/admin/service-categories/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceCategoryItem>(response)
}

export const deleteAdminServiceCategory = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/admin/service-categories/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteAdminServiceCategories = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/admin/service-categories/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const fetchAdminServiceApprovals = async (params?: {
  keyword?: string
  categoryId?: number
  status?: HousekeepServiceStatus
}): Promise<HousekeepServiceItem[]> => {
  const url = new URL(buildUrl('/api/admin/services/approval'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  if (typeof params?.categoryId === 'number') {
    url.searchParams.set('categoryId', String(params.categoryId))
  }
  if (params?.status) {
    url.searchParams.set('status', params.status)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  const result = await handleResponse<HousekeepServiceItem[]>(response)
  return withFixedServiceTime(result)
}

export const reviewAdminService = async (
  serviceId: number,
  payload: ServiceApprovalPayload,
): Promise<HousekeepServiceItem> => {
  const response = await fetch(buildUrl(`/api/admin/services/${serviceId}/review`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<HousekeepServiceItem>(response)
}

export const fetchCompanyOrders = async (params?: { keyword?: string; categoryId?: number }): Promise<ServiceOrderItem[]> => {
  const url = new URL(buildUrl('/api/company/services/orders'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  if (typeof params?.categoryId === 'number') {
    url.searchParams.set('categoryId', String(params.categoryId))
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
}

export const deleteCompanyOrder = async (orderId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/company/services/orders/${orderId}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteCompanyOrders = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/company/services/orders/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const updateCompanyOrderProgress = async (
  orderId: number,
  payload: UpdateOrderProgressPayload,
): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/company/services/orders/${orderId}/progress`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const fetchCompanyConversations = async (): Promise<CompanyConversationItem[]> => {
  const response = await fetch(buildUrl('/api/company/messages/threads'), withAuthHeaders())
  return handleResponse<CompanyConversationItem[]>(response)
}

export const fetchCompanyMessages = async (
  orderId: number,
  params?: { since?: string },
): Promise<CompanyMessageItem[]> => {
  const url = new URL(buildUrl(`/api/company/messages/orders/${orderId}`))
  if (params?.since) {
    url.searchParams.set('since', params.since)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<CompanyMessageItem[]>(response)
}

export const sendCompanyMessage = async (
  orderId: number,
  payload: CompanyMessagePayload,
): Promise<CompanyMessageItem> => {
  const response = await fetch(buildUrl(`/api/company/messages/orders/${orderId}`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<CompanyMessageItem>(response)
}

export const markCompanyConversationRead = async (orderId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/company/messages/orders/${orderId}/read`), {
    ...withAuthHeaders({ method: 'POST' }),
  })
  await handleResponse<null>(response)
}

// 管理员接口
export const fetchAdminUsers = async (params?: { keyword?: string }): Promise<UserAccountItem[]> => {
  const url = new URL(buildUrl('/api/admin/users'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<UserAccountItem[]>(response)
}

export const updateAdminWallet = async (
  userId: number,
  payload: UpdateWalletPayload,
): Promise<UserAccountItem> => {
  const response = await fetch(buildUrl(`/api/admin/users/${userId}/wallet`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<UserAccountItem>(response)
}

export const updateAdminLoyalty = async (
  userId: number,
  payload: UpdateLoyaltyPayload,
): Promise<UserAccountItem> => {
  const response = await fetch(buildUrl(`/api/admin/users/${userId}/loyalty`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<UserAccountItem>(response)
}

export const updateAdminPassword = async (
  userId: number,
  payload: UpdatePasswordPayload,
): Promise<UserAccountItem> => {
  const response = await fetch(buildUrl(`/api/admin/users/${userId}/password`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<UserAccountItem>(response)
}

export const deleteAdminUser = async (userId: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/admin/users/${userId}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteAdminUsers = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/admin/users/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const fetchAdminRefunds = async (params?: {
  stage?: 'pending' | 'processed' | 'all'
  keyword?: string
}): Promise<ServiceOrderItem[]> => {
  const url = new URL(buildUrl('/api/admin/refunds'))
  if (params?.stage) {
    url.searchParams.set('stage', params.stage)
  }
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
}

export const handleAdminRefund = async (
  orderId: number,
  payload: RefundDecisionPayload,
): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/admin/refunds/${orderId}`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const deleteAdminRefunds = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/admin/refunds'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const fetchCurrentAccount = async (): Promise<AccountProfileItem> => {
  const response = await fetch(buildUrl('/api/account/me'), withAuthHeaders())
  return handleResponse<AccountProfileItem>(response)
}

export const updateCurrentAccount = async (
  payload: UpdateAccountProfilePayload,
): Promise<AccountProfileItem> => {
  const response = await fetch(buildUrl('/api/account/me'), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<AccountProfileItem>(response)
}

export const updateCurrentPassword = async (
  payload: UpdateAccountPasswordPayload,
): Promise<void> => {
  const response = await fetch(buildUrl('/api/account/password'), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  await handleResponse<void>(response)
}

export const fetchAdminOverview = async (): Promise<AdminOverviewItem> => {
  const response = await fetch(buildUrl('/api/admin/overview'), withAuthHeaders())
  return handleResponse<AdminOverviewItem>(response)
}

export const fetchAdminCompanies = async (): Promise<UserAccountItem[]> => {
  const response = await fetch(buildUrl('/api/admin/companies'), withAuthHeaders())
  return handleResponse<UserAccountItem[]>(response)
}

export const fetchAdminManagers = async (): Promise<UserAccountItem[]> => {
  const response = await fetch(buildUrl('/api/admin/managers'), withAuthHeaders())
  return handleResponse<UserAccountItem[]>(response)
}

export const fetchAdminOrders = async (params?: { keyword?: string }): Promise<ServiceOrderItem[]> => {
  const url = new URL(buildUrl('/api/admin/orders'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
}

export const deleteAdminOrders = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/admin/orders'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const settleAdminOrder = async (orderId: number): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/admin/orders/${orderId}/settle`), withAuthHeaders({ method: 'POST' }))
  return handleResponse<ServiceOrderItem>(response)
}

export const fetchAdminTransactions = async (params?: { keyword?: string }): Promise<AccountTransactionItem[]> => {
  const url = new URL(buildUrl('/api/admin/transactions'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<AccountTransactionItem[]>(response)
}

export const deleteAdminTransactions = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/admin/transactions'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const fetchAdminFavorites = async (params?: { keyword?: string }): Promise<ServiceFavoriteItem[]> => {
  const url = new URL(buildUrl('/api/admin/favorites'))
  if (params?.keyword) {
    url.searchParams.set('keyword', params.keyword)
  }
  const response = await fetch(url.toString(), withAuthHeaders())
  return handleResponse<ServiceFavoriteItem[]>(response)
}

export const deleteAdminFavorites = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/admin/favorites'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const createDashboardTip = async (payload: DashboardTipItem): Promise<DashboardTipItem> => {
  const response = await fetch(buildUrl('/api/user/dashboard/tips'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify({ title: payload.title, content: payload.content }),
  })
  return handleResponse<DashboardTipItem>(response)
}

export const updateDashboardTip = async (
  id: number,
  payload: DashboardTipItem,
): Promise<DashboardTipItem> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/tips/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify({ title: payload.title, content: payload.content }),
  })
  return handleResponse<DashboardTipItem>(response)
}

export const deleteDashboardTip = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/tips/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteDashboardTips = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/user/dashboard/tips/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const createDashboardCarousel = async (
  payload: DashboardCarouselItem,
): Promise<DashboardCarouselItem> => {
  const response = await fetch(buildUrl('/api/user/dashboard/carousels'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify({
      title: payload.title,
      imageUrl: payload.imageUrl,
      serviceLink: payload.serviceLink ?? null,
    }),
  })
  return handleResponse<DashboardCarouselItem>(response)
}

export const updateDashboardCarousel = async (
  id: number,
  payload: DashboardCarouselItem,
): Promise<DashboardCarouselItem> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/carousels/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify({
      title: payload.title,
      imageUrl: payload.imageUrl,
      serviceLink: payload.serviceLink ?? null,
    }),
  })
  return handleResponse<DashboardCarouselItem>(response)
}

export const deleteDashboardCarousel = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/carousels/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteDashboardCarousels = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/user/dashboard/carousels/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}

export const createDashboardAnnouncement = async (
  payload: DashboardAnnouncementItem,
): Promise<DashboardAnnouncementItem> => {
  const response = await fetch(buildUrl('/api/user/dashboard/announcements'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify({ title: payload.title, content: payload.content }),
  })
  return handleResponse<DashboardAnnouncementItem>(response)
}

export const updateDashboardAnnouncement = async (
  id: number,
  payload: DashboardAnnouncementItem,
): Promise<DashboardAnnouncementItem> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/announcements/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify({ title: payload.title, content: payload.content }),
  })
  return handleResponse<DashboardAnnouncementItem>(response)
}

export const deleteDashboardAnnouncement = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/announcements/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const deleteDashboardAnnouncements = async (ids: number[]): Promise<void> => {
  const response = await fetch(buildUrl('/api/user/dashboard/announcements/batch'), {
    ...withAuthHeaders({ method: 'DELETE' }),
    body: JSON.stringify({ ids }),
  })
  await handleResponse<null>(response)
}
