import { createRouter, createWebHistory } from 'vue-router'

import { AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/user',
      name: 'user-dashboard',
      component: () => import('../views/UserDashboardView.vue'),
      meta: {
        requiresAuth: true,
        allowedRoles: ['user'],
      },
    },
    {
      path: '/role',
      name: 'role-landing',
      component: () => import('../views/RoleLandingView.vue'),
      meta: {
        requiresAuth: true,
      },
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const meta = to.meta as {
    requiresAuth?: boolean
    allowedRoles?: string[]
  }

  if (meta?.requiresAuth) {
    const token = sessionStorage.getItem(AUTH_TOKEN_KEY)
    if (!token) {
      window.alert('请先登录后再访问页面')
      next({ name: 'login' })
      return
    }

    const role = sessionStorage.getItem(AUTH_ROLE_KEY)
    if (meta.allowedRoles && (!role || !meta.allowedRoles.includes(role))) {
      window.alert('当前页面仅对普通用户开放')
      next({ name: 'login' })
      return
    }
  }

  next()
})

export default router
