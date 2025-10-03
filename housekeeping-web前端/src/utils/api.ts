// API工具类
const API_BASE_URL = 'http://localhost:8082/api'

// 存储键
const TOKEN_KEY = 'jwt_token'
const TOKEN_EXPIRES_AT_KEY = 'token_expires_at'
const USER_TYPE_KEY = 'user_type'
const SESSION_PROFILE_KEY = 'session_profile'

// 获取存储的token
const getToken = () => {
  return localStorage.getItem(TOKEN_KEY)
}

const getTokenExpiry = () => {
  const value = localStorage.getItem(TOKEN_EXPIRES_AT_KEY)
  return value ? Number(value) : null
}

const getUserType = () => localStorage.getItem(USER_TYPE_KEY)

// 设置token
const setToken = (token: string, expiresAt?: number) => {
  localStorage.setItem(TOKEN_KEY, token)
  if (expiresAt) {
    localStorage.setItem(TOKEN_EXPIRES_AT_KEY, String(expiresAt))
  }
}

const setUserType = (userType: string) => {
  localStorage.setItem(USER_TYPE_KEY, userType)
}

const setSessionProfile = (profile: any) => {
  localStorage.setItem(SESSION_PROFILE_KEY, JSON.stringify(profile))
}

const getSessionProfile = () => {
  const value = localStorage.getItem(SESSION_PROFILE_KEY)
  if (!value) return null
  try {
    return JSON.parse(value)
  } catch (error) {
    console.error('解析本地用户信息失败:', error)
    return null
  }
}

// 移除token
const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_EXPIRES_AT_KEY)
  localStorage.removeItem(USER_TYPE_KEY)
  localStorage.removeItem(SESSION_PROFILE_KEY)
}

const redirectToLogin = () => {
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

// 通用请求方法
const request = async (url: string, options: RequestInit = {}) => {
  const token = getToken()

  const headers = new Headers(options.headers ?? {})
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers
  })

  let payload: any = null
  const text = await response.text()
  if (text) {
    try {
      payload = JSON.parse(text)
    } catch (error) {
      console.error('解析响应失败:', error)
    }
  }

  if (response.status === 401) {
    removeToken()
    redirectToLogin()
    throw new Error(payload?.message || '未授权访问，请重新登录')
  }

  if (!response.ok) {
    throw new Error(payload?.message || '请求失败')
  }

  if (payload && typeof payload.code === 'number' && payload.code !== 200) {
    if (payload.code === 401) {
      removeToken()
      redirectToLogin()
    }
    throw new Error(payload.message || '请求失败')
  }

  return payload
}

// 登录API
export const authAPI = {
  login: (role: 'user' | 'provider' | 'admin', username: string, password: string) => {
    return request(`/auth/${role}/login`, {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
  },
  userLogin: (username: string, password: string) => {
    return authAPI.login('user', username, password)
  },
  adminLogin: (username: string, password: string) => {
    return authAPI.login('admin', username, password)
  },
  providerLogin: (username: string, password: string) => {
    return authAPI.login('provider', username, password)
  },
  userRegister: (username: string, password: string, phone: string) => {
    return request('/auth/user/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, phone })
    })
  },
  providerRegister: (username: string, password: string, phone: string) => {
    return request('/auth/provider/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, phone })
    })
  },
  adminRegister: (username: string, password: string, phone: string) => {
    return request('/auth/admin/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, phone })
    })
  },
  changePassword: (userId: number, userType: string, oldPassword: string, newPassword: string) => {
    return request('/auth/change-password', {
      method: 'POST',
      body: JSON.stringify({ userId, userType, oldPassword, newPassword })
    })
  },
  currentUser: () => {
    return request('/auth/me')
  }
}

