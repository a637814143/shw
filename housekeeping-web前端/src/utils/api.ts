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
  serviceItem: ServiceItem
}

export interface BookingPayload {
  customerName: string
  phone: string
  address: string
  serviceDate: string
  notes: string
  serviceItemId: number
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8082/api'

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${url}`, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...options,
  })

  if (!response.ok) {
    const error = await response.json().catch(() => ({}))
    throw new Error(error.message || '请求失败，请稍后重试。')
  }

  return response.json() as Promise<T>
}

export async function fetchServices(): Promise<ServiceItem[]> {
  return request<ServiceItem[]>('/services')
}

export async function fetchBookings(): Promise<Booking[]> {
  return request<Booking[]>('/bookings')
}

export async function createBooking(payload: BookingPayload): Promise<Booking> {
  return request<Booking>('/bookings', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
