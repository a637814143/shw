export const USER_STORAGE_KEY = 'housekeeping-users'
export const AUTH_ROLE_KEY = 'housekeeping-auth-role'
export const AUTH_ACCOUNT_KEY = 'housekeeping-auth-account'

export type UserRole = 'admin' | 'staff' | 'user'

export interface RegisteredUser {
  account: string
  password: string
  role: UserRole
}
