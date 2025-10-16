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
  refundReason?: string | null
  refundResponse?: string | null
  handledBy?: string | null
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
}

export interface UserAccountItem {
  id: number
  username: string
  role: string
  balance: number
}

export interface AccountProfileItem {
  id: number
  username: string
  role: string
  balance: number
}

export interface CreateOrderPayload {
  serviceId: number
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

// 公共接口
export const fetchPublicServices = async (): Promise<HousekeepServiceItem[]> => {
  const response = await fetch(buildUrl('/api/public/services'))
  return handleResponse<HousekeepServiceItem[]>(response)
}

export const fetchServiceReviews = async (serviceId: number): Promise<ServiceReviewItem[]> => {
  const response = await fetch(buildUrl(`/api/public/services/${serviceId}/reviews`))
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
