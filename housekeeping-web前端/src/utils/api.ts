// API工具类
const API_BASE_URL = (import.meta.env?.VITE_API_BASE_URL as string) || 'http://localhost:8082/api'

// 获取存储的token
const getToken = () => {
  return localStorage.getItem('jwt_token')
}

// 设置token
const setToken = (token: string) => {
  localStorage.setItem('jwt_token', token)
}

// 移除token
const removeToken = () => {
  localStorage.removeItem('jwt_token')
}

// 通用请求方法
const request = async (url: string, options: RequestInit = {}) => {
  const token = getToken()
  
  const config: RequestInit = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers,
    },
  }

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config)
    
    // 如果返回401，说明token无效或过期
    if (response.status === 401) {
      removeToken()
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
      throw new Error('未授权访问，请重新登录')
    }
    
    const data = await response.json()

    if (!response.ok) {
      throw new Error(data.message || '请求失败')
    }

    if (data && typeof data === 'object' && 'code' in data && data.code !== 200) {
      throw new Error(data.message || '请求失败')
    }

    return data
  } catch (error) {
    console.error('API请求错误:', error)
    throw error
  }
}

// 登录API
export const authAPI = {
  // 用户登录
  userLogin: (username: string, password: string) => {
    return request('/auth/user/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
  },
  
  // 管理员登录
  adminLogin: (username: string, password: string) => {
    return request('/auth/admin/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
  },
  
  // 服务者登录
  providerLogin: (username: string, password: string) => {
    return request('/auth/provider/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
  },
  
  // 用户注册
  userRegister: (username: string, password: string, phone: string) => {
    return request('/auth/user/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, phone })
    })
  },
  
  // 服务者注册
  providerRegister: (username: string, password: string, phone: string) => {
    return request('/auth/provider/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, phone })
    })
  }
}

// 用户API
export const userAPI = {
  // 获取用户信息
  getUserInfo: () => {
    return request('/user/profile')
  },
  
  // 更新用户信息
  updateUserInfo: (userData: any) => {
    return request('/user/profile', {
      method: 'PUT',
      body: JSON.stringify(userData)
    })
  },
  
  // 修改密码
  changePassword: (userId: number, userType: string, oldPassword: string, newPassword: string) => {
    return request('/auth/change-password', {
      method: 'POST',
      body: JSON.stringify({ userId, userType, oldPassword, newPassword })
    })
  }
}

// 服务者API
export const providerAPI = {
  // 获取服务者信息
  getProviderInfo: () => {
    return request('/provider/profile')
  },
  
  // 更新服务者信息
  updateProviderInfo: (providerData: any) => {
    return request('/provider/profile', {
      method: 'PUT',
      body: JSON.stringify(providerData)
    })
  }
}

// 服务API
export const serviceAPI = {
  // 获取热门服务列表
  getPopularServices: (page = 1, size = 10) => {
    return request(`/service/public/popular?page=${page}&size=${size}`)
  },

  // 兼容旧方法名称
  getServices: (page = 1, size = 10) => {
    return request(`/service/public/popular?page=${page}&size=${size}`)
  },

  // 根据分类获取服务
  getServicesByCategory: (categoryId: number, page = 1, size = 10) => {
    return request(`/service/public/category/${categoryId}?page=${page}&size=${size}`)
  },

  // 搜索服务
  searchServices: (keyword: string, page = 1, size = 10) => {
    const encodedKeyword = encodeURIComponent(keyword)
    return request(`/service/public/search?keyword=${encodedKeyword}&page=${page}&size=${size}`)
  },
  
  // 获取服务详情
  getServiceById: (id: number) => {
    return request(`/service/public/${id}`)
  },
  
  // 获取服务者的服务列表
  getProviderServices: (page = 1, size = 10) => {
    return request(`/service/provider/list?page=${page}&size=${size}`)
  },
  
  // 创建服务
  createService: (serviceData: any) => {
    return request('/service', {
      method: 'POST',
      body: JSON.stringify(serviceData)
    })
  },
  
  // 更新服务
  updateService: (id: number, serviceData: any) => {
    return request(`/service/${id}`, {
      method: 'PUT',
      body: JSON.stringify(serviceData)
    })
  },
  
  // 删除服务
  deleteService: (id: number) => {
    return request(`/service/${id}`, {
      method: 'DELETE'
    })
  }
}

// 服务分类API
export const categoryAPI = {
  // 获取公开的分类列表
  getPublicCategories: () => {
    return request('/category/public/list')
  }
}

export { getToken, setToken, removeToken }
