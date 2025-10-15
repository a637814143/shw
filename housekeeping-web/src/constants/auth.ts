export const AUTH_TOKEN_KEY = 'housekeeping-auth-token'
export const AUTH_ROLE_KEY = 'housekeeping-auth-role'
export const AUTH_ACCOUNT_KEY = 'housekeeping-auth-account'

export type UserRole = 'admin' | 'company' | 'user'

export const ROLE_LABELS: Record<UserRole, string> = {
  admin: '系统管理员',
  company: '家政公司',
  user: '普通用户',
}

export interface AuthCredentials {
  account: string
  password: string
  role: UserRole
}
