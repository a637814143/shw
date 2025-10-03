import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getTokenExpiry, getUserType, removeToken, getSessionProfile } from '@/utils/api'

const roleDefaultRoute: Record<string, string> = {
  ADMIN: '/admin-home',
  PROVIDER: '/provider-home',
  USER: '/user-home'
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: () => {
        const token = getToken()
        const expiresAt = getTokenExpiry()
        const userType = getUserType()
        if (token && expiresAt && expiresAt > Date.now() && userType) {
          return roleDefaultRoute[userType] ?? '/login'
        }
        return '/login'
      }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/user-home',
      name: 'user-home',
      component: () => import('../views/UserHomeView.vue'),
      meta: { requiresAuth: true, roles: ['USER'] }
    },
    {
      path: '/provider-home',
      name: 'provider-home',
      component: () => import('../views/ProviderHomeView.vue'),
      meta: { requiresAuth: true, roles: ['PROVIDER'] }
    },
    {
      path: '/admin-home',
      name: 'admin-home',
      component: () => import('../views/AdminHomeView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  if (!to.meta.requiresAuth) {
    if (to.name === 'login' || to.name === 'register') {
      const token = getToken()
      const expiresAt = getTokenExpiry()
      const userType = getUserType()
      if (token && expiresAt && expiresAt > Date.now() && userType) {
        return next(roleDefaultRoute[userType] ?? '/user-home')
      }
    }
    return next()
  }

  const token = getToken()
  const expiresAt = getTokenExpiry()
  const userType = getUserType()

  if (!token || !expiresAt || expiresAt <= Date.now() || !userType) {
    removeToken()
    return next({ name: 'login' })
  }

  const allowedRoles = to.meta.roles as string[] | undefined
  const normalizedRole = userType.toUpperCase()

  if (allowedRoles && !allowedRoles.includes(normalizedRole)) {
    const fallback = roleDefaultRoute[normalizedRole] ?? '/login'
    return next(fallback)
  }

  if (!getSessionProfile()) {
    // 如果本地没有缓存的用户资料，让页面自行调用 /auth/me
    // 这里不做额外处理，仅保证守卫不阻塞
  }

  return next()
})

export default router
