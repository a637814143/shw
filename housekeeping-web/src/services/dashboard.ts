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

export type ServiceOrderStatus =
  | 'PENDING'
  | 'SCHEDULED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'REFUND_REQUESTED'
  | 'REFUND_APPROVED'
  | 'REFUND_REJECTED'

export interface HousekeepServiceItem {
  id: number
  name: string
  unit: string
  price: number
  contact: string
  description?: string | null
  companyId: number
  companyName: string
}

export interface ServiceOrderItem {
  id: number
  serviceId: number
  serviceName: string
  unit: string
  price: number
  contact: string
  companyName: string
  username: string
  status: ServiceOrderStatus
  scheduledAt: string
  specialRequest?: string | null
  progressNote?: string | null
  loyaltyPoints: number
  refundReason?: string | null
  refundResponse?: string | null
  handledBy?: string | null
  assignedWorker?: string | null
  workerContact?: string | null
  createdAt: string
  updatedAt: string
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
}

export interface UpdateAccountProfilePayload {
  displayName: string
  avatarBase64?: string
}

export interface CreateOrderPayload {
  serviceId: number
  scheduledAt: string
  specialRequest?: string
}

export interface RefundPayload {
  reason: string
}

export interface ReviewPayload {
  serviceId: number
  rating: number
  content: string
}

export interface CompanyServicePayload {
  name: string
  unit: string
  price: number
  contact: string
  description?: string
}

export interface RefundDecisionPayload {
  approve: boolean
  message?: string
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

export interface UpdateOrderProgressPayload {
  status: ServiceOrderStatus
  progressNote?: string
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

export interface AssignWorkerPayload {
  workerName: string
  workerContact: string
}

// 公共接口
export const fetchPublicServices = async (): Promise<HousekeepServiceItem[]> => {
  const response = await fetch(buildUrl('/api/public/services'))
  return handleResponse<HousekeepServiceItem[]>(response)
}

export const fetchServiceReviews = async (serviceId: number): Promise<ServiceReviewItem[]> => {
  const response = await fetch(buildUrl(`/api/public/services/${serviceId}/reviews`))
  return handleResponse<ServiceReviewItem[]>(response)
}

export const fetchCompanyReviews = async (): Promise<ServiceReviewItem[]> => {
  const response = await fetch(buildUrl('/api/company/services/reviews'), withAuthHeaders())
  return handleResponse<ServiceReviewItem[]>(response)
}

// 普通用户接口
export const fetchUserServices = async (): Promise<HousekeepServiceItem[]> => {
  const response = await fetch(buildUrl('/api/user/services'), withAuthHeaders())
  return handleResponse<HousekeepServiceItem[]>(response)
}

export const fetchUserOrders = async (): Promise<ServiceOrderItem[]> => {
  const response = await fetch(buildUrl('/api/user/services/orders'), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
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

export const fetchDashboardCarousels = async (): Promise<DashboardCarouselItem[]> => {
  const response = await fetch(buildUrl('/api/user/dashboard/carousels'), withAuthHeaders())
  return handleResponse<DashboardCarouselItem[]>(response)
}

export const fetchDashboardTips = async (): Promise<DashboardTipItem[]> => {
  const response = await fetch(buildUrl('/api/user/dashboard/tips'), withAuthHeaders())
  return handleResponse<DashboardTipItem[]>(response)
}

export const fetchDashboardAnnouncements = async (): Promise<DashboardAnnouncementItem[]> => {
  const response = await fetch(buildUrl('/api/user/dashboard/announcements'), withAuthHeaders())
  return handleResponse<DashboardAnnouncementItem[]>(response)
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
export const fetchCompanyServices = async (): Promise<HousekeepServiceItem[]> => {
  const response = await fetch(buildUrl('/api/company/services'), withAuthHeaders())
  return handleResponse<HousekeepServiceItem[]>(response)
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

export const fetchCompanyRefunds = async (): Promise<ServiceOrderItem[]> => {
  const response = await fetch(buildUrl('/api/company/services/refunds'), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
}

export const handleCompanyRefund = async (
  orderId: number,
  payload: RefundDecisionPayload,
): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/company/services/refunds/${orderId}`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const fetchCompanyOrders = async (): Promise<ServiceOrderItem[]> => {
  const response = await fetch(buildUrl('/api/company/services/orders'), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
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
export const fetchAdminUsers = async (): Promise<UserAccountItem[]> => {
  const response = await fetch(buildUrl('/api/admin/users'), withAuthHeaders())
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

export const fetchAdminRefunds = async (): Promise<ServiceOrderItem[]> => {
  const response = await fetch(buildUrl('/api/admin/refunds'), withAuthHeaders())
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

export const fetchAdminOrders = async (): Promise<ServiceOrderItem[]> => {
  const response = await fetch(buildUrl('/api/admin/orders'), withAuthHeaders())
  return handleResponse<ServiceOrderItem[]>(response)
}

export const assignAdminWorker = async (
  orderId: number,
  payload: AssignWorkerPayload,
): Promise<ServiceOrderItem> => {
  const response = await fetch(buildUrl(`/api/admin/orders/${orderId}/assign`), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<ServiceOrderItem>(response)
}

export const fetchAdminTransactions = async (): Promise<AccountTransactionItem[]> => {
  const response = await fetch(buildUrl('/api/admin/transactions'), withAuthHeaders())
  return handleResponse<AccountTransactionItem[]>(response)
}

export const fetchAdminFavorites = async (): Promise<ServiceFavoriteItem[]> => {
  const response = await fetch(buildUrl('/api/admin/favorites'), withAuthHeaders())
  return handleResponse<ServiceFavoriteItem[]>(response)
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
