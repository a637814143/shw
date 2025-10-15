export const AUTH_TOKEN_KEY = 'housekeeping-auth-token'
export const AUTH_ROLE_KEY = 'housekeeping-auth-role'
export const AUTH_ACCOUNT_KEY = 'housekeeping-auth-account'

export type UserRole = 'admin' | 'staff' | 'user'

export interface AuthCredentials {
  account: string
  password: string
  role: UserRole
}