// 用户API
export const userAPI = {
  getUserInfo: () => {
    return request('/user/profile')
  },
  updateUserInfo: (userData: any) => {
    return request('/user/profile', {
      method: 'PUT',
      body: JSON.stringify(userData)
    })
  },
  changePassword: (userId: number, userType: string, oldPassword: string, newPassword: string) => {
    return authAPI.changePassword(userId, userType, oldPassword, newPassword)
  },
  listUsers: (page = 1, size = 10, status?: number, keyword?: string) => {
    const params = new URLSearchParams({ page: String(page), size: String(size) })
    if (status !== undefined) params.append('status', String(status))
    if (keyword) params.append('keyword', keyword)
    return request(`/user/list?${params.toString()}`)
  },
  updateStatus: (id: number, status: number) => {
    return request(`/user/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status })
    })
  },
  deleteUser: (id: number) => {
    return request(`/user/${id}`, { method: 'DELETE' })
  },
  statistics: () => request('/user/statistics')
}

// 服务者API
export const providerAPI = {
  getProviderInfo: () => {
    return request('/provider/profile')
  },
  updateProviderInfo: (providerData: any) => {
    return request('/provider/profile', {
      method: 'PUT',
      body: JSON.stringify(providerData)
    })
  },
  listProviders: (page = 1, size = 10, status?: number, certificationStatus?: number, keyword?: string) => {
    const params = new URLSearchParams({ page: String(page), size: String(size) })
    if (status !== undefined) params.append('status', String(status))
    if (certificationStatus !== undefined) params.append('certificationStatus', String(certificationStatus))
    if (keyword) params.append('keyword', keyword)
    return request(`/provider/list?${params.toString()}`)
  },
  updateStatus: (id: number, status: number) => {
    return request(`/provider/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status })
    })
  },
  updateCertification: (id: number, certificationStatus: number) => {
    return request(`/provider/${id}/certification`, {
      method: 'PUT',
      body: JSON.stringify({ certificationStatus })
    })
  },
  deleteProvider: (id: number) => {
    return request(`/provider/${id}`, { method: 'DELETE' })
  },
  statistics: () => request('/provider/statistics')
}

// 分类API
export const categoryAPI = {
  listPublic: () => request('/category/public/list'),
  listAll: () => request('/category/list'),
  create: (payload: any) => request('/category', {
    method: 'POST',
    body: JSON.stringify(payload)
  }),
  update: (id: number, payload: any) => request(`/category/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  }),
  updateStatus: (id: number, status: number) => request(`/category/${id}/status`, {
    method: 'PUT',
    body: JSON.stringify({ status })
  }),
  remove: (id: number) => request(`/category/${id}`, { method: 'DELETE' })
}

// 服务API
export const serviceAPI = {
  getPopularServices: (page = 1, size = 10) => {
    return request(`/service/public/popular?page=${page}&size=${size}`)
  },
  searchServices: (keyword: string, page = 1, size = 10) => {
    return request(`/service/public/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`)
  },
  getServicesByCategory: (categoryId: number, page = 1, size = 10) => {
    return request(`/service/public/category/${categoryId}?page=${page}&size=${size}`)
  },
  getServiceById: (id: number) => {
    return request(`/service/public/${id}`)
  },
  getProviderServices: (page = 1, size = 10) => {
    return request(`/service/provider/list?page=${page}&size=${size}`)
  },
  createService: (serviceData: any) => {
    return request('/service', {
      method: 'POST',
      body: JSON.stringify(serviceData)
    })
  },
  updateService: (id: number, serviceData: any) => {
    return request(`/service/${id}`, {
      method: 'PUT',
      body: JSON.stringify(serviceData)
    })
  },
  deleteService: (id: number) => {
    return request(`/service/${id}`, {
      method: 'DELETE'
    })
  },
  updateServiceStatus: (id: number, status: number) => {
    return request(`/service/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status })
    })
  },
  getAdminStatistics: () => request('/service/admin/statistics')
}

export { getToken, setToken, removeToken, getUserType, setUserType, getTokenExpiry, setSessionProfile, getSessionProfile }
