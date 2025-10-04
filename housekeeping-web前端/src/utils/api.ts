export type UserRole = 'ADMIN' | 'USER' | 'PROVIDER'

export interface ServiceItem {
  id: number
  name: string
  description: string
  price: number
}

export interface Booking {
  id: number
  customerName: string
  phone: string
  address: string
  serviceDate: string
  notes: string
  status: string
  price: number
  createdBy: string
  assignedProvider?: string | null
  serviceItem: ServiceItem
}

export interface BookingPayload {
  customerName: string
  phone: string
  address: string
  serviceDate: string
  notes: string
  serviceItemId: number
  createdBy?: string
}

export interface AuthSession {
  token: string
  user: {
    userName: string
    types: UserRole
    money: number
  }
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8082/api'
const AUTH_STORAGE_KEY = 'hk.auth.session'
export const AUTH_EVENT = 'hk-auth-changed'

export function getSession(): AuthSession | null {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthSession
  } catch (error) {
    localStorage.removeItem(AUTH_STORAGE_KEY)
    return null
  }
}

export function saveSession(session: AuthSession) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session))
  window.dispatchEvent(new CustomEvent(AUTH_EVENT, { detail: { session } }))
}

export function clearSession() {
  localStorage.removeItem(AUTH_STORAGE_KEY)
  window.dispatchEvent(new CustomEvent(AUTH_EVENT))
}

async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
  const session = getSession()
  const headers = new Headers(options.headers || {})
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  if (session?.token) {
    headers.set('Authorization', `Bearer ${session.token}`)
  }

  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers,
    redirect: 'manual',
  })

  if (response.status === 302) {
    clearSession()
    const location = response.headers.get('Location') ?? '/login'
    window.location.replace(location)
    throw new Error('登录状态已失效，请重新登录')
  }

  if (response.status === 204) {
    return null as T
  }

  const text = await response.text()
  const body = text ? JSON.parse(text) : null

  if (!response.ok) {
    if (body && typeof body === 'object' && 'message' in body) {
      throw new Error((body as { message: string }).message)
    }
    throw new Error('请求失败，请稍后重试。')
  }

  return body as T
}

export async function login(payload: { username: string; password: string }) {
  const data = await request<AuthSession>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
  saveSession(data)
  return data
}

export async function register(payload: {
  username: string
  password: string
  type: Exclude<UserRole, 'ADMIN'>
}) {
  return request<{ userName: string; types: UserRole; money: number }>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function fetchWallet() {
  return request<{ userName: string; types: UserRole; money: number; role: UserRole }>('/wallet/me')
}

export async function fetchServices() {
  return request<ServiceItem[]>('/services')
}

export async function fetchAllBookings() {
  return request<Booking[]>('/bookings')
}

export async function fetchMyBookings() {
  return request<Booking[]>('/bookings/mine')
}

export async function createBooking(payload: BookingPayload) {
  return request<Booking>('/bookings', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateBooking(id: number, payload: BookingPayload) {
  return request<Booking>(`/bookings/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteBooking(id: number) {
  await request<void>(`/bookings/${id}`, {
    method: 'DELETE',
  })
}

export async function acceptBooking(id: number) {
  return request<Booking>(`/bookings/${id}/accept`, {
    method: 'POST',
  })
}
