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
  const json = (await response.json()) as ApiResponse<T | null>
  if (!response.ok || json.code !== 200) {
    throw new Error(json.message || '请求失败')
  }
  return json.data as T
}

export interface DashboardServiceItem {
  id: number
  name: string
  description: string
  icon: string
}

export interface DashboardTipItem {
  id: number
  title: string
  content: string
}

export interface DashboardReviewItem {
  id: number
  author: string
  serviceName: string
  rating: number
  content: string
}

export interface DashboardOfferItem {
  id: number
  title: string
  content: string
  tag?: string | null
}

export type ServicePayload = Omit<DashboardServiceItem, 'id'>
export type TipPayload = Omit<DashboardTipItem, 'id'>
export type ReviewPayload = Omit<DashboardReviewItem, 'id'>
export type OfferPayload = Omit<DashboardOfferItem, 'id'>

export const fetchServices = async (): Promise<DashboardServiceItem[]> => {
  const response = await fetch(buildUrl('/api/user/dashboard/services'), withAuthHeaders())
  return handleResponse<DashboardServiceItem[]>(response)
}

export const createService = async (payload: ServicePayload): Promise<DashboardServiceItem> => {
  const response = await fetch(buildUrl('/api/user/dashboard/services'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardServiceItem>(response)
}

export const updateService = async (
  id: number,
  payload: ServicePayload,
): Promise<DashboardServiceItem> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/services/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardServiceItem>(response)
}

export const deleteService = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/services/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const fetchTips = async (): Promise<DashboardTipItem[]> => {
  const response = await fetch(buildUrl('/api/user/dashboard/tips'), withAuthHeaders())
  return handleResponse<DashboardTipItem[]>(response)
}

export const createTip = async (payload: TipPayload): Promise<DashboardTipItem> => {
  const response = await fetch(buildUrl('/api/user/dashboard/tips'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardTipItem>(response)
}

export const updateTip = async (
  id: number,
  payload: TipPayload,
): Promise<DashboardTipItem> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/tips/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardTipItem>(response)
}

export const deleteTip = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/tips/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const fetchReviews = async (): Promise<DashboardReviewItem[]> => {
  const response = await fetch(buildUrl('/api/user/dashboard/reviews'), withAuthHeaders())
  return handleResponse<DashboardReviewItem[]>(response)
}

export const createReview = async (payload: ReviewPayload): Promise<DashboardReviewItem> => {
  const response = await fetch(buildUrl('/api/user/dashboard/reviews'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardReviewItem>(response)
}

export const updateReview = async (
  id: number,
  payload: ReviewPayload,
): Promise<DashboardReviewItem> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/reviews/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardReviewItem>(response)
}

export const deleteReview = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/reviews/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}

export const fetchOffers = async (): Promise<DashboardOfferItem[]> => {
  const response = await fetch(buildUrl('/api/user/dashboard/offers'), withAuthHeaders())
  return handleResponse<DashboardOfferItem[]>(response)
}

export const createOffer = async (payload: OfferPayload): Promise<DashboardOfferItem> => {
  const response = await fetch(buildUrl('/api/user/dashboard/offers'), {
    ...withAuthHeaders({ method: 'POST' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardOfferItem>(response)
}

export const updateOffer = async (
  id: number,
  payload: OfferPayload,
): Promise<DashboardOfferItem> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/offers/${id}`), {
    ...withAuthHeaders({ method: 'PUT' }),
    body: JSON.stringify(payload),
  })
  return handleResponse<DashboardOfferItem>(response)
}

export const deleteOffer = async (id: number): Promise<void> => {
  const response = await fetch(buildUrl(`/api/user/dashboard/offers/${id}`), {
    ...withAuthHeaders({ method: 'DELETE' }),
  })
  await handleResponse<null>(response)
}
