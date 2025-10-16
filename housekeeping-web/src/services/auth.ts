import type { UserRole } from '../constants/auth'

const API_BASE_URL =
  typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE_URL
    ? String(import.meta.env.VITE_API_BASE_URL)
    : 'http://localhost:8080'

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

interface RawLoginResponse {
  token: string
  username: string
  realName: string
  role: string
}

export interface LoginResult {
  token: string
  account: string
  role: UserRole
}

export interface RegisterPayload {
  account: string
  password: string
  role: UserRole
}

export interface LoginPayload extends RegisterPayload {}

export interface RegisteredAccount {
  id: number
  account: string
  role: UserRole
}

const buildUrl = (path: string) => `${API_BASE_URL.replace(/\/$/, '')}${path}`

const handleResponse = async <T>(response: Response): Promise<T> => {
  const json = (await response.json()) as ApiResponse<T | null>
  if (!response.ok || json.code !== 200 || !json.data) {
    throw new Error(json.message || '请求失败')
  }
  return json.data
}

export const registerAccount = async (
  payload: RegisterPayload,
): Promise<RegisteredAccount> => {
  const response = await fetch(buildUrl('/api/public/accounts/register'), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  const data = await handleResponse<{ id: number; account: string; role: string }>(response)
  return {
    id: data.id,
    account: data.account,
    role: data.role.toLowerCase() as UserRole,
  }
}

export const loginAccount = async (payload: LoginPayload): Promise<LoginResult> => {
  const response = await fetch(buildUrl('/api/public/accounts/login'), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  const data = await handleResponse<RawLoginResponse>(response)
  return {
    token: data.token,
    account: data.username,
    role: data.role.toLowerCase() as UserRole,
  }
}
